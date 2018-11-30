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

public class DashboardDescription {
    @JsonProperty("name")
    private String name = null;

    @JsonProperty("panels")
    private List<DashboardPanel> panels = null;

    @JsonProperty("users")
    private List<String> users = null;

    @JsonProperty("plottedTime")
    private Integer plottedTime = null;
    @JsonProperty("refreshTime")
    private RefreshTimeEnum refreshTime = null;

    public DashboardDescription name(String name) {
        this.name = name;
        return this;
    }

    /**
     * the name of the dashboard
     *
     * @return name
     **/
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DashboardDescription panels(List<DashboardPanel> panels) {
        this.panels = panels;
        return this;
    }

    public DashboardDescription addPanelsItem(DashboardPanel panelsItem) {
        if (this.panels == null) {
            this.panels = new ArrayList<>();
        }
        this.panels.add(panelsItem);
        return this;
    }

    /**
     * the panels to be included in the dashboard
     *
     * @return panels
     **/
    public List<DashboardPanel> getPanels() {
        return panels;
    }

    public void setPanels(List<DashboardPanel> panels) {
        this.panels = panels;
    }

    public DashboardDescription users(List<String> users) {
        this.users = users;
        return this;
    }

    public DashboardDescription addUsersItem(String usersItem) {
        if (this.users == null) {
            this.users = new ArrayList<>();
        }
        this.users.add(usersItem);
        return this;
    }

    /**
     * the users which should be allowed to see this dashboard
     *
     * @return users
     **/
    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public DashboardDescription plottedTime(Integer plottedTime) {
        this.plottedTime = plottedTime;
        return this;
    }

    /**
     * the time interval to be shown on the graphs, in minutes
     *
     * @return plottedTime
     **/
    public Integer getPlottedTime() {
        return plottedTime;
    }

    public void setPlottedTime(Integer plottedTime) {
        this.plottedTime = plottedTime;
    }

    public DashboardDescription refreshTime(RefreshTimeEnum refreshTime) {
        this.refreshTime = refreshTime;
        return this;
    }

    /**
     * the time interval to wait before refreshing the graphs
     *
     * @return refreshTime
     **/
    public RefreshTimeEnum getRefreshTime() {
        return refreshTime;
    }

    public void setRefreshTime(RefreshTimeEnum refreshTime) {
        this.refreshTime = refreshTime;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DashboardDescription dashboardDescription = (DashboardDescription) o;
        return Objects.equals(this.name, dashboardDescription.name) &&
                Objects.equals(this.panels, dashboardDescription.panels) &&
                Objects.equals(this.users, dashboardDescription.users) &&
                Objects.equals(this.plottedTime, dashboardDescription.plottedTime) &&
                Objects.equals(this.refreshTime, dashboardDescription.refreshTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, panels, users, plottedTime, refreshTime);
    }

    @Override
    public String toString() {

        return "class DashboardDescription {\n" +
                "    name: " + toIndentedString(name) + "\n" +
                "    panels: " + toIndentedString(panels) + "\n" +
                "    users: " + toIndentedString(users) + "\n" +
                "    plottedTime: " + toIndentedString(plottedTime) + "\n" +
                "    refreshTime: " + toIndentedString(refreshTime) + "\n" +
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

