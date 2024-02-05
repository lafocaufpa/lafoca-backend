package com.ufpa.lafocabackend.api.exceptionhandler;

import lombok.Getter;

/**
 * Enum utilizada para compor o corpo da exceção em formalidade com a RFC 7807
 */
@Getter
public enum ProblemType {

    RECURSO_NAO_ENCONTRADO("/recurso-nao-encontrado", "Recurso não encontrado");
//    ,
//    ENTIDADE_EM_USO("/entidade-em-uso", "Entidade em uso"),
//    ERRO_NEGOCIO("/erro-negocio", "Violação de regra de negócio"),
//    CORPO_NAO_LEGIVEL("/corpo-nao-legivel", "Corpo não legível"),
//    PARAMETRO_INVALIDO("/parametro-invalido", "parametro inválido"),
//    ERRO_DE_SISTEMA("/erro-de-sistema","Erro de interno do sistema."),
//    DADOS_INVALIDOS("/dados-invalidos", "Dados inválidos"),
//    ACESSO_NEGADO("/acesso-negado", "Acesso negado");

    private String title;
    private String uri;

    ProblemType(String path, String title){
        this.uri = "http://localhost:8080" + path;
        this.title = title;

    }
}
