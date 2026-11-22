package br.com.precifico.decorator;

import br.com.precifico.model.TipoBrinde;

import java.util.ArrayList;
import java.util.List;

public class BalaDecorator extends BrindeDecorator {

    public BalaDecorator(ProdutoDecoravel produtoDecorado) {
        super(produtoDecorado);
    }

    @Override
    public String getDescricao() {
        return produtoDecorado.getDescricao() + ", Brinde: Bala";
    }

    @Override
    public List<TipoBrinde> getBrindes() {
        List<TipoBrinde> brindes = new ArrayList<>(produtoDecorado.getBrindes());
        if (!brindes.contains(TipoBrinde.BALA)) {
            brindes.add(TipoBrinde.BALA);
        }
        return brindes;
    }
}
