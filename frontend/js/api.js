// Configuração central da API Spring Boot
const API_BASE_URL = 'http://localhost:8080';

const CHAVE_VENDEDOR = 'precifico_vendedor_logado';
const CHAVE_FLASH = 'precifico_mensagem_flash';

// Formatadores numéricos padrão PT-BR
const formatarMoeda = valor =>
    new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(Number(valor) || 0);

const formatarDecimal = (valor, casas = 2) =>
    (Number(valor) || 0).toLocaleString('pt-BR', {
        minimumFractionDigits: casas,
        maximumFractionDigits: casas
    });

// Gerenciamento de sessão no navegador
function salvarVendedorLogado(vendedor) {
    localStorage.setItem(CHAVE_VENDEDOR, JSON.stringify(vendedor));
}

function obterVendedorLogado() {
    try {
        const raw = localStorage.getItem(CHAVE_VENDEDOR);
        return raw ? JSON.parse(raw) : null;
    } catch (e) {
        return null;
    }
}

function limparSessaoLocal() {
    localStorage.removeItem(CHAVE_VENDEDOR);
}

// Mensagens temporárias entre redirecionamentos de tela
function salvarMensagemFlash(tipo, texto) {
    sessionStorage.setItem(CHAVE_FLASH, JSON.stringify({ tipo, texto }));
}

function consumirMensagemFlash() {
    try {
        const raw = sessionStorage.getItem(CHAVE_FLASH);
        if (!raw) return null;
        sessionStorage.removeItem(CHAVE_FLASH);
        return JSON.parse(raw);
    } catch (e) {
        return null;
    }
}

function exibirAlerta(containerId, tipo, texto) {
    const container = document.getElementById(containerId);
    if (!container) return;
    if (!texto) {
        container.innerHTML = '';
        return;
    }
    const div = document.createElement('div');
    div.className = `aviso ${tipo}`;
    div.style.marginBottom = '24px';
    div.textContent = texto;
    container.innerHTML = '';
    container.appendChild(div);
}

function verificarMensagemFlash(containerId) {
    const flash = consumirMensagemFlash();
    if (flash && flash.texto) {
        exibirAlerta(containerId, flash.tipo, flash.texto);
    }
}

// Proteção de páginas autenticadas e preenchimento do cabeçalho
function exigirAutenticacao() {
    const vendedor = obterVendedorLogado();
    if (!vendedor || !vendedor.id) {
        salvarMensagemFlash('perigo', 'Acesso restrito. Faça login para continuar.');
        window.location.href = 'login.html';
        return null;
    }

    const spanNome = document.getElementById('nome-vendedor-logado');
    if (spanNome) {
        spanNome.textContent = vendedor.nome;
    }

    const btnSair = document.getElementById('btn-sair');
    if (btnSair) {
        btnSair.addEventListener('click', async (e) => {
            e.preventDefault();
            try {
                await requisitarComando('/ControleVendedor', { btnop: 'Sair' }, 'POST');
            } catch (ignorado) {}
            limparSessaoLocal();
            salvarMensagemFlash('sucesso', 'Você saiu da sua conta com segurança.');
            window.location.href = 'login.html';
        });
    }

    return vendedor;
}

// Função genérica para acionar os Controladores / Commands no Spring Boot
async function requisitarComando(caminho, parametros = {}, metodo = 'POST') {
    const vendedor = obterVendedorLogado();
    const headers = {};

    if (vendedor && vendedor.id) {
        headers['X-Vendedor-Id'] = String(vendedor.id);
    }

    const opcoes = {
        method: metodo,
        headers,
        credentials: 'include'
    };

    let url = `${API_BASE_URL}${caminho}`;
    const searchParams = new URLSearchParams();
    Object.entries(parametros).forEach(([chave, valor]) => {
        if (valor !== undefined && valor !== null) {
            searchParams.append(chave, String(valor));
        }
    });

    if (metodo.toUpperCase() === 'GET') {
        const queryString = searchParams.toString();
        if (queryString) {
            url += (url.includes('?') ? '&' : '?') + queryString;
        }
    } else {
        headers['Content-Type'] = 'application/x-www-form-urlencoded;charset=UTF-8';
        opcoes.body = searchParams.toString();
    }

    const resposta = await fetch(url, opcoes);
    const dados = await resposta.json();

    if (resposta.status === 401 && !caminho.includes('ControleVendedor')) {
        limparSessaoLocal();
        salvarMensagemFlash('perigo', dados.msgErro || 'Sessão expirada. Faça login novamente.');
        window.location.href = 'login.html';
        throw new Error('Não autenticado');
    }

    return { status: resposta.status, ok: resposta.ok, dados };
}
