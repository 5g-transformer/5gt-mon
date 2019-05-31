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

package it.nextworks.nfvmano.configmanager.alerts;

import io.vertx.core.Future;
import it.nextworks.nfvmano.configmanager.alerts.model.Alert;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Created by Marco Capitani on 08/04/19.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
public class MemoryAlertRepo implements AlertRepo {


    private Map<String, Alert> map = new HashMap<>();

    @Override
    public Future<Alert> save(Alert alert) {
        map.put(alert.getAlertId(), alert);
        return Future.succeededFuture(alert);
    }

    @Override
    public Future<Alert> update(Alert alert) {
        return deleteById(alert.getAlertId(), true)
                .compose(deleted -> save(alert));
    }

    @Override
    public Future<Optional<Alert>> findById(String alertId) {
        return Future.succeededFuture(Optional.ofNullable(map.get(alertId)));
    }

    @Override
    public Future<Set<String>> deleteById(String alertId) {
        Alert removed = map.remove(alertId);
        if (removed == null) {
            return Future.succeededFuture(Collections.emptySet());
        } else {
            return Future.succeededFuture(Collections.singleton(alertId));
        }
    }

    @Override
    public Future<Set<String>> deleteById(String alertId, boolean strict) {
        return deleteById(alertId)
                .compose(deleted -> {
                            if (deleted.isEmpty()) {
                                throw new IllegalArgumentException(
                                        String.format("No alert with ID %s", alertId)
                                );
                            } else {
                                return Future.succeededFuture(deleted);
                            }
                        }
                );
    }

    @Override
    public Future<Set<Alert>> findAll() {
        return Future.succeededFuture(new HashSet<>(map.values()));
    }
}
