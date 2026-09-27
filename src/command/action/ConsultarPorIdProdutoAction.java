package command.action;

import command.ICommand;
import dao.ProdutoDAO;
import model.Precificacao;
import model.Produto;
import model.Vendedor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ConsultarPorIdProdutoAction implements ICommand {

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
            request.setAttribute("msgErro", "Identificador do produto não informado.");
            return new ConsultarTodosProdutoAction().executar(request, response);
        }

        int id = Integer.parseInt(strId);
        ProdutoDAO produtoDAO = new ProdutoDAO();
        Produto p = new Produto();
        p.setId(id);
        Produto produto = produtoDAO.consultarPorId(p);

        if (produto == null) {
            request.setAttribute("msgErro", "Produto não encontrado.");
            return new ConsultarTodosProdutoAction().executar(request, response);
        }

        if (produto.getVendedor() == null || produto.getVendedor().getId() != vendedorLogado.getId()) {
            request.setAttribute("msgErro", "Acesso não autorizado ao produto solicitado.");
            return new ConsultarTodosProdutoAction().executar(request, response);
        }

        request.setAttribute("produto", produto);

        double margemEfetiva = 0.0;
        double precoSugerido = 0.0;
        String diagnosticoComercial = "Sem precificação cadastrada.";

        if (produto.getPrecificacao() != null) {
            Precificacao prec = produto.getPrecificacao();
            margemEfetiva = prec.getMargemEfetiva();
            precoSugerido = prec.calcularPrecoSugerido(25.0);
            diagnosticoComercial = prec.classificarPreco(25.0);
        }

        request.setAttribute("margemEfetiva", margemEfetiva);
        request.setAttribute("precoSugerido", precoSugerido);
        request.setAttribute("diagnosticoComercial", diagnosticoComercial);

        String modo = request.getParameter("modo");
        if ("editar".equalsIgnoreCase(modo)) {
            return "formulario_produto.jsp";
        }

        return "detalhe_produto.jsp";
    }
}
