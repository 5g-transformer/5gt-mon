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

import it.nextworks.nfvmano.configmanager.exporters.ExporterRepo;
import it.nextworks.nfvmano.configmanager.exporters.model.Endpoint;
import it.nextworks.nfvmano.configmanager.exporters.model.Exporter;
import it.nextworks.nfvmano.configmanager.exporters.model.ExporterDescription;
import it.nextworks.nfvmano.configmanager.sb.prometheus.model.PrometheusConfig;
import it.nextworks.nfvmano.configmanager.sb.prometheus.model.StaticConfigs;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Marco Capitani on 22/10/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
public class ExporterService implements ExporterRepo {

    private PrometheusConnector pConnector;

    private ExporterRepo db;

    public ExporterService(PrometheusConnector pConnector, ExporterRepo db) {
        this.pConnector = pConnector;
        this.db = db;
    }

    private static StaticConfigs makeStaticConfigs(Exporter exporter) {
        List<Endpoint> endpoint = exporter.getEndpoint();
        List<String> targets = endpoint.stream().map(
                x -> x.getAddress() + ':' + x.getPort().toString()
        ).collect(Collectors.toList());
        return new StaticConfigs(targets);
    }

    private static void extendConfig(PrometheusConfig config, Exporter... exporters) {
        for (Exporter exporter : exporters) {
            config.addScrapeConfig(
                    exporter.getExporterId(),
                    exporter.getCollectionPeriod().toString() + "s",
                    makeStaticConfigs(exporter)
            );
        }
    }

    @Override
    public Exporter save(ExporterDescription description) {
        Exporter exporter = db.save(description);
        PrometheusConfig config = pConnector.getConfig();
        extendConfig(config, exporter);
        pConnector.setConfig(config);
        return exporter;
    }

    @Override
    public Exporter save(Exporter exporter) {
        Exporter newExporter = db.save(exporter);
        PrometheusConfig config = pConnector.getConfig();
        extendConfig(config, newExporter);
        pConnector.setConfig(config);
        return newExporter;
    }

    @Override
    public Exporter update(Exporter exporter) {
        Exporter updated = db.update(exporter);
        PrometheusConfig config = pConnector.getConfig();
        config.removeScrapeConfig(updated.getExporterId());
        extendConfig(config, updated);
        pConnector.setConfig(config);
        return updated;
    }

    @Override
    public Exporter findById(String exporterId) {
        return db.findById(exporterId);
    }

    @Override
    public Set<String> deleteById(String exporterId) {
        return deleteById(exporterId, false);
    }

    @Override
    public Set<String> deleteById(String exporterId, boolean strict) {
        Set<String> deleted = db.deleteById(exporterId);

        if (deleted.size() != 0) {
            PrometheusConfig config = pConnector.getConfig();
            config.removeScrapeConfig(exporterId);
            pConnector.setConfig(config);
        } else if (strict) {
            throw new IllegalArgumentException(String.format("No such exporter: %s", exporterId));
        }

        return deleted;
    }

    @Override
    public Set<Exporter> findAll() {
        return db.findAll();
    }

    public void refresh() {
        PrometheusConfig oldConfig = pConnector.getConfig();
        PrometheusConfig config = new PrometheusConfig(
                new ArrayList<>(),
                oldConfig.getGlobal(),
                oldConfig.getAlerting(),
                oldConfig.getRuleFiles()
        );
        Set<Exporter> exporters = db.findAll();
        extendConfig(config, exporters.toArray(new Exporter[0]));
        pConnector.setConfig(config);
    }
}
