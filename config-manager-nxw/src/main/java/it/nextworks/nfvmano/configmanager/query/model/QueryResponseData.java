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

package it.nextworks.nfvmano.configmanager.query.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;
import java.util.Objects;

public class QueryResponseData {
    @JsonProperty("legend")
    private String legend = null;

    @JsonProperty("timestamp")
    private OffsetDateTime timestamp = null;

    @JsonProperty("value")
    private Double value = null;

    public QueryResponseData legend(String legend) {
        this.legend = legend;
        return this;
    }

    /**
     * Get legend
     *
     * @return legend
     **/
    public String getLegend() {
        return legend;
    }

    public void setLegend(String legend) {
        this.legend = legend;
    }

    public QueryResponseData timestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    /**
     * Get timestamp
     *
     * @return timestamp
     **/
    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public QueryResponseData value(Double value) {
        this.value = value;
        return this;
    }

    /**
     * Get value
     *
     * @return value
     **/
    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        QueryResponseData queryResponseData = (QueryResponseData) o;
        return Objects.equals(this.legend, queryResponseData.legend) &&
                Objects.equals(this.timestamp, queryResponseData.timestamp) &&
                Objects.equals(this.value, queryResponseData.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(legend, timestamp, value);
    }


    @Override
    public String toString() {

        return "class QueryResponseData {\n" +
                "    legend: " + toIndentedString(legend) + "\n" +
                "    timestamp: " + toIndentedString(timestamp) + "\n" +
                "    value: " + toIndentedString(value) + "\n" +
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

