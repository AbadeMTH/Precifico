package dao;

import database.FabricaConexao;
import model.Vendedor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class VendedorDAO {
    public void cadastrar(Vendedor vendedor) throws ClassNotFoundException, SQLException {
        Connection con = FabricaConexao.getConexao();
        String SQL = "insert into vendedores (nome, email, senha) values (?)";
        PreparedStatement comando = con.prepareStatement(SQL);
        comando.setString(1, vendedor.getNome());
        comando.setString(2, vendedor.getEmail());
        comando.setString(3, vendedor.getSenha());
        comando.execute();
        con.close();
    }
}
