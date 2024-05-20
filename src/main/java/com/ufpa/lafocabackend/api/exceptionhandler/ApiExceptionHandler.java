package com.ufpa.lafocabackend.api.exceptionhandler;

import com.nimbusds.jose.proc.BadJWSException;
import com.ufpa.lafocabackend.domain.enums.ErrorMessage;
import com.ufpa.lafocabackend.domain.exception.*;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.OffsetDateTime;
import java.util.List;

@RestControllerAdvice
@ComponentScan
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class) /*também subclasses*/
    public ResponseEntity<?> handleEntityNotFoundException(Exception ex, WebRequest request) {
        HttpStatus statusNotFound = HttpStatus.NOT_FOUND;
        ProblemType problemType = ProblemType.RECURSO_NAO_ENCONTRADO;
        final Problem problem = createProblemType(statusNotFound, problemType, ex.getMessage()).userMessage(ex.getMessage()).build();

        return handleExceptionInternal(ex, problem, new HttpHeaders(), statusNotFound, request);
    }

    @ExceptionHandler(JwtValidationException.class) /*também subclasses*/
    public ResponseEntity<?> handleTokenExpired(Exception ex, WebRequest request) {
        HttpStatus unauthorized = HttpStatus.UNAUTHORIZED;
        ProblemType problemType = ProblemType.TOKEN_EXPIRADO;
        String userMessage = "O tempo de validade do token expirou. Por favor, renove suas credenciais para continuar acessando o sistema.";

        final Problem problem = createProblemType(unauthorized, problemType, ex.getMessage()).userMessage(userMessage).build();

        return handleExceptionInternal(ex, problem, new HttpHeaders(), unauthorized, request);
    }

    @ExceptionHandler(BadJWSException.class) /*também subclasses*/
    public ResponseEntity<?> handleTokenInvalid(Exception ex, WebRequest request) {
        HttpStatus unauthorized = HttpStatus.UNAUTHORIZED;
        ProblemType problemType = ProblemType.TOKEN_EXPIRADO;
        String userMessage = "Assinatura JWT inválida: assinatura inválida";

        final Problem problem = createProblemType(unauthorized, problemType, ex.getMessage()).userMessage(userMessage).build();

        return handleExceptionInternal(ex, problem, new HttpHeaders(), unauthorized, request);
    }

    @ExceptionHandler(BadCredentialsException.class) /*também subclasses*/
    public ResponseEntity<?> handleBadCredentialsException(Exception ex, WebRequest request) {
        HttpStatus unauthorized = HttpStatus.UNAUTHORIZED;
        ProblemType problemType = ProblemType.ACESSO_NEGADO;
        String userMessage = ex.getMessage();

        final Problem problem = createProblemType(unauthorized, problemType, ex.getMessage()).userMessage(userMessage).build();

        return handleExceptionInternal(ex, problem, new HttpHeaders(), unauthorized, request);
    }

    @ExceptionHandler(EntityAlreadyRegisteredException.class)
    public ResponseEntity<?> handleEntityAlreadyRegisteredException(Exception ex, WebRequest request) {
        HttpStatus statusNotFound = HttpStatus.BAD_REQUEST;
        ProblemType problemType = ProblemType.ERRO_NEGOCIO;
        final Problem problem = createProblemType(statusNotFound, problemType, ex.getMessage()).userMessage(ex.getMessage()).build();

        return handleExceptionInternal(ex, problem, new HttpHeaders(), statusNotFound, request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(Exception ex, WebRequest request) {
        HttpStatus statusNotFound = HttpStatus.FORBIDDEN;
        ProblemType problemType = ProblemType.ACESSO_NEGADO;
        final Problem problem = createProblemType(statusNotFound, problemType, ex.getMessage()).userMessage(ex.getMessage()).build();

        return handleExceptionInternal(ex, problem, new HttpHeaders(), statusNotFound, request);
    }

    @ExceptionHandler(EntityInUseException.class)
    public ResponseEntity<?> handleEntityInUseException(Exception ex, WebRequest request) {
        HttpStatus statusNotFound = HttpStatus.CONFLICT;
        ProblemType problemType = ProblemType.ENTIDADE_EM_USO;
        final Problem problem = createProblemType(statusNotFound, problemType, ex.getMessage()).userMessage(ex.getMessage()).build();

        return handleExceptionInternal(ex, problem, new HttpHeaders(), statusNotFound, request);
    }

    @ExceptionHandler(PasswordDoesNotMachException.class)
    public ResponseEntity<?> handlePasswordDoesNotMachException(Exception ex, WebRequest request) {
        HttpStatus statusNotFound = HttpStatus.UNPROCESSABLE_ENTITY;
        ProblemType problemType = ProblemType.ERRO_NEGOCIO;
        final Problem problem = createProblemType(statusNotFound, problemType, "")
                .userMessage(ex.getMessage())
                .build();

        return handleExceptionInternal(ex, problem, new HttpHeaders(), statusNotFound, request);
    }

    @ExceptionHandler(InvalidFileException.class)
    public ResponseEntity<?> handleInvalidFileException(Exception ex, WebRequest request) {

        HttpStatus unprocessableEntity = HttpStatus.UNPROCESSABLE_ENTITY;
        ProblemType problemType = ProblemType.ARQUIVO_INVALIDO;
        final Problem problem = createProblemType(unprocessableEntity, problemType, ErrorMessage.PROPRIEDADES_INVALIDAS.get())
                .userMessage(ex.getMessage())
                .build();

        return handleExceptionInternal(ex, problem, new HttpHeaders(), unprocessableEntity, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        final List<Problem.Field> problemFieldErros = ex.getFieldErrors()
                .stream()
                .map(fd -> Problem.Field.builder()
                        .name(fd.getField())
                        .userMessage(fd.getDefaultMessage())
                        .build())
                .toList();

        ProblemType problemType = ProblemType.DADOS_INVALIDOS;
        String detail = "Um ou mais campos estão inválidos. Faça o preenchimento correto e tente novamente.";

        Problem problem = createProblemType(status, problemType, detail)
                .userMessage(detail)
                .fields(problemFieldErros)
                .build();

        return handleExceptionInternal(ex, problem, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        return super.handleExceptionInternal(ex, body, headers, status, request);
    }

        private Problem.ProblemBuilder createProblemType (HttpStatusCode status, ProblemType problemType, String detail){

        return Problem.builder()
                .status(status.value())
                .path(problemType.getUri())
                .title(problemType.getTitle())
                .timeStamp(OffsetDateTime.now())
                .detail(detail);
    }
}