package br.com.precifico.decorator;

import br.com.precifico.model.TipoBrinde;

import java.util.ArrayList;
import java.util.List;

public class AdesivoDecorator extends BrindeDecorator {

    public AdesivoDecorator(ProdutoDecoravel produtoDecorado) {
        super(produtoDecorado);
    }

    @Override
    public String getDescricao() {
        return produtoDecorado.getDescricao() + ", Brinde: Adesivo";
    }

    @Override
    public List<TipoBrinde> getBrindes() {
        List<TipoBrinde> brindes = new ArrayList<>(produtoDecorado.getBrindes());
        if (!brindes.contains(TipoBrinde.ADESIVO)) {
            brindes.add(TipoBrinde.ADESIVO);
        }
        return brindes;
    }
}
