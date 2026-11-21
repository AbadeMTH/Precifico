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

public class CadastrarProdutoAction implements ICommand {

    @Override
    public Map<String, Object> executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> resultado = new LinkedHashMap<>();
        Vendedor vendedorLogado = SessaoHelper.obterVendedorLogado(request);

        if (vendedorLogado == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resultado.put("sucesso", false);
            resultado.put("msgErro", "Sessão expirada. Faça login novamente.");
            return resultado;
        }

        try {
            String codigoProduto = request.getParameter("codigoProduto");
            String nome = request.getParameter("nome");
            String descricao = request.getParameter("descricao");
            String imagem = request.getParameter("imagem");
            String situacao = request.getParameter("situacao");
            String strCusto = request.getParameter("custo");
            String strValorVenda = request.getParameter("valorVenda");

            if (codigoProduto == null || codigoProduto.trim().isEmpty() ||
                nome == null || nome.trim().isEmpty() ||
                descricao == null || descricao.trim().isEmpty() ||
                imagem == null || imagem.trim().isEmpty() ||
                strCusto == null || strValorVenda == null) {

                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resultado.put("sucesso", false);
                resultado.put("msgErro", "Por favor, preencha todos os campos obrigatórios.");
                return resultado;
            }

            double custo = Double.parseDouble(strCusto.replace(",", "."));
            double valorVenda = Double.parseDouble(strValorVenda.replace(",", "."));

            if (custo <= 0 || valorVenda <= 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resultado.put("sucesso", false);
                resultado.put("msgErro", "Os valores de custo e venda devem ser maiores que zero.");
                return resultado;
            }

            // cria precificação e atribui a um produto (1:1) usando Builder
            Precificacao precificacao = new Precificacao(valorVenda, custo);
            Produto produto = new ProdutoBuilder()
                    .comCodigoProduto(codigoProduto.trim())
                    .comNome(nome.trim())
                    .comDescricao(descricao.trim())
                    .comImagem(imagem.trim())
                    .comSituacao(situacao != null && !situacao.trim().isEmpty() ? situacao.trim() : "DISPONIVEL")
                    .comVendedor(vendedorLogado)
                    .comPrecificacao(precificacao)
                    .constroi();

            ProdutoDAO produtoDAO = new ProdutoDAO();
            produtoDAO.cadastrar(produto);

            resultado.put("sucesso", true);
            resultado.put("msgSucesso", "Produto '" + produto.getNome() + "' cadastrado com sucesso!");
            resultado.put("produto", produto);
            return resultado;
        } catch (NumberFormatException ex) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultado.put("sucesso", false);
            resultado.put("msgErro", "Valores numéricos inválidos informados para custo ou preço de venda.");
            return resultado;
        } catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resultado.put("sucesso", false);
            resultado.put("msgErro", "Erro ao cadastrar produto: " + ex.getMessage());
            return resultado;
        }
    }
}
