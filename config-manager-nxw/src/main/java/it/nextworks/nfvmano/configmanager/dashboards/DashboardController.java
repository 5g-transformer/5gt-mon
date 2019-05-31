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

package it.nextworks.nfvmano.configmanager.dashboards;

import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.impl.HttpStatusException;
import it.nextworks.nfvmano.configmanager.dashboards.model.Dashboard;
import it.nextworks.nfvmano.configmanager.dashboards.model.DashboardDescription;
import it.nextworks.nfvmano.configmanager.dashboards.model.DashboardQueryResult;
import it.nextworks.nfvmano.configmanager.common.DeleteResponse;
import it.nextworks.nfvmano.configmanager.utils.ContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Marco Capitani on 25/10/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
public class DashboardController {

    private static final Logger log = LoggerFactory.getLogger(DashboardController.class);

    private DashboardRepo repo;

    public DashboardController(DashboardRepo repo) {
        this.repo = repo;
    }

    public void postDashboard(RoutingContext ctx) {
        log.info("Received call POST dashboard.");
        DashboardDescription desc = ctx.get("_parsed");
        log.debug("Dashboard description:\n{}", desc);
        Future<Dashboard> ar = repo.save(desc);
        ar.setHandler(
                future -> {
                    if (future.failed()) {
                        ctx.fail(future.cause());
                    } else {
                        log.debug("Dashboard saved.");
                        ContextUtils.respond(ctx, 201, future.result());
                        log.info("POST dashboard call completed.");
                    }
                }
        );
    }

    public void getAllDashboards(RoutingContext ctx) {
        log.info("Received call GET all dashboard.");
        Future<Set<Dashboard>> ar = repo.findAll();
        ar.setHandler(future -> {
                    if (future.failed()) {
                        ctx.fail(future.cause());
                    } else {
                        log.debug("Retrieved {} dashboards", future.result().size());
                        DashboardQueryResult response = new DashboardQueryResult(new ArrayList<>(future.result()));
                        ContextUtils.respond(ctx, response);
                        log.info("GET all dashboards call completed.");
                    }
                }
        );

    }

    public void getDashboard(RoutingContext ctx) {
        String dashboardId = ctx.pathParam("dashId");
        log.info("Received call GET dashboard {}.", dashboardId);
        Future<Dashboard> ar = repo.findById(dashboardId);
        ar.setHandler(
                future -> {
                    if (future.failed()) {
                        ctx.fail(future.cause());
                    } else {
                        Dashboard dashboard = future.result();
                        if (dashboard == null) {
                            ctx.fail(new HttpStatusException(
                                    404,
                                    String.format("No dashboard with id '%s'", dashboardId)
                            ));
                        } else {
                            ContextUtils.respond(ctx, dashboard);
                        }
                        log.info("GET dashboard {} call completed.", dashboardId);
                    }
                }
        );
    }

    public void deleteDashboard(RoutingContext ctx) {
        String dashboardId = ctx.pathParam("dashId");
        log.info("Received call DELETE dashboard {}.", dashboardId);
        Future<Set<String>> ar = repo.deleteById(dashboardId);
        ar.setHandler(
                future -> {
                    if (future.failed()) {
                        ctx.fail(future.cause());
                    } else {
                        Set<String> deleted = future.result();
                        DeleteResponse response = new DeleteResponse();
                        response.setDeleted(new ArrayList<>(deleted));
                        ContextUtils.respond(ctx, response);
                        log.info("DELETE dashboard {} call completed.", dashboardId);
                    }
                }
        );
    }

    public void updateDashboard(RoutingContext ctx) {
        String dashboardId = ctx.pathParam("dashId");
        log.info("Received call PUT dashboard {}.", dashboardId);
        Dashboard dashboard = ctx.get("_parsed");
        log.debug("New dashboard:\n{}", dashboard);
        if (!dashboardId.equals(dashboard.getDashboardId())) {
            ctx.fail(new HttpStatusException(
                    400,
                    "Dashboard ID in body and path must match"
            ));
            log.info("PUT dashboard {} call completed.", dashboardId);
        } else {
            Future<Dashboard> ar = repo.update(dashboard);
            ar.setHandler(
                    future -> {
                        if (future.failed()) {
                            ctx.fail(future.cause());
                        } else {
                            Dashboard updated = future.result();
                            ContextUtils.respond(ctx, updated);
                            log.info("PUT dashboard {} call completed.", dashboardId);
                        }
                    }
            );
        }
    }
}
