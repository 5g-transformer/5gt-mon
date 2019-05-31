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

package it.nextworks.nfvmano.configmanager.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.impl.HttpStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Supplier;

/**
 * Created by Marco Capitani on 03/10/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
public class ContextUtils {

    private static final Logger log = LoggerFactory.getLogger(ContextUtils.class);

    private static final String APPLICATION_YAML = "application/x-yaml";
    private static final String APPLICATION_JSON = "application/json";

    private static final Map<String, ObjectMapper> workingTypes;
    private static final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
    private static final ObjectMapper jsonMapper = new ObjectMapper();
    private static String ERROR_RESPONDING = new StringJoiner("\n")
            .add("Internal server error")
            .add("Error during generation of response")
            .toString();

    static {
        workingTypes = new HashMap<>();
        workingTypes.put(APPLICATION_JSON, jsonMapper);
        workingTypes.put(APPLICATION_YAML, yamlMapper);
    }

    public static void respond(RoutingContext ctx, Object object) {
        respond(ctx, 200, object);
    }

    public static void respond(RoutingContext ctx, int statusCode) {
        ctx.response().setStatusCode(statusCode).end();
    }

    public static void respond(RoutingContext ctx, int statusCode, Object object) {
        // Check if the response is an optional for special behaviour
        if (object instanceof Optional) {
            Optional opt = (Optional) object;
            if (opt.isPresent()) {
                // Unwrap it
                object = opt.get();
            } else {
                // Not found
                ctx.fail(new HttpStatusException(404, "No such element"));
                return;
            }
        }
        String accepts = ctx.getAcceptableContentType();
        if (accepts == null) {
            accepts = APPLICATION_JSON;
        }
        // Strip parameters (e.g. charset)
        if (accepts.contains(";")) {
            String[] split = accepts.split(";");
            for (String subString : split) {
                if (subString.contains("charset")) {
                    continue;
                }
                accepts = subString.trim();
            }
        }
        if ((!workingTypes.containsKey(accepts))) {
            ctx.response().setStatusCode(406).end(new StringJoiner("\n")
                    .add("Not Acceptable")
                    .add("Cannot provide requested media-type")
                    .add(String.format(
                            "Not acceptable. Requested mime-type %s, available mime-types: %s",
                            accepts,
                            workingTypes.keySet().toString()
                    ))
                    .toString()
            );
            return;
        }
        ctx.response().setStatusCode(statusCode);
        ObjectMapper objectMapper = workingTypes.get(accepts);
        try {
            String serialized = objectMapper.writeValueAsString(object);
            ctx.response().putHeader(HttpHeaderNames.CONTENT_TYPE, accepts).end(serialized);
        } catch (JsonProcessingException exc) {
            log.error("Could not serialize object {}", object);
            ctx.response().setStatusCode(500).end(ERROR_RESPONDING);
        }
    }

    private static String checkContentType(String headerValue) {
        if (headerValue == null) {
            return null;
        }
        if (headerValue.contains(";")) {
            String[] split = headerValue.split(";");
            for (String subString : split) {
                if (subString.contains("charset")) {
                    continue;
                }
                headerValue = subString.trim();
            }
        }
        if (workingTypes.containsKey(headerValue)) {
            return headerValue;
        } else {
            return null;
        }
    }

    public static <T> T readBody(RoutingContext ctx, Class<T> type) {
        String contentType = ctx.request().getHeader(HttpHeaders.CONTENT_TYPE);
        String parsedContentType = checkContentType(contentType);
        if (parsedContentType == null) {
            ctx.response().setStatusCode(415).end(new StringJoiner("\n")
                    .add("Unsupported Media Type")
                    .add("Cannot understand provided media type")
                    .add(String.format(
                            "Provided mime-type %s, understood mime-types: %s",
                            contentType,
                            workingTypes.keySet().toString()
                    )).toString()
            );
            return null;
        } else {
            String body = ctx.getBodyAsString();
            try {
                ObjectMapper mapper = workingTypes.get(parsedContentType);
                return mapper.readValue(body, type);
            } catch (IOException exc) {
                ctx.fail(new HttpStatusException(
                        400,
                        String.format("Cannot parse request body: %s", exc.getMessage())
                ));
                return null;
            }
        }
    }

    public static <T> T readBodyThrowing(RoutingContext ctx, Class<T> type) {
        String contentType = ctx.request().getHeader(HttpHeaders.CONTENT_TYPE);
        String parsedContentType = checkContentType(contentType);
        if (parsedContentType == null) {
            log.warn("Not supported content type {}", contentType);
            throw new HttpStatusException(
                    415,
                    String.format(
                            "Cannot understand media type. Provided %s, understood: %s",
                            contentType,
                            workingTypes.keySet().toString()
                    )
            );
        } else {
            String body = ctx.getBodyAsString();
            try {
                ObjectMapper mapper = workingTypes.get(parsedContentType);
                return mapper.readValue(body, type);
            } catch (IOException exc) {
                log.warn("Deserialization error: {}", exc.getMessage());
                throw new HttpStatusException(
                        400,
                        String.format("Cannot parse request body: %s", exc.getMessage())
                );
            }
        }
    }

    public static <T> Handler<RoutingContext> makeParsingHandler(Handler<RoutingContext> handler, Class<T> bodyType) {
        return ctx -> {
            T body = readBody(ctx, bodyType);
            if (body != null) {
                ctx.put("_parsed", body);
                handler.handle(ctx);
            }
        };
    }

    public static <T> Handler<RoutingContext> parsing(Class<T> bodyType) {
        return ctx -> {
            T body = readBodyThrowing(ctx, bodyType);
            if (body != null) {
                ctx.put("_parsed", body);
                ctx.next();
            } else {
                ctx.fail(new HttpStatusException(400, "Null payload"));
            }
        };
    }

    public static <T> void handleLogging(
            Supplier<Future<T>> supplier,
            String operation,
            Handler<T> handler,
            Handler<Throwable> errorHandler
    ) {
        Future<T> future = supplier.get();
        future.setHandler(ar -> {
            if (ar.failed()) {
                log.error("Error in op '{}': {}", operation, ar.cause().getMessage());
                log.debug("Error details: ", ar.cause());
                errorHandler.handle(ar.cause());
            } else {
                log.debug("Op '{}' successful.", operation);
                handler.handle(ar.result());
            }
        });
    }

    public static <T> void handleLogging(
            RoutingContext ctx,
            Supplier<Future<T>> supplier,
            String operation,
            Handler<T> handler
    ) {
        handleLogging(
                supplier,
                operation,
                handler,
                ctx::fail
        );
    }

    public static <T> void handleAndRespond(
            RoutingContext ctx,
            Supplier<Future<T>> supplier,
            String operation,
            String apiCallName,
            int successCode
    ) {
        handleLogging(
                supplier,
                operation,
                res -> {
                    ContextUtils.respond(ctx, successCode, res);
                    log.info("Response to '{}' call sent.", apiCallName);
                },
                ctx::fail
        );
    }

    public static <T> void handleAndRespond(
            RoutingContext ctx,
            Supplier<Future<T>> supplier,
            String operation,
            String apiCallName
    ) {
        handleAndRespond(
                ctx,
                supplier,
                operation,
                apiCallName,
                200
        );
    }

    public static <T> void await(Future<T> future, RoutingContext ctx) {
        future.setHandler(ar -> {
            if (ar.failed()) {
                ctx.fail(ar.cause());
            } else {
                ctx.put("_awaited", ar.result());
                ctx.next();
            }
        });
    }
}
