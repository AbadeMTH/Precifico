package br.com.precifico.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class RespostaApi {

    @JsonIgnore
    private final int status;
    private final boolean sucesso;
    private final String mensagem;
    private final Object dados;

    public RespostaApi(int status, boolean sucesso, String mensagem, Object dados) {
        this.status = status;
        this.sucesso = sucesso;
        this.mensagem = mensagem;
        this.dados = dados;
    }

    public static RespostaApi sucesso(int status, String mensagem, Object dados) {
        return new RespostaApi(status, true, mensagem, dados);
    }

    public static RespostaApi erro(int status, String mensagem) {
        return new RespostaApi(status, false, mensagem, null);
    }

    public int getStatus() {
        return status;
    }

    public boolean isSucesso() {
        return sucesso;
    }

    public String getMensagem() {
        return mensagem;
    }

    public Object getDados() {
        return dados;
    }
}
