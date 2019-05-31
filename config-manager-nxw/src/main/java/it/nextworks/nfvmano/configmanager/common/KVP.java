package it.nextworks.nfvmano.configmanager.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.ext.web.api.validation.ValidationException;
import it.nextworks.nfvmano.configmanager.utils.Validated;

import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

/**
 * Created by Marco Capitani on 05/04/19.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
public class KVP implements Validated {

    public Optional<ValidationException> validate() {
        if (key == null) {
            return Optional.of(new ValidationException("KVP: key cannot be null"));
        }
        if (key.contains("-")) {
            return Optional.of(new ValidationException("KVP: key cannot contain dash ('-')"));
        }
        if (value == null) {
            return Optional.of(new ValidationException("KVP: value cannot be null"));
        }
        return Optional.empty();
    }

    @JsonProperty("key")
    private String key = null;

    @JsonProperty("value")
    private String value = null;

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KVP)) return false;
        KVP kvp = (KVP) o;
        return Objects.equals(getKey(), kvp.getKey()) &&
                Objects.equals(getValue(), kvp.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), getValue());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", KVP.class.getSimpleName() + "[", "]")
                .add("\"" + key + "\":")
                .add("\"" + value + "\"")
                .toString();
    }
}
