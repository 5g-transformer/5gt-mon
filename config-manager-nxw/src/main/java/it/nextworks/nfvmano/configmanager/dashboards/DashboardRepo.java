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
import io.vertx.core.Handler;
import it.nextworks.nfvmano.configmanager.dashboards.model.Dashboard;
import it.nextworks.nfvmano.configmanager.dashboards.model.DashboardDescription;

import java.util.Set;

/**
 * Created by Marco Capitani on 25/10/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
public interface DashboardRepo {

    Future<Dashboard> save(DashboardDescription description);

    Future<Dashboard> save(Dashboard dashboard);

    Future<Dashboard> update(Dashboard dashboard);

    Future<Dashboard> findById(String dashboardId);

    Future<Set<String>> deleteById(String dashboardId);  // Implicitly not strict

    Future<Set<String>> deleteById(String dashboardId, boolean strict);

    Future<Set<Dashboard>> findAll();
}
