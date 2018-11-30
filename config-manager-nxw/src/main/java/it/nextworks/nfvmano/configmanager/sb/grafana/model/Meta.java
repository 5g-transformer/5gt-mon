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

public class Meta {

    private boolean canSave;
    private boolean canEdit;
    private String createdBy;
    private boolean canStar;

    public Meta() {

    }

    @JsonProperty("canSave")
    public boolean isCanSave() {
        return canSave;
    }

    @JsonProperty("canSave")
    private void setCanSave(boolean cansave) {
        this.canSave = cansave;
    }

    public Meta canSave(boolean cansave) {
        this.canSave = cansave;
        return this;
    }

    @JsonProperty("canEdit")
    public boolean isCanEdit() {
        return canEdit;
    }

    @JsonProperty("canEdit")
    private void setCanEdit(boolean canedit) {
        this.canEdit = canedit;
    }

    public Meta canEdit(boolean canedit) {
        this.canEdit = canedit;
        return this;
    }

    @JsonProperty("createdBy")
    public String getCreatedBy() {
        return createdBy;
    }

    @JsonProperty("createdBy")
    private void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Meta createdBy(String createdby) {
        this.createdBy = createdby;
        return this;
    }

    @JsonProperty("canStar")
    public boolean isCanStar() {
        return canStar;
    }

    @JsonProperty("canStar")
    private void setCanStar(boolean canstar) {
        this.canStar = canstar;
    }

    public Meta canStar(boolean canstar) {
        this.canStar = canstar;
        return this;
    }

}
