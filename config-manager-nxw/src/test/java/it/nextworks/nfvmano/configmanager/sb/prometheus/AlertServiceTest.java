package it.nextworks.nfvmano.configmanager.sb.prometheus;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import io.vertx.core.Future;
import it.nextworks.nfvmano.configmanager.alerts.AlertRepo;
import it.nextworks.nfvmano.configmanager.alerts.model.Alert;
import it.nextworks.nfvmano.configmanager.sb.prometheus.model.AlertManagerConfig;
import it.nextworks.nfvmano.configmanager.sb.prometheus.model.AlertRules;
import it.nextworks.nfvmano.configmanager.sb.prometheus.model.Groups;
import it.nextworks.nfvmano.configmanager.sb.prometheus.model.Receivers;
import it.nextworks.nfvmano.configmanager.sb.prometheus.model.Route;
import it.nextworks.nfvmano.configmanager.sb.prometheus.model.Routes;
import it.nextworks.nfvmano.configmanager.sb.prometheus.model.Rules;
import it.nextworks.nfvmano.configmanager.sb.prometheus.model.WebhookConfigs;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Marco Capitani on 08/04/19.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
@ExtendWith(RandomBeansExtension.class)
@ExtendWith(MockitoExtension.class)
public class AlertServiceTest {

    /*
     * Save
     * Update
     * find
     * delete (x2)
     * findall
     */

    private void assertExtensionRuleL(List<Rules> oldList, List<Rules> newList, Alert addition) {
        assertEquals("Unexpected rules number", oldList.size() + 1, newList.size());
        List<Rules> difference = new ArrayList<>(newList);
        difference.removeAll(oldList);
        assertEquals("Unexpected change in old rules",1, difference.size());
        assertEquals(
                "The installed rule is not as expected",
                AlertService.convert(addition),  // TODO do this properly
                difference.get(0)
        );
    }

    private void assertExtensionGroupL(List<Groups> oldList, List<Groups> newList, Alert addition) {
        Optional<Groups> oldOpt = oldList.stream()
                .filter(g -> g.getName().equals(AlertService.GROUP_NAME))
                .findAny();
        boolean preExisting = oldOpt.isPresent();
        Optional<Groups> newOpt = newList.stream()
                .filter(g -> g.getName().equals(AlertService.GROUP_NAME))
                .findAny();
        assertTrue(
                "Service group not found",
                newOpt.isPresent()
        );
        Groups oldG;
        if (preExisting) {
            assertEquals("Unexpected Group number", oldList.size(), newList.size());
            oldG = oldOpt.get();
        }
        else {
            assertEquals("Unexpected Group number", oldList.size() + 1, newList.size());
            oldG = new Groups();
            oldG.setName(AlertService.GROUP_NAME);
            oldG.setRules(new ArrayList<>());
        }
        Groups newG = newOpt.get();
        assertEquals("Group name changed", oldG.getName(), newG.getName());
        assertExtensionRuleL(oldG.getRules(), newG.getRules(), addition);
    }

    private void assertExtension(AlertRules oldRules, AlertRules newRules, Alert addition) {
        assertExtensionGroupL(oldRules.getGroups(), newRules.getGroups(), addition);
    }

    private void assertExtensionReceivers(List<Receivers> oldRecvs, List<Receivers> newRecvs, Alert addition) {
        Optional<Receivers> optOld = oldRecvs.stream()
                .filter(r -> r.getName().equals(recvId))
                .findAny();
        Optional<Receivers> optNew = newRecvs.stream()
                .filter(r -> r.getName().equals(recvId))
                .findAny();
        boolean preExisting = optOld.isPresent();
        assertTrue("Missing receiver", optNew.isPresent());

        Receivers newRecv = optNew.get();
        if (preExisting) {
            assertEquals(
                    "Receivers list should not have changed",
                    oldRecvs.size(),
                    newRecvs.size()
            );
            assertEquals("Receiver changed", optOld.get(), newRecv);
        } else {
            assertEquals(
                    "Receiver list size not as expected",
                    oldRecvs.size() + 1,
                    newRecvs.size()
            );
        }

        assertEquals(newRecv.getName(), recvId);
        assertEquals(1, newRecv.getWebhookConfigs().size());

        WebhookConfigs whConfig = newRecv.getWebhookConfigs().get(0);
        assertTrue(whConfig.isSendResolved());
        assertEquals(addition.getTarget().toString(), whConfig.getUrl());
    }

    private void assertExtensionRoutes(List<Routes> oldRoutes, List<Routes> newRoutes, Alert addition) {
        assertEquals("Unexpected routes size", oldRoutes.size() + 1, newRoutes.size());
        Optional<Routes> newRouteOpt = newRoutes.stream()
                .filter(r -> r.getMatch().get("alertname").equals(addition.getAlertId()))
                .findAny();
        assertTrue("New Route not found", newRouteOpt.isPresent());
        Routes newRoute = newRouteOpt.get();
        Map<String, String> match = newRoute.getMatch();
        assertEquals("Unexpected match fields", 1, match.size());
        assertTrue("Unexpected match key", match.containsKey("alertname"));
        assertEquals("Unexpected match value", addition.getAlertId(), match.get("alertname"));
        assertEquals("Unexpected receiver", recvId, newRoute.getReceiver());
    }

