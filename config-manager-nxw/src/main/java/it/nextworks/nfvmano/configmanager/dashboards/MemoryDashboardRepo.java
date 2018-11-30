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

package it.nextworks.nfvmano.configmanager.dashboards;

import io.vertx.core.Future;
import it.nextworks.nfvmano.configmanager.dashboards.model.Dashboard;
import it.nextworks.nfvmano.configmanager.dashboards.model.DashboardDescription;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Marco Capitani on 25/10/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
public class MemoryDashboardRepo implements DashboardRepo {

    private Map<String, Dashboard> map = new HashMap<>();

    @Override
    public Future<Dashboard> save(DashboardDescription description) {
        String uuid = UUID.randomUUID().toString();
        Dashboard dashboard = new Dashboard(description)
                .dashboardId(uuid)
                .url("/this/is/an/url/" + uuid);
        return save(dashboard);
    }

    @Override
    public Future<Dashboard> save(Dashboard dashboard) {
        map.put(dashboard.getDashboardId(), dashboard);
        return Future.succeededFuture(dashboard);
    }

    @Override
    public Future<Dashboard> update(Dashboard dashboard) {
        return deleteById(dashboard.getDashboardId(), true)
                .compose(deleted -> save(dashboard));
    }

    @Override
    public Future<Dashboard> findById(String dashboardId) {
        return Future.succeededFuture(map.get(dashboardId));
    }

    @Override
    public Future<Set<String>> deleteById(String dashboardId) {
        Dashboard removed = map.remove(dashboardId);
        if (removed == null) {
            return Future.succeededFuture(Collections.emptySet());
        } else {
            return Future.succeededFuture(Collections.singleton(dashboardId));
        }
    }

    @Override
    public Future<Set<String>> deleteById(String dashboardId, boolean strict) {
        return deleteById(dashboardId)
                .compose(deleted -> {
                            if (deleted.isEmpty()) {
                                throw new IllegalArgumentException(
                                        String.format("No dashboard with ID %s", dashboardId)
                                );
                            } else {
                                return Future.succeededFuture(deleted);
                            }
                        }
                );
    }

    @Override
    public Future<Set<Dashboard>> findAll() {
        return Future.succeededFuture(new HashSet<>(map.values()));
    }
}
