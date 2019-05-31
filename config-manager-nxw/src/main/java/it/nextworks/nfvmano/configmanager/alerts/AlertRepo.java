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

import java.util.Optional;
import java.util.Set;

/**
 * Created by Marco Capitani on 05/04/19.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
public interface AlertRepo {
    
    Future<Alert> save(Alert alert);

    Future<Alert> update(Alert alert);

    Future<Optional<Alert>> findById(String alertId);

    Future<Set<String>> deleteById(String alertId);  // Implicitly not strict

    Future<Set<String>> deleteById(String alertId, boolean strict);

    Future<Set<Alert>> findAll();
}
