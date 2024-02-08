package com.ufpa.lafocabackend.domain.exception;

import com.ufpa.lafocabackend.domain.enums.ErrorMessage;

public class EntityAlreadyRegisteredException extends RuntimeException {

    public EntityAlreadyRegisteredException (String entity, String id) {
        super(String.format(ErrorMessage.ENTIDADE_EXISTENTE.get(), entity, id));
    }
}
