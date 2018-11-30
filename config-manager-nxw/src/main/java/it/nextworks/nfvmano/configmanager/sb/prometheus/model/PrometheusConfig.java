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

    private List<ScrapeConfigs> scrapeConfigs;
    private Global global;
    private Alerting alerting;
    private List<String> ruleFiles;

    public PrometheusConfig() {
        this.scrapeConfigs = new ArrayList<>();
        this.ruleFiles = new ArrayList<>();
    }

    public PrometheusConfig(List<ScrapeConfigs> scrapeConfigs, Global global, Alerting alerting, List<String> ruleFiles) {
        this.scrapeConfigs = scrapeConfigs;
        this.global = global;
        this.alerting = alerting;
        this.ruleFiles = ruleFiles;
    }

    @JsonProperty("scrape_configs")
    public List<ScrapeConfigs> getScrapeConfigs() {
        return scrapeConfigs;
    }

    @JsonProperty("scrape_configs")
    private void setScrapeConfigs(List<ScrapeConfigs> scrapeConfigs) {
        this.scrapeConfigs = scrapeConfigs;
    }

    public void addScrapeConfig(ScrapeConfigs scrapeConfig) {
        this.scrapeConfigs.add(scrapeConfig);
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
        this.scrapeConfigs.add(scrapeConfig);
    }

    @JsonProperty("global")
    public Global getGlobal() {
        return global;
    }

    @JsonProperty("global")
    private void setGlobal(Global global) {
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