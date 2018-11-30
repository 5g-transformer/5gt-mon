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

package it.nextworks.nfvmano.configmanager.sb.grafana.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by Marco Capitani on 29/10/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
public class PostDashboardResponse {

    public int id;

    public String slug;

    public String status;

    public String message;

    public String uid;

    public String url;

    /* The subsequent POST request should have this version */
    public int version;

    @JsonIgnore
    public boolean isSuccessful() {
        return status.equals("success");
    }

    @JsonIgnore
    public String getError() {
        if (isSuccessful()) {
            return null;
        } else {
            return status;
        }
    }
}
