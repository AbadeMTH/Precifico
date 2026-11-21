package br.com.precifico.dao;

import br.com.precifico.database.IConexaoFactory;
import br.com.precifico.model.Precificacao;
import br.com.precifico.model.Produto;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class PrecificacaoDAO implements IPrecificacaoDAO {

    private final IConexaoFactory conexaoFactory;

    public PrecificacaoDAO(IConexaoFactory conexaoFactory) {
        this.conexaoFactory = conexaoFactory;
    }

    @Override
    public void cadastrar(Precificacao precificacao) throws ClassNotFoundException, SQLException {
        Connection conexao = conexaoFactory.getConexao();
        PreparedStatement comando = conexao.prepareStatement(
                "insert into precificacoes (id_produto, valor_venda, custo_produto, lucro) values (?, ?, ?, ?)");
        comando.setInt(1, precificacao.getIdProduto());
        comando.setDouble(2, precificacao.getValorVenda());
        comando.setDouble(3, precificacao.getCustoProduto());
        comando.setDouble(4, precificacao.getLucro());
        comando.execute();
        conexao.close();
    }

    @Override
    public void atualizar(Precificacao precificacao) throws ClassNotFoundException, SQLException {
        Connection conexao = conexaoFactory.getConexao();
        PreparedStatement comando = conexao.prepareStatement(
                "update precificacoes set valor_venda = ?, custo_produto = ?, lucro = ? where id_produto = ?");
        comando.setDouble(1, precificacao.getValorVenda());
        comando.setDouble(2, precificacao.getCustoProduto());
        comando.setDouble(3, precificacao.getLucro());
        comando.setInt(4, precificacao.getIdProduto());
        comando.execute();
        conexao.close();
    }

    @Override
    public Precificacao consultarPorProduto(Produto produto) throws ClassNotFoundException, SQLException {
        Connection conexao = conexaoFactory.getConexao();
        PreparedStatement comando = conexao.prepareStatement(
                "select * from precificacoes where id_produto = ?");
        comando.setInt(1, produto.getId());
        ResultSet resultado = comando.executeQuery();

        Precificacao precificacao = null;
        if (resultado.next()) {
            precificacao = mapearPrecificacao(resultado);
        }

        conexao.close();
        return precificacao;
    }

    private Precificacao mapearPrecificacao(ResultSet resultado) throws SQLException {
        return new Precificacao(
                resultado.getInt("id"),
                resultado.getInt("id_produto"),
                resultado.getDouble("valor_venda"),
                resultado.getDouble("custo_produto"),
                resultado.getDouble("lucro")
        );
    }
}
