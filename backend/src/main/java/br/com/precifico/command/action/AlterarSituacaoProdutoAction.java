package br.com.precifico.command.action;

import br.com.precifico.command.ICommand;
import br.com.precifico.command.SessaoHelper;
import br.com.precifico.dao.ProdutoDAO;
import br.com.precifico.model.Produto;
import br.com.precifico.model.Vendedor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.LinkedHashMap;
import java.util.Map;

public class AlterarSituacaoProdutoAction implements ICommand {

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

        try {
            int id = Integer.parseInt(strId);
            ProdutoDAO produtoDAO = new ProdutoDAO();
            Produto p = new Produto();
            p.setId(id);
            Produto produto = produtoDAO.consultarPorId(p);

            if (produto == null || produto.getVendedor() == null || produto.getVendedor().getId() != vendedorLogado.getId()) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                resultado.put("sucesso", false);
                resultado.put("msgErro", "Permissão negada ou produto inexistente.");
                return resultado;
            }

            String situacaoAtual = produto.getSituacao();
            String novaSituacao = "VENDIDO".equalsIgnoreCase(situacaoAtual) ? "DISPONIVEL" : "VENDIDO";

            Produto prodAtualizar = new Produto();
            prodAtualizar.setId(id);
            prodAtualizar.setSituacao(novaSituacao);
            produtoDAO.alterarSituacao(prodAtualizar);

            resultado.put("sucesso", true);
            resultado.put("novaSituacao", novaSituacao);
            resultado.put("msgSucesso", "Situação do produto '" + produto.getNome() + "' alterada para " + novaSituacao + ".");
            return resultado;
        } catch (NumberFormatException ex) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultado.put("sucesso", false);
            resultado.put("msgErro", "Identificador de produto inválido.");
            return resultado;
        }
    }
}
