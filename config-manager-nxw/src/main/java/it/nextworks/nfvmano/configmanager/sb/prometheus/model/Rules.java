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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by json2java on 22/06/17.
 * json2java author: Marco Capitani (m.capitani AT nextworks DOT it)
 */

public class Rules {

    @JsonProperty("annotations")
    private Map<String, String> annotations = new HashMap<>();
    @JsonProperty("expr")
    private String expr;
    @JsonProperty("alert")
    private String alert;
    @JsonProperty("labels")
    private Map<String, String> labels = new HashMap<>();
    @JsonProperty("for")
    private String forTime;

    public Rules() {

    }

    public Rules(
            Map<String, String> annotations,
            String expr,
            String alert,
            Map<String, String> labels,
            String forTime
    ) {
        this.annotations = annotations;
        this.expr = expr;
        this.alert = alert;
        this.labels = labels;
        this.forTime = forTime;
    }

    @JsonProperty("annotations")
    public Map<String, String> getAnnotations() {
        return annotations;
    }

    @JsonProperty("annotations")
    public void setAnnotations(Map<String, String> annotations) {
        this.annotations = annotations;
    }

    @JsonProperty("expr")
    public String getExpr() {
        return expr;
    }

    @JsonProperty("expr")
    public void setExpr(String expr) {
        this.expr = expr;
    }

    @JsonProperty("alert")
    public String getAlert() {
        return alert;
    }

    @JsonProperty("alert")
    public void setAlert(String alert) {
        this.alert = alert;
    }

    @JsonProperty("labels")
    public Map<String, String> getLabels() {
        return labels;
    }

    @JsonProperty("labels")
    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }

    @JsonProperty("for")
    public String getForTime() {
        return forTime;
    }

    @JsonProperty("for")
    public void setForTime(String forTime) {
        this.forTime = forTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Rules)) return false;
        Rules rules = (Rules) o;
        return Objects.equals(getAnnotations(), rules.getAnnotations()) &&
                Objects.equals(getExpr(), rules.getExpr()) &&
                Objects.equals(getAlert(), rules.getAlert()) &&
                Objects.equals(getLabels(), rules.getLabels()) &&
                Objects.equals(getForTime(), rules.getForTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAnnotations(), getExpr(), getAlert(), getLabels(), getForTime());
    }
}
