package br.com.precifico.command.action;

import br.com.precifico.command.ICommand;
import br.com.precifico.dao.VendedorDAO;
import br.com.precifico.model.Vendedor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.LinkedHashMap;
import java.util.Map;

public class CadastrarVendedorAction implements ICommand {

    @Override
    public Map<String, Object> executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> resultado = new LinkedHashMap<>();

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

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultado.put("sucesso", false);
            resultado.put("msgErro", "Nome, e-mail e senha são campos obrigatórios.");
            return resultado;
        }

        if (senha.length() < 6) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultado.put("sucesso", false);
            resultado.put("msgErro", "A senha deve conter no mínimo 6 caracteres.");
            return resultado;
        }

        if (confirmaSenha != null && !senha.equals(confirmaSenha)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultado.put("sucesso", false);
            resultado.put("msgErro", "As senhas não conferem. Digite a mesma senha nos dois campos.");
            return resultado;
        }

        VendedorDAO vendedorDAO = new VendedorDAO();
        Vendedor filtroEmail = new Vendedor();
        filtroEmail.setEmail(email.trim().toLowerCase());
        Vendedor existente = vendedorDAO.consultarPorEmail(filtroEmail);
        if (existente != null) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            resultado.put("sucesso", false);
            resultado.put("msgErro", "Já existe um vendedor cadastrado com este e-mail.");
            return resultado;
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
            Vendedor vendedorSalvo = vendedorDAO.consultarPorEmail(filtroEmail);
            Vendedor logado = vendedorSalvo != null ? vendedorSalvo : vendedor;
            request.getSession(true).setAttribute("vendedorLogado", logado);

            resultado.put("sucesso", true);
            resultado.put("msgSucesso", "Bem-vindo, " + logado.getNome() + "! Cadastro realizado com sucesso.");
            resultado.put("vendedor", logado);
            return resultado;
        } catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resultado.put("sucesso", false);
            resultado.put("msgErro", "Erro ao cadastrar vendedor: " + ex.getMessage());
            return resultado;
        }
    }
}
