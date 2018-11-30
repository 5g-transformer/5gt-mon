/*
* Copyright 2018 Nextworks s.r.l.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package it.nextworks.nfvmano.configmanager;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.impl.HttpStatusException;
import it.nextworks.nfvmano.configmanager.common.ErrorResponse;
import it.nextworks.nfvmano.configmanager.dashboards.DashboardController;
import it.nextworks.nfvmano.configmanager.dashboards.DashboardRepo;
import it.nextworks.nfvmano.configmanager.dashboards.MemoryDashboardRepo;
import it.nextworks.nfvmano.configmanager.dashboards.model.Dashboard;
import it.nextworks.nfvmano.configmanager.dashboards.model.DashboardDescription;
import it.nextworks.nfvmano.configmanager.exporters.ExporterController;
import it.nextworks.nfvmano.configmanager.exporters.ExporterRepo;
import it.nextworks.nfvmano.configmanager.exporters.MemoryExporterRepo;
import it.nextworks.nfvmano.configmanager.exporters.model.Exporter;
import it.nextworks.nfvmano.configmanager.exporters.model.ExporterDescription;
import it.nextworks.nfvmano.configmanager.sb.grafana.GrafanaConnector;
import it.nextworks.nfvmano.configmanager.sb.grafana.GrafanaDashboardService;
import it.nextworks.nfvmano.configmanager.sb.prometheus.ExporterService;
import it.nextworks.nfvmano.configmanager.sb.prometheus.PrometheusConnector;
import it.nextworks.nfvmano.configmanager.utils.ConfigReader;
import it.nextworks.nfvmano.configmanager.utils.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static it.nextworks.nfvmano.configmanager.utils.ContextUtils.makeParsingHandler;
import static it.nextworks.nfvmano.configmanager.utils.ContextUtils.respond;

public class MainVerticle extends AbstractVerticle {

    // STATICS

    private static final Logger log = LoggerFactory.getLogger(MainVerticle.class);

    private static final Set<String> AVAILABLE_TYPES;

    static {
        HashSet<String> temp = new HashSet<>();
        temp.add("application/json");
        temp.add("application/x-yaml");
        AVAILABLE_TYPES = Collections.unmodifiableSet(
                temp
        );
    }

    private int port;

    // CLASS MEMBERS
    private String promConfigPath;
    private String promAlertRulesPath;
    private String grafanaHost;
    private int grafanaPort;
    private String grafanaToken;
    private ExporterController exporterController;
    private DashboardController dashboardController;

    private static Route producing(Route route) {
        for (String type : AVAILABLE_TYPES) {
            route.produces(type);
        }
        return route;
    }

    private void readConfig() {
        ConfigReader config = new ConfigReader();
        port = config.getIntProperty("server.port");
        promConfigPath = config.getProperty("prometheus.config");
        promAlertRulesPath = config.getProperty("prometheus.alertRules");
        grafanaHost = config.getProperty("grafana.host");
        grafanaPort = config.getIntProperty("grafana.port");
        grafanaToken = config.getProperty("grafana.token");
    }

    private void makeExporterController() {
        PrometheusConnector pConnector = new PrometheusConnector(promConfigPath, promAlertRulesPath);
        ExporterRepo exporterRepo = new MemoryExporterRepo(); // TODO make expRepo
        ExporterService exporterService = new ExporterService(pConnector, exporterRepo);
        exporterController = new ExporterController(exporterService);
    }

    private void makeDashboardController() {
        DashboardRepo dashboardRepo = new MemoryDashboardRepo();  // TODO make dash repo
        WebClient webClient = WebClient.create(
                vertx,
                new WebClientOptions()
                        .setDefaultHost(grafanaHost)
                        .setDefaultPort(grafanaPort)
        );
        GrafanaConnector gConnector = new GrafanaConnector(webClient, grafanaToken);
        GrafanaDashboardService service = new GrafanaDashboardService(gConnector, dashboardRepo);
        dashboardController = new DashboardController(service);
    }

    private void makeControllers() {
        makeExporterController();
        makeDashboardController();
    }

    private void makeExporterRoutes(Router router) {

        if (exporterController == null) {
            throw new IllegalStateException("Initialization incomplete, missing ExporterController");
        }

        producing(router.get(Paths.Rest.EXP))
                .handler(exporterController::getAllExporters);

        producing(router.post(Paths.Rest.EXP))
                .handler(makeParsingHandler(exporterController::postExporter, ExporterDescription.class));

        producing(router.get(Paths.Rest.ONE_EXP))
                .handler(exporterController::getExporter);

        producing(router.delete(Paths.Rest.ONE_EXP))
                .handler(exporterController::deleteExporter);

        producing(router.put(Paths.Rest.ONE_EXP))
                .handler(makeParsingHandler(exporterController::updateExporter, Exporter.class));
    }

    private void makeDashboardRoutes(Router router) {

        if (dashboardController == null) {
            throw new IllegalStateException("Initialization incomplete, missing dashboard controller");
        }

        producing(router.post(Paths.Rest.DASHBOARD))
                .handler(makeParsingHandler(dashboardController::postDashboard, DashboardDescription.class));

        producing(router.get(Paths.Rest.DASHBOARD))
                .handler(dashboardController::getAllDashboards);

        producing(router.get(Paths.Rest.ONE_DASHBOARD))
                .handler(dashboardController::getDashboard);

        producing(router.delete(Paths.Rest.ONE_DASHBOARD))
                .handler(dashboardController::deleteDashboard);

        producing(router.put(Paths.Rest.ONE_DASHBOARD))
                .handler(makeParsingHandler(dashboardController::updateDashboard, Dashboard.class));
    }

    @Override
    public void start() {
        readConfig();
        makeControllers();
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create()).failureHandler(this::handleFailure);
        makeExporterRoutes(router);
        makeDashboardRoutes(router);
        vertx.createHttpServer().requestHandler(router::accept).listen(port);
    }


    private void handleFailure(RoutingContext ctx) {
        Throwable error = ctx.failure();
        if (!(error instanceof HttpStatusException)) {
            log.error("Unexpected error:", error);
            respond(ctx, 500, new ErrorResponse(
                    "500",
                    "Internal Server Error",
                    "Unexpected error"
            ));
        } else {
            HttpStatusException httpError = (HttpStatusException) error;
            respond(
                    ctx,
                    httpError.getStatusCode(),
                    new ErrorResponse(
                            String.valueOf(httpError.getStatusCode()),
                            HttpResponseStatus.valueOf(httpError.getStatusCode()).reasonPhrase(),
                            httpError.getPayload()
                    )
            );
        }
    }
}
