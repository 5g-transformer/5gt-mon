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

package it.nextworks.nfvmano.configmanager.exporters;

import it.nextworks.nfvmano.configmanager.exporters.model.Endpoint;
import it.nextworks.nfvmano.configmanager.exporters.model.Exporter;
import it.nextworks.nfvmano.configmanager.exporters.model.ExporterDescription;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Marco Capitani on 22/10/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
public class MemoryExporterRepo implements ExporterRepo {

    private Map<String, Exporter> map = new HashMap<>();

    private void populateMap() {
        map.put("1", makeExporter("1", 1, "One", "192.168.1.1"));
        map.put("2", makeExporter("2", 2, "Two", "192.168.1.2", "192.168.2.2"));
        map.put("3", makeExporter("3", 3, "Three", "192.168.3.3"));
    }

    private Exporter makeExporter(String id, Integer period, String name, String... addresses) {
        List<Endpoint> endpoints = new ArrayList<>();
        for (String address : addresses) {
            Endpoint endpoint = new Endpoint();
            endpoint.setAddress(address);
            endpoint.setPort(8765);
            endpoints.add(endpoint);
        }
        Exporter output = new Exporter();
        output.setExporterId(id);
        output.setEndpoint(endpoints);
        output.setCollectionPeriod(period);
        output.setName(name);
        return output;
    }

    @Override
    public Exporter save(Exporter exporter) {
        map.put(exporter.getExporterId(), exporter);
        return exporter;
    }

    @Override
    public Exporter findById(String exporterId) {
        return map.get(exporterId);
    }

    @Override
    public Set<String> deleteById(String exporterId) {
        Exporter removed = map.remove(exporterId);
        if (removed == null) {
            return Collections.emptySet();
        } else {
            return Collections.singleton(exporterId);
        }
    }

    @Override
    public Set<Exporter> findAll() {
        return new HashSet<>(map.values());
    }

    @Override
    public Exporter save(ExporterDescription description) {
        Exporter exporter = new Exporter();
        exporter.setCollectionPeriod(description.getCollectionPeriod());
        exporter.setEndpoint(description.getEndpoint());
        exporter.setName(description.getName());
        String uuid = UUID.randomUUID().toString();
        exporter.setExporterId(uuid);
        return save(exporter);
    }

    @Override
    public Exporter update(Exporter exporter) {
        deleteById(exporter.getExporterId(), true);
        return save(exporter);
    }

    @Override
    public Set<String> deleteById(String exporterId, boolean strict) {
        Set<String> deleted = deleteById(exporterId);
        if (deleted.isEmpty()) {
            throw new IllegalArgumentException("No such exporter");
        }
        return deleted;
    }
}
