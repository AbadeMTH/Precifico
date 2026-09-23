import dao.VendedorDAO;
import model.Vendedor;

import java.sql.SQLException;

void main() {
    Vendedor vendedor = new Vendedor();
    vendedor.setNome("Pedro");
    VendedorDAO vendedorDAO = new VendedorDAO();

    try{
        vendedorDAO.cadastrar(vendedor);
    } catch (SQLException | ClassNotFoundException ex){

        System.out.println(ex);

    }
}
