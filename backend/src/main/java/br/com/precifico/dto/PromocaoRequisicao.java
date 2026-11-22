package br.com.precifico.dto;

import java.math.BigDecimal;

public class PromocaoRequisicao {

    private BigDecimal valorPromocional;
    private BigDecimal percentualDesconto;

    public BigDecimal getValorPromocional() {
        return valorPromocional;
    }

    public void setValorPromocional(BigDecimal valorPromocional) {
        this.valorPromocional = valorPromocional;
    }

    public BigDecimal getPercentualDesconto() {
        return percentualDesconto;
    }

    public void setPercentualDesconto(BigDecimal percentualDesconto) {
        this.percentualDesconto = percentualDesconto;
    }
}
