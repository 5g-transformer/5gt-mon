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

import java.util.Objects;
import java.util.StringJoiner;

/**
 * Created by json2java on 22/06/17.
 * json2java author: Marco Capitani (m.capitani AT nextworks DOT it)
 */

public class WebhookConfigs {

    public WebhookConfigs() {

    }

    private boolean sendResolved;

    @JsonProperty("send_resolved")
    public boolean isSendResolved() {
        return sendResolved;
    }

    @JsonProperty("send_resolved")
    public void setSendResolved(boolean sendResolved) {
        this.sendResolved = sendResolved;
    }

    public WebhookConfigs sendResolved(boolean sendResolved) {
        this.sendResolved = sendResolved;
        return this;
    }

    private String url;

    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

    public WebhookConfigs url(String url) {
        this.url = url;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WebhookConfigs)) return false;
        WebhookConfigs that = (WebhookConfigs) o;
        return isSendResolved() == that.isSendResolved() &&
                Objects.equals(getUrl(), that.getUrl());
    }

    @Override
    public int hashCode() {
        return Objects.hash(isSendResolved(), getUrl());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", WebhookConfigs.class.getSimpleName() + "[", "]")
                .add("sendResolved=" + sendResolved)
                .add("url='" + url + "'")
                .toString();
    }
}