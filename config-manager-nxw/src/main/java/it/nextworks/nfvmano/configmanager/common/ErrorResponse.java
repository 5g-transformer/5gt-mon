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

package it.nextworks.nfvmano.configmanager.common;

/**
 * Created by Marco Capitani on 03/10/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
public class ErrorResponse {

    private final String code;

    private final String error;

    private final String description;

    public ErrorResponse(String code, String error, String description) {
        this.code = code;
        this.error = error;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getError() {
        return error;
    }

    public String getDescription() {
        return description;
    }
}
