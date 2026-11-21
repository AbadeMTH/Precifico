function formatarMoeda(valor) {
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(Number(valor) || 0);
}

function formatarDecimal(valor, casas = 2) {
    return (Number(valor) || 0).toLocaleString('pt-BR', {
        minimumFractionDigits: casas,
        maximumFractionDigits: casas
    });
}

function formatarPorcentagem(valor) {
    return new Intl.NumberFormat('pt-BR', { maximumFractionDigits: 2 }).format(Number(valor) || 0) + '%';
}

function escaparHtml(texto) {
    const div = document.createElement('div');
    div.textContent = texto || '';
    return div.innerHTML;
}

function exibirAlerta(containerId, tipo, texto) {
    const container = document.getElementById(containerId);
    if (!container) {
        return;
    }
    if (!texto) {
        container.innerHTML = '';
        return;
    }

    const div = document.createElement('div');
    div.className = 'aviso ' + tipo;
    div.style.marginBottom = '24px';
    div.textContent = texto;
    container.innerHTML = '';
    container.appendChild(div);
}

function verificarMensagemFlash(containerId, sessao) {
    const flash = sessao.consumirMensagemFlash();
    if (flash && flash.texto) {
        exibirAlerta(containerId, flash.tipo, flash.texto);
    }
}

function classificarEstiloAviso(custo, venda, diagnostico) {
    if (custo <= 0 || venda <= 0) {
        return 'aviso';
    }
    if (venda < custo) {
        return 'perigo';
    }
    if (diagnostico && diagnostico.includes('próximo')) {
        return 'sucesso';
    }
    if (diagnostico && (diagnostico.includes('Atenção') || diagnostico.includes('abaixo') || diagnostico.includes('acima'))) {
        return 'atencao';
    }
    return 'aviso';
}

function tratarNaoAutorizado(resposta, sessao) {
    if (resposta.status !== 401) {
        return false;
    }

    sessao.limparVendedor();
    sessao.salvarMensagemFlash('perigo', resposta.mensagem || 'Sessão expirada. Faça login novamente.');
    window.location.href = 'login.html';
    return true;
}

function exigirAutenticacao(sessao, authServico = null) {
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

    const btnSair = document.getElementById('btn-sair');
    if (btnSair && authServico) {
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

    return vendedor;
}
