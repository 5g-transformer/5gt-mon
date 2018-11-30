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

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Created by json2java on 22/06/17.
 * json2java author: Marco Capitani (m.capitani AT nextworks DOT it)
 */

public class Alerting {

    private List<AlertManagers> alertmanagers;

    public Alerting() {

    }

    @JsonProperty("alertmanagers")
    public List<AlertManagers> getAlertmanagers() {
        return alertmanagers;
    }

    @JsonProperty("alertmanagers")
    private void setAlertmanagers(List<AlertManagers> alertmanagers) {
        this.alertmanagers = alertmanagers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Alerting)) return false;
        Alerting alerting = (Alerting) o;
        return Objects.equals(getAlertmanagers(), alerting.getAlertmanagers());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAlertmanagers());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Alerting.class.getSimpleName() + "[", "]")
                .add("alertmanagers=" + alertmanagers)
                .toString();
    }
}
