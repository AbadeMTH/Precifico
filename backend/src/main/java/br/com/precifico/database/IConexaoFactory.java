package br.com.precifico.database;

import java.sql.Connection;
import java.sql.SQLException;

public interface IConexaoFactory {
    Connection getConexao() throws ClassNotFoundException, SQLException;
}
