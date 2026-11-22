package br.com.precifico.service;

import br.com.precifico.dto.PromocaoRequisicao;
import br.com.precifico.dto.RespostaApi;
import br.com.precifico.model.Vendedor;

public interface IPromocaoService {
    RespostaApi aplicarPromocao(int idProduto, PromocaoRequisicao dados, Vendedor vendedorLogado) throws Exception;

    RespostaApi removerPromocao(int idProduto, Vendedor vendedorLogado) throws Exception;
}
