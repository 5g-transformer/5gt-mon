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

import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.ext.web.codec.impl.BodyCodecImpl;
import io.vertx.ext.web.handler.impl.HttpStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Created by Marco Capitani on 31/10/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
public final class BodyCodecs {

    private static final Logger log = LoggerFactory.getLogger(BodyCodecs.class);

    public static <T> BodyCodec<T> jsonCatching(Class<T> type) {
        return new BodyCodecImpl<>(b -> {
            try {
                return Json.decodeValue(b.toString(), type);
            } catch (DecodeException exc) {
                log.warn("Error decoding message: {}", exc.getMessage());
                log.warn("Original message:\n{}", b.toString());
                throw new HttpStatusException(
                        500,
                        "Error during communication with Grafana"
                );
            }
        });
    }
}
