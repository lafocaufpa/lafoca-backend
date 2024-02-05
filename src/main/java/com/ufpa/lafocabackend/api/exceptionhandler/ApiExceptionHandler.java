package com.ufpa.lafocabackend.api.exceptionhandler;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.OffsetDateTime;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(RuntimeException.class) /*também subclasses*/
    public ResponseEntity<?> handleEntidadeNaoEncontradaException(Exception ex, WebRequest request) {
        HttpStatus statusNotFound = HttpStatus.NOT_FOUND;
        ProblemType problemType = ProblemType.RECURSO_NAO_ENCONTRADO;
        final Problem problem = createProblemType(statusNotFound, problemType, ex.getMessage()).userMessage(ex.getMessage()).build();

        return handleExceptionInternal(ex, problem, new HttpHeaders(), statusNotFound, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {

        return super.handleExceptionInternal(ex, body, headers, status, request);
    }

    private Problem.ProblemBuilder createProblemType (HttpStatus status, ProblemType problemType, String detail){

        return Problem.builder()
                .status(status.value())
                .type(problemType.getUri())
                .title(problemType.getTitle())
                .timeStamp(OffsetDateTime.now())
                .detail(detail);
    }
}