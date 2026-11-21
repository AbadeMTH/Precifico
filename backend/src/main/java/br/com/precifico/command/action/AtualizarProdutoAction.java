package br.com.precifico.command.action;

import br.com.precifico.builder.ProdutoBuilder;
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

public class AtualizarProdutoAction implements ICommand {

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

        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String codigoProduto = request.getParameter("codigoProduto");
            String nome = request.getParameter("nome");
            String descricao = request.getParameter("descricao");
            String imagem = request.getParameter("imagem");
            String situacao = request.getParameter("situacao");
            String strCusto = request.getParameter("custo");
            String strValorVenda = request.getParameter("valorVenda");

            ProdutoDAO produtoDAO = new ProdutoDAO();
            Produto p = new Produto();
            p.setId(id);
            Produto existente = produtoDAO.consultarPorId(p);

            if (existente == null || existente.getVendedor() == null || existente.getVendedor().getId() != vendedorLogado.getId()) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                resultado.put("sucesso", false);
                resultado.put("msgErro", "Permissão negada ou produto inexistente.");
                return resultado;
            }

            double custo = Double.parseDouble(strCusto.replace(",", "."));
            double valorVenda = Double.parseDouble(strValorVenda.replace(",", "."));

            if (custo <= 0 || valorVenda <= 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resultado.put("sucesso", false);
                resultado.put("msgErro", "Os valores de custo e venda devem ser maiores que zero.");
                resultado.put("produto", existente);
                return resultado;
            }

            Precificacao precificacao = new Precificacao(valorVenda, custo);
            precificacao.setIdProduto(id);

            Produto produtoAtualizado = new ProdutoBuilder()
                    .comId(id)
                    .comCodigoProduto(codigoProduto.trim())
                    .comNome(nome.trim())
                    .comDescricao(descricao.trim())
                    .comImagem(imagem.trim())
                    .comSituacao(situacao != null ? situacao : existente.getSituacao())
                    .comVendedor(vendedorLogado)
                    .comPrecificacao(precificacao)
                    .constroi();

            produtoDAO.atualizar(produtoAtualizado);

            resultado.put("sucesso", true);
            resultado.put("msgSucesso", "Produto atualizado com sucesso!");
            resultado.put("produto", produtoAtualizado);
            return resultado;

        } catch (NumberFormatException ex) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultado.put("sucesso", false);
            resultado.put("msgErro", "Formato numérico inválido para custo ou preço de venda.");
            return resultado;
        } catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resultado.put("sucesso", false);
            resultado.put("msgErro", "Erro ao atualizar produto: " + ex.getMessage());
            return resultado;
        }
    }
}
