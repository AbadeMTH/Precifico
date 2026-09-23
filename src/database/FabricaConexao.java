package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe responsáel por fazer a conexão no banco de dados
 */
public class FabricaConexao {
    public static Connection getConexao() throws ClassNotFoundException, SQLException {
        String DRIVER = "com.mysql.cj.jdbc.Driver";
        String URL = "jdbc:mysql://localhost:3306/precifico";
        String USERNAME = "root";
        String PASSWORD = "root";

        Class.forName(DRIVER);

        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}

