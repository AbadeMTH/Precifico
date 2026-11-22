package br.com.precifico.dao;

import br.com.precifico.model.Vendedor;

import java.sql.SQLException;

public interface IVendedorDAO {
    void cadastrar(Vendedor vendedor) throws ClassNotFoundException, SQLException;

    Vendedor autenticar(Vendedor vendedor) throws ClassNotFoundException, SQLException;

    Vendedor consultarPorId(Vendedor vendedor) throws ClassNotFoundException, SQLException;

    Vendedor consultarPorEmail(Vendedor vendedor) throws ClassNotFoundException, SQLException;
}
