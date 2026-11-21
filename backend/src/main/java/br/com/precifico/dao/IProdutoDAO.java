package br.com.precifico.dao;

import br.com.precifico.model.Produto;
import br.com.precifico.model.Vendedor;

import java.sql.SQLException;
import java.util.List;

public interface IProdutoDAO {
    void cadastrar(Produto produto) throws ClassNotFoundException, SQLException;

    void atualizar(Produto produto) throws ClassNotFoundException, SQLException;

    void alterarSituacao(Produto produto) throws ClassNotFoundException, SQLException;

    void deletar(Produto produto) throws ClassNotFoundException, SQLException;

    Produto consultarPorId(Produto produto) throws ClassNotFoundException, SQLException;

    List<Produto> consultarPorVendedor(Vendedor vendedor) throws ClassNotFoundException, SQLException;
}
