<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String msg = (String) request.getAttribute("msg");
    if (msg == null) msg = (String) request.getAttribute("msgSucesso");
    if (msg == null) msg = "Operação realizada com sucesso.";
%>
<!doctype html>
<html lang="pt-BR">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Precifico • Sucesso</title>
    <link rel="stylesheet" href="css/estilo.css">
</head>
<body>
<header>
    <div class="logotipo">Precifico</div>
</header>

<main style="max-width: 600px;">
    <div class="painel" style="text-align: center; padding: 40px 30px;">
        <div class="sobretitulo" style="margin-bottom: 12px;">SUCESSO</div>
        <h1 style="font-size: 26px; margin-bottom: 16px;">Operação Concluída</h1>
        <div class="aviso sucesso" style="font-size: 15px; margin: 20px 0;">
            <%= msg %>
        </div>
        <div class="acoes" style="justify-content: center; margin-top: 30px;">
            <a href="ControleProduto?btnop=ConsultarTodos" class="botao botao-primario" style="text-decoration: none;">Voltar para Meus Produtos</a>
        </div>
    </div>
</main>
</body>
</html>
