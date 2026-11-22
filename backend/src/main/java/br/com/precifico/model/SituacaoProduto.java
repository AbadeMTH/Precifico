package br.com.precifico.model;

public enum SituacaoProduto {
    DISPONIVEL,
    VENDIDO;

    public SituacaoProduto alternar() {
        if (this == VENDIDO) {
            return DISPONIVEL;
        }
        return VENDIDO;
    }

    public static SituacaoProduto deTexto(String texto) {
        if (texto != null && VENDIDO.name().equalsIgnoreCase(texto.trim())) {
            return VENDIDO;
        }
        return DISPONIVEL;
    }
}
