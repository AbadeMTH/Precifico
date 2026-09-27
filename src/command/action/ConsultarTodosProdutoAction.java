package command.action;

import command.ICommand;
import dao.ProdutoDAO;
import model.Produto;
import model.Vendedor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

public class ConsultarTodosProdutoAction implements ICommand {

    @Override
    public String executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("vendedorLogado") == null) {
            request.setAttribute("msgErro", "Acesso restrito. Faça login para continuar.");
            return "login.jsp";
        }

        Vendedor vendedorLogado = (Vendedor) session.getAttribute("vendedorLogado");
        ProdutoDAO produtoDAO = new ProdutoDAO();
        // consultar todos no sistema tem nome de consultarPorVendedor
        List<Produto> listaProdutos = produtoDAO.consultarPorVendedor(vendedorLogado);

        request.setAttribute("listaProdutos", listaProdutos);
        return "produtos.jsp";
    }
}
