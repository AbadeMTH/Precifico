package br.com.precifico.command.action;

import br.com.precifico.command.ICommand;
import br.com.precifico.dao.VendedorDAO;
import br.com.precifico.model.Vendedor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class EntrarVendedorAction implements ICommand {

    @Override
    public Map<String, Object> executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> resultado = new LinkedHashMap<>();
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");

        if (email == null || email.trim().isEmpty() || senha == null || senha.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultado.put("sucesso", false);
            resultado.put("msgErro", "Informe e-mail e senha para acessar.");
            return resultado;
        }

        VendedorDAO vendedorDAO = new VendedorDAO();
        Vendedor credenciais = new Vendedor();
        credenciais.setEmail(email.trim().toLowerCase());
        credenciais.setSenha(senha);
        Vendedor vendedor = vendedorDAO.autenticar(credenciais);

        if (vendedor != null) {
            HttpSession session = request.getSession(true);
            session.setAttribute("vendedorLogado", vendedor);
            resultado.put("sucesso", true);
            resultado.put("msgSucesso", "Login efetuado com sucesso! Olá, " + vendedor.getNome() + ".");
            resultado.put("vendedor", vendedor);
            return resultado;
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resultado.put("sucesso", false);
            resultado.put("msgErro", "E-mail ou senha inválidos. Verifique suas credenciais.");
            return resultado;
        }
    }
}
