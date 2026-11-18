package br.com.precifico.controller;

import br.com.precifico.command.ICommand;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class ControleProduto {

    @RequestMapping(
            value = {"/ControleProduto", "/produto", "/api/produtos"},
            method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE}
    )
    public Map<String, Object> processRequest(HttpServletRequest request, HttpServletResponse response) {
        String paramAction = request.getParameter("btnop");
        if (paramAction == null || paramAction.trim().isEmpty()) {
            paramAction = request.getParameter("op");
        }
        if (paramAction == null || paramAction.trim().isEmpty()) {
            paramAction = "ConsultarTodos";
        }

        try {
            String nomeDaClasse = "br.com.precifico.command.action." + paramAction + "ProdutoAction";
            Class<?> classAction = Class.forName(nomeDaClasse);
            ICommand commandAction = (ICommand) classAction.getDeclaredConstructor().newInstance();

            return commandAction.executar(request, response);
        } catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Map<String, Object> erro = new LinkedHashMap<>();
            erro.put("sucesso", false);
            erro.put("msgErro", "Ocorreu um erro no processamento: " + ex.getMessage());
            return erro;
        }
    }
}
