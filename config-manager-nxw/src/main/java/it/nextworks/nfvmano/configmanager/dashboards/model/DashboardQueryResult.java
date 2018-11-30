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

package it.nextworks.nfvmano.configmanager.dashboards.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DashboardQueryResult {

    @JsonProperty("dashboard")
    private List<Dashboard> dashboard;

    public DashboardQueryResult(List<Dashboard> dashboard) {
        this.dashboard = dashboard;
    }

    public DashboardQueryResult alert(List<Dashboard> dashboard) {
        this.dashboard = dashboard;
        return this;
    }

    public DashboardQueryResult addDashboardItem(Dashboard dashboardItem) {
        if (this.dashboard == null) {
            this.dashboard = new ArrayList<>();
        }
        this.dashboard.add(dashboardItem);
        return this;
    }

    /**
     * the list of dashboards matching the query
     *
     * @return alert
     **/
    public List<Dashboard> getDashboard() {
        return dashboard;
    }

    public void setDashboard(List<Dashboard> dashboard) {
        this.dashboard = dashboard;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DashboardQueryResult dashboardQueryResult = (DashboardQueryResult) o;
        return Objects.equals(this.dashboard, dashboardQueryResult.dashboard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dashboard);
    }


    @Override
    public String toString() {

        return "class DashboardQueryResult {\n" +
                "    dashboard: " + toIndentedString(dashboard) + "\n" +
                "}";
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

}

