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

package it.nextworks.nfvmano.configmanager.sb.prometheus.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by json2java on 22/06/17.
 * json2java author: Marco Capitani (m.capitani AT nextworks DOT it)
 */

public class AlertManagerConfig {

    public AlertManagerConfig() {

    }

    private AMGlobal global = new AMGlobal();

    @JsonProperty("global")
    public AMGlobal getGlobal() {
        return global;
    }

    @JsonProperty("global")
    public void setGlobal(AMGlobal global) {
        this.global = global;
    }

    public AlertManagerConfig global(AMGlobal global) {
        this.global = global;
        return this;
    }

    private Route route = new Route();

    @JsonProperty("route")
    public Route getRoute() {
        return route;
    }

    @JsonProperty("route")
    public void setRoute(Route route) {
        this.route = route;
    }

    public AlertManagerConfig route(Route route) {
        this.route = route;
        return this;
    }

    private List<Receivers> receivers = new ArrayList<>();

    @JsonProperty("receivers")
    public List<Receivers> getReceivers() {
        return receivers;
    }

    @JsonProperty("receivers")
    public void setReceivers(List<Receivers> receivers) {
        if (null == receivers) {
            receivers = new ArrayList<>();
        }
        this.receivers = receivers;
    }

    public AlertManagerConfig receivers(List<Receivers> receivers) {
        this.receivers = receivers;
        return this;
    }

}