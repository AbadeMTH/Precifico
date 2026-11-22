package br.com.precifico.dao;

import br.com.precifico.database.IConexaoFactory;
import br.com.precifico.model.Precificacao;
import br.com.precifico.model.Produto;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

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
                "insert into precificacoes (id_produto, valor_venda, custo_produto, lucro, valor_promocional, percentual_desconto) values (?, ?, ?, ?, ?, ?)");
        comando.setInt(1, precificacao.getIdProduto());
        comando.setDouble(2, precificacao.getValorVenda());
        comando.setDouble(3, precificacao.getCustoProduto());
        comando.setDouble(4, precificacao.getLucro());
        definirDecimalOpcional(comando, 5, precificacao.getValorPromocional());
        definirDecimalOpcional(comando, 6, precificacao.getPercentualDesconto());
        comando.execute();
        conexao.close();
    }

    @Override
    public void atualizar(Precificacao precificacao) throws ClassNotFoundException, SQLException {
        Connection conexao = conexaoFactory.getConexao();
        PreparedStatement comando = conexao.prepareStatement(
                "update precificacoes set valor_venda = ?, custo_produto = ?, lucro = ?, valor_promocional = ?, percentual_desconto = ? where id_produto = ?");
        comando.setDouble(1, precificacao.getValorVenda());
        comando.setDouble(2, precificacao.getCustoProduto());
        comando.setDouble(3, precificacao.getLucro());
        definirDecimalOpcional(comando, 4, precificacao.getValorPromocional());
        definirDecimalOpcional(comando, 5, precificacao.getPercentualDesconto());
        comando.setInt(6, precificacao.getIdProduto());
        comando.execute();
        conexao.close();
    }

    @Override
    public void atualizarPromocao(Precificacao precificacao) throws ClassNotFoundException, SQLException {
        Connection conexao = conexaoFactory.getConexao();
        PreparedStatement comando = conexao.prepareStatement(
                "update precificacoes set valor_promocional = ?, percentual_desconto = ? where id_produto = ?");
        definirDecimalOpcional(comando, 1, precificacao.getValorPromocional());
        definirDecimalOpcional(comando, 2, precificacao.getPercentualDesconto());
        comando.setInt(3, precificacao.getIdProduto());
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

    private void definirDecimalOpcional(PreparedStatement comando, int indice, BigDecimal valor) throws SQLException {
        if (valor == null) {
            comando.setNull(indice, Types.DECIMAL);
            return;
        }
        comando.setBigDecimal(indice, valor);
    }

    private Precificacao mapearPrecificacao(ResultSet resultado) throws SQLException {
        return new Precificacao(
                resultado.getInt("id"),
                resultado.getInt("id_produto"),
                resultado.getDouble("valor_venda"),
                resultado.getDouble("custo_produto"),
                resultado.getDouble("lucro"),
                resultado.getBigDecimal("valor_promocional"),
                resultado.getBigDecimal("percentual_desconto")
        );
    }
}
