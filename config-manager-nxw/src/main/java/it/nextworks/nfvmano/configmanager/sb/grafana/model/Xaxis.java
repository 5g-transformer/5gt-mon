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

public class Xaxis {

    private String mode;
    private boolean show;
    private String name;

    public Xaxis() {

    }

    @JsonProperty("mode")
    public String getMode() {
        return mode;
    }

    @JsonProperty("mode")
    private void setMode(String mode) {
        this.mode = mode;
    }

    public Xaxis mode(String mode) {
        this.mode = mode;
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

    public Xaxis show(boolean show) {
        this.show = show;
        return this;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    private void setName(String name) {
        this.name = name;
    }

    public Xaxis name(String name) {
        this.name = name;
        return this;
    }

}
