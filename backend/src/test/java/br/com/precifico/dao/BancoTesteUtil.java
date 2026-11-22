package br.com.precifico.dao;

import br.com.precifico.database.IConexaoFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;

public final class BancoTesteUtil {

    private BancoTesteUtil() {
    }

    public static void inicializarBanco(IConexaoFactory conexaoFactory) throws Exception {
        String scriptSql = carregarSchemaSql();
        String[] comandos = scriptSql.split(";");

        try (Connection conexao = conexaoFactory.getConexao();
             Statement statement = conexao.createStatement()) {
            for (String comando : comandos) {
                String sqlLimpo = comando.trim();
                if (!sqlLimpo.isEmpty()) {
                    statement.execute(sqlLimpo);
                }
            }
        }
    }

    private static String carregarSchemaSql() throws IOException {
        try (InputStream stream = BancoTesteUtil.class.getClassLoader().getResourceAsStream("schema-test.sql")) {
            if (stream == null) {
                throw new IllegalStateException("Arquivo schema-test.sql não encontrado em src/test/resources.");
            }
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
