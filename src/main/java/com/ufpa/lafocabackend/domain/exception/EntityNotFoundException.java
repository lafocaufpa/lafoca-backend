package com.ufpa.lafocabackend.domain.exception;

import com.ufpa.lafocabackend.domain.enums.ErrorMessage;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException (String message, String id){
        super(String.format(ErrorMessage.ENTIDADE_NOT_FOUND.get(), message, id));
    }

    public EntityNotFoundException(String message, Throwable cause){
        super(message, cause);
    }
}
