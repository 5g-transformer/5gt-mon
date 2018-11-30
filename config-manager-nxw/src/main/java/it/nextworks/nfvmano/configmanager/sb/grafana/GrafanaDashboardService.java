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

package it.nextworks.nfvmano.configmanager.sb.grafana;

import io.vertx.core.Future;
import it.nextworks.nfvmano.configmanager.dashboards.DashboardRepo;
import it.nextworks.nfvmano.configmanager.dashboards.model.Dashboard;
import it.nextworks.nfvmano.configmanager.dashboards.model.DashboardDescription;
import it.nextworks.nfvmano.configmanager.dashboards.model.DashboardPanel;
import it.nextworks.nfvmano.configmanager.sb.grafana.model.GrafanaDashboard;
import it.nextworks.nfvmano.configmanager.sb.grafana.model.GrafanaDashboardWrapper;
import it.nextworks.nfvmano.configmanager.sb.grafana.model.Meta;
import it.nextworks.nfvmano.configmanager.sb.grafana.model.Panel;
import it.nextworks.nfvmano.configmanager.sb.grafana.model.PostDashboardResponse;
import it.nextworks.nfvmano.configmanager.sb.grafana.model.Row;
import it.nextworks.nfvmano.configmanager.sb.grafana.model.Target;
import it.nextworks.nfvmano.configmanager.sb.grafana.model.Xaxis;
import it.nextworks.nfvmano.configmanager.sb.grafana.model.Yaxes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Marco Capitani on 25/10/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
public class GrafanaDashboardService implements DashboardRepo {

    private GrafanaConnector connector;

    private DashboardRepo repo;

    public GrafanaDashboardService(GrafanaConnector connector, DashboardRepo repo) {
        this.connector = connector;
        this.repo = repo;
    }

    private GrafanaDashboardWrapper translate(DashboardDescription description) {
        return translate(new Dashboard(description));
    }

    void ensureSize(List<DashboardPanel> panels) {
        assert panels.size() > 0;
        Set<DashboardPanel.SizeEnum> sizes = panels.stream().map(DashboardPanel::getSize).collect(Collectors.toSet());
        if (sizes.size() == 1) {
            DashboardPanel.SizeEnum size = sizes.iterator().next();
            if (size == null) {
                // Only null sizes, we do dynamic page building
                int smaller = 0;
                DashboardPanel.SizeEnum lowerSize = null;
                int count = panels.size();
                if (count >= 24) {
                    size = DashboardPanel.SizeEnum.SMALL;
                    // No smaller panels: we let the page spill
                } else if (count >= 16) {
                    size = DashboardPanel.SizeEnum.MEDIUM;
                    lowerSize = DashboardPanel.SizeEnum.SMALL;
                    smaller = (count - 16) * 3;
                } else if (count >= 8) {
                    size = DashboardPanel.SizeEnum.WIDE;
                    lowerSize = DashboardPanel.SizeEnum.MEDIUM;
                    smaller = (count - 8) * 2;
                } else if (count >= 4) {
                    size = DashboardPanel.SizeEnum.QUARTERSCREEN;
                    lowerSize = DashboardPanel.SizeEnum.WIDE;
                    smaller = (count - 4) * 2;
                } else if (count >= 2) {
                    size = DashboardPanel.SizeEnum.FULLWIDE;
                    lowerSize = DashboardPanel.SizeEnum.QUARTERSCREEN;
                    smaller = (count - 2) * 2;
                } else {
                    size = DashboardPanel.SizeEnum.FULLSCREEN;
                    // No smaller panels, this block is only reached if panels.size == 1
                }
                int fullSized = count - smaller;
                for (int i = 0; i < count; i++) {
                    DashboardPanel panel = panels.get(i);
                    if (i < fullSized) {
                        panel.setSize(size);
                    } else {
                        panel.setSize(lowerSize);
                    }
                }
            }
            // else: nothing to do, it's just one non null size
        } else {
            // We have more than one size
            if (sizes.contains(null)) {
                panels.stream().filter(p -> p.getSize() == null).forEach(p -> p.setSize(DashboardPanel.SizeEnum.MEDIUM));
                // Just set the nulls to a default value
            }
        }
    }

    List<Panel> makePanels(List<DashboardPanel> panels) {
        return panels.stream().map(
                p -> new Panel()
                        .datasource("Prometheus")
                        .title(p.getTitle())
                        .height(String.format("%spx", 200 * p.getSize().height))
                        .lines(true)
                        .linewidth(1)
                        .span(p.getSize().width)
                        .type("graph")
                        .fill(1)
                        .editable(true)
                        .targets(Collections.singletonList(
                                new Target()
                                        .expr(p.getQuery())
                                        .refId(p.getTitle())
                        ))
                        .xAxis(
                                new Xaxis()
                                        .name("timestamp")
                                        .mode("time")
                                        .show(true)
                        )
                        .yAxes(Arrays.asList(
                                new Yaxes()
                                        .label(p.getQuery())
                                        .format("short")
                                        .logBase(1)
                                        .min(null)
                                        .max(null)
                                        .show(true),
                                new Yaxes()
                                        .label(p.getQuery())
                                        .format("short")
                                        .logBase(1)
                                        .min(null)
                                        .max(null)
                                        .show(false)
                        ))
        ).collect(Collectors.toList());
    }

