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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Created by json2java on 22/06/17.
 * json2java author: Marco Capitani (m.capitani AT nextworks DOT it)
 */

public class Receivers {

    public Receivers() {

    }

    public Receivers(String name) {
        this.name = name;
    }

    private String name;

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    public Receivers name(String name) {
        this.name = name;
        return this;
    }

    private List<WebhookConfigs> webhookConfigs = new ArrayList<>();

    @JsonProperty("webhook_configs")
    public List<WebhookConfigs> getWebhookConfigs() {
        return webhookConfigs;
    }

    @JsonProperty("webhook_configs")
    public void setWebhookConfigs(List<WebhookConfigs> webhookConfigs) {
        this.webhookConfigs = webhookConfigs;
    }

    public Receivers webhookConfigs(List<WebhookConfigs> webhookConfigs) {
        this.webhookConfigs = webhookConfigs;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Receivers)) return false;
        Receivers receivers = (Receivers) o;
        return Objects.equals(getName(), receivers.getName()) &&
                Objects.equals(getWebhookConfigs(), receivers.getWebhookConfigs());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getWebhookConfigs());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Receivers.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("webhookConfigs=" + webhookConfigs)
                .toString();
    }
}