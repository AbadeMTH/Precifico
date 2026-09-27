package dao;

import database.FabricaConexao;
import model.Vendedor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VendedorDAO {

    public void cadastrar(Vendedor vendedor) throws ClassNotFoundException, SQLException {
        Connection con = FabricaConexao.getConexao();
        PreparedStatement comando = con.prepareStatement(
                "insert into vendedores (nome, email, senha, idade, endereco, cpf) values (?, ?, ?, ?, ?, ?)");
        comando.setString(1, vendedor.getNome());
        comando.setString(2, vendedor.getEmail());
        comando.setString(3, vendedor.getSenha());
        comando.setInt(4, vendedor.getIdade());
        comando.setString(5, vendedor.getEndereco());
        comando.setString(6, vendedor.getCpf());
        comando.execute();
        con.close();
    }

    public Vendedor autenticar(Vendedor vendedor) throws ClassNotFoundException, SQLException {
        Connection con = FabricaConexao.getConexao();
        PreparedStatement comando = con.prepareStatement(
                "select * from vendedores where email = ? and senha = ?");
        comando.setString(1, vendedor.getEmail());
        comando.setString(2, vendedor.getSenha());
        ResultSet rs = comando.executeQuery();
        Vendedor v = null;
        if (rs.next()) {
            v = new Vendedor();
            v.setId(rs.getInt("id"));
            v.setNome(rs.getString("nome"));
            v.setEmail(rs.getString("email"));
            v.setSenha(rs.getString("senha"));
            v.setIdade(rs.getInt("idade"));
            v.setEndereco(rs.getString("endereco"));
            v.setCpf(rs.getString("cpf"));
        }
        con.close();
        return v;
    }

    public Vendedor consultarPorId(Vendedor vendedor) throws ClassNotFoundException, SQLException {
        Connection con = FabricaConexao.getConexao();
        PreparedStatement comando = con.prepareStatement(
                "select * from vendedores where id = ?");
        comando.setInt(1, vendedor.getId());
        ResultSet rs = comando.executeQuery();
        Vendedor v = null;
        if (rs.next()) {
            v = new Vendedor();
            v.setId(rs.getInt("id"));
            v.setNome(rs.getString("nome"));
            v.setEmail(rs.getString("email"));
            v.setSenha(rs.getString("senha"));
            v.setIdade(rs.getInt("idade"));
            v.setEndereco(rs.getString("endereco"));
            v.setCpf(rs.getString("cpf"));
        }
        con.close();
        return v;
    }

    public Vendedor consultarPorEmail(Vendedor vendedor) throws ClassNotFoundException, SQLException {
        Connection con = FabricaConexao.getConexao();
        PreparedStatement comando = con.prepareStatement(
                "select * from vendedores where email = ?");
        comando.setString(1, vendedor.getEmail());
        ResultSet rs = comando.executeQuery();
        Vendedor v = null;
        if (rs.next()) {
            v = new Vendedor();
            v.setId(rs.getInt("id"));
            v.setNome(rs.getString("nome"));
            v.setEmail(rs.getString("email"));
            v.setSenha(rs.getString("senha"));
            v.setIdade(rs.getInt("idade"));
            v.setEndereco(rs.getString("endereco"));
            v.setCpf(rs.getString("cpf"));
        }
        con.close();
        return v;
    }
}
