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
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import it.nextworks.nfvmano.configmanager.sb.prometheus.model.AlertManagerConfig;
import it.nextworks.nfvmano.configmanager.sb.prometheus.model.AlertRules;
import it.nextworks.nfvmano.configmanager.sb.prometheus.model.PrometheusConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
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

    private File alertManager;

    private ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    private Runtime runtime = Runtime.getRuntime();

    private String promPid;

    private String promServiceName;

    private WebClient client;

    private String promHost;
    private int promPort;

    private String amHost;
    private int amPort;


    public PrometheusConnector(
            String promConfigPath,
            String alertRulesPath,
            String alertManagerPath,
            String promHost,
            int promPort,
            String amHost,
            int amPort,
            WebClient client
    ) {
        this.promHost = promHost;
        this.promPort = promPort;
        this.amHost = amHost;
        this.amPort = amPort;
        this.client = client;

        log.info("Looking for prometheus config at {}", promConfigPath);
        promConfig = new File(promConfigPath);
        if (!promConfig.isFile()) {
            throw new IllegalArgumentException(String.format(
                    "Illegal prometheus config file path '%s'",
                    promConfigPath
            ));
        }
        log.info("Prometheus config found");

        log.info("Looking for alert rules at {}", promConfigPath);
        alertRules = new File(alertRulesPath);
        if (!alertRules.isFile()) {
            throw new IllegalArgumentException(String.format(
                    "Illegal alert rules path '%s'",
                    alertRulesPath
            ));
        }
        log.info("Alert rules found");

        log.info("Looking for alertmanager config at {}", alertManagerPath);
        alertManager = new File(alertManagerPath);
        if (!alertManager.isFile()) {
            throw new IllegalArgumentException(String.format(
                    "Illegal alert rules path '%s'",
                    alertManagerPath
            ));
        }
        log.info("Alertmanager config found");

        log.info("Trying to determine Prometheus service name or PID");
        String promServiceName = getPromServiceName();
        if (promServiceName != null) { // Actually found a service
            this.promServiceName = promServiceName;
            log.debug("Prometheus service name is {}", promPid);
        } else {
            log.warn("Could not find Prometheus service");
            try {
                promPid = getPrometheusPid();
                log.debug("Prometheus PID is {}", promPid);
            } catch (IllegalStateException exc) {
                log.warn("Could not find Prometheus PID");
                log.warn(exc.getMessage());
                promPid = null;
            }
        }

        if (log.isTraceEnabled()) {
            // Log everything once
            getConfig();
            getRules();
            getAMConfig();
        }
    }

    private Future<Void> reloadAMConfig() {
        Future<Void> voidFuture = callReloadEndpoint(amHost, amPort);
        return voidFuture.recover(e -> {
            log.error(
                    "Could not reload AM configuration; {}: {}",
                    e.getClass().getSimpleName(),
                    e.getMessage()
            );
            return Future.failedFuture(e);
        });
    }

    private Future<Void> callReloadEndpoint(String host, int port) {
        log.debug("Reloading endpoint {}:{}", host, port);
        HttpRequest<Buffer> post =
                client.post("/-/reload")
                        .host(host)
                        .port(port);
        Future<HttpResponse<Buffer>> future = Future.future();
        post.send(future);

        // Add error logging
        Future<HttpResponse<Buffer>> loggingfuture = future.recover(error ->
                Future.failedFuture(new IllegalStateException(String.format(
                        "Could not reach endpoint %s:%s; %s",
                        host,
                        port,
                        error.getMessage()
                ),
                        error
                ))
        );

        // Handle response
        return loggingfuture.map(resp -> {
            if (resp.statusCode() >= 300 || resp.statusCode() < 200) {
                throw new IllegalStateException(String.format(
                        "Endpoint %s:%s HTTP error; %s: %s",
                        host,
                        port,
                        resp.statusCode(),
                        resp.statusMessage()
                ));
            }
            // Success: complete the future with no output
            log.debug("Endpoint {}:{} reloaded  ", host, port);
            return null;
        });
    }

    private Future<Void> reloadPromViaSighup() {
        Future<Void> future = Future.future();
        Thread t = new Thread(() -> {
            try {
                Process ps;
                if (promServiceName != null) {
                    ps = runtime.exec(new String[]{"sudo", "systemctl", "kill", "-s", "SIGHUP", promServiceName});
                } else if (promPid != null) {
                    ps = runtime.exec(new String[]{"kill", "-SIGHUP", promPid});
                } else {
                    future.fail(new IllegalStateException("No prometheus reloading method found."));
                    return;
                }
                try {
                    boolean done = ps.waitFor(1000, TimeUnit.MILLISECONDS);
                    if (!done) {
                        future.fail(new IllegalStateException(
                                "Error reloading prometheus config: process timed out"
                        ));
                    } else if (ps.exitValue() != 0) {
                        future.fail(new IllegalStateException(String.format(
                                "Prometheus reloading failed with exit code %d",
                                ps.exitValue()
                        )));
                    } else {  // All good
                        future.complete();
                    }
                } catch (InterruptedException e) {
                    future.fail(new IllegalStateException("Interrupted"));
                }
            } catch (IOException e) {
                future.fail(new IllegalStateException(String.format(
                        "Could not reload prometheus configuration: %s",
                        e.getMessage()
                )));
            }
        });
        t.start();
        return future.setHandler((nl) -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                log.warn("Interrupted while waiting for thread. Aborting.");
            }
        });
    }

    private Future<Void> reloadPrometheusConfig() {
        Future<Void> future = callReloadEndpoint(promHost, promPort);
        future.recover(e -> {
            log.warn("Could not reload Prom Conf via HTTP.");
            log.debug("Error: ", e);
            if (promServiceName != null || promPid != null) {
                return reloadPromViaSighup();
            } else {
                return Future.failedFuture(e);
            }
        });
        future.recover(e -> {
            log.error("Could not reload Prom Conf via kill.");
            log.debug("Error: ", e);
            // The fallback did not work
            return Future.failedFuture(e);
        });
        return future;
    }

    private String getPromServiceName() {
        try {
            for (String promName: new String[]{"prometheus", "prometheus-core", "prometheus-server"}) {
                Process ps = runtime.exec(new String[]{"systemctl", "is-active", "--quiet", promName});
                try {
                    boolean done = ps.waitFor(1000, TimeUnit.MILLISECONDS);
                    if (!done) {
                        log.warn("Error fetching prometheus service name: process timed out");
                        return null;
                    }
                    if (ps.exitValue() == 0) {
                        return promName;
                    }
                    // Else return null
                } catch (InterruptedException exc) {
                    log.warn("Error fetching prometheus service name: Interrupted");
                }
            }
        } catch (Exception exc) {
            log.warn("Error fetching prometheus service name: {}", exc.getMessage());
            log.debug("Details: ", exc);
        }
        return null;
    }

    private String getPrometheusPid() {
        try {
            Process ps = runtime.exec(new String[]{"pgrep", "prometheus"});
            try {
                boolean done = ps.waitFor(1000, TimeUnit.MILLISECONDS);
                if (!done) {
                    throw new IllegalStateException(
                            "Error fetching prometheus PID: process timed out"
                    );
                }
                InputStream stdOut = ps.getInputStream();
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

    public PrometheusConfig getConfig() {
        try {
            byte[] readPromConfig = Files.readAllBytes(promConfig.toPath());
            if (log.isTraceEnabled()) {
                log.trace("Reading Prometheus config:\n");
                log.trace(new String(readPromConfig));
            }
            return mapper.readValue(Files.readAllBytes(promConfig.toPath()), PrometheusConfig.class);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read Prometheus config file", e);
        }
    }

    public Future<Void> setConfig(PrometheusConfig newConfig) {
        try {
            byte[] writtenConfig = mapper.writeValueAsBytes(newConfig);
            if (log.isTraceEnabled()) {
                log.trace("Writing Prometheus config:\n");
                log.trace(new String(writtenConfig));
            }
            Files.write(promConfig.toPath(), writtenConfig);
            return reloadPrometheusConfig();
        } catch (JsonProcessingException e) {
            log.debug("Illegal configuration provided: {}", e.getMessage());
            throw new IllegalArgumentException("Provided Prometheus configuration is illegal", e);
        } catch (IOException e) {
            throw new IllegalStateException("Could not write Prometheus config file", e);
        }
    }

    public AlertRules getRules() {
        try {
            byte[] readRules = Files.readAllBytes(alertRules.toPath());
            if (log.isTraceEnabled()) {
                log.trace("Reading Rules config:\n");
                log.trace(new String(readRules));
            }
            return mapper.readValue(readRules, AlertRules.class);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read rules file", e);
        }
    }

    public Future<Void> setRules(AlertRules newRules) {
        try {
            byte[] writtenRules = mapper.writeValueAsBytes(newRules);
            if (log.isTraceEnabled()) {
                log.trace("Writing Rules config:\n");
                log.trace(new String(writtenRules));
            }
            Files.write(alertRules.toPath(), writtenRules);
            return reloadPrometheusConfig();
        } catch (JsonProcessingException e) {
            log.debug("Illegal rules provided: {}", e.getMessage());
            throw new IllegalArgumentException("Provided rules are illegal", e);
        } catch (IOException e) {
            throw new IllegalStateException("Could not write rules file", e);
        }
    }

    public AlertManagerConfig getAMConfig() {
        try {
            byte[] reatConfig = Files.readAllBytes(alertManager.toPath());
            if (log.isTraceEnabled()) {
                log.trace("Reading Alertmanager config:\n");
                log.trace(new String(reatConfig));
            }
            return mapper.readValue(reatConfig, AlertManagerConfig.class);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read Alertmanager file", e);
        }
    }

    public Future<Void> setAMConfig(AlertManagerConfig newConfig) {
        try {
            byte[] writtenConfig = mapper.writeValueAsBytes(newConfig);
            if (log.isTraceEnabled()) {
                log.trace("Writing Alertmanager config:\n");
                log.trace(new String(writtenConfig));
            }
            Files.write(alertManager.toPath(), writtenConfig);
            return reloadAMConfig();
        } catch (JsonProcessingException e) {
            log.debug("Illegal Alertmanager config provided: {}", e.getMessage());
            throw new IllegalArgumentException("Provided Alertmanager is illegal", e);
        } catch (IOException e) {
            throw new IllegalStateException("Could not write Alertmanager file", e);
        }
    }
}
