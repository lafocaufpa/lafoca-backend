package com.ufpa.lafocabackend.api.exceptionhandler;

import lombok.Getter;

/**
 * Enum utilizada para compor o corpo da exceção em formalidade com a RFC 7807
 */
@Getter
public enum ProblemType {

    RECURSO_NAO_ENCONTRADO("recurso-nao-encontrado", "Recurso não encontrado"),
    ACESSO_NEGADO("acesso-negado", "Acesso negado"),
    ENTIDADE_EM_USO("entidade-em-uso", "Entidade em uso"),
    ERRO_NEGOCIO("erro-negocio", "Violação de regra de negócio"),
    TOKEN_EXPIRADO("token-expirado", "O token está inválido"),
    DADOS_INVALIDOS("/dados-invalidos", "Dados inválidos"),
    ARQUIVO_INVALIDO("/arquivo-invalido", "O arquivo enviado não pôde ser processado"),
    ERRO_NO_SISTEMA("/erro-interno", "Consulte o administrador do sistema.");
//    CORPO_NAO_LEGIVEL("/corpo-nao-legivel", "Corpo não legível"),
//    PARAMETRO_INVALIDO("/parametro-invalido", "parametro inválido"),
//    ERRO_DE_SISTEMA("/erro-de-sistema","Erro de interno do sistema."),
    ;

    private final String title;
    private final String uri;
    ProblemType(String path, String title){
        this.uri = path;
        this.title = title;
    }

}
