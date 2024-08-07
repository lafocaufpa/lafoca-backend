package com.ufpa.lafocabackend.domain.exception;

import com.ufpa.lafocabackend.domain.enums.ErrorMessage;

public class EmailException extends RuntimeException {
    public EmailException(Exception e) {
        super(ErrorMessage.FALHA_EMAIL.get(), e);
    }
}
