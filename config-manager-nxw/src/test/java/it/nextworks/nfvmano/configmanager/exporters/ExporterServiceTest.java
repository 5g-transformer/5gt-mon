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

import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import io.vertx.core.Future;
import it.nextworks.nfvmano.configmanager.exporters.model.Exporter;
import it.nextworks.nfvmano.configmanager.exporters.model.ExporterDescription;
import it.nextworks.nfvmano.configmanager.sb.prometheus.ExporterService;
import it.nextworks.nfvmano.configmanager.sb.prometheus.PrometheusConnector;
import it.nextworks.nfvmano.configmanager.sb.prometheus.model.PrometheusConfig;
import it.nextworks.nfvmano.configmanager.sb.prometheus.model.ScrapeConfigs;
import it.nextworks.nfvmano.configmanager.sb.prometheus.model.StaticConfigs;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Created by Marco Capitani on 23/10/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
@ExtendWith(RandomBeansExtension.class)
@ExtendWith(MockitoExtension.class)
class ExporterServiceTest {

    @Mock
    private PrometheusConnector connector;

    @Mock
    private ExporterRepo repo;

    @InjectMocks
    private ExporterService service;

    private void assertEqualsExceptScrape(PrometheusConfig config, PrometheusConfig backupConfig, String scrapeConfigId) {
        assertEquals(backupConfig.getAlerting(), config.getAlerting());
        assertEquals(backupConfig.getGlobal(), config.getGlobal());
        assertEquals(backupConfig.getRuleFiles(), config.getRuleFiles());
        assertTrue(config.getScrapeConfigs().stream().anyMatch(x -> x.getJobName().equals(scrapeConfigId)));
        assertEquals(
                backupConfig.getScrapeConfigs(),
                config.getScrapeConfigs().stream()
                        .filter(x -> !x.getJobName().equals(scrapeConfigId))
                        .collect(Collectors.toList())

        );
    }

    private void assertContains(PrometheusConfig config, Exporter exporter) {
        Optional<ScrapeConfigs> optConfig = config.getScrapeConfigs().stream().filter(x -> x.getJobName().equals(exporter.getExporterId())).findAny();
        assert optConfig.isPresent();
        ScrapeConfigs newConfig = optConfig.get();
        assertEquals(exporter.getExporterId(), newConfig.getJobName());
        assertEquals(exporter.getCollectionPeriod().toString() + "s", newConfig.getScrapeInterval());
        assertEquals(1, newConfig.getStaticConfigs().size());
        StaticConfigs staticConfig = newConfig.getStaticConfigs().get(0);
        assertEquals(
                exporter.getEndpoint().stream()
                        .map(x -> x.getAddress() + ':' + x.getPort().toString())
                        .collect(Collectors.toList()),
                staticConfig.getTargets()
        );
    }

    @Test
    void saveDesc(@Random ExporterDescription description, @Random PrometheusConfig config) {
        // Make exporter from description
        Exporter exporter = new Exporter();
        exporter.setCollectionPeriod(description.getCollectionPeriod());
        exporter.setEndpoint(description.getEndpoint());
        exporter.setName(description.getName());
        exporter.setNsId(description.getNsId());
        exporter.setVnfdId(description.getVnfdId());
        String uuid = UUID.randomUUID().toString();
        exporter.setExporterId(uuid);

        PrometheusConfig backupConfig = new PrometheusConfig(
                new ArrayList<>(config.getScrapeConfigs()),
                config.getGlobal(),
                config.getAlerting(),
                new ArrayList<>(config.getRuleFiles())
        );

        when(repo.save(description)).thenReturn(Future.succeededFuture(exporter));
        when(connector.getConfig()).thenReturn(config);
        when(connector.setConfig(any())).thenReturn(Future.succeededFuture());
        Future<Exporter> future = service.save(description);

        assertTrue(future.isComplete());
        assertTrue(future.succeeded());
        Exporter saved = future.result();

        assertEquals(saved, exporter);

        verify(repo, times(1)).save(description);
        verify(connector, times(1)).getConfig();
        verify(connector, times(1)).setConfig(config);

        assertEqualsExceptScrape(config, backupConfig, uuid);

        assertContains(config, exporter);
    }

