package it.nextworks.nfvmano.configmanager.alerts.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Marco Capitani on 05/04/19.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
class AlertTest {

    @Test
    void getForSeconds() {
        Alert a = new Alert();
        int[] testCases = new int[] {
                0,
                1,
                2,
                3,
                5,
                12,
                59,
                60,
                120,
                182,
                1234
        };
        for (int c : testCases) {
            a.setForSeconds(c);
            assertEquals(c, a.getForSeconds());
        }
    }
}