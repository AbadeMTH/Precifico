package command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interface do Padrão Command
 * Conforme estudado em Aula07 - Command _ Factory Method
 */
public interface ICommand {
    public String executar(HttpServletRequest request, HttpServletResponse response) throws Exception;
}
