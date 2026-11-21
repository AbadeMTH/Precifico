package br.com.precifico.dao;

import br.com.precifico.database.IConexaoFactory;
import br.com.precifico.model.Vendedor;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class VendedorDAO implements IVendedorDAO {

    private final IConexaoFactory conexaoFactory;

    public VendedorDAO(IConexaoFactory conexaoFactory) {
        this.conexaoFactory = conexaoFactory;
    }

    @Override
    public void cadastrar(Vendedor vendedor) throws ClassNotFoundException, SQLException {
        Connection conexao = conexaoFactory.getConexao();
        PreparedStatement comando = conexao.prepareStatement(
                "insert into vendedores (nome, email, senha, idade, endereco, cpf) values (?, ?, ?, ?, ?, ?)");
        comando.setString(1, vendedor.getNome());
        comando.setString(2, vendedor.getEmail());
        comando.setString(3, vendedor.getSenha());
        comando.setInt(4, vendedor.getIdade());
        comando.setString(5, vendedor.getEndereco());
        comando.setString(6, vendedor.getCpf());
        comando.execute();
        conexao.close();
    }

    @Override
    public Vendedor autenticar(Vendedor vendedor) throws ClassNotFoundException, SQLException {
        Connection conexao = conexaoFactory.getConexao();
        PreparedStatement comando = conexao.prepareStatement(
                "select * from vendedores where email = ? and senha = ?");
        comando.setString(1, vendedor.getEmail());
        comando.setString(2, vendedor.getSenha());
        ResultSet resultado = comando.executeQuery();

        Vendedor vendedorEncontrado = null;
        if (resultado.next()) {
            vendedorEncontrado = mapearVendedor(resultado);
        }

        conexao.close();
        return vendedorEncontrado;
    }

    @Override
    public Vendedor consultarPorId(Vendedor vendedor) throws ClassNotFoundException, SQLException {
        Connection conexao = conexaoFactory.getConexao();
        PreparedStatement comando = conexao.prepareStatement(
                "select * from vendedores where id = ?");
        comando.setInt(1, vendedor.getId());
        ResultSet resultado = comando.executeQuery();

        Vendedor vendedorEncontrado = null;
        if (resultado.next()) {
            vendedorEncontrado = mapearVendedor(resultado);
        }

        conexao.close();
        return vendedorEncontrado;
    }

    @Override
    public Vendedor consultarPorEmail(Vendedor vendedor) throws ClassNotFoundException, SQLException {
        Connection conexao = conexaoFactory.getConexao();
        PreparedStatement comando = conexao.prepareStatement(
                "select * from vendedores where email = ?");
        comando.setString(1, vendedor.getEmail());
        ResultSet resultado = comando.executeQuery();

        Vendedor vendedorEncontrado = null;
        if (resultado.next()) {
            vendedorEncontrado = mapearVendedor(resultado);
        }

        conexao.close();
        return vendedorEncontrado;
    }

    private Vendedor mapearVendedor(ResultSet resultado) throws SQLException {
        return new Vendedor(
                resultado.getInt("id"),
                resultado.getString("nome"),
                resultado.getString("email"),
                resultado.getString("senha"),
                resultado.getInt("idade"),
                resultado.getString("endereco"),
                resultado.getString("cpf")
        );
    }
}
