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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Objects;

public class DashboardPanel {
    @JsonProperty("title")
    private String title = null;

    @JsonProperty("query")
    private String query = null;
    @JsonProperty("size")
    private SizeEnum size = null;

    public DashboardPanel title(String title) {
        this.title = title;
        return this;
    }

    /**
     * Get title
     *
     * @return title
     **/
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public DashboardPanel query(String query) {
        this.query = query;
        return this;
    }

    /**
     * the query whose value the graph in the panel should show.
     * <p>
     * See https://prometheus.io/docs/prometheus/latest/querying/basics/ for details
     *
     * @return query
     **/
    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public DashboardPanel size(SizeEnum size) {
        this.size = size;
        return this;
    }

    /**
     * the size of the panel
     *
     * @return size
     **/
    public SizeEnum getSize() {
        return size;
    }

    public void setSize(SizeEnum size) {
        this.size = size;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DashboardPanel dashboardPanel = (DashboardPanel) o;
        return Objects.equals(this.title, dashboardPanel.title) &&
                Objects.equals(this.query, dashboardPanel.query) &&
                Objects.equals(this.size, dashboardPanel.size);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, query, size);
    }

    @Override
    public String toString() {

        return "class DashboardPanel {\n" +
                "    title: " + toIndentedString(title) + "\n" +
                "    query: " + toIndentedString(query) + "\n" +
                "    size: " + toIndentedString(size) + "\n" +
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

    /**
     * the size of the panel
     */
    public enum SizeEnum {
        FULLSCREEN("fullscreen", 12, 4),

        FULLWIDE("fullwide", 12, 2),

        FULLTALL("fulltall", 6, 4),

        QUARTERSCREEN("quarterscreen", 6, 2),

        WIDE("wide", 6, 1),

        TALL("tall", 3, 2),

        MEDIUM("medium", 3, 1),

        SMALL("small", 2, 1);

        public int width;
        public int height;
        private String value;

        SizeEnum(String value, int width, int height) {
            this.value = value;
            this.width = width;
            this.height = height;
        }

        @JsonCreator
        public static SizeEnum fromValue(String text) {
            for (SizeEnum b : SizeEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }

        @JsonValue
        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }

}

