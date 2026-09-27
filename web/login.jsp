<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html lang="pt-BR">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Precifico • Entrar</title>
    <link rel="stylesheet" href="css/estilo.css">
</head>
<body>
<header>
    <div class="logotipo">Precifico</div>
</header>

<main style="max-width: 520px;">
    <div class="cabecalho-secao" style="justify-content: center; text-align: center; margin-bottom: 20px;">
        <div>
            <div class="sobretitulo">ACESSO DO VENDEDOR</div>
            <h1>Entrar na sua conta</h1>
        </div>
    </div>

    <% 
        String msgErro = (String) request.getAttribute("msgErro");
        String msgSucesso = (String) request.getAttribute("msgSucesso");
        if (msgErro != null && !msgErro.trim().isEmpty()) { 
    %>
        <div class="aviso perigo" style="margin-bottom: 20px;"><%= msgErro %></div>
    <% } %>

    <% if (msgSucesso != null && !msgSucesso.trim().isEmpty()) { %>
        <div class="aviso sucesso" style="margin-bottom: 20px;"><%= msgSucesso %></div>
    <% } %>

    <div class="painel">
        <form action="ControleVendedor" method="POST" novalidate>
            <input type="hidden" name="btnop" value="Entrar">
            <div class="campos" style="grid-template-columns: 1fr;">
                <div class="campo">
                    <label for="email">E-mail *</label>
                    <input id="email" name="email" type="email" required placeholder="seu@email.com" autofocus>
                </div>
                <div class="campo">
                    <label for="senha">Senha *</label>
                    <input id="senha" name="senha" type="password" required placeholder="Sua senha de acesso">
                </div>
            </div>

            <div class="acoes" style="justify-content: space-between; align-items: center; margin-top: 24px;">
                <a href="cadastro_vendedor.jsp" style="color: var(--cor-marca); font-size: 14px; text-decoration: none; font-weight: 600;">Criar nova conta de vendedor →</a>
                <button type="submit" class="primario">Entrar no sistema</button>
            </div>
        </form>
    </div>
</main>
</body>
</html>
