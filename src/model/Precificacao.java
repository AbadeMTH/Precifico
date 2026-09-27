package model;

public class Precificacao {
    private int id;
    private int idProduto;
    private double valorVenda;
    private double custoProduto;
    private double lucro;

    public Precificacao() {
    }

    public Precificacao(int id, int idProduto, double valorVenda, double custoProduto, double lucro) {
        this.id = id;
        this.idProduto = idProduto;
        this.valorVenda = valorVenda;
        this.custoProduto = custoProduto;
        this.lucro = lucro;
    }

    public Precificacao(double valorVenda, double custoProduto) {
        this.valorVenda = valorVenda;
        this.custoProduto = custoProduto;
        this.lucro = calcularLucro();
    }

    // Métodos de instância para cálculo e regras de negócio
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
        if (diferencaPercentual < -20.0) {
            return "Atenção: o preço está muito abaixo da sugestão.";
        } else if (diferencaPercentual >= -20.0 && diferencaPercentual < -5.0) {
            return "O preço está abaixo da sugestão.";
        } else if (diferencaPercentual >= -5.0 && diferencaPercentual <= 5.0) {
            return "O preço está próximo do valor sugerido.";
        } else if (diferencaPercentual > 5.0 && diferencaPercentual <= 20.0) {
            return "O preço está acima da sugestão.";
        } else {
            return "Atenção: o preço está muito acima da sugestão.";
        }
    }

    public void recalcularLucro() {
        this.lucro = calcularLucro();
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
}
