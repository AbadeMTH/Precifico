package command.action;

import builder.ProdutoBuilder;
import command.ICommand;
import dao.ProdutoDAO;
import model.Precificacao;
import model.Produto;
import model.Vendedor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AtualizarProdutoAction implements ICommand {

    @Override
    public String executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("vendedorLogado") == null) {
            request.setAttribute("msgErro", "Acesso restrito. Faça login para continuar.");
            return "login.jsp";
        }

        Vendedor vendedorLogado = (Vendedor) session.getAttribute("vendedorLogado");

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
                request.setAttribute("msgErro", "Permissão negada ou produto inexistente.");
                return new ConsultarTodosProdutoAction().executar(request, response);
            }

            double custo = Double.parseDouble(strCusto.replace(",", "."));
            double valorVenda = Double.parseDouble(strValorVenda.replace(",", "."));

            if (custo <= 0 || valorVenda <= 0) {
                request.setAttribute("msgErro", "Os valores de custo e venda devem ser maiores que zero.");
                request.setAttribute("produto", existente);
                return "formulario_produto.jsp";
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
            request.setAttribute("msgSucesso", "Produto atualizado com sucesso!");
            return new ConsultarTodosProdutoAction().executar(request, response);

        } catch (NumberFormatException ex) {
            request.setAttribute("msgErro", "Formato numérico inválido para custo ou preço de venda.");
            return new ConsultarTodosProdutoAction().executar(request, response);
        } catch (Exception ex) {
            request.setAttribute("msgErro", "Erro ao atualizar produto: " + ex.getMessage());
            return new ConsultarTodosProdutoAction().executar(request, response);
        }
    }
}
