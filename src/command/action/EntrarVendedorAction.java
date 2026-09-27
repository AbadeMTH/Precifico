package command.action;

import command.ICommand;
import dao.VendedorDAO;
import model.Vendedor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class EntrarVendedorAction implements ICommand {

    @Override
    public String executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");

        if (email == null || email.trim().isEmpty() || senha == null || senha.trim().isEmpty()) {
            request.setAttribute("msgErro", "Informe e-mail e senha para acessar.");
            return "login.jsp";
        }

        VendedorDAO vendedorDAO = new VendedorDAO();
        Vendedor credenciais = new Vendedor();
        credenciais.setEmail(email.trim().toLowerCase());
        credenciais.setSenha(senha);
        Vendedor vendedor = vendedorDAO.autenticar(credenciais);

        if (vendedor != null) {
            HttpSession session = request.getSession(true);
            session.setAttribute("vendedorLogado", vendedor);
            request.setAttribute("msgSucesso", "Login efetuado com sucesso! Olá, " + vendedor.getNome() + ".");
            return new ConsultarTodosProdutoAction().executar(request, response);
        } else {
            request.setAttribute("msgErro", "E-mail ou senha inválidos. Verifique suas credenciais.");
            return "login.jsp";
        }
    }
}
