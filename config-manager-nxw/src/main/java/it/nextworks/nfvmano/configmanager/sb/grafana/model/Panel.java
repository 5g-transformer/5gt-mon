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

import java.util.List;

/**
 * Created by json2java on 22/06/17.
 * json2java author: Marco Capitani (m.capitani AT nextworks DOT it)
 */

public class Panel {

    private List<Target> targets;
    private Xaxis xaxis;
    private String height;
    private List<Yaxes> yaxes;
    private int span;
    private boolean lines;
    private int fill;
    private String datasource;
    private String type;
    private String title;
    private int linewidth;
    private boolean editable;

    public Panel() {

    }

    @JsonProperty("targets")
    public List<Target> getTargets() {
        return targets;
    }

    @JsonProperty("targets")
    private void setTargets(List<Target> targets) {
        this.targets = targets;
    }

    public Panel targets(List<Target> targets) {
        this.targets = targets;
        return this;
    }

    @JsonProperty("xaxis")
    public Xaxis getXAxis() {
        return xaxis;
    }

    @JsonProperty("xaxis")
    private void setXAxis(Xaxis xaxis) {
        this.xaxis = xaxis;
    }

    public Panel xAxis(Xaxis xaxis) {
        this.xaxis = xaxis;
        return this;
    }

    @JsonProperty("height")
    public String getHeight() {
        return height;
    }

    @JsonProperty("height")
    private void setHeight(String height) {
        this.height = height;
    }

    public Panel height(String height) {
        this.height = height;
        return this;
    }

    @JsonProperty("yaxes")
    public List<Yaxes> getYAxes() {
        return yaxes;
    }

    @JsonProperty("yaxes")
    private void setYAxes(List<Yaxes> yaxes) {
        this.yaxes = yaxes;
    }

    public Panel yAxes(List<Yaxes> yaxes) {
        this.yaxes = yaxes;
        return this;
    }

    @JsonProperty("span")
    public int getSpan() {
        return span;
    }

    @JsonProperty("span")
    private void setSpan(int span) {
        this.span = span;
    }

    public Panel span(int span) {
        this.span = span;
        return this;
    }

    @JsonProperty("lines")
    public boolean isLines() {
        return lines;
    }

    @JsonProperty("lines")
    private void setLines(boolean lines) {
        this.lines = lines;
    }

    public Panel lines(boolean lines) {
        this.lines = lines;
        return this;
    }

    @JsonProperty("fill")
    public int getFill() {
        return fill;
    }

    @JsonProperty("fill")
    private void setFill(int fill) {
        this.fill = fill;
    }

    public Panel fill(int fill) {
        this.fill = fill;
        return this;
    }

    @JsonProperty("datasource")
    public String getDatasource() {
        return datasource;
    }

    @JsonProperty("datasource")
    private void setDatasource(String datasource) {
        this.datasource = datasource;
    }

    public Panel datasource(String datasource) {
        this.datasource = datasource;
        return this;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    private void setType(String type) {
        this.type = type;
    }

    public Panel type(String type) {
        this.type = type;
        return this;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    private void setTitle(String title) {
        this.title = title;
    }

    public Panel title(String title) {
        this.title = title;
        return this;
    }

    @JsonProperty("linewidth")
    public int getLinewidth() {
        return linewidth;
    }

    @JsonProperty("linewidth")
    private void setLinewidth(int linewidth) {
        this.linewidth = linewidth;
    }

    public Panel linewidth(int linewidth) {
        this.linewidth = linewidth;
        return this;
    }

    @JsonProperty("editable")
    public boolean isEditable() {
        return editable;
    }

    @JsonProperty("editable")
    private void setEditable(boolean editable) {
        this.editable = editable;
    }

    public Panel editable(boolean editable) {
        this.editable = editable;
        return this;
    }

}
