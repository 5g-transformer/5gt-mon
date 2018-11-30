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

package it.nextworks.nfvmano.configmanager.dashboards.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * the time interval to wait before refreshing the graphs
 */
public enum RefreshTimeEnum {
    _5S("5s"),

    _10S("10s"),

    _30S("30s"),

    _1M("1m"),

    _5M("5m"),

    _15M("15m"),

    _30M("30m"),

    _1H("1h"),

    _2H("2h"),

    _1D("1d");

    private String value;

    RefreshTimeEnum(String value) {
        this.value = value;
    }

    @JsonCreator
    public static RefreshTimeEnum fromValue(String text) {
        for (RefreshTimeEnum b : RefreshTimeEnum.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}

