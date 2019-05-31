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

package it.nextworks.nfvmano.configmanager.sb.grafana;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.vertx.core.Future;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.handler.impl.HttpStatusException;
import it.nextworks.nfvmano.configmanager.common.BodyCodecs;
import it.nextworks.nfvmano.configmanager.sb.grafana.model.GrafanaDashboardWrapper;
import it.nextworks.nfvmano.configmanager.sb.grafana.model.PostDashboardResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.StringJoiner;

/**
 * Created by Marco Capitani on 22/10/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
public class GrafanaConnector {

    private static final Logger log = LoggerFactory.getLogger(GrafanaConnector.class);

    private WebClient client;

    private String authHeader;

    private String baseUrl;

    private boolean reportFullUrl;

    public GrafanaConnector(WebClient client, String bearerToken) {
        this(client, bearerToken, null, false);
    }

    public GrafanaConnector(WebClient client, String bearerToken, String baseUrl) {
        this(client, bearerToken, baseUrl, true);
    }

    public GrafanaConnector(WebClient client, String bearerToken, String baseUrl, boolean fullUrl) {
        this.client = client;
        this.authHeader = "Bearer " + bearerToken;
        this.baseUrl = baseUrl;
        this.reportFullUrl = fullUrl;
    }

    Future<PostDashboardResponse> postDashboard(
            GrafanaDashboardWrapper dashboard
    ) {
        log.debug("DashBoard:\n{}", dashboard.toString());
        log.debug("Requesting new dashboard {} to Grafana", dashboard.getDashboard().getTitle());
        HttpRequest<PostDashboardResponse> post =
                client.post("/api/dashboards/db")
                        .as(BodyCodecs.jsonCatching(PostDashboardResponse.class))
                        .putHeader(HttpHeaderNames.AUTHORIZATION.toString(), authHeader);

        Future<HttpResponse<PostDashboardResponse>> future = Future.future();
        post.sendJson(dashboard, future);
        // Now the post result is stored in future

        // Check output before returning the value
        return future.compose(this::checkGrafanaOutput);
    }

    Future<HttpResponse<DeleteResponse>> deleteDashboard(
            String uid
    ) {
        HttpRequest<DeleteResponse> delete = client.delete("/api/dashboards/uid/" + uid)
                .as(BodyCodecs.jsonCatching(DeleteResponse.class))
                .putHeader(HttpHeaderNames.AUTHORIZATION.toString(), authHeader);

        Future<HttpResponse<DeleteResponse>> future = Future.future();
        delete.send(future);
        return future;
    }

    @SuppressWarnings("WeakerAccess") // Needed for Jackson injection
    static class DeleteResponse {
        public String message;
        public String title;

        @Override
        public String toString() {
            return new StringJoiner(", ", DeleteResponse.class.getSimpleName() + "[", "]")
                    .add("message='" + message + "'")
                    .add("title='" + title + "'")
                    .toString();
        }
    }

    private Future<PostDashboardResponse> checkGrafanaOutput(
            HttpResponse<PostDashboardResponse> response
    ) {
        int code = response.statusCode();
        if (code == 412) {
            // 412 == precondition failed == name conflict
            return Future.failedFuture(
                    new HttpStatusException(409, "Dashboard name conflict, please use a different name")
            );
        } else if (code == 401 || code == 403) {
            log.warn("Grafana authentication error: code {}, response: {}", code, response.body());
            return Future.failedFuture(new IllegalArgumentException(
                    "Authentication error, please check the API key provided"
            ));
        } else if (code < 200 || 300 <= code) {
            // Problematic code or body not parsed correctly
            log.warn("Grafana unexpected error: code {}, response: {}", code, response.body());
            return Future.failedFuture(new IllegalArgumentException(
                    String.format("Unexpected response from Grafana: status code %s, response %s", code, response.body())
            ));
        } else {
            // All good
            // edit the URL adding the base
            PostDashboardResponse out = response.body();
            String rUrl = out.url;
            if ((!rUrl.startsWith("http://")) && reportFullUrl) {
                rUrl = baseUrl + rUrl;
                out.url = rUrl;
            }
            return Future.succeededFuture(out);
        }
    }
}
