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

package it.nextworks.nfvmano.configmanager.exporters;

import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.impl.HttpStatusException;
import it.nextworks.nfvmano.configmanager.exporters.model.Exporter;
import it.nextworks.nfvmano.configmanager.exporters.model.ExporterDescription;
import it.nextworks.nfvmano.configmanager.exporters.model.ExporterQueryResult;
import it.nextworks.nfvmano.configmanager.common.DeleteResponse;
import it.nextworks.nfvmano.configmanager.utils.ContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Marco Capitani on 01/10/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
public class ExporterController {

    private static final Logger log = LoggerFactory.getLogger(ExporterController.class);

    private final ExporterRepo repo;

    public ExporterController(ExporterRepo repo) {
        this.repo = repo;
    }

    public void postExporter(RoutingContext ctx) {
        log.info("Received call POST exporter.");
        ExporterDescription desc = ctx.get("_parsed");
        log.debug("Exporter description:\n{}", desc);
        ContextUtils.handleAndRespond(
                ctx,
                () -> repo.save(desc),
                "save exporter",
                "POST exporter",
                201
        );
    }

    public void getAllExporters(RoutingContext ctx) {
        log.info("Received call GET all exporter.");
        ContextUtils.handleAndRespond(
                ctx,
                () -> repo.findAll().map(ExporterQueryResult::new),
                "get all exporter",
                "GET all exporter"
        );
    }

    public void getExporter(RoutingContext ctx) {
        String expId = ctx.pathParam("expId");
        log.info("Received call GET exporter {}.", expId);
        ContextUtils.handleLogging(
                ctx,
                () -> repo.findById(expId),
                "get exporter",
                exporter -> {
                    log.info("GET exporter {} call completed.", expId);
                    ContextUtils.respond(ctx, exporter); // Respond will take care of the optional
                }
        );
    }

    public void deleteExporter(RoutingContext ctx) {
        String expId = ctx.pathParam("expId");
        log.info("Received call DELETE exporter {}.", expId);
        ContextUtils.handleAndRespond(
                ctx,
                () -> {
                    Future<Set<String>> future = repo.deleteById(expId);
                    return future.map(deleted -> {
                        DeleteResponse response = new DeleteResponse();
                        response.setDeleted(new ArrayList<>(deleted));
                        return response;
                    });
                },
                "delete exporter",
                "DELETE exporter"
        );
    }

    public void updateExporter(RoutingContext ctx) {
        String expId = ctx.pathParam("expId");
        Exporter exporter = ctx.get("_parsed");
        log.info("Received call PUT exporter {}.", expId);
        log.debug("New exporter:\n{}", exporter);
        if (!expId.equals(exporter.getExporterId())) {
            ctx.fail(new HttpStatusException(
                    400,
                    "Exporter ID in body and path must match"
            ));
            log.info("PUT exporter {} call completed.", expId);
        } else {
            ContextUtils.handleAndRespond(
                    ctx,
                    () -> repo.update(exporter),
                    "update exporter",
                    "PUT exporter"
            );
        }
    }
}
