package command.action;

import command.ICommand;
import dao.ProdutoDAO;
import model.Produto;
import model.Vendedor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class EditaProdutoAction implements ICommand {

    @Override
    public String executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("vendedorLogado") == null) {
            request.setAttribute("msgErro", "Acesso restrito. Faça login para continuar.");
            return "login.jsp";
        }

        Vendedor vendedorLogado = (Vendedor) session.getAttribute("vendedorLogado");
        String strId = request.getParameter("id");

        if (strId == null || strId.trim().isEmpty()) {
            request.setAttribute("msgErro", "Código do produto não informado para edição.");
            return new ConsultarTodosProdutoAction().executar(request, response);
        }

        int id = Integer.parseInt(strId);
        ProdutoDAO produtoDAO = new ProdutoDAO();
        Produto p = new Produto();
        p.setId(id);
        Produto produto = produtoDAO.consultarPorId(p);

        if (produto == null || produto.getVendedor() == null || produto.getVendedor().getId() != vendedorLogado.getId()) {
            request.setAttribute("msgErro", "Produto não encontrado ou acesso não autorizado.");
            return new ConsultarTodosProdutoAction().executar(request, response);
        }

        request.setAttribute("produto", produto);
        return "formulario_produto.jsp";
    }
}
