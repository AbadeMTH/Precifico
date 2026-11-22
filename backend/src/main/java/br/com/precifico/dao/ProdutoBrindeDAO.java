package br.com.precifico.dao;

import br.com.precifico.database.IConexaoFactory;
import br.com.precifico.model.TipoBrinde;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProdutoBrindeDAO implements IProdutoBrindeDAO {

    private final IConexaoFactory conexaoFactory;

    public ProdutoBrindeDAO(IConexaoFactory conexaoFactory) {
        this.conexaoFactory = conexaoFactory;
    }

    @Override
    public void salvarBrindes(int idProduto, List<TipoBrinde> brindes) throws ClassNotFoundException, SQLException {
        if (brindes == null || brindes.isEmpty()) {
            return;
        }

        try (Connection conexao = conexaoFactory.getConexao()) {
            inserirBrindes(conexao, idProduto, brindes);
        }
    }

    @Override
    public void substituirBrindes(int idProduto, List<TipoBrinde> brindes) throws ClassNotFoundException, SQLException {
        try (Connection conexao = conexaoFactory.getConexao()) {
            conexao.setAutoCommit(false);
            try {
                try (PreparedStatement comandoExclusao = conexao.prepareStatement(
                        "delete from produto_brindes where id_produto = ?")) {
                    comandoExclusao.setInt(1, idProduto);
                    comandoExclusao.executeUpdate();
                }

                if (brindes != null && !brindes.isEmpty()) {
                    inserirBrindes(conexao, idProduto, brindes);
                }

                conexao.commit();
            } catch (SQLException erro) {
                conexao.rollback();
                throw erro;
            }
        }
    }

    @Override
    public List<TipoBrinde> consultarPorProduto(int idProduto) throws ClassNotFoundException, SQLException {
        List<TipoBrinde> brindes = new ArrayList<>();

        try (Connection conexao = conexaoFactory.getConexao();
             PreparedStatement comando = conexao.prepareStatement(
                     "select tipo_brinde from produto_brindes where id_produto = ? order by id asc")) {
            comando.setInt(1, idProduto);

            try (ResultSet resultado = comando.executeQuery()) {
                while (resultado.next()) {
                    TipoBrinde tipoBrinde = TipoBrinde.deTexto(resultado.getString("tipo_brinde"));
                    if (tipoBrinde != null && !brindes.contains(tipoBrinde)) {
                        brindes.add(tipoBrinde);
                    }
                }
            }
        }

        return brindes;
    }

    private void inserirBrindes(Connection conexao, int idProduto, List<TipoBrinde> brindes) throws SQLException {
        try (PreparedStatement comandoInsercao = conexao.prepareStatement(
                "insert into produto_brindes (id_produto, tipo_brinde) values (?, ?)")) {
            for (TipoBrinde brinde : brindes) {
                if (brinde == null) {
                    continue;
                }
                comandoInsercao.setInt(1, idProduto);
                comandoInsercao.setString(2, brinde.name());
                comandoInsercao.executeUpdate();
            }
        }
    }
}
