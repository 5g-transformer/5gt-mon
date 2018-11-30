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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class QueryResponse {
    @JsonProperty("query")
    private String query = null;

    @JsonProperty("data")
    private List<QueryResponseData> data = null;

    public QueryResponse query(String query) {
        this.query = query;
        return this;
    }

    /**
     * The query which generated the data.
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

    public QueryResponse data(List<QueryResponseData> data) {
        this.data = data;
        return this;
    }

    public QueryResponse addDataItem(QueryResponseData dataItem) {
        if (this.data == null) {
            this.data = new ArrayList<>();
        }
        this.data.add(dataItem);
        return this;
    }

    /**
     * Get data
     *
     * @return data
     **/
    public List<QueryResponseData> getData() {
        return data;
    }

    public void setData(List<QueryResponseData> data) {
        this.data = data;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        QueryResponse queryResponse = (QueryResponse) o;
        return Objects.equals(this.query, queryResponse.query) &&
                Objects.equals(this.data, queryResponse.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(query, data);
    }


    @Override
    public String toString() {

        return "class QueryResponse {\n" +
                "    query: " + toIndentedString(query) + "\n" +
                "    data: " + toIndentedString(data) + "\n" +
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