    private void assertExtension(
            AlertManagerConfig oldConfig,
            AlertManagerConfig newConfig,
            Alert addition
    ) {
        assertExtensionReceivers(oldConfig.getReceivers(), newConfig.getReceivers(), addition);
        assertEquals("Global should not change", oldConfig.getGlobal(), newConfig.getGlobal());

        Route oldRoute = oldConfig.getRoute();
        Route newRoute = newConfig.getRoute();
        assertEquals(oldRoute.getGroupBy(), newRoute.getGroupBy());
        assertEquals(oldRoute.getGroupInterval(), newRoute.getGroupInterval());
        assertEquals(oldRoute.getGroupWait(), newRoute.getGroupWait());
        assertEquals(oldRoute.getReceiver(), newRoute.getReceiver());
        assertEquals(oldRoute.getRepeatInterval(), newRoute.getRepeatInterval());

        assertExtensionRoutes(oldRoute.getRoutes(), newRoute.getRoutes(), addition);
    }

    @Mock
    private PrometheusConnector connector;

    @Mock
    private AlertRepo repo;

    @Mock
    private TargetRepo tRepo;

    @InjectMocks
    private AlertService service;

    private static String recvId = "00000000-0000-0000-0000-000000000001";
    private static UUID recvUUID = new UUID(0,1);

    @Test
    public void testSaveRuleEmpty(@Random Alert alert) throws Exception  {
        // Make empty AMConfig
        AlertManagerConfig amConfig = new AlertManagerConfig();
        // Make empty alertRules
        AlertRules oldRules = new AlertRules();

        // Cloning data for mock returns
        ObjectMapper mapper = new ObjectMapper();
        AlertRules clonedRules = mapper.readValue(mapper.writeValueAsString(oldRules), AlertRules.class);
        AlertManagerConfig clonedAMConfig = mapper.readValue(mapper.writeValueAsString(amConfig), AlertManagerConfig.class);

        when(connector.getRules()).thenReturn(clonedRules);
        when(connector.getAMConfig()).thenReturn(clonedAMConfig);
        when(connector.setRules(any())).thenReturn(Future.succeededFuture());
        when(connector.setAMConfig(any())).thenReturn(Future.succeededFuture());
        when(repo.save(any())).thenReturn(Future.succeededFuture());
        when(tRepo.saveOrGet(any())).thenReturn(recvUUID);

        service.save(alert);

        verify(repo).save(alert);
        // Verify rules
        ArgumentCaptor<AlertRules> captor = ArgumentCaptor.forClass(AlertRules.class);
        verify(connector).setRules(captor.capture());
        AlertRules value = captor.getValue();

        assertExtension(oldRules, value, alert);

        // Verify AMConfig
        ArgumentCaptor<AlertManagerConfig> captor2 = ArgumentCaptor.forClass(AlertManagerConfig.class);
        verify(connector).setAMConfig(captor2.capture());
        AlertManagerConfig value2 = captor2.getValue();

        assertExtension(amConfig, value2, alert);
    }

    @Test
    public void testSaveRuleRules(@Random Alert alert, @Random AlertRules oldRules) throws Exception  {
        // Make empty AMConfig
        AlertManagerConfig amConfig = new AlertManagerConfig();

        // Cloning data for mock returns
        ObjectMapper mapper = new ObjectMapper();
        AlertRules clonedRules = mapper.readValue(mapper.writeValueAsString(oldRules), AlertRules.class);
        AlertManagerConfig clonedAMConfig = mapper.readValue(mapper.writeValueAsString(amConfig), AlertManagerConfig.class);

        when(connector.getRules()).thenReturn(clonedRules);
        when(connector.getAMConfig()).thenReturn(clonedAMConfig);
        when(connector.setRules(any())).thenReturn(Future.succeededFuture());
        when(connector.setAMConfig(any())).thenReturn(Future.succeededFuture());
        when(repo.save(any())).thenReturn(Future.succeededFuture());
        when(tRepo.saveOrGet(any())).thenReturn(recvUUID);

        service.save(alert);

        verify(repo).save(alert);
        // Verify rules
        ArgumentCaptor<AlertRules> captor = ArgumentCaptor.forClass(AlertRules.class);
        verify(connector).setRules(captor.capture());
        AlertRules value = captor.getValue();

        assertExtension(oldRules, value, alert);

        // Verify AMConfig
        ArgumentCaptor<AlertManagerConfig> captor2 = ArgumentCaptor.forClass(AlertManagerConfig.class);
        verify(connector).setAMConfig(captor2.capture());
        AlertManagerConfig value2 = captor2.getValue();

        assertExtension(amConfig, value2, alert);
    }

