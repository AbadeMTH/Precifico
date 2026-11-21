package br.com.precifico.database;

import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
public class FabricaConexao implements IConexaoFactory {
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/precifico";
    private static final String USUARIO = "root";
    private static final String SENHA = "root";

    @Override
    public Connection getConexao() throws ClassNotFoundException, SQLException {
        Class.forName(DRIVER);
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }
}
