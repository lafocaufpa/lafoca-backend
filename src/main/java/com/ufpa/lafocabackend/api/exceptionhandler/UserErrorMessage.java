package com.ufpa.lafocabackend.api.exceptionhandler;

/**
 * Constantes de String que representam a mensagem de erro de exceptions.
 * O método get() retorna a string que é utilizada em um
 * String.format().
 * O 1º argumento recebe um valor em String,
 * o 2º argumento recebe um valor númerico inteiro (Long, int, Integer).
 * Ex: String.format(ENTIDADE_NOT_FOUND.get(), entidade, entidadeId)
 * -> A entidade Entidade de codigo x não pôde ser encontrada.
 */
public enum UserErrorMessage {
    USUARIO_EXISTENTE("Usuário existente.");
    private String mensagem;

    UserErrorMessage(String mensagem){
        this.mensagem = mensagem;
    }

    public String get() {
        return mensagem;
    }
}