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

import io.vertx.core.Future;
import it.nextworks.nfvmano.configmanager.alerts.AlertRepo;
import it.nextworks.nfvmano.configmanager.alerts.model.Alert;
import it.nextworks.nfvmano.configmanager.common.KVP;
import it.nextworks.nfvmano.configmanager.sb.prometheus.model.AlertManagerConfig;
import it.nextworks.nfvmano.configmanager.sb.prometheus.model.AlertRules;
import it.nextworks.nfvmano.configmanager.sb.prometheus.model.Groups;
import it.nextworks.nfvmano.configmanager.sb.prometheus.model.Receivers;
import it.nextworks.nfvmano.configmanager.sb.prometheus.model.Route;
import it.nextworks.nfvmano.configmanager.sb.prometheus.model.Routes;
import it.nextworks.nfvmano.configmanager.sb.prometheus.model.Rules;
import it.nextworks.nfvmano.configmanager.sb.prometheus.model.WebhookConfigs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Marco Capitani on 08/04/19.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
public class AlertService implements AlertRepo {

    private static final Logger log = LoggerFactory.getLogger(AlertService.class);

    static String GROUP_NAME = "X-ConfigManager";

    private PrometheusConnector connector;

    private AlertRepo db;

    private TargetRepo targetDb;

    public AlertService(
            PrometheusConnector connector,
            AlertRepo db,
            TargetRepo targetDb
    ) {
        this.connector = connector;
        this.db = db;
        this.targetDb = targetDb;
    }

    static Rules convert(Alert alert) {
        Map<String, String> labels = new HashMap<>();
        labels.put("alertName", alert.getAlertName());
        labels.put("severity", alert.getSeverity());
        Map<String, String> annotations = new HashMap<>();
        String expression = String.format(
                "%s %s %s",
                alert.getQuery(),
                alert.getKind().operator(),
                alert.getValue()
        );
        for (KVP kvp : alert.getLabels()) {
            annotations.put(kvp.getKey(), kvp.getValue());
        }
        return new Rules(
                annotations,
                expression,
                alert.getAlertId(),
                labels,
                alert.getForTime()
        );
    }

    private void extendGroup(Groups group, Rules rule) {
        List<Rules> temp = group.getRules();
        temp.add(rule);
        group.setRules(temp);
    }

    private Groups getCMGroup(List<Groups> groups) {
        Iterator<Groups> iterator = groups.iterator();
        Groups cmGroup = null;
        while (iterator.hasNext()) {
            Groups g = iterator.next();
            if (g.getName().equals(GROUP_NAME)) {
                cmGroup = g;
                iterator.remove();
                break;
            }
        }
        if (cmGroup == null) {
            cmGroup = new Groups();
            cmGroup.setName(GROUP_NAME);
        }
        return cmGroup;
    }

    private void addReceiver(List<Receivers> receivers, String id, URI target) {
        boolean found = receivers.stream().anyMatch(r -> r.getName().equals(id));
        if (!found) {
            WebhookConfigs whConfig = new WebhookConfigs()
                    .sendResolved(true)
                    .url(target.toString());
            Receivers rec = new Receivers(id)
                    .webhookConfigs(Collections.singletonList(whConfig));
            receivers.add(rec);
        }
    }

    private void addRoute(List<Routes> routes, String alertId, String recId) {
        Map<String, String> matchMap = new HashMap<>();
        matchMap.put("alertname", alertId);
        Routes route = new Routes().match(matchMap).receiver(recId);
        routes.add(route);
    }

    /**
     * Removes the route routing the alert "alertId"
     * @param routes the list of all routes
     * @param alertId the id of the alert which should be de-routed
     * @return true iff something has been removed.
     */
    private boolean removeRoute(List<Routes> routes, String alertId) {
        return routes.removeIf(r -> {
            if (r.getMatch() != null) {
                return r.getMatch().get("alertname").equals(alertId);
            } else {
                return false;
            }
        });
    }

    private Future<Void> makeTarget(Alert alert) {
        URI target = alert.getTarget();
        UUID uuid = targetDb.saveOrGet(target);
        AlertManagerConfig amConfig = connector.getAMConfig();

        List<Receivers> receivers = amConfig.getReceivers();
        addReceiver(receivers, uuid.toString(), target);
        amConfig.setReceivers(receivers);

        List<Routes> routes = amConfig.getRoute().getRoutes();
        addRoute(routes, alert.getAlertId(), uuid.toString());
        amConfig.getRoute().setRoutes(routes);

        return connector.setAMConfig(amConfig);
    }

    private Future<Void> loadAlertInPrometheus(Alert alert) {
        AlertRules rules = connector.getRules();
        List<Groups> groups = rules.getGroups();
        Groups cmGroup = getCMGroup(groups);
        Rules converted = convert(alert);
        extendGroup(cmGroup, converted);
        Future<Void> targetFuture = makeTarget(alert);
        groups.add(cmGroup);
        rules.setGroups(groups);
        return targetFuture.compose(n -> connector.setRules(rules));
    }

    @Override
    public Future<Alert> save(Alert alert) {
        if (alert.getAlertId() != null) {
            log.warn("AlertId {} specified in request. Will be ignored", alert.getAlertId());
        }
        alert.setAlertId(UUID.randomUUID().toString());
        Future<Void> voidFuture = loadAlertInPrometheus(alert);
        return voidFuture.compose(n -> db.save(alert));
    }

    @Override
    public Future<Alert> update(Alert alert) {
        Future<Void> voidFuture = loadAlertInPrometheus(alert);
        return voidFuture.compose(n -> db.update(alert));
    }

    @Override
    public Future<Optional<Alert>> findById(String alertId) {
        return db.findById(alertId);
    }

    @Override
    public Future<Set<String>> deleteById(String alertId) {
        return deleteById(alertId, false);
    }

    @Override
    public Future<Set<String>> deleteById(String alertId, boolean strict) {
        Set<String> output = new HashSet<>();
        AlertManagerConfig amConfig = connector.getAMConfig();
        Route route = amConfig.getRoute();
        List<Routes> routes = route.getRoutes();
        boolean deleted = removeRoute(routes, alertId);
        if (!deleted && strict) {
            throw new IllegalArgumentException("No such alert");
        }
        if (!deleted) { // Nothing to do, return empty set
            return Future.succeededFuture(output);
        }
        // Go on with the work
        route.setRoutes(routes);
        amConfig.setRoute(route);
        Future<Void> amFuture = connector.setAMConfig(amConfig);

        // Do the alert Rules
        // (remember, we already know we are actually deleting)
        AlertRules rules = connector.getRules();
        List<Groups> groups = rules.getGroups();
        Groups cmGroup = getCMGroup(groups);
        List<Rules> alertRules = cmGroup.getRules();
        Iterator<Rules> iterator = alertRules.iterator();
        while (iterator.hasNext()) {
            Rules rule = iterator.next();
            if (rule.getAlert().equals(alertId)) {
                iterator.remove();
                break;
            }
        }
        cmGroup.setRules(alertRules);
        groups.add(cmGroup);
        rules.setGroups(groups);
        Future<Void> rulesFuture = amFuture.compose(n -> connector.setRules(rules));
        return rulesFuture.compose(n -> db.deleteById(alertId, strict));
    }

    @Override
    public Future<Set<Alert>> findAll() {
        return db.findAll();
    }
}
