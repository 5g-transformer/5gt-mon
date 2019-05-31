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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Created by json2java on 22/06/17.
 * json2java author: Marco Capitani (m.capitani AT nextworks DOT it)
 */

public class StaticConfigs {

    private List<String> targets;

    private Map<String, String> labels = new HashMap<>();


    public StaticConfigs() {

    }

    public StaticConfigs(List<String> targets, Map<String, String> labels) {
        this.targets = targets;
        this.labels = labels;
    }

    @JsonProperty("targets")
    public List<String> getTargets() {
        return targets;
    }

    @JsonProperty("targets")
    private void setTargets(List<String> targets) {
        this.targets = targets;
    }

    @JsonProperty("labels")
    public Map<String, String> getLabels() {
        return labels;
    }

    @JsonProperty("labels")
    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StaticConfigs)) return false;
        StaticConfigs that = (StaticConfigs) o;
        return Objects.equals(getTargets(), that.getTargets());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTargets());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", StaticConfigs.class.getSimpleName() + "[", "]")
                .add("targets=" + targets.toString())
                .toString();
    }
}
