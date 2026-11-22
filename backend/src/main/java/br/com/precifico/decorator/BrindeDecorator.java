package br.com.precifico.decorator;

import br.com.precifico.model.TipoBrinde;

import java.util.List;

public abstract class BrindeDecorator implements ProdutoDecoravel {

    protected ProdutoDecoravel produtoDecorado;

    public BrindeDecorator(ProdutoDecoravel produtoDecorado) {
        this.produtoDecorado = produtoDecorado;
    }

    @Override
    public String getDescricao() {
        return produtoDecorado.getDescricao();
    }

    @Override
    public List<TipoBrinde> getBrindes() {
        return produtoDecorado.getBrindes();
    }
}
