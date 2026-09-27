package command.action;

import command.ICommand;
import dao.VendedorDAO;
import model.Vendedor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CadastrarVendedorAction implements ICommand {

    @Override
    public String executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String nome = request.getParameter("nome");
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");
        String confirmaSenha = request.getParameter("confirmaSenha");
        String strIdade = request.getParameter("idade");
        String endereco = request.getParameter("endereco");
        String cpf = request.getParameter("cpf");

        if (nome == null || nome.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            senha == null || senha.trim().isEmpty()) {

            request.setAttribute("msgErro", "Nome, e-mail e senha são campos obrigatórios.");
            return "cadastro_vendedor.jsp";
        }

        if (senha.length() < 6) {
            request.setAttribute("msgErro", "A senha deve conter no mínimo 6 caracteres.");
            return "cadastro_vendedor.jsp";
        }

        if (confirmaSenha != null && !senha.equals(confirmaSenha)) {
            request.setAttribute("msgErro", "As senhas não conferem. Digite a mesma senha nos dois campos.");
            return "cadastro_vendedor.jsp";
        }

        VendedorDAO vendedorDAO = new VendedorDAO();
        Vendedor filtroEmail = new Vendedor();
        filtroEmail.setEmail(email.trim().toLowerCase());
        Vendedor existente = vendedorDAO.consultarPorEmail(filtroEmail);
        if (existente != null) {
            request.setAttribute("msgErro", "Já existe um vendedor cadastrado com este e-mail.");
            return "cadastro_vendedor.jsp";
        }

        int idade = 0;
        if (strIdade != null && !strIdade.trim().isEmpty()) {
            try {
                idade = Integer.parseInt(strIdade.trim());
            } catch (NumberFormatException ignored) {}
        }

        Vendedor vendedor = new Vendedor(
                nome.trim(),
                email.trim().toLowerCase(),
                senha,
                idade,
                endereco != null ? endereco.trim() : "",
                cpf != null ? cpf.trim() : ""
        );

        try {
            vendedorDAO.cadastrar(vendedor);
            // pega o vendedor cadastrado recentemente para ter o ID preenchido na sessao
            Vendedor vendedorSalvo = vendedorDAO.consultarPorEmail(filtroEmail);
            request.getSession(true).setAttribute("vendedorLogado", vendedorSalvo != null ? vendedorSalvo : vendedor);
            request.setAttribute("msgSucesso", "Bem-vindo, " + vendedor.getNome() + "! Cadastro realizado com sucesso.");
            return new ConsultarTodosProdutoAction().executar(request, response);
        } catch (Exception ex) {
            request.setAttribute("msgErro", "Erro ao cadastrar vendedor: " + ex.getMessage());
            return "cadastro_vendedor.jsp";
        }
    }
}
