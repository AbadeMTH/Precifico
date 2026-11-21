package br.com.precifico.service;

import br.com.precifico.dto.ProdutoRequisicao;
import br.com.precifico.dto.RespostaApi;
import br.com.precifico.model.Vendedor;

public interface IProdutoService {
    RespostaApi listarTodos(Vendedor vendedorLogado) throws Exception;

    RespostaApi consultarPorId(int idProduto, Vendedor vendedorLogado) throws Exception;

    RespostaApi cadastrar(ProdutoRequisicao dados, Vendedor vendedorLogado) throws Exception;

    RespostaApi atualizar(int idProduto, ProdutoRequisicao dados, Vendedor vendedorLogado) throws Exception;

    RespostaApi alterarSituacao(int idProduto, Vendedor vendedorLogado) throws Exception;

    RespostaApi deletar(int idProduto, Vendedor vendedorLogado) throws Exception;
}
