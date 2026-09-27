<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html lang="pt-BR">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Precifico • Cadastro do Vendedor</title>
    <link rel="stylesheet" href="css/estilo.css">
</head>
<body>
<header>
    <div class="logotipo">Precifico</div>
</header>

<main style="max-width: 640px;">
    <%
        String msgErro = (String) request.getAttribute("msgErro");
        if (msgErro != null && !msgErro.trim().isEmpty()) { 
    %>
        <div class="aviso perigo" style="margin-bottom: 24px;"><%= msgErro %></div>
    <% } %>

    <form action="ControleVendedor" method="POST" class="painel" novalidate>
        <input type="hidden" name="btnop" value="Cadastrar">
        <h2>Cadastro do vendedor</h2>
        
        <div class="campos">
            <div class="campo linha-inteira">
                <label for="nome">Nome completo *</label>
                <input id="nome" name="nome" required maxlength="100" placeholder="Seu nome completo" autofocus>
            </div>

            <div class="campo linha-inteira">
                <label for="email">E-mail *</label>
                <input id="email" name="email" type="email" required maxlength="150" placeholder="voce@exemplo.com">
            </div>

            <div class="campo">
                <label for="senha">Senha *</label>
                <input id="senha" name="senha" type="password" required minlength="6">
                <small>No mínimo 6 caracteres.</small>
            </div>

            <div class="campo">
                <label for="confirmaSenha">Confirme a senha *</label>
                <input id="confirmaSenha" name="confirmaSenha" type="password" required minlength="6">
                <small>Confirme a mesma senha informada ao lado.</small>
            </div>

            <div class="campo">
                <label for="cpf">CPF</label>
                <input id="cpf" name="cpf" maxlength="14" placeholder="000.000.000-00">
            </div>

            <div class="campo">
                <label for="idade">Idade</label>
                <input id="idade" name="idade" type="number" min="16" max="120" placeholder="Ex.: 28">
            </div>

            <div class="campo linha-inteira">
                <label for="endereco">Endereço</label>
                <input id="endereco" name="endereco" maxlength="200" placeholder="Rua, número, bairro, cidade">
            </div>
        </div>

        <div class="acoes">
            <a href="login.jsp" class="botao" style="text-decoration:none;">Já possuo conta</a>
            <button class="primario" type="submit">Cadastrar vendedor e continuar →</button>
        </div>
    </form>
</main>
</body>
</html>
