package com.ufpa.lafocabackend.domain.enums;

/**
 * Constantes de String que representam a mensagem de erro de exceptions.
 * O método get() retorna a string que é utilizada em um
 * String.format().
 * O 1º argumento recebe um valor em String,
 * o 2º argumento recebe um valor númerico inteiro (Long, int, Integer).
 * Ex: String.format(ENTIDADE_NOT_FOUND.get(), entidade, entidadeId)
 * -> A entidade Entidade de codigo x não pôde ser encontrada.
 */
public enum ErrorMessage {
    ENTIDADE_NOT_FOUND("A entidade %s de código %s não foi encontrada."),
    ENTIDADE_EM_USO("A entidade %s de código %d não pode ser excluída, pois está em uso."),
    SENHA_NAO_COINCIDE("Senha atual informada não coincide com a senha do usuário."),
    ENTIDADE_EXISTENTE("Entidade %s de identificação %s já existe."),
    PERMISSAO_NAO_ENCONTRADA("Não existe permissão de código %d."),
    ACESSO_NEGADO("Credenciais inválidas."),
    PROPRIEDADES_INVALIDAS("Propriedades Inválidas"),
    ARQUIVO_VAZIO("O arquivo não pode ser nulo ou vazio."),
    NOME_ARQUIVO_INVALIDO("O arquivo deve ter um nome e uma extensão."),
    TIPO_NAO_PERMITIDO("Extensão de arquivo não permitida. Apenas .jpg, .png, .svg, .jpeg são aceitas."),
    TAMANHO_INVALIDO("O arquivo deve ter entre 20KB e 5MB");
    private String mensagem;

    private ErrorMessage(String mensagem){
        this.mensagem = mensagem;
    }

    public String get() {
        return mensagem;
    }
}