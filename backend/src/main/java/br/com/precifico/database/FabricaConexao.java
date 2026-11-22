package br.com.precifico.database;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
public class FabricaConexao implements IConexaoFactory {
    private final String driver;
    private final String url;
    private final String usuario;
    private final String senha;

    public FabricaConexao(
            @Value("${spring.datasource.driver-class-name:com.mysql.cj.jdbc.Driver}") String driver,
            @Value("${spring.datasource.url:jdbc:mysql://localhost:3306/precifico}") String url,
            @Value("${spring.datasource.username:root}") String usuario,
            @Value("${spring.datasource.password:root}") String senha
    ) {
        this.driver = driver;
        this.url = url;
        this.usuario = usuario;
        this.senha = senha;
    }

    @Override
    public Connection getConexao() throws ClassNotFoundException, SQLException {
        Class.forName(driver);
        return DriverManager.getConnection(url, usuario, senha);
    }
}
