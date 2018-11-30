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

import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import it.nextworks.nfvmano.configmanager.dashboards.DashboardRepo;
import it.nextworks.nfvmano.configmanager.dashboards.model.DashboardPanel;
import it.nextworks.nfvmano.configmanager.sb.grafana.model.Panel;
import it.nextworks.nfvmano.configmanager.sb.grafana.model.Row;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Marco Capitani on 31/10/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
@ExtendWith(RandomBeansExtension.class)
@ExtendWith(MockitoExtension.class)
class GrafanaDashboardServiceTest {

  @Mock
  private GrafanaConnector connector;

  @Mock
  private DashboardRepo repo;

  @InjectMocks
  private GrafanaDashboardService service;

  @Test
  void ensureSizeNullDefaulted(@Random(type = DashboardPanel.class) List<DashboardPanel> panels) {
    panels.get(0).setSize(DashboardPanel.SizeEnum.FULLSCREEN);
    panels.get(panels.size() - 1).setSize(null);
    List<DashboardPanel.SizeEnum> sizes = panels.stream().map(DashboardPanel::getSize).collect(Collectors.toList());
    service.ensureSize(panels);
    assertEquals(panels.size(), sizes.size());
    for (int i = 0; i < panels.size(); i++) {
      DashboardPanel.SizeEnum prevSize = sizes.get(i);
      DashboardPanel.SizeEnum newSize = panels.get(i).getSize();
      if (prevSize == null) {
        assertEquals(DashboardPanel.SizeEnum.MEDIUM, newSize);
      } else {
        assertEquals(prevSize, newSize);
      }
    }
  }

  @Test
  void ensureSizeAllNulls(@Random(type = DashboardPanel.class) List<DashboardPanel> panels) {
    for (DashboardPanel panel : panels) {
      panel.setSize(null);
    }
        service.ensureSize(panels);
    for (DashboardPanel panel : panels) {
      assertNotEquals(panel.getSize(), null);
    }
  }

  @Test
  void makePanels(@Random(type = DashboardPanel.class) List<DashboardPanel> panels) {
    service.ensureSize(panels);
    List<Panel> newPanels = service.makePanels(panels);
    assertEquals(panels.size(), newPanels.size());
    for (int i = 0; i < panels.size(); i++) {
      DashboardPanel oldPanel = panels.get(i);
      Panel newPanel = newPanels.get(i);
      assertEquals(oldPanel.getTitle(), newPanel.getTitle());

      assertEquals(1, newPanel.getTargets().size());
      assertEquals(oldPanel.getQuery(), newPanel.getTargets().get(0).getExpr());
      assertEquals(oldPanel.getTitle(), newPanel.getTargets().get(0).getRefId());

      switch (oldPanel.getSize()) {
        case FULLSCREEN:
          assertEquals("800px", newPanel.getHeight());
          assertEquals(12, newPanel.getSpan());
          break;
        case FULLWIDE:
          assertEquals("400px", newPanel.getHeight());
          assertEquals(12, newPanel.getSpan());
          break;
        case FULLTALL:
          assertEquals("800px", newPanel.getHeight());
          assertEquals(6, newPanel.getSpan());
          break;
        case QUARTERSCREEN:
          assertEquals("400px", newPanel.getHeight());
          assertEquals(6, newPanel.getSpan());
          break;
        case WIDE:
          assertEquals("200px", newPanel.getHeight());
          assertEquals(6, newPanel.getSpan());
          break;
        case TALL:
          assertEquals("400px", newPanel.getHeight());
          assertEquals(3, newPanel.getSpan());
          break;
        case MEDIUM:
          assertEquals("200px", newPanel.getHeight());
          assertEquals(3, newPanel.getSpan());
          break;
        case SMALL:
          assertEquals("200px", newPanel.getHeight());
          assertEquals(2, newPanel.getSpan());
          break;
      }
    }
  }

  @Test
  void makeRow(
    @Random(type = Panel.class) List<Panel> panels,
    @Random DashboardPanel.SizeEnum size
  ) {
    Row row = service.makeRow("title", panels, size);
    assertEquals("title", row.getTitle());
    assertEquals(panels, row.getPanels());
    assertEquals(size.height, row.getHeight());
  }

  @Test
  void makeBatches(
    @Random(type = DashboardPanel.class) List<DashboardPanel> panels
  ) {
    for (DashboardPanel.SizeEnum size : DashboardPanel.SizeEnum.values()) {
      for (DashboardPanel panel : panels) {
        panel.setSize(size);
      }
      Map<String, List<DashboardPanel>> batches = service.makeBatches(panels, size);

      int inBatches = batches.entrySet().stream()
        .map(Map.Entry::getValue)
        .reduce(
          0,
          (i, l) -> i + l.size(),
          Integer::sum
        );

      assertEquals(panels.size(), inBatches);

      List<List<DashboardPanel>> batchList = batches.entrySet().stream()
        .sorted(Comparator.comparing(Map.Entry::getKey))
        .map(Map.Entry::getValue)
        .collect(Collectors.toList());
      List<DashboardPanel> parsed = new ArrayList<>();
      for (int i = 0; i < batchList.size(); i++) {
        List<DashboardPanel> batch = batchList.get(i);
        if (i < batches.size() - 1) {
          assertEquals(12 / size.width, batch.size());
        } else {
          assertTrue(12 / size.width >= batch.size());
        }
        assertTrue(panels.containsAll(batch));
        ArrayList<DashboardPanel> intersection = new ArrayList<>(parsed);
        intersection.retainAll(batch);
        assertTrue(intersection.isEmpty());
        parsed.addAll(batch);
      }
    }
  }

  @Test
  void makeRows() {
  }

  @Test
  void translate() {
  }

  @Test
  void save() {
  }

  @Test
  void save1() {
  }

  @Test
  void update() {
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
}