    @Test
    void saveExp(@Random Exporter exporter, @Random PrometheusConfig config) {
        PrometheusConfig backupConfig = new PrometheusConfig(
                new ArrayList<>(config.getScrapeConfigs()),
                config.getGlobal(),
                config.getAlerting(),
                new ArrayList<>(config.getRuleFiles())
        );

        when(repo.save(exporter)).thenReturn(Future.succeededFuture(exporter));
        when(connector.getConfig()).thenReturn(config);
        when(connector.setConfig(any())).thenReturn(Future.succeededFuture());

        Future<Exporter> future = service.save(exporter);

        assertTrue(future.isComplete());
        assertTrue(future.succeeded());
        Exporter saved = future.result();

        assertEquals(saved, exporter);

        verify(repo, times(1)).save(exporter);
        verify(connector, times(1)).getConfig();
        verify(connector, times(1)).setConfig(config);

        assertEqualsExceptScrape(config, backupConfig, exporter.getExporterId());

        assertContains(config, exporter);
    }

    @Test
    void update(
      @Random Exporter exporter,
      @Random PrometheusConfig config,
      @Random(type=ScrapeConfigs.class) List<ScrapeConfigs> scrapeConfigs
    ) {
        config = new PrometheusConfig(scrapeConfigs, config.getGlobal(), config.getAlerting(), config.getRuleFiles());
        String expId = config.getScrapeConfigs().get(0).getJobName();
        exporter.setExporterId(expId);

        PrometheusConfig backupConfig = new PrometheusConfig(
                new ArrayList<>(config.getScrapeConfigs().subList(1, config.getScrapeConfigs().size())),
                config.getGlobal(),
                config.getAlerting(),
                new ArrayList<>(config.getRuleFiles())
        );

        when(repo.update(exporter)).thenReturn(Future.succeededFuture(exporter));
        when(connector.getConfig()).thenReturn(config);
        when(connector.setConfig(any())).thenReturn(Future.succeededFuture());

        Future<Exporter> future = service.update(exporter);

        assertTrue(future.isComplete());
        assertTrue(future.succeeded());
        Exporter updated = future.result();

        assertEquals(exporter, updated);

        verify(repo, times(1)).update(exporter);
        verify(connector, times(1)).getConfig();
        verify(connector, times(1)).setConfig(config);

        assertEqualsExceptScrape(config, backupConfig, expId);

        assertContains(config, exporter);
    }

    @Test
    void findById() {
    }

    @Test
    void deleteById() {
    }

    @Test
    void deleteById1() {
    }

    @Test
    void findAll() {
    }

//    @Test
//    void refresh(@Random(type=Exporter.class) Set<Exporter> exporters, @Random PrometheusConfig backupConfig) throws Exception {
//
//        when(repo.findAll()).thenReturn(exporters);
//        when(connector.getConfig()).thenReturn(backupConfig);
//
//        service.refresh();
//
//        verify(repo, times(1)).findAll();
//        ArgumentCaptor<PrometheusConfig> argument = ArgumentCaptor.forClass(PrometheusConfig.class);
//        verify(connector, times(1)).setConfig(argument.capture());
//
//        PrometheusConfig config = argument.getValue();
//
//        assertEquals(backupConfig.getAlerting(), config.getAlerting());
//        assertEquals(backupConfig.getGlobal(), config.getGlobal());
//        assertEquals(backupConfig.getRuleFiles(), config.getRuleFiles());
//
//        assertEquals(exporters.size(), config.getScrapeConfigs().size());
//        for (Exporter exporter: exporters) {
//            assertContains(config, exporter);
//        }
//    }
}
