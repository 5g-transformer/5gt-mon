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

package it.nextworks.nfvmano.configmanager.sb.prometheus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import it.nextworks.nfvmano.configmanager.sb.prometheus.model.AlertRules;
import it.nextworks.nfvmano.configmanager.sb.prometheus.model.PrometheusConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * Created by Marco Capitani on 22/10/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
public class PrometheusConnector {

    private static final Logger log = LoggerFactory.getLogger(PrometheusConnector.class);

    private File promConfig;

    private File alertRules;

    private ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    private Runtime runtime = Runtime.getRuntime();

    private String promPid;

    private String getPrometheusPid() {
        try {
            Process ps = runtime.exec(new String[]{"pgrep", "prometheus"});
            try {
                InputStream stdOut = ps.getInputStream();
                boolean done = ps.waitFor(1000, TimeUnit.MILLISECONDS);
                if (!done) {
                    throw new IllegalStateException(
                            "Error fetching prometheus PID: process timed out"
                    );
                }
                Scanner s = new Scanner(stdOut);
                int pid = -1;
                while (s.hasNext()) {
                    if (s.hasNextInt()) {
                        pid = s.nextInt();
                        if (s.hasNext()) {
                            // more than one pid
                            throw new IllegalStateException("Too many prometheus processes");
                        }
                    } else {
                        throw new IllegalStateException("Could not retrieve prometheus PID, unexpected output");
                    }
                }
                if (pid != -1) {
                    return String.valueOf(pid);
                } else throw new IllegalStateException("Could not find prometheus process");
            } catch (InterruptedException exc) {
                throw new IllegalStateException("Interrupted");
            }
        } catch (IOException exc) {
            throw new IllegalStateException(
                    String.format("Error fetching prometheus PID: %s", exc.getMessage()),
                    exc
            );
        }
    }

    public PrometheusConnector(String promConfigPath, String alertRulesPath) {
        promConfig = new File(promConfigPath);
        if (!promConfig.isFile()) {
            throw new IllegalArgumentException("Illegal prometheus config file path");
        }
        alertRules = new File(alertRulesPath);
        if (!alertRules.isFile()) {
            throw new IllegalArgumentException("Illegal alert rules path");
        }
        promPid = getPrometheusPid();
        log.debug("Prometheus PID is {}.", promPid);
    }

    public PrometheusConfig getConfig() {
        try {
            return mapper.readValue(Files.readAllBytes(promConfig.toPath()), PrometheusConfig.class);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read Prometheus config file", e);
        }
    }

    public void setConfig(PrometheusConfig newConfig) {
        try {
            // Substitute the scrape_configs field with the new config
            boolean inScrapeConfigs = false;
            Iterator<String> iterator = Files.readAllLines(promConfig.toPath()).iterator();
            List<String> updated = new LinkedList<>();
            while (iterator.hasNext()) {
                String cur = iterator.next();
                if (!inScrapeConfigs) {
                    if (cur.startsWith("scrape_configs:")) {
                        // Start removing (but keep this line)
                        updated.add(cur);
                        inScrapeConfigs = true;
                    } else {
                        // Keep it unchanged
                        updated.add(cur);
                    }
                } else { // In scrape_configs
                    if (!( cur.startsWith(" ") || cur.startsWith("-") || cur.startsWith("#") )) {
                        // We reached the end of the scrape_configs field
                        inScrapeConfigs = false;
                        // Append the new scrape_configs
                        String addition = mapper.writeValueAsString(newConfig.getScrapeConfigs());
                        String actualUsed = addition.substring(4); // skip the ---\n preface
                        updated.addAll(Arrays.asList(actualUsed.split("\n")));
                        // Then keep writing the rest of the file
                        updated.add(cur);
                    }
                    // else do nothing, i.e. remove the line
                }
            }
            Files.write(promConfig.toPath(), updated);
            runtime.exec(new String[] {"kill", "-SIGHUP", promPid});
        } catch (JsonProcessingException e) {
            log.debug("Illegal configuration provided: {}", e.getMessage());
            throw new IllegalArgumentException("Provided configuration is illegal", e);
        } catch (IOException e) {
            throw new IllegalStateException("Could not write Prometheus config file", e);
        }
    }

    public AlertRules getRules() {
        try {
            return mapper.readValue(Files.readAllBytes(alertRules.toPath()), AlertRules.class);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read rules file", e);
        }
    }

    public void setRules(AlertRules newRules) {
        try {
            Files.write(alertRules.toPath(), mapper.writeValueAsBytes(newRules));
        } catch (JsonProcessingException e) {
            log.debug("Illegal rules provided: {}", e.getMessage());
            throw new IllegalArgumentException("Provided rules are illegal", e);
        } catch (IOException e) {
            throw new IllegalStateException("Could not write rules file", e);
        }
    }
}
