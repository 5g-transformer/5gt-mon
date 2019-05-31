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

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class DeleteResponse {

    @JsonProperty("deleted")
    private List<String> deleted = null;

    public DeleteResponse deleted(Collection<String> deleted) {
        this.deleted(new ArrayList<>(deleted));
        return this;
    }

    public DeleteResponse deleted(List<String> deleted) {
        this.deleted = deleted;
        return this;
    }

    public DeleteResponse addDeletedItem(String deletedItem) {
        if (this.deleted == null) {
            this.deleted = new ArrayList<>();
        }
        this.deleted.add(deletedItem);
        return this;
    }

    /**
     * Get deleted
     *
     * @return deleted
     **/
    public List<String> getDeleted() {
        return deleted;
    }

    public void setDeleted(List<String> deleted) {
        this.deleted = deleted;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DeleteResponse deleteResponse = (DeleteResponse) o;
        return Objects.equals(this.deleted, deleteResponse.deleted);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deleted);
    }

    @Override
    public String toString() {

        return "class DeleteResponse {\n" +
                "    deleted: " + toIndentedString(deleted) + "\n" +
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
