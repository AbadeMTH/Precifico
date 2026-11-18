package br.com.precifico.command;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;

public interface ICommand {
    Map<String, Object> executar(HttpServletRequest request, HttpServletResponse response) throws Exception;
}
