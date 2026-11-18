package br.com.precifico.command;

import br.com.precifico.dao.VendedorDAO;
import br.com.precifico.model.Vendedor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class SessaoHelper {

    public static Vendedor obterVendedorLogado(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("vendedorLogado") != null) {
            return (Vendedor) session.getAttribute("vendedorLogado");
        }

        // Fallback via cabeçalho X-Vendedor-Id ou parâmetro idVendedor para cenários cross-origin / file://
        String strId = request.getHeader("X-Vendedor-Id");
        if (strId == null || strId.trim().isEmpty()) {
            strId = request.getParameter("idVendedor");
        }

        if (strId != null && !strId.trim().isEmpty()) {
            try {
                int id = Integer.parseInt(strId.trim());
                Vendedor filtro = new Vendedor();
                filtro.setId(id);
                return new VendedorDAO().consultarPorId(filtro);
            } catch (Exception ignored) {
            }
        }

        return null;
    }
}
