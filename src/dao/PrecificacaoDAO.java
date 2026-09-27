package dao;

import database.FabricaConexao;
import model.Precificacao;
import model.Produto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PrecificacaoDAO {

    public void cadastrar(Precificacao prec) throws ClassNotFoundException, SQLException {
        Connection con = FabricaConexao.getConexao();
        PreparedStatement comando = con.prepareStatement(
                "insert into precificacoes (id_produto, valor_venda, custo_produto, lucro) values (?, ?, ?, ?)");
        comando.setInt(1, prec.getIdProduto());
        comando.setDouble(2, prec.getValorVenda());
        comando.setDouble(3, prec.getCustoProduto());
        comando.setDouble(4, prec.getLucro());
        comando.execute();
        con.close();
    }

    public void atualizar(Precificacao prec) throws ClassNotFoundException, SQLException {
        Connection con = FabricaConexao.getConexao();
        PreparedStatement comando = con.prepareStatement(
                "update precificacoes set valor_venda = ?, custo_produto = ?, lucro = ? where id_produto = ?");
        comando.setDouble(1, prec.getValorVenda());
        comando.setDouble(2, prec.getCustoProduto());
        comando.setDouble(3, prec.getLucro());
        comando.setInt(4, prec.getIdProduto());
        comando.execute();
        con.close();
    }

    public Precificacao consultarPorProduto(Produto prod) throws ClassNotFoundException, SQLException {
        Connection con = FabricaConexao.getConexao();
        PreparedStatement comando = con.prepareStatement(
                "select * from precificacoes where id_produto = ?");
        comando.setInt(1, prod.getId());
        ResultSet rs = comando.executeQuery();
        Precificacao prec = null;
        if (rs.next()) {
            prec = new Precificacao();
            prec.setId(rs.getInt("id"));
            prec.setIdProduto(rs.getInt("id_produto"));
            prec.setValorVenda(rs.getDouble("valor_venda"));
            prec.setCustoProduto(rs.getDouble("custo_produto"));
            prec.setLucro(rs.getDouble("lucro"));
        }
        con.close();
        return prec;
    }
}
