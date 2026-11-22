function tratarNaoAutorizado(resposta, sessao) {
    if (resposta.status !== 401) {
        return false;
    }

    sessao.limparVendedor();
    sessao.salvarMensagemFlash('perigo', resposta.mensagem || 'Sessão expirada. Faça login novamente.');
    window.location.href = 'login.html';
    return true;
}

function exigirAutenticacao(sessao) {
    const vendedor = sessao.obterVendedor();
    if (!vendedor || !vendedor.id) {
        sessao.salvarMensagemFlash('perigo', 'Acesso restrito. Faça login para continuar.');
        window.location.href = 'login.html';
        return null;
    }

    const spanNome = document.getElementById('nome-vendedor-logado');
    if (spanNome) {
        spanNome.textContent = vendedor.nome;
    }

    return vendedor;
}

function configurarBotaoSair(botaoId, sessao, authServico) {
    const btnSair = document.getElementById(botaoId);
    if (!btnSair || !authServico) {
        return;
    }

    btnSair.addEventListener('click', async function (evento) {
        evento.preventDefault();
        try {
            await authServico.sair();
        } catch (erroIgnorado) {
        }
        sessao.limparVendedor();
        sessao.salvarMensagemFlash('sucesso', 'Você saiu da sua conta com segurança.');
        window.location.href = 'login.html';
    });
}
