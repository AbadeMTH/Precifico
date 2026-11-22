package br.com.precifico.decorator;

import br.com.precifico.model.Produto;
import br.com.precifico.model.TipoBrinde;

import java.util.ArrayList;
import java.util.List;

public class CompositorBrindes {

    public ProdutoDecoravel compor(Produto produto, List<TipoBrinde> brindes) {
        ProdutoDecoravel produtoDecorado = produto;
        if (brindes == null || brindes.isEmpty()) {
            return produtoDecorado;
        }

        for (TipoBrinde tipoBrinde : brindes) {
            if (tipoBrinde == TipoBrinde.BALA) {
                produtoDecorado = new BalaDecorator(produtoDecorado);
            } else if (tipoBrinde == TipoBrinde.PIRULITO) {
                produtoDecorado = new PirulitoDecorator(produtoDecorado);
            } else if (tipoBrinde == TipoBrinde.ADESIVO) {
                produtoDecorado = new AdesivoDecorator(produtoDecorado);
            }
        }

        return produtoDecorado;
    }

    public void aplicarNoProduto(Produto produto, List<TipoBrinde> brindes) {
        produto.setBrindes(new ArrayList<>());
        ProdutoDecoravel produtoDecorado = compor(produto, brindes);
        produto.setBrindes(produtoDecorado.getBrindes());
        produto.setDescricaoComBrindes(produtoDecorado.getDescricao());
    }
}
