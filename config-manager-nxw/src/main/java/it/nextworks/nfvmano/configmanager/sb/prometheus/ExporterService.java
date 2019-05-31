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
import it.nextworks.nfvmano.configmanager.exporters.ExporterRepo;
import it.nextworks.nfvmano.configmanager.exporters.model.Endpoint;
import it.nextworks.nfvmano.configmanager.exporters.model.Exporter;
import it.nextworks.nfvmano.configmanager.exporters.model.ExporterDescription;
import it.nextworks.nfvmano.configmanager.sb.prometheus.model.PrometheusConfig;
import it.nextworks.nfvmano.configmanager.sb.prometheus.model.StaticConfigs;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
        Map<String, String> labels = new HashMap<>();
        if (exporter.getNsId() != null) {
            labels.put("nsId", exporter.getNsId());
        }
        if (exporter.getVnfdId() != null) {
            labels.put("vnfdId", exporter.getVnfdId());
        }
        return new StaticConfigs(targets, labels);
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
    public Future<Exporter> save(ExporterDescription description) {
        Future<Exporter> future = db.save(description);
        return future.compose(exporter -> {
            PrometheusConfig config = pConnector.getConfig();
            extendConfig(config, exporter);
            Future<Void> aux = pConnector.setConfig(config);
            return aux.map(exporter);
        });
    }

    @Override
    public Future<Exporter> save(Exporter exporter) {
        Future<Exporter> future = db.save(exporter);
        return future.compose(newExporter -> {
            PrometheusConfig config = pConnector.getConfig();
            extendConfig(config, newExporter);
            return pConnector.setConfig(config).map(newExporter);
        });
    }

    @Override
    public Future<Exporter> update(Exporter exporter) {
        PrometheusConfig config = pConnector.getConfig();
        config.removeScrapeConfig(exporter.getExporterId());
        extendConfig(config, exporter);
        Future<Void> aux = pConnector.setConfig(config);
        return aux.compose(n1 -> db.update(exporter));
    }

    @Override
    public Future<Optional<Exporter>> findById(String exporterId) {
        return db.findById(exporterId);
    }

    @Override
    public Future<Set<String>> deleteById(String exporterId) {
        return deleteById(exporterId, false);
    }

    @Override
    public Future<Set<String>> deleteById(String exporterId, boolean strict) {
        Future<Set<String>> future = db.deleteById(exporterId);
        return future.compose(deleted -> {
            if (deleted.size() > 0) {
                PrometheusConfig config = pConnector.getConfig();
                config.removeScrapeConfig(exporterId);
                return pConnector.setConfig(config).map(deleted);
            } else if (strict) {
                throw new IllegalArgumentException(String.format("No such exporter: %s", exporterId));
            } else { // Empty, not strict
                return Future.succeededFuture(Collections.emptySet());
            }
        });
    }

    @Override
    public Future<Set<Exporter>> findAll() {
        return db.findAll();
    }

//    public void refresh() {
//        PrometheusConfig oldConfig = pConnector.getConfig();
//        PrometheusConfig config = new PrometheusConfig(
//                new ArrayList<>(),
//                oldConfig.getGlobal(),
//                oldConfig.getAlerting(),
//                oldConfig.getRuleFiles()
//        );
//        Future<Set<Exporter>> exporters = db.findAll();
//        extendConfig(config, exporters.toArray(new Exporter[0]));
//        pConnector.setConfig(config);
//    }
}
