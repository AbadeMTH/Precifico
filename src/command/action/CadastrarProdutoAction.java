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

public class CadastrarProdutoAction implements ICommand {

    @Override
    public String executar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("vendedorLogado") == null) {
            request.setAttribute("msgErro", "Sessão expirada. Faça login novamente.");
            return "login.jsp";
        }

        Vendedor vendedorLogado = (Vendedor) session.getAttribute("vendedorLogado");

        try {
            String codigoProduto = request.getParameter("codigoProduto");
            String nome = request.getParameter("nome");
            String descricao = request.getParameter("descricao");
            String imagem = request.getParameter("imagem");
            String strCusto = request.getParameter("custo");
            String strValorVenda = request.getParameter("valorVenda");

            if (codigoProduto == null || codigoProduto.trim().isEmpty() ||
                nome == null || nome.trim().isEmpty() ||
                descricao == null || descricao.trim().isEmpty() ||
                imagem == null || imagem.trim().isEmpty() ||
                strCusto == null || strValorVenda == null) {

                request.setAttribute("msgErro", "Por favor, preencha todos os campos obrigatórios.");
                return "formulario_produto.jsp";
            }

            double custo = Double.parseDouble(strCusto.replace(",", "."));
            double valorVenda = Double.parseDouble(strValorVenda.replace(",", "."));

            if (custo <= 0 || valorVenda <= 0) {
                request.setAttribute("msgErro", "Os valores de custo e venda devem ser maiores que zero.");
                return "formulario_produto.jsp";
            }

            // Criação da precificação 1:1 e do produto via Builder
            Precificacao precificacao = new Precificacao(valorVenda, custo);
            Produto produto = new ProdutoBuilder()
                    .comCodigoProduto(codigoProduto.trim())
                    .comNome(nome.trim())
                    .comDescricao(descricao.trim())
                    .comImagem(imagem.trim())
                    .comSituacao("DISPONIVEL")
                    .comVendedor(vendedorLogado)
                    .comPrecificacao(precificacao)
                    .constroi();

            ProdutoDAO produtoDAO = new ProdutoDAO();
            produtoDAO.cadastrar(produto);
            request.setAttribute("msgSucesso", "Produto '" + produto.getNome() + "' cadastrado com sucesso!");
            return new ConsultarTodosProdutoAction().executar(request, response);
        } catch (NumberFormatException ex) {
            request.setAttribute("msgErro", "Valores numéricos inválidos informados para custo ou preço de venda.");
            return "formulario_produto.jsp";
        } catch (Exception ex) {
            request.setAttribute("msgErro", "Erro ao cadastrar produto: " + ex.getMessage());
            return "formulario_produto.jsp";
        }
    }
}
