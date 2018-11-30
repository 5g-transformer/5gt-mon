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

package it.nextworks.nfvmano.configmanager.exporters.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Endpoint {
    @JsonProperty("address")
    private String address = null;

    @JsonProperty("port")
    private Integer port = null;

    public Endpoint address(String address) {
        this.address = address;
        return this;
    }

    /**
     * an IP address
     *
     * @return address
     **/
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Endpoint port(Integer port) {
        this.port = port;
        return this;
    }

    /**
     * a TCP port number
     *
     * @return port
     **/
    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Endpoint endpoint = (Endpoint) o;
        return Objects.equals(this.address, endpoint.address) &&
                Objects.equals(this.port, endpoint.port);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, port);
    }


    @Override
    public String toString() {

        return "class Endpoint {\n" +
                "    address: " + toIndentedString(address) + "\n" +
                "    port: " + toIndentedString(port) + "\n" +
                "}";
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

}

