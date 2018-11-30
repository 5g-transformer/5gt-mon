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

/**
 * Created by json2java on 22/06/17.
 * json2java author: Marco Capitani (m.capitani AT nextworks DOT it)
 */

public class Rules {

    @JsonProperty("annotations")
    private Annotations annotations;
    @JsonProperty("expr")
    private String expr;
    @JsonProperty("alert")
    private String alert;
    @JsonProperty("labels")
    private Labels labels;

    public Rules() {

    }

    @JsonProperty("annotations")
    public Annotations getAnnotations() {
        return annotations;
    }

    @JsonProperty("annotations")
    private void setAnnotations(Annotations annotations) {
        this.annotations = annotations;
    }

    @JsonProperty("expr")
    public String getExpr() {
        return expr;
    }

    @JsonProperty("expr")
    private void setExpr(String expr) {
        this.expr = expr;
    }

    @JsonProperty("alert")
    public String getAlert() {
        return alert;
    }

    @JsonProperty("alert")
    private void setAlert(String alert) {
        this.alert = alert;
    }

    @JsonProperty("labels")
    public Labels getLabels() {
        return labels;
    }

    @JsonProperty("labels")
    private void setLabels(Labels labels) {
        this.labels = labels;
    }

}
