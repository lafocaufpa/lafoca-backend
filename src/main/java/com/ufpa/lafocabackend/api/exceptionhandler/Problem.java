package com.ufpa.lafocabackend.api.exceptionhandler;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.List;

/*Classe criada para representar a mensagem de erro da exceção de acordo com a RFC 7807*/
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Problem {

    private Integer status;

    private OffsetDateTime timeStamp;

    private String path;

    private String title;

    private String detail;

    private String userMessage;

    private List<Field> fields;

    @Getter
    @Builder
    public static class Field{
        private String name;
        private String userMessage;
    }
}
