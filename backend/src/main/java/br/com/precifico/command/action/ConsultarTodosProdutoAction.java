package br.com.precifico.command.action;

import br.com.precifico.command.ICommand;
import br.com.precifico.command.SessaoHelper;
import br.com.precifico.dao.ProdutoDAO;
import br.com.precifico.model.Produto;
import br.com.precifico.model.Vendedor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ConsultarTodosProdutoAction implements ICommand {

    @Override
    public Map<String, Object> executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> resultado = new LinkedHashMap<>();
        Vendedor vendedorLogado = SessaoHelper.obterVendedorLogado(request);

        if (vendedorLogado == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resultado.put("sucesso", false);
            resultado.put("msgErro", "Acesso restrito. Faça login para continuar.");
            return resultado;
        }

        ProdutoDAO produtoDAO = new ProdutoDAO();
        List<Produto> listaProdutos = produtoDAO.consultarPorVendedor(vendedorLogado);

        resultado.put("sucesso", true);
        resultado.put("listaProdutos", listaProdutos);
        return resultado;
    }
}
