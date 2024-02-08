package com.ufpa.lafocabackend.domain.exception;

import com.ufpa.lafocabackend.domain.enums.ErrorMessage;

public class PasswordDoesNotMachException extends RuntimeException {

    public PasswordDoesNotMachException() {
        super(ErrorMessage.SENHA_NAO_COINCIDE.get());
    }
}
