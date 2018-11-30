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

public class Row {

    private String title;
    private boolean collapse;
    private List<Panel> panels;
    private int titleSize;
    private int height;

    public Row() {

    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    private void setTitle(String title) {
        this.title = title;
    }

    public Row title(String title) {
        this.title = title;
        return this;
    }

    @JsonProperty("collapse")
    public boolean isCollapse() {
        return collapse;
    }

    @JsonProperty("collapse")
    private void setCollapse(boolean collapse) {
        this.collapse = collapse;
    }

    public Row collapse(boolean collapse) {
        this.collapse = collapse;
        return this;
    }

    @JsonProperty("panels")
    public List<Panel> getPanels() {
        return panels;
    }

    @JsonProperty("panels")
    private void setPanels(List<Panel> panels) {
        this.panels = panels;
    }

    public Row panels(List<Panel> panels) {
        this.panels = panels;
        return this;
    }

    @JsonProperty("titleSize")
    public int getTitleSize() {
        return titleSize;
    }

    @JsonProperty("titleSize")
    private void setTitleSize(int titlesize) {
        this.titleSize = titlesize;
    }

    public Row titleSize(int titlesize) {
        this.titleSize = titlesize;
        return this;
    }

    @JsonProperty("height")
    public int getHeight() {
        return height;
    }

    @JsonProperty("height")
    private void setHeight(int height) {
        this.height = height;
    }

    public Row height(int height) {
        this.height = height;
        return this;
    }

}
