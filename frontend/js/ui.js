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
