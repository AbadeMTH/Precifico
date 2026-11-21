package br.com.precifico.command.action;

import br.com.precifico.command.ICommand;
import br.com.precifico.command.SessaoHelper;
import br.com.precifico.dao.ProdutoDAO;
import br.com.precifico.model.Precificacao;
import br.com.precifico.model.Produto;
import br.com.precifico.model.Vendedor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.LinkedHashMap;
import java.util.Map;

public class ConsultarPorIdProdutoAction implements ICommand {

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

        String strId = request.getParameter("id");
        if (strId == null || strId.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultado.put("sucesso", false);
            resultado.put("msgErro", "Identificador do produto não informado.");
            return resultado;
        }

        int id = Integer.parseInt(strId);
        ProdutoDAO produtoDAO = new ProdutoDAO();
        Produto p = new Produto();
        p.setId(id);
        Produto produto = produtoDAO.consultarPorId(p);

        if (produto == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resultado.put("sucesso", false);
            resultado.put("msgErro", "Produto não encontrado.");
            return resultado;
        }

        if (produto.getVendedor() == null || produto.getVendedor().getId() != vendedorLogado.getId()) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resultado.put("sucesso", false);
            resultado.put("msgErro", "Acesso não autorizado ao produto solicitado.");
            return resultado;
        }

        double margemEfetiva = 0.0;
        double precoSugerido = 0.0;
        String diagnosticoComercial = "Sem precificação cadastrada.";

        if (produto.getPrecificacao() != null) {
            Precificacao prec = produto.getPrecificacao();
            margemEfetiva = prec.getMargemEfetiva();
            precoSugerido = prec.calcularPrecoSugerido(25.0);
            diagnosticoComercial = prec.classificarPreco(25.0);
        }

        resultado.put("sucesso", true);
        resultado.put("produto", produto);
        resultado.put("margemEfetiva", margemEfetiva);
        resultado.put("precoSugerido", precoSugerido);
        resultado.put("diagnosticoComercial", diagnosticoComercial);
        return resultado;
    }
}
