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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

/**
 * Created by json2java on 22/06/17.
 * json2java author: Marco Capitani (m.capitani AT nextworks DOT it)
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class PrometheusConfig {

    private List<ScrapeConfigs> scrapeConfigs = new ArrayList<>();
    private PromGlobal global;
    private Alerting alerting;
    private List<String> ruleFiles = new ArrayList<>();

    public PrometheusConfig() {

    }

    public PrometheusConfig(
            List<ScrapeConfigs> scrapeConfigs,
            PromGlobal global,
            Alerting alerting,
            List<String> ruleFiles
    ) {
        if (scrapeConfigs == null) {
            scrapeConfigs = new ArrayList<>();
        }
        this.scrapeConfigs = scrapeConfigs;
        this.global = global;
        this.alerting = alerting;
        if (ruleFiles == null) {
            ruleFiles = new ArrayList<>();
        }
        this.ruleFiles = ruleFiles;
    }

    @JsonProperty("scrape_configs")
    public List<ScrapeConfigs> getScrapeConfigs() {
        return scrapeConfigs;
    }

    @JsonProperty("scrape_configs")
    public void setScrapeConfigs(List<ScrapeConfigs> scrapeConfigs) {
        if (null == scrapeConfigs) {
            scrapeConfigs = new ArrayList<>();
        }
        this.scrapeConfigs = scrapeConfigs;
    }

    public void addScrapeConfig(ScrapeConfigs scrapeConfig) {
        if (scrapeConfigs == null) {
            scrapeConfigs = new ArrayList<>();
        }
        scrapeConfigs.add(scrapeConfig);
    }

    public void removeScrapeConfig(ScrapeConfigs scrapeConfig) {
        this.scrapeConfigs.remove(scrapeConfig);
    }

    public void removeScrapeConfig(String jobName) {
        Optional<ScrapeConfigs> toBeRemoved = this.scrapeConfigs.stream()
                .filter(x -> x.getJobName().equals(jobName))
                .findAny();
        if (!toBeRemoved.isPresent()) {
            throw new IllegalArgumentException(String.format("No scrape config %s", jobName));
        }
        removeScrapeConfig(toBeRemoved.get());
    }

    public void addScrapeConfig(String jobName, String scrapeInterval, StaticConfigs... staticConfigs) {
        ScrapeConfigs scrapeConfig = new ScrapeConfigs(jobName, scrapeInterval, staticConfigs);
        addScrapeConfig(scrapeConfig);
    }

    @JsonProperty("global")
    public PromGlobal getGlobal() {
        return global;
    }

    @JsonProperty("global")
    private void setGlobal(PromGlobal global) {
        this.global = global;
    }

    @JsonProperty("alerting")
    public Alerting getAlerting() {
        return alerting;
    }

    @JsonProperty("alerting")
    private void setAlerting(Alerting alerting) {
        this.alerting = alerting;
    }

    @JsonProperty("rule_files")
    public List<String> getRuleFiles() {
        return ruleFiles;
    }

    @JsonProperty("rule_files")
    private void setRuleFiles(List<String> ruleFiles) {
        this.ruleFiles = ruleFiles;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PrometheusConfig.class.getSimpleName() + "[", "]")
                .add("scrapeConfigs=" + scrapeConfigs)
                .add("global=" + global)
                .add("alerting=" + alerting)
                .add("ruleFiles=" + ruleFiles)
                .toString();
    }
}
