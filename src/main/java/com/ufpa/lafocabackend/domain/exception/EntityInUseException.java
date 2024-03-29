package com.ufpa.lafocabackend.domain.exception;

import com.ufpa.lafocabackend.domain.enums.ErrorMessage;

public class EntityInUseException extends RuntimeException {

    public EntityInUseException (String message, String id){
        super(String.format(ErrorMessage.ENTIDADE_EM_USO.get(), message, id));
    }
}
