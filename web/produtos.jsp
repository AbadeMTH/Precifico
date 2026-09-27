<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Produto" %>
<%@ page import="model.Vendedor" %>
<%@ page import="model.Precificacao" %>
<%@ page import="java.util.List" %>
<%
    Vendedor vendedorLogado = (Vendedor) session.getAttribute("vendedorLogado");
    if (vendedorLogado == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    List<Produto> listaProdutos = (List<Produto>) request.getAttribute("listaProdutos");
    if (listaProdutos == null) {
        // Redireciona via controlador caso a página seja acessada diretamente
        response.sendRedirect("ControleProduto?btnop=ConsultarTodos");
        return;
    }
%>
<!doctype html>
<html lang="pt-BR">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Precifico • Gerenciador de Produtos</title>
    <link rel="stylesheet" href="css/estilo.css">
</head>
<body>
<header>
    <div class="logotipo">Precifico</div>
    <div class="cabecalho-usuario">
        <span>Vendedor: <strong><%= vendedorLogado.getNome() %></strong></span>
        <a href="ControleVendedor?btnop=Sair" class="botao botao-pequeno botao-perigo" style="text-decoration:none;">Sair</a>
    </div>
</header>

<main>
    <div class="cabecalho-secao">
        <div>
            <h1>Meus Produtos Cadastrados</h1>
        </div>
        <a href="formulario_produto.jsp" class="botao botao-primario" style="text-decoration:none;">+ Novo Produto</a>
    </div>

    <% 
        String msgErro = (String) request.getAttribute("msgErro");
        String msgSucesso = (String) request.getAttribute("msgSucesso");
        if (msgErro != null && !msgErro.trim().isEmpty()) { 
    %>
        <div class="aviso perigo" style="margin-bottom: 24px;"><%= msgErro %></div>
    <% } %>

    <% if (msgSucesso != null && !msgSucesso.trim().isEmpty()) { %>
        <div class="aviso sucesso" style="margin-bottom: 24px;"><%= msgSucesso %></div>
    <% } %>

    <div class="painel" style="padding: 0; overflow: hidden;">
        <% if (listaProdutos.isEmpty()) { %>
            <div class="painel-vazio">
                <h2 style="margin-bottom: 8px;">Nenhum produto cadastrado ainda</h2>
                <p>Cadastre seu primeiro produto para começar a calcular preços e gerenciar suas vendas.</p>
                <a href="formulario_produto.jsp" class="botao botao-primario" style="margin-top: 10px; text-decoration:none;">Cadastrar primeiro produto →</a>
            </div>
        <% } else { %>
            <div class="tabela-responsiva">
                <table>
                    <thead>
                        <tr>
                            <th style="width: 70px;">Foto</th>
                            <th>Código</th>
                            <th>Produto / Descrição</th>
                            <th>Custo</th>
                            <th>Preço de Venda</th>
                            <th>Lucro Estimado</th>
                            <th>Situação</th>
                            <th style="text-align: right;">Ações</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Produto p : listaProdutos) { 
                            Precificacao prec = p.getPrecificacao();
                            double custo = prec != null ? prec.getCustoProduto() : 0.0;
                            double venda = prec != null ? prec.getValorVenda() : 0.0;
                            double lucro = prec != null ? prec.getLucro() : 0.0;
                            double margem = prec != null ? prec.getMargemEfetiva() : 0.0;
                            boolean disponivel = "DISPONIVEL".equalsIgnoreCase(p.getSituacao());
                        %>
                        <tr>
                            <td>
                                <img src="<%= p.getImagem() %>" alt="<%= p.getNome() %>" class="miniatura" onerror="this.src='https://via.placeholder.com/54?text=Sem+Foto'">
                            </td>
                            <td>
                                <strong><%= p.getCodigoProduto() %></strong>
                            </td>
                            <td>
                                <strong><%= p.getNome() %></strong>
                                <small style="max-width: 280px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;"><%= p.getDescricao() %></small>
                            </td>
                            <td>
                                R$ <%= String.format("%.2f", custo) %>
                            </td>
                            <td>
                                <strong>R$ <%= String.format("%.2f", venda) %></strong>
                            </td>
                            <td>
                                <strong style="color: <%= lucro >= 0 ? "var(--cor-marca)" : "#9a382c" %>;">
                                    R$ <%= String.format("%.2f", lucro) %>
                                </strong>
                                <small><%= String.format("%.1f", margem) %>% sobre o custo</small>
                            </td>
                            <td>
                                <span class="selo <%= disponivel ? "selo-disponivel" : "selo-vendido" %>">
                                    <%= p.getSituacao() %>
                                </span>
                            </td>
                            <td style="text-align: right; white-space: nowrap;">
                                <a href="ControleProduto?btnop=ConsultarPorId&id=<%= p.getId() %>" class="botao botao-pequeno" title="Visualizar Detalhes e Precificação">Visualizar</a>
                                <a href="ControleProduto?btnop=Edita&id=<%= p.getId() %>" class="botao botao-pequeno" title="Editar Produto">Editar</a>
                                <a href="ControleProduto?btnop=AlterarSituacao&id=<%= p.getId() %>" class="botao botao-pequeno" title="Alternar Disponível / Vendido">
                                    <%= disponivel ? "Marcar Vendido" : "Reativar" %>
                                </a>
                                <a href="ControleProduto?btnop=Deletar&id=<%= p.getId() %>" class="botao botao-pequeno botao-perigo" onclick="return confirm('Tem certeza que deseja excluir o produto <%= p.getNome() %>?');" title="Excluir Produto">Excluir</a>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        <% } %>
    </div>
</main>
</body>
</html>
