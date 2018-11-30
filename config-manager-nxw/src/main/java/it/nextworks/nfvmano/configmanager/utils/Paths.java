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

package it.nextworks.nfvmano.configmanager.utils;

/**
 * Created by Marco Capitani on 01/10/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
public class Paths {

    public static class Rest {

        public static final String EXP = "/prom-manager/exporter";
        public static final String ONE_EXP = "/prom-manager/exporter/:expId";

        public static final String ALERT = "/prom-manager/alert";
        public static final String ONE_ALERT = "/prom-manager/alert/:alertId";

        public static final String DASHBOARD = "/prom-manager/dashboard";
        public static final String ONE_DASHBOARD = "/prom-manager/dashboard/:dashId";

        public static final String QUERY = "/prom-manager/query";

        public static final String LOGIN = "/prom-manager/login";
        public static final String LOGOUT = "/prom-manager/logout";
    }
}
