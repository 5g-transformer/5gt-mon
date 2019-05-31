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

package it.nextworks.nfvmano.configmanager.sb.prometheus.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Created by json2java on 22/06/17.
 * json2java author: Marco Capitani (m.capitani AT nextworks DOT it)
 */

public class PromGlobal {

    private String evaluationInterval;
    private String scrapeInterval;
    private Map<String, String> externalLabels;

    public PromGlobal() {

    }

    @JsonProperty("evaluation_interval")
    public String getEvaluationInterval() {
        return evaluationInterval;
    }

    @JsonProperty("evaluation_interval")
    private void setEvaluationInterval(String evaluationInterval) {
        this.evaluationInterval = evaluationInterval;
    }

    @JsonProperty("scrape_interval")
    public String getScrapeInterval() {
        return scrapeInterval;
    }

    @JsonProperty("scrape_interval")
    private void setScrapeInterval(String scrapeInterval) {
        this.scrapeInterval = scrapeInterval;
    }

    @JsonProperty("external_labels")
    public Map<String, String> getExternalLabels() {
        return externalLabels;
    }

    @JsonProperty("external_labels")
    public void setExternalLabels(Map<String, String> externalLabels) {
        this.externalLabels = externalLabels;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PromGlobal)) return false;
        PromGlobal global = (PromGlobal) o;
        return Objects.equals(getEvaluationInterval(), global.getEvaluationInterval()) &&
                Objects.equals(getScrapeInterval(), global.getScrapeInterval());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEvaluationInterval(), getScrapeInterval());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PromGlobal.class.getSimpleName() + "[", "]")
                .add("evaluationInterval='" + evaluationInterval + "'")
                .add("scrapeInterval='" + scrapeInterval + "'")
                .toString();
    }
}
