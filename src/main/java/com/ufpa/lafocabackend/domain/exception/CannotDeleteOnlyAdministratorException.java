package com.ufpa.lafocabackend.domain.exception;

public class CannotDeleteOnlyAdministratorException extends RuntimeException{

    public CannotDeleteOnlyAdministratorException(String message) {
        super(message);
    }
}
