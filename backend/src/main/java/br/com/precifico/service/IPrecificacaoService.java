package br.com.precifico.service;

import br.com.precifico.dto.RespostaApi;
import br.com.precifico.dto.SimulacaoRequisicao;
import br.com.precifico.model.Produto;

import java.util.Map;

public interface IPrecificacaoService {
    RespostaApi simular(SimulacaoRequisicao requisicao);

    Map<String, Object> montarDetalhesComerciais(Produto produto);
}
