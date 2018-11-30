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

/**
 * Created by json2java on 22/06/17.
 * json2java author: Marco Capitani (m.capitani AT nextworks DOT it)
 */

public class Yaxes {

    private String label;
    private String format;
    private int logBase;
    private boolean show;
    private Integer min;
    private Integer max;

    public Yaxes() {

    }

    @JsonProperty("label")
    public String getLabel() {
        return label;
    }

    @JsonProperty("label")
    private void setLabel(String label) {
        this.label = label;
    }

    public Yaxes label(String label) {
        this.label = label;
        return this;
    }

    @JsonProperty("format")
    public String getFormat() {
        return format;
    }

    @JsonProperty("format")
    private void setFormat(String format) {
        this.format = format;
    }

    public Yaxes format(String format) {
        this.format = format;
        return this;
    }

    @JsonProperty("logBase")
    public int getLogBase() {
        return logBase;
    }

    @JsonProperty("logBase")
    private void setLogBase(int logBase) {
        this.logBase = logBase;
    }

    public Yaxes logBase(int logbase) {
        this.logBase = logbase;
        return this;
    }

    @JsonProperty("show")
    public boolean isShow() {
        return show;
    }

    @JsonProperty("show")
    private void setShow(boolean show) {
        this.show = show;
    }

    public Yaxes show(boolean show) {
        this.show = show;
        return this;
    }

    @JsonProperty("min")
    public Integer getMin() {
        return min;
    }

    @JsonProperty("min")
    private void setMin(Integer min) {
        this.min = min;
    }

    public Yaxes min(Integer min) {
        this.min = min;
        return this;
    }

    @JsonProperty("max")
    public Integer getMax() {
        return max;
    }

    @JsonProperty("max")
    private void setMax(Integer max) {
        this.max = max;
    }

    public Yaxes max(Integer max) {
        this.max = max;
        return this;
    }

}
