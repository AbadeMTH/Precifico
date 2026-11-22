package br.com.precifico.dao;

import br.com.precifico.model.TipoBrinde;

import java.sql.SQLException;
import java.util.List;

public interface IProdutoBrindeDAO {
    void salvarBrindes(int idProduto, List<TipoBrinde> brindes) throws ClassNotFoundException, SQLException;

    void substituirBrindes(int idProduto, List<TipoBrinde> brindes) throws ClassNotFoundException, SQLException;

    List<TipoBrinde> consultarPorProduto(int idProduto) throws ClassNotFoundException, SQLException;
}
