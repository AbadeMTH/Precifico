package command.action;

import command.ICommand;
import dao.ProdutoDAO;
import model.Produto;
import model.Vendedor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AlterarSituacaoProdutoAction implements ICommand {

    @Override
    public String executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession(false); //retorna sessão se existir sem criar outra
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

        try {
            int id = Integer.parseInt(strId);
            ProdutoDAO produtoDAO = new ProdutoDAO();
            Produto p = new Produto();
            p.setId(id);
            Produto produto = produtoDAO.consultarPorId(p);

            if (produto == null || produto.getVendedor() == null || produto.getVendedor().getId() != vendedorLogado.getId()) {
                request.setAttribute("msgErro", "Permissão negada ou produto inexistente.");
                return new ConsultarTodosProdutoAction().executar(request, response);
            }

            String situacaoAtual = produto.getSituacao();
            String novaSituacao = "VENDIDO".equalsIgnoreCase(situacaoAtual) ? "DISPONIVEL" : "VENDIDO";

            Produto prodAtualizar = new Produto();
            prodAtualizar.setId(id);
            prodAtualizar.setSituacao(novaSituacao);
            produtoDAO.alterarSituacao(prodAtualizar);
            request.setAttribute("msgSucesso", "Situação do produto '" + produto.getNome() + "' alterada para " + novaSituacao + ".");
        } catch (NumberFormatException ex) {
            request.setAttribute("msgErro", "Identificador de produto inválido.");
        }

        return new ConsultarTodosProdutoAction().executar(request, response);
    }
}