    @Test
    public void testSaveRuleAMConfig(@Random Alert alert, @Random AlertManagerConfig amConfig) throws Exception  {
        // Make empty alertRules
        AlertRules oldRules = new AlertRules();

        // Cloning data for mock returns
        ObjectMapper mapper = new ObjectMapper();
        AlertRules clonedRules = mapper.readValue(mapper.writeValueAsString(oldRules), AlertRules.class);
        AlertManagerConfig clonedAMConfig = mapper.readValue(mapper.writeValueAsString(amConfig), AlertManagerConfig.class);

        when(connector.getRules()).thenReturn(clonedRules);
        when(connector.getAMConfig()).thenReturn(clonedAMConfig);
        when(connector.setRules(any())).thenReturn(Future.succeededFuture());
        when(connector.setAMConfig(any())).thenReturn(Future.succeededFuture());
        when(repo.save(any())).thenReturn(Future.succeededFuture());
        when(tRepo.saveOrGet(any())).thenReturn(recvUUID);

        service.save(alert);

        verify(repo).save(alert);
        // Verify rules
        ArgumentCaptor<AlertRules> captor = ArgumentCaptor.forClass(AlertRules.class);
        verify(connector).setRules(captor.capture());
        AlertRules value = captor.getValue();

        assertExtension(oldRules, value, alert);

        // Verify AMConfig
        ArgumentCaptor<AlertManagerConfig> captor2 = ArgumentCaptor.forClass(AlertManagerConfig.class);
        verify(connector).setAMConfig(captor2.capture());
        AlertManagerConfig value2 = captor2.getValue();

        assertExtension(amConfig, value2, alert);
    }

    @Test
    public void testSaveRuleHavingReceiver(
            @Random Alert alert,
            @Random AlertRules oldRules,
            @Random AlertManagerConfig amConfig
    ) throws Exception {
        // Add receiver
        WebhookConfigs webhookConfigs = new WebhookConfigs().url(alert.getTarget().toString()).sendResolved(true);
        Receivers recv = new Receivers(recvId).webhookConfigs(Collections.singletonList(webhookConfigs));
        amConfig.getReceivers().add(recv);

        // Cloning data for mock returns
        ObjectMapper mapper = new ObjectMapper();
        AlertRules clonedRules = mapper.readValue(mapper.writeValueAsString(oldRules), AlertRules.class);
        AlertManagerConfig clonedAMConfig = mapper.readValue(mapper.writeValueAsString(amConfig), AlertManagerConfig.class);

        when(connector.getRules()).thenReturn(clonedRules);
        when(connector.getAMConfig()).thenReturn(clonedAMConfig);
        when(connector.setRules(any())).thenReturn(Future.succeededFuture());
        when(connector.setAMConfig(any())).thenReturn(Future.succeededFuture());
        when(repo.save(any())).thenReturn(Future.succeededFuture());
        when(tRepo.saveOrGet(any())).thenReturn(recvUUID);

        service.save(alert);

        verify(repo).save(alert);
        // Verify rules
        ArgumentCaptor<AlertRules> captor = ArgumentCaptor.forClass(AlertRules.class);
        verify(connector).setRules(captor.capture());
        AlertRules value = captor.getValue();

        assertExtension(oldRules, value, alert);

        // Verify AMConfig
        ArgumentCaptor<AlertManagerConfig> captor2 = ArgumentCaptor.forClass(AlertManagerConfig.class);
        verify(connector).setAMConfig(captor2.capture());
        AlertManagerConfig value2 = captor2.getValue();

        assertExtension(amConfig, value2, alert);
    }

    @Test
    public void testSaveRuleFull(
            @Random Alert alert,
            @Random AlertRules oldRules,
            @Random AlertManagerConfig amConfig
    ) throws Exception {
        // Cloning data for mock returns
        ObjectMapper mapper = new ObjectMapper();
        AlertRules clonedRules = mapper.readValue(mapper.writeValueAsString(oldRules), AlertRules.class);
        AlertManagerConfig clonedAMConfig = mapper.readValue(mapper.writeValueAsString(amConfig), AlertManagerConfig.class);

        when(connector.getRules()).thenReturn(clonedRules);
        when(connector.getAMConfig()).thenReturn(clonedAMConfig);
        when(connector.setRules(any())).thenReturn(Future.succeededFuture());
        when(connector.setAMConfig(any())).thenReturn(Future.succeededFuture());
        when(repo.save(any())).thenReturn(Future.succeededFuture());
        when(tRepo.saveOrGet(any())).thenReturn(recvUUID);

        service.save(alert);

        verify(repo).save(alert);
        // Verify rules
        ArgumentCaptor<AlertRules> captor = ArgumentCaptor.forClass(AlertRules.class);
        verify(connector).setRules(captor.capture());
        AlertRules value = captor.getValue();

        assertExtension(oldRules, value, alert);

        // Verify AMConfig
        ArgumentCaptor<AlertManagerConfig> captor2 = ArgumentCaptor.forClass(AlertManagerConfig.class);
        verify(connector).setAMConfig(captor2.capture());
        AlertManagerConfig value2 = captor2.getValue();

        assertExtension(amConfig, value2, alert);
    }
}
