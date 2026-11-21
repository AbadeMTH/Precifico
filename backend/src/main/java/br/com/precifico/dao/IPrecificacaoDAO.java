package br.com.precifico.dao;

import br.com.precifico.model.Precificacao;
import br.com.precifico.model.Produto;

import java.sql.SQLException;

public interface IPrecificacaoDAO {
    void cadastrar(Precificacao precificacao) throws ClassNotFoundException, SQLException;

    void atualizar(Precificacao precificacao) throws ClassNotFoundException, SQLException;

    Precificacao consultarPorProduto(Produto produto) throws ClassNotFoundException, SQLException;
}
