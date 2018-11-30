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

package it.nextworks.nfvmano.configmanager.sb.grafana.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;

import static io.vertx.core.json.Json.prettyMapper;

/**
 * Created by json2java on 22/06/17.
 * json2java author: Marco Capitani (m.capitani AT nextworks DOT it)
 */

public class GrafanaDashboardWrapper {

    private Meta meta;
    private GrafanaDashboard dashboard;

    public GrafanaDashboardWrapper() {

    }

    @JsonProperty("meta")
    public Meta getMeta() {
        return meta;
    }

    @JsonProperty("meta")
    private void setMeta(Meta meta) {
        this.meta = meta;
    }

    public GrafanaDashboardWrapper meta(Meta meta) {
        this.meta = meta;
        return this;
    }

    @JsonProperty("dashboard")
    public GrafanaDashboard getDashboard() {
        return dashboard;
    }

    @JsonProperty("dashboard")
    private void setDashboard(GrafanaDashboard dashboard) {
        this.dashboard = dashboard;
    }

    public GrafanaDashboardWrapper dashboard(GrafanaDashboard dashboard) {
        this.dashboard = dashboard;
        return this;
    }

    @Override
    public String toString() {
        try {
            return prettyMapper.writerWithDefaultPrettyPrinter().writeValueAsString(dashboard);
        } catch (JsonProcessingException exc) {
            throw new RuntimeException(exc);
        }
    }

}
