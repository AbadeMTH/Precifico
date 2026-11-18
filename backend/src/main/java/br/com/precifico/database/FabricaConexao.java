package br.com.precifico.database;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Component
public class FabricaConexao {
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = System.getProperty("precifico.db.url",
            "jdbc:mysql://localhost:3306/precifico?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
    private static final String USUARIO = System.getProperty("precifico.db.username", "root");
    private static final String SENHA = System.getProperty("precifico.db.password", "root");

    private static volatile boolean tabelasVerificadas = false;

    @PostConstruct
    public void init() {
        try {
            getConexao().close();
        } catch (Exception ignored) {
        }
    }

    public static Connection getConexao() throws ClassNotFoundException, SQLException {
        Class.forName(DRIVER);
        Connection conexao = DriverManager.getConnection(URL, USUARIO, SENHA);
        if (!tabelasVerificadas) {
            inicializarTabelas(conexao);
            tabelasVerificadas = true;
        }
        return conexao;
    }

    private static synchronized void inicializarTabelas(Connection conexao) throws SQLException {
        if (tabelasVerificadas) {
            return;
        }
        try (Statement stmt = conexao.createStatement()) {
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS vendedores (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    nome VARCHAR(100) NOT NULL,
                    email VARCHAR(150) NOT NULL UNIQUE,
                    senha VARCHAR(100) NOT NULL,
                    idade INT,
                    endereco VARCHAR(200),
                    cpf VARCHAR(14)
                )
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS produtos (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    codigo_produto VARCHAR(50) NOT NULL,
                    nome VARCHAR(100) NOT NULL,
                    descricao TEXT NOT NULL,
                    imagem VARCHAR(500) NOT NULL,
                    situacao VARCHAR(20) NOT NULL DEFAULT 'DISPONIVEL',
                    id_vendedor INT NOT NULL,
                    CONSTRAINT fk_produto_vendedor FOREIGN KEY (id_vendedor)
                        REFERENCES vendedores(id) ON DELETE CASCADE
                )
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS precificacoes (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    id_produto INT NOT NULL UNIQUE,
                    valor_venda DOUBLE NOT NULL,
                    custo_produto DOUBLE NOT NULL,
                    lucro DOUBLE NOT NULL,
                    CONSTRAINT fk_precificacao_produto FOREIGN KEY (id_produto)
                        REFERENCES produtos(id) ON DELETE CASCADE
                )
            """);
        }
    }
}
