package br.com.precifico.command.action;

import br.com.precifico.command.ICommand;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.LinkedHashMap;
import java.util.Map;

public class SairVendedorAction implements ICommand {

    @Override
    public Map<String, Object> executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> resultado = new LinkedHashMap<>();
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        resultado.put("sucesso", true);
        resultado.put("msgSucesso", "Você saiu da sua conta com segurança.");
        return resultado;
    }
}
