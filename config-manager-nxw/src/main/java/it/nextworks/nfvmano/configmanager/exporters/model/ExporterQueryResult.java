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

package it.nextworks.nfvmano.configmanager.exporters.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class ExporterQueryResult {
    @JsonProperty("exporter")
    private List<Exporter> exporter;

    public ExporterQueryResult(Exporter... exporters) {
        this.exporter = Arrays.asList(exporters);
    }

    public ExporterQueryResult(Collection<Exporter> exporters) {
        this.exporter = new ArrayList<>(exporters);
    }

    public ExporterQueryResult exporter(List<Exporter> exporter) {
        this.exporter = exporter;
        return this;
    }

    public ExporterQueryResult addExporterItem(Exporter exporterItem) {
        if (this.exporter == null) {
            this.exporter = new ArrayList<>();
        }
        this.exporter.add(exporterItem);
        return this;
    }

    /**
     * The list of exporters matching the query
     *
     * @return exporter
     **/
    public List<Exporter> getExporter() {
        return exporter;
    }

    public void setExporter(List<Exporter> exporter) {
        this.exporter = exporter;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ExporterQueryResult exporterQueryResult = (ExporterQueryResult) o;
        return Objects.equals(this.exporter, exporterQueryResult.exporter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(exporter);
    }


    @Override
    public String toString() {

        return "class ExporterQueryResult {\n" +
                "    exporter: " + toIndentedString(exporter) + "\n" +
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

