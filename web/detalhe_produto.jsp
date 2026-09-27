<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Produto" %>
<%@ page import="model.Vendedor" %>
<%@ page import="model.Precificacao" %>
<%
    Vendedor vendedorLogado = (Vendedor) session.getAttribute("vendedorLogado");
    if (vendedorLogado == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    Produto produto = (Produto) request.getAttribute("produto");
    if (produto == null) {
        response.sendRedirect("ControleProduto?btnop=ConsultarTodos");
        return;
    }
    Precificacao prec = produto.getPrecificacao();
    double custo = prec != null ? prec.getCustoProduto() : 0.0;
    double venda = prec != null ? prec.getValorVenda() : 0.0;
    double lucro = prec != null ? prec.getLucro() : 0.0;
    Double margemRequisicao = (Double) request.getAttribute("margemEfetiva");
    double margem = margemRequisicao != null ? margemRequisicao : (prec != null ? prec.getMargemEfetiva() : 0.0);
    String diagnosticoComercial = (String) request.getAttribute("diagnosticoComercial");
    if (diagnosticoComercial == null && prec != null) {
        diagnosticoComercial = prec.classificarPreco(25.0);
    }
    boolean disponivel = "DISPONIVEL".equalsIgnoreCase(produto.getSituacao());
%>
<!doctype html>
<html lang="pt-BR">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Precifico • Detalhes de <%= produto.getNome() %></title>
    <link rel="stylesheet" href="css/estilo.css">
</head>
<body>
<header>
    <div class="logotipo">Precifico</div>
    <div class="cabecalho-usuario">
        <span>Vendedor: <strong><%= vendedorLogado.getNome() %></strong></span>
        <a href="ControleProduto?btnop=ConsultarTodos" class="botao botao-pequeno" style="text-decoration:none;">Meus Produtos</a>
    </div>
</header>

<main>
    <button class="voltar" onclick="window.location.href='ControleProduto?btnop=ConsultarTodos'">← Voltar para lista de produtos</button>

    <div class="cabecalho-secao">
        <div>
            <h1><%= produto.getNome() %></h1>
        </div>
        <span class="selo <%= disponivel ? "selo-disponivel" : "selo-vendido" %>" style="font-size: 13px; padding: 6px 14px;">
            <%= produto.getSituacao() %>
        </span>
    </div>

    <div class="grade">
        <div class="painel">
            <h2>Dados Gerais do Produto</h2>
            <div style="text-align: center; margin-bottom: 20px;">
                <% 
                    String urlFotoDetalhe = (produto.getImagem() != null && !produto.getImagem().trim().isEmpty()) 
                            ? produto.getImagem() 
                            : "img/sem-foto.jpg"; 
                %>
                <img src="<%= urlFotoDetalhe %>" alt="<%= produto.getNome() %>" class="imagem-detalhe" 
                     onerror="this.onerror=null; this.src='img/sem-foto.jpg';">
            </div>

            <div class="campos" style="grid-template-columns: 1fr;">
                <div>
                    <label>Código do Produto</label>
                    <div style="font-size: 16px; font-weight: 600; padding: 8px 0;"><%= produto.getCodigoProduto() %></div>
                </div>

                <div>
                    <label>Descrição do Produto</label>
                    <div class="descricao-detalhe"><%= produto.getDescricao() %></div>
                </div>

                <div>
                    <label>URL da Imagem</label>
                    <small style="display:block; word-break: break-all; margin-top: 4px;">
                        <a href="<%= produto.getImagem() %>" target="_blank" style="color: var(--cor-marca);"><%= produto.getImagem() %></a>
                    </small>
                </div>
            </div>
        </div>

        <div class="pilha">
            <div class="painel cartao-preco">
                <h2>Análise Financeira</h2>

                <div class="metricas" style="border-top: none; padding-top: 0; margin-top: 0;">
                    <div>
                        <small>Valor de Custo</small>
                        <strong style="color: var(--cor-texto);">R$ <%= String.format("%.2f", custo) %></strong>
                    </div>
                    <div>
                        <small>Preço de Venda Praticado</small>
                        <strong style="color: var(--cor-marca);">R$ <%= String.format("%.2f", venda) %></strong>
                    </div>
                </div>

                <div class="metricas">
                    <div>
                        <small>Lucro Financeiro (R$)</small>
                        <strong style="color: <%= lucro >= 0 ? "var(--cor-marca)" : "#9a382c" %>;">
                            R$ <%= String.format("%.2f", lucro) %>
                        </strong>
                    </div>
                    <div>
                        <small>Margem Efetiva sobre Custo</small>
                        <strong><%= String.format("%.1f", margem) %>%</strong>
                    </div>
                </div>

                <div class="aviso <%= lucro >= 0 ? "sucesso" : "perigo" %>" style="margin-top: 24px;">
                    <strong>Diagnóstico Comercial:</strong><br>
                    <%= diagnosticoComercial %>
                </div>
            </div>

            <div class="painel">
                <h2>Ações do Produto</h2>
                <div style="display: flex; flex-direction: column; gap: 10px;">
                    <a href="ControleProduto?btnop=Edita&id=<%= produto.getId() %>" class="botao botao-primario" style="justify-content: center; text-decoration: none;">
                        Editar este produto
                    </a>
                    <a href="ControleProduto?btnop=AlterarSituacao&id=<%= produto.getId() %>" class="botao" style="justify-content: center; text-decoration: none;">
                        <%= disponivel ? "Alterar para Vendido" : "Reativar Produto" %>
                    </a>
                    <a href="ControleProduto?btnop=Deletar&id=<%= produto.getId() %>" class="botao botao-perigo" style="justify-content: center; text-decoration: none;" onclick="return confirm('Deseja realmente excluir este produto?');">
                        Excluir produto
                    </a>
                </div>
            </div>
        </div>
    </div>
</main>
</body>
</html>
