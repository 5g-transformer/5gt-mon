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

public class Route {

    public Route() {

    }

    private String receiver;

    @JsonProperty("receiver")
    public String getReceiver() {
        return receiver;
    }

    @JsonProperty("receiver")
    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public Route receiver(String receiver) {
        this.receiver = receiver;
        return this;
    }

    // TODO this is a hard-coded default. Different way?
    private String groupWait = "1s";

    @JsonProperty("group_wait")
    public String getGroupWait() {
        return groupWait;
    }

    @JsonProperty("group_wait")
    public void setGroupWait(String groupWait) {
        this.groupWait = groupWait;
    }

    public Route groupWait(String groupWait) {
        this.groupWait = groupWait;
        return this;
    }

    // TODO this is a hard-coded default. Different way?
    private String groupInterval = "1s";

    @JsonProperty("group_interval")
    public String getGroupInterval() {
        return groupInterval;
    }

    @JsonProperty("group_interval")
    public void setGroupInterval(String groupInterval) {
        this.groupInterval = groupInterval;
    }

    public Route groupInterval(String groupInterval) {
        this.groupInterval = groupInterval;
        return this;
    }

    // TODO this is a hard-coded default. Different way?
    private String repeatInterval = "1s";

    @JsonProperty("repeat_interval")
    public String getRepeatInterval() {
        return repeatInterval;
    }

    @JsonProperty("repeat_interval")
    public void setRepeatInterval(String repeatInterval) {
        this.repeatInterval = repeatInterval;
    }

    public Route repeatInterval(String repeatInterval) {
        this.repeatInterval = repeatInterval;
        return this;
    }

    private List<String> groupBy = new ArrayList<>();

    @JsonProperty("group_by")
    public List<String> getGroupBy() {
        return groupBy;
    }

    @JsonProperty("group_by")
    public void setGroupBy(List<String> groupBy) {
        if (null == groupBy) {
            groupBy = new ArrayList<>();
        }
        this.groupBy = groupBy;
    }

    public Route groupBy(List<String> groupBy) {
        this.groupBy = groupBy;
        return this;
    }

    private List<Routes> routes = new ArrayList<>();

    @JsonProperty("routes")
    public List<Routes> getRoutes() {
        return routes;
    }

    @JsonProperty("routes")
    public void setRoutes(List<Routes> routes) {
        if (routes == null) {
            routes = new ArrayList<>();
        }
        this.routes = routes;
    }

    public Route routes(List<Routes> routes) {
        this.routes = routes;
        return this;
    }

}