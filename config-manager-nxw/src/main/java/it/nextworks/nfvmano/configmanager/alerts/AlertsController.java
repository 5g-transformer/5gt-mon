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

package it.nextworks.nfvmano.configmanager.alerts;

import io.vertx.core.Future;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.api.validation.ValidationException;
import io.vertx.ext.web.handler.impl.HttpStatusException;
import it.nextworks.nfvmano.configmanager.alerts.model.Alert;
import it.nextworks.nfvmano.configmanager.common.DeleteResponse;
import it.nextworks.nfvmano.configmanager.utils.ContextUtils;
import it.nextworks.nfvmano.configmanager.utils.Validated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by Marco Capitani on 05/04/19.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
public class AlertsController {

    private static final Logger log = LoggerFactory.getLogger(AlertsController.class);

    private AlertRepo repo;

    private static void validate(RoutingContext ctx) {
        Validated raw = ctx.get("_parsed");
        Optional<ValidationException> error = raw.validate();
        if (error.isPresent()) {
            ctx.fail(new HttpStatusException(400, error.get().getMessage()));
        } else {
            ctx.next();
        }
    }

    public AlertsController(AlertRepo repo) {
        this.repo = repo;
    }

    public static <T extends Validated, S> void installHandler(
            Route route,
            Function<T, Future<S>> main,
            String loggedOp
    ) {
        // Validation
        route.handler(ctx -> {
            log.info("Received call to {}", loggedOp);
            validate(ctx);
        });
        // Business
        route.handler(ctx -> {
            // Transform
            log.info("Validation successful, executing op {}", loggedOp);
            Future<S> future = main.apply(ctx.get("_parsed"));
            // Await result
            ContextUtils.await(future, ctx);
        });
        // Respond
        route.handler(ctx -> {
            log.info("Sending response for op {}", loggedOp);
            Object response = ctx.get("_awaited");
            log.debug("Response: {}", response);
            ContextUtils.respond(ctx, response);
        });
    }

    public static <S> void installHandler(
            Route route,
            Supplier<Future<S>> main,
            String loggedOp
    ) {
        // No input, no validation
        // Business
        route.handler(ctx -> {
            // Transform
            log.info("Validation successful, executing op {}", loggedOp);
            Future<S> future = main.get();
            // Await result
            ContextUtils.await(future, ctx);
        });
        // Respond
        route.handler(ctx -> {
            log.info("Sending response for op {}", loggedOp);
            Object response = ctx.get("_awaited");
            log.debug("Response: {}", response);
            ContextUtils.respond(ctx, response);
        });
    }

    public void getAllAlerts(Route route) {
        installHandler(route, repo::findAll, "getAllAlerts");
    }

    public void postAlert(Route route) {
        route.handler(ContextUtils.parsing(Alert.class));
        installHandler(route, repo::save, "postAlert");
    }

    public void getAlert(Route route) {
        String loggedOp = "getAlert";
        // No Validation
        // Business
        route.handler(ctx -> {
            // Transform
            String alertId = ctx.pathParam("alertId");
            log.info("Validation successful, executing op {} on {}", loggedOp, alertId);
            Future<Optional<Alert>> future = repo.findById(alertId);
            ContextUtils.await(future, ctx);
        });
        // Respond
        route.handler(ctx -> {
            log.info("Sending response for op {}", loggedOp);
            Object response = ctx.get("_awaited");
            log.debug("Response: {}", response);
            ContextUtils.respond(ctx, response);
        });
    }

    public void putAlert(Route route) {
        String loggedOp = "putAlert";
        route.handler(ContextUtils.parsing(Alert.class));
        // Validation
        route.handler(ctx -> {
            log.info("Received call to {}", loggedOp);
            validate(ctx);
        });
        // Business
        route.handler(ctx -> {
            // Transform
            String alertId = ctx.pathParam("alertId");
            log.info("Validation successful, executing op {} on {}", loggedOp, alertId);
            Alert newAlert = ctx.get("_parsed");
            if (alertId.equals(newAlert.getAlertId())) {
                ctx.fail(new HttpStatusException(400, "Cannot modify alertId"));
            }
            Future<Alert> future = repo.update(newAlert);
            ContextUtils.await(future, ctx);
        });
        // Respond
        route.handler(ctx -> {
            log.info("Sending response for op {}", loggedOp);
            Object response = ctx.get("_awaited");
            log.debug("Response: {}", response);
            ContextUtils.respond(ctx, response);
        });
    }

    public void deleteAlert(Route route) {
        String loggedOp = "deleteAlert";
        // No Validation
        // Business
        route.handler(ctx -> {
            // Transform
            String alertId = ctx.pathParam("alertId");
            log.info("Validation successful, executing op {} on {}", loggedOp, alertId);
            Future<Set<String>> future = repo.deleteById(alertId);
            // Translate missing alert into 404 error
            future = future.compose(s -> {
                if (s.size() == 0) {
                    ctx.fail(new HttpStatusException(404, "No such alert"));
                }
                return Future.succeededFuture(s);
            });
            ContextUtils.await(future, ctx);
        });
        // Respond
        route.handler(ctx -> {
            log.info("Sending response for op {}", loggedOp);
            DeleteResponse response = new DeleteResponse().deleted(ctx.<Set<String>>get("_awaited"));
            log.debug("Response: {}", response);
            ContextUtils.respond(ctx, response);
        });
    }
}
