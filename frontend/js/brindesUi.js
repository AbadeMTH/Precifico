const CATALOGO_BRINDES = [
    { codigo: 'BALA', rotulo: 'Bala' },
    { codigo: 'PIRULITO', rotulo: 'Pirulito' },
    { codigo: 'ADESIVO', rotulo: 'Adesivo' }
];

function formatarRotuloBrinde(codigo) {
    const chave = String(codigo || '').trim().toUpperCase();
    for (const item of CATALOGO_BRINDES) {
        if (item.codigo === chave) {
            return item.rotulo;
        }
    }
    return codigo;
}

function renderizarOpcoesBrindes(containerId) {
    const container = document.getElementById(containerId);
    if (!container) {
        return;
    }

    container.innerHTML = CATALOGO_BRINDES.map(function (item) {
        const idCampo = 'brinde-' + item.codigo.toLowerCase();
        return `
            <label class="opcao-brinde" for="${idCampo}">
                <input type="checkbox" id="${idCampo}" name="brindes" value="${item.codigo}">
                <span>${escaparHtml(item.rotulo)}</span>
            </label>
        `;
    }).join('');
}

function renderizarSelosBrindes(brindes, textoVazio = '') {
    const lista = Array.isArray(brindes) ? brindes : [];
    if (lista.length === 0) {
        return textoVazio;
    }

    const selos = lista.map(function (codigo) {
        return `<span class="selo selo-brinde">+ ${escaparHtml(formatarRotuloBrinde(codigo))}</span>`;
    }).join('');

    return `<div class="lista-brindes-selos">${selos}</div>`;
}

function obterBrindesSelecionados() {
    const checkboxes = document.querySelectorAll('input[name="brindes"]:checked');
    const selecionados = [];
    checkboxes.forEach(function (checkbox) {
        const valor = String(checkbox.value || '').trim().toUpperCase();
        if (valor !== '' && !selecionados.includes(valor)) {
            selecionados.push(valor);
        }
    });
    return selecionados;
}

function preencherBrindesSelecionados(brindes) {
    const selecionados = Array.isArray(brindes)
        ? brindes.map(function (b) { return String(b || '').trim().toUpperCase(); })
        : [];

    document.querySelectorAll('input[name="brindes"]').forEach(function (checkbox) {
        checkbox.checked = selecionados.includes(checkbox.value.toUpperCase());
    });
}
