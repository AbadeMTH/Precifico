<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String msg = (String) request.getAttribute("msg");
    if (msg == null) msg = (String) request.getAttribute("msgErro");
    if (msg == null) msg = "Ocorreu um erro inesperado ao processar a solicitação.";
%>
<!doctype html>
<html lang="pt-BR">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Precifico • Atenção</title>
    <link rel="stylesheet" href="css/estilo.css">
</head>
<body>
<header>
    <div class="logotipo">Precifico</div>
</header>

<main style="max-width: 600px;">
    <div class="painel" style="text-align: center; padding: 40px 30px;">
        <div class="sobretitulo" style="color: #9a382c; margin-bottom: 12px;">AVISO</div>
        <h1 style="font-size: 26px; margin-bottom: 16px;">Não foi possível concluir a ação</h1>
        <div class="aviso perigo" style="font-size: 15px; margin: 20px 0;">
            <%= msg %>
        </div>
        <p style="font-size: 13px; color: var(--cor-atenuada);">Verifique os dados informados e tente novamente.</p>
        <div class="acoes" style="justify-content: center; margin-top: 30px;">
            <button onclick="history.back();" class="botao">← Voltar</button>
            <a href="ControleProduto?btnop=ConsultarTodos" class="botao botao-primario" style="text-decoration: none;">Ir para Meus Produtos</a>
        </div>
    </div>
</main>
</body>
</html>
