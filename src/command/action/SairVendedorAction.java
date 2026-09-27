package command.action;

import command.ICommand;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SairVendedorAction implements ICommand {

    @Override
    public String executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        request.setAttribute("msgSucesso", "Você saiu da sua conta com segurança.");
        return "login.jsp";
    }
}