    Row makeRow(String title, List<Panel> panels, DashboardPanel.SizeEnum size) {
        assert panels.size() > 0;
        return new Row()
                .height(size.height)
                .panels(panels)
                .title(title)
                .titleSize(5)
                .collapse(false);
    }

    Map<String, List<DashboardPanel>> makeBatches(List<DashboardPanel> panels, DashboardPanel.SizeEnum size) {
        int batchSize = 12 / size.width;
        Map<String, List<DashboardPanel>> out = new HashMap<>();
        int count = panels.size();
        for (int i = 0; i < count; i = i + batchSize) {
            int stop = Math.min(i + batchSize, count);
            out.put(
                    String.format("%s: %02d", size.getValue(), (i / batchSize) + 1),
                    panels.subList(i, stop)
            );
        }
        return out;
    }

    private List<Row> makeRows(List<DashboardPanel> panels) {
        ensureSize(panels); // No more null sizes.
        Map<DashboardPanel.SizeEnum, List<DashboardPanel>> bySize = panels.stream()
                .collect(Collectors.groupingBy(DashboardPanel::getSize));
        List<Row> out = new ArrayList<>();
        for (DashboardPanel.SizeEnum size : DashboardPanel.SizeEnum.values()) {
            Map<String, List<DashboardPanel>> batches = makeBatches(bySize.getOrDefault(size, new ArrayList<>()), size);
            for (Map.Entry<String, List<DashboardPanel>> batch : batches.entrySet()) {
                List<Panel> newPanels = makePanels(batch.getValue());
                out.add(makeRow(
                        batch.getKey(),
                        newPanels,
                        size
                ));
            }
        }
        return out;
    }

    private GrafanaDashboardWrapper translate(Dashboard dashboard) {
        Meta meta = new Meta()
                .createdBy("5G-Transformer Monitoring Config Manager")
                .canEdit(true)
                .canSave(true)
                .canStar(true);
        GrafanaDashboard gDashboard = new GrafanaDashboard()
                .uid(dashboard.getDashboardId())
                .title(dashboard.getName())
                .rows(makeRows(dashboard.getPanels()))
                .schemaVersion(1)
                .tags(Arrays.asList("generated", "5GT", "ConfigManager"))
                .timezone("browser")
                .version(dashboard.getVersion());
        return new GrafanaDashboardWrapper()
                .meta(meta)
                .dashboard(gDashboard);
    }

    @Override
    public Future<Dashboard> save(DashboardDescription description) {
        GrafanaDashboardWrapper translated = translate(description);
        Future<PostDashboardResponse> responseFuture = connector.postDashboard(translated);
        return responseFuture.compose(
                resp -> {
                        Dashboard created =
                                new Dashboard(description)
                                        .dashboardId(resp.uid)
                                        .url(resp.url)
                                        .version(0);
                        return repo.save(created);
                }
        );
    }

    @Override
    public Future<Dashboard> save(Dashboard dashboard) {
        throw new IllegalStateException("Save should only be called with a description.");
    }

    @Override
    public Future<Dashboard> update(Dashboard dashboard) {
        return repo.findById(dashboard.getDashboardId())
                .compose(
                        old -> {
                            dashboard.setVersion(old.getVersion() + 1);
                            GrafanaDashboardWrapper translated = translate(dashboard);
                            return connector.postDashboard(translated);
                        }
                ).compose(
                        resp -> {
                            assert resp.uid.equals(dashboard.getDashboardId());
                            assert resp.version == dashboard.getVersion() + 1;
                            return repo.save(dashboard);
                        }
                );
    }

    @Override
    public Future<Dashboard> findById(String dashboardId) {
        return repo.findById(dashboardId);
    }

    @Override
    public Future<Set<String>> deleteById(String dashboardId) {
        return deleteById(dashboardId, false);
    }

    @Override
    public Future<Set<String>> deleteById(String dashboardId, boolean strict) {
        return repo.findById(dashboardId).compose(
                deleting -> {
                    if (deleting == null) {
                        if (strict) {
                            return Future.failedFuture(new IllegalArgumentException(
                                    String.format("No dashboard with id %s", dashboardId)
                            ));
                        } else {
                            return Future.succeededFuture(Collections.emptySet());
                        }
                    } else {  // the id exists
                        return connector.deleteDashboard(dashboardId)
                                // Then delete it from the db too
                                .compose(resp -> repo.deleteById(dashboardId, true));
                    }
                }
        );
    }

    @Override
    public Future<Set<Dashboard>> findAll() {
        return repo.findAll();
    }
}
