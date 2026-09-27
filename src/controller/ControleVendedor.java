package controller;

import command.ICommand;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "ControleVendedor", urlPatterns = {"/ControleVendedor", "/vendedor"})
public class ControleVendedor extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String paramAction = request.getParameter("btnop");
        if (paramAction == null || paramAction.trim().isEmpty()) {
            paramAction = request.getParameter("op");
        }

        try {
            String nomeDaClasse = "command.action." + paramAction + "VendedorAction";
            Class classAction = Class.forName(nomeDaClasse);
            ICommand commandAction = (ICommand) classAction.newInstance();

            String pageAction = commandAction.executar(request, response);
            request.getRequestDispatcher(pageAction).forward(request, response);
        } catch (Exception ex) {
            request.setAttribute("msgErro", "Ocorreu um erro no processamento: " + ex.getMessage());
            request.getRequestDispatcher("erro.jsp").forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Controlador de Vendedores do Precifico v1";
    }
}
