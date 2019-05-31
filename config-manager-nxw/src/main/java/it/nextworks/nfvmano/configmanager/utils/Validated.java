package it.nextworks.nfvmano.configmanager.utils;

import io.vertx.ext.web.api.validation.ValidationException;

import java.util.Optional;

/**
 * Created by Marco Capitani on 05/04/19.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
public interface Validated {

    public Optional<ValidationException> validate();
}
