package br.com.precifico.decorator;

import br.com.precifico.model.TipoBrinde;

import java.util.List;

public interface ProdutoDecoravel {
    String getDescricao();

    List<TipoBrinde> getBrindes();
}
