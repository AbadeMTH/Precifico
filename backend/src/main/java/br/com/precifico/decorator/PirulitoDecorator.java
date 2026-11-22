package br.com.precifico.decorator;

import br.com.precifico.model.TipoBrinde;

import java.util.ArrayList;
import java.util.List;

public class PirulitoDecorator extends BrindeDecorator {

    public PirulitoDecorator(ProdutoDecoravel produtoDecorado) {
        super(produtoDecorado);
    }

    @Override
    public String getDescricao() {
        return produtoDecorado.getDescricao() + ", Brinde: Pirulito";
    }

    @Override
    public List<TipoBrinde> getBrindes() {
        List<TipoBrinde> brindes = new ArrayList<>(produtoDecorado.getBrindes());
        if (!brindes.contains(TipoBrinde.PIRULITO)) {
            brindes.add(TipoBrinde.PIRULITO);
        }
        return brindes;
    }
}
