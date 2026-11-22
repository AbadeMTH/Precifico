package br.com.precifico.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Precificacao {
    private static final BigDecimal CEM = new BigDecimal("100");

    private int id;
    private int idProduto;
    private double valorVenda;
    private double custoProduto;
    private double lucro;
    private BigDecimal valorPromocional;
    private BigDecimal percentualDesconto;

    public Precificacao() {
    }

    public Precificacao(int id, int idProduto, double valorVenda, double custoProduto, double lucro) {
        this(id, idProduto, valorVenda, custoProduto, lucro, null, null);
    }

    public Precificacao(
            int id,
            int idProduto,
            double valorVenda,
            double custoProduto,
            double lucro,
            BigDecimal valorPromocional,
            BigDecimal percentualDesconto
    ) {
        this.id = id;
        this.idProduto = idProduto;
        this.valorVenda = valorVenda;
        this.custoProduto = custoProduto;
        this.lucro = lucro;
        this.valorPromocional = valorPromocional;
        this.percentualDesconto = percentualDesconto;
    }

    public Precificacao(double valorVenda, double custoProduto) {
        this.valorVenda = valorVenda;
        this.custoProduto = custoProduto;
        this.lucro = calcularLucro();
    }

    public boolean isEmPromocao() {
        if (this.valorPromocional == null || this.percentualDesconto == null) {
            return false;
        }
        BigDecimal valorOriginal = obterValorVendaDecimal();
        return this.valorPromocional.compareTo(BigDecimal.ZERO) > 0
                && this.valorPromocional.compareTo(valorOriginal) < 0
                && this.percentualDesconto.compareTo(BigDecimal.ZERO) > 0
                && this.percentualDesconto.compareTo(CEM) < 0;
    }

    public BigDecimal obterValorVendaDecimal() {
        return BigDecimal.valueOf(this.valorVenda).setScale(2, RoundingMode.HALF_UP);
    }

    public void aplicarPromocao(BigDecimal novoValorPromocional, BigDecimal novoPercentualDesconto) {
        this.valorPromocional = novoValorPromocional.setScale(2, RoundingMode.HALF_UP);
        this.percentualDesconto = novoPercentualDesconto.setScale(2, RoundingMode.HALF_UP);
    }

    public void removerPromocao() {
        this.valorPromocional = null;
        this.percentualDesconto = null;
    }

    public BigDecimal getLucroPromocional() {
        if (!isEmPromocao()) {
            return null;
        }
        BigDecimal custoDecimal = BigDecimal.valueOf(this.custoProduto).setScale(2, RoundingMode.HALF_UP);
        return this.valorPromocional.subtract(custoDecimal).setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal calcularPercentualDesconto(BigDecimal valorOriginal, BigDecimal valorPromocional) {
        return valorOriginal.subtract(valorPromocional)
                .multiply(CEM)
                .divide(valorOriginal, 2, RoundingMode.HALF_UP);
    }

    public static BigDecimal calcularValorPromocional(BigDecimal valorOriginal, BigDecimal percentualDesconto) {
        BigDecimal fator = BigDecimal.ONE.subtract(percentualDesconto.divide(CEM, 6, RoundingMode.HALF_UP));
        return valorOriginal.multiply(fator).setScale(2, RoundingMode.HALF_UP);
    }

    public double calcularLucro() {
        return Math.round((this.valorVenda - this.custoProduto) * 100.0) / 100.0;
    }

    public double calcularPrecoSugerido(double percentualLucro) {
        return Math.round((this.custoProduto * (1.0 + (percentualLucro / 100.0))) * 100.0) / 100.0;
    }

    public double getMargemEfetiva() {
        if (this.custoProduto <= 0) {
            return 0.0;
        }
        return Math.round(((this.lucro / this.custoProduto) * 100.0) * 100.0) / 100.0;
    }

    public String classificarPreco(double percentualLucroDesejado) {
        if (this.valorVenda < this.custoProduto) {
            return "Atenção: este preço resultará em prejuízo. Você pode continuar.";
        }

        double precoSugerido = calcularPrecoSugerido(percentualLucroDesejado);
        if (precoSugerido <= 0) {
            return "Preço definido.";
        }

        double diferencaPercentual = ((this.valorVenda - precoSugerido) / precoSugerido) * 100.0;
        return classificarPorDiferenca(diferencaPercentual);
    }

    private String classificarPorDiferenca(double diferencaPercentual) {
        if (diferencaPercentual < -20.0) {
            return "Atenção: o preço está muito abaixo da sugestão.";
        }
        if (diferencaPercentual < -5.0) {
            return "O preço está abaixo da sugestão.";
        }
        if (diferencaPercentual <= 5.0) {
            return "O preço está próximo do valor sugerido.";
        }
        if (diferencaPercentual <= 20.0) {
            return "O preço está acima da sugestão.";
        }
        return "Atenção: o preço está muito acima da sugestão.";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(int idProduto) {
        this.idProduto = idProduto;
    }

    public double getValorVenda() {
        return valorVenda;
    }

    public void setValorVenda(double valorVenda) {
        this.valorVenda = valorVenda;
    }

    public double getCustoProduto() {
        return custoProduto;
    }

    public void setCustoProduto(double custoProduto) {
        this.custoProduto = custoProduto;
    }

    public double getLucro() {
        return lucro;
    }

    public void setLucro(double lucro) {
        this.lucro = lucro;
    }

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
