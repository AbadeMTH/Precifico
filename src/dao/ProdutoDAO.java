package dao;

import builder.ProdutoBuilder;
import database.FabricaConexao;
import model.Produto;
import model.Vendedor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {

    public void cadastrar(Produto prod) throws ClassNotFoundException, SQLException {
        Connection con = FabricaConexao.getConexao();
        PreparedStatement comando = con.prepareStatement(
                "insert into produtos (codigo_produto, nome, descricao, imagem, situacao, id_vendedor) values (?, ?, ?, ?, ?, ?)");
        comando.setString(1, prod.getCodigoProduto());
        comando.setString(2, prod.getNome());
        comando.setString(3, prod.getDescricao());
        comando.setString(4, prod.getImagem());
        comando.setString(5, prod.getSituacao() != null ? prod.getSituacao() : "DISPONIVEL");
        comando.setInt(6, prod.getVendedor() != null ? prod.getVendedor().getId() : 0);
        comando.execute();

        PreparedStatement cmdId = con.prepareStatement("select max(id) as id from produtos");
        ResultSet rs = cmdId.executeQuery();
        rs.next();
        int idGerado = rs.getInt("id");
        prod.setId(idGerado);
        con.close();

        // salva no banco a precificacao do produto
        prod.getPrecificacao().setIdProduto(idGerado);
        new PrecificacaoDAO().cadastrar(prod.getPrecificacao());
    }

    public void atualizar(Produto prod) throws ClassNotFoundException, SQLException {
        Connection con = FabricaConexao.getConexao();
        PreparedStatement comando = con.prepareStatement(
                "update produtos set codigo_produto = ?, nome = ?, descricao = ?, imagem = ?, situacao = ? where id = ?");
        comando.setString(1, prod.getCodigoProduto());
        comando.setString(2, prod.getNome());
        comando.setString(3, prod.getDescricao());
        comando.setString(4, prod.getImagem());
        comando.setString(5, prod.getSituacao());
        comando.setInt(6, prod.getId());
        comando.execute();
        con.close();

        // atualiza a precificacao do produto
        prod.getPrecificacao().setIdProduto(prod.getId());
        new PrecificacaoDAO().atualizar(prod.getPrecificacao());
    }

    public void alterarSituacao(Produto prod) throws ClassNotFoundException, SQLException {
        Connection con = FabricaConexao.getConexao();
        PreparedStatement comando = con.prepareStatement("update produtos set situacao = ? where id = ?");
        comando.setString(1, prod.getSituacao());
        comando.setInt(2, prod.getId());
        comando.execute();
        con.close();
    }

    public void deletar(Produto prod) throws ClassNotFoundException, SQLException {
        Connection con = FabricaConexao.getConexao();
        PreparedStatement comando = con.prepareStatement("delete from produtos where id = ?");
        comando.setInt(1, prod.getId());
        comando.execute();
        con.close();
    }

    public Produto consultarPorId(Produto prod) throws ClassNotFoundException, SQLException {
        Connection con = FabricaConexao.getConexao();
        PreparedStatement comando = con.prepareStatement("select * from produtos where id = ?");
        comando.setInt(1, prod.getId());
        ResultSet rs = comando.executeQuery();
        Produto p = null;
        if (rs.next()) {
            Vendedor vFiltro = new Vendedor();
            vFiltro.setId(rs.getInt("id_vendedor"));
            Vendedor vendedor = new VendedorDAO().consultarPorId(vFiltro);

            Produto temp = new Produto();
            temp.setId(rs.getInt("id"));

            p = new ProdutoBuilder()
                    .comId(rs.getInt("id"))
                    .comCodigoProduto(rs.getString("codigo_produto"))
                    .comNome(rs.getString("nome"))
                    .comDescricao(rs.getString("descricao"))
                    .comImagem(rs.getString("imagem"))
                    .comSituacao(rs.getString("situacao"))
                    .comVendedor(vendedor)
                    .comPrecificacao(new PrecificacaoDAO().consultarPorProduto(temp))
                    .constroi();
        }
        con.close();
        return p;
    }

    // metodo consultar todos
    public List<Produto> consultarPorVendedor(Vendedor vendedor) throws ClassNotFoundException, SQLException {
        Connection con = FabricaConexao.getConexao();
        PreparedStatement comando = con.prepareStatement(
                "select * from produtos where id_vendedor = ? order by id desc");
        comando.setInt(1, vendedor.getId());
        ResultSet rs = comando.executeQuery();
        List<Produto> lista = new ArrayList<>();
        Vendedor vCompleto = new VendedorDAO().consultarPorId(vendedor);
        PrecificacaoDAO precDAO = new PrecificacaoDAO();

        while (rs.next()) {
            int id = rs.getInt("id");
            Produto temp = new Produto();
            temp.setId(id);

            Produto p = new ProdutoBuilder()
                    .comId(id)
                    .comCodigoProduto(rs.getString("codigo_produto"))
                    .comNome(rs.getString("nome"))
                    .comDescricao(rs.getString("descricao"))
                    .comImagem(rs.getString("imagem"))
                    .comSituacao(rs.getString("situacao"))
                    .comVendedor(vCompleto)
                    .comPrecificacao(precDAO.consultarPorProduto(temp))
                    .constroi();
            lista.add(p);
        }
        con.close();
        return lista;
    }
}
