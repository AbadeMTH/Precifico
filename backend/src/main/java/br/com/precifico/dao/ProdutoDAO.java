package br.com.precifico.dao;

import br.com.precifico.builder.ProdutoBuilder;
import br.com.precifico.database.IConexaoFactory;
import br.com.precifico.model.Produto;
import br.com.precifico.model.SituacaoProduto;
import br.com.precifico.model.Vendedor;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProdutoDAO implements IProdutoDAO {

    private final IConexaoFactory conexaoFactory;

    public ProdutoDAO(IConexaoFactory conexaoFactory) {
        this.conexaoFactory = conexaoFactory;
    }

    @Override
    public void cadastrar(Produto produto) throws ClassNotFoundException, SQLException {
        Connection conexao = conexaoFactory.getConexao();
        PreparedStatement comando = conexao.prepareStatement(
                "insert into produtos (codigo_produto, nome, descricao, imagem, situacao, id_vendedor) values (?, ?, ?, ?, ?, ?)");
        comando.setString(1, produto.getCodigoProduto());
        comando.setString(2, produto.getNome());
        comando.setString(3, produto.getDescricao());
        comando.setString(4, produto.getImagem());
        comando.setString(5, produto.obterNomeSituacao());
        comando.setInt(6, produto.obterIdVendedor());
        comando.execute();

        PreparedStatement comandoId = conexao.prepareStatement("select max(id) as id from produtos");
        ResultSet resultado = comandoId.executeQuery();
        resultado.next();
        produto.setId(resultado.getInt("id"));
        conexao.close();
    }

    @Override
    public void atualizar(Produto produto) throws ClassNotFoundException, SQLException {
        Connection conexao = conexaoFactory.getConexao();
        PreparedStatement comando = conexao.prepareStatement(
                "update produtos set codigo_produto = ?, nome = ?, descricao = ?, imagem = ?, situacao = ? where id = ?");
        comando.setString(1, produto.getCodigoProduto());
        comando.setString(2, produto.getNome());
        comando.setString(3, produto.getDescricao());
        comando.setString(4, produto.getImagem());
        comando.setString(5, produto.obterNomeSituacao());
        comando.setInt(6, produto.getId());
        comando.execute();
        conexao.close();
    }

    @Override
    public void alterarSituacao(Produto produto) throws ClassNotFoundException, SQLException {
        Connection conexao = conexaoFactory.getConexao();
        PreparedStatement comando = conexao.prepareStatement("update produtos set situacao = ? where id = ?");
        comando.setString(1, produto.obterNomeSituacao());
        comando.setInt(2, produto.getId());
        comando.execute();
        conexao.close();
    }

    @Override
    public void deletar(Produto produto) throws ClassNotFoundException, SQLException {
        Connection conexao = conexaoFactory.getConexao();
        PreparedStatement comando = conexao.prepareStatement("delete from produtos where id = ?");
        comando.setInt(1, produto.getId());
        comando.execute();
        conexao.close();
    }

    @Override
    public Produto consultarPorId(Produto produtoFiltro) throws ClassNotFoundException, SQLException {
        Connection conexao = conexaoFactory.getConexao();
        PreparedStatement comando = conexao.prepareStatement("select * from produtos where id = ?");
        comando.setInt(1, produtoFiltro.getId());
        ResultSet resultado = comando.executeQuery();

        Produto produto = null;
        if (resultado.next()) {
            produto = mapearProduto(resultado);
        }

        conexao.close();
        return produto;
    }

    @Override
    public List<Produto> consultarPorVendedor(Vendedor vendedor) throws ClassNotFoundException, SQLException {
        Connection conexao = conexaoFactory.getConexao();
        PreparedStatement comando = conexao.prepareStatement(
                "select * from produtos where id_vendedor = ? order by id desc");
        comando.setInt(1, vendedor.getId());
        ResultSet resultado = comando.executeQuery();

        List<Produto> listaProdutos = new ArrayList<>();
        while (resultado.next()) {
            listaProdutos.add(mapearProduto(resultado));
        }

        conexao.close();
        return listaProdutos;
    }

    private Produto mapearProduto(ResultSet resultado) throws SQLException {
        Vendedor vendedor = new Vendedor();
        vendedor.setId(resultado.getInt("id_vendedor"));

        return new ProdutoBuilder()
                .comId(resultado.getInt("id"))
                .comCodigoProduto(resultado.getString("codigo_produto"))
                .comNome(resultado.getString("nome"))
                .comDescricao(resultado.getString("descricao"))
                .comImagem(resultado.getString("imagem"))
                .comSituacao(SituacaoProduto.deTexto(resultado.getString("situacao")))
                .comVendedor(vendedor)
                .constroi();
    }
}
