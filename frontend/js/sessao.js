class SessaoLocal {
    constructor() {
        this.chaveVendedor = 'precifico_vendedor_logado';
        this.chaveFlash = 'precifico_mensagem_flash';
    }

    salvarVendedor(vendedor) {
        localStorage.setItem(this.chaveVendedor, JSON.stringify(vendedor));
    }

    obterVendedor() {
        try {
            const texto = localStorage.getItem(this.chaveVendedor);
            return texto ? JSON.parse(texto) : null;
        } catch (erro) {
            return null;
        }
    }

    limparVendedor() {
        localStorage.removeItem(this.chaveVendedor);
    }

    salvarMensagemFlash(tipo, texto) {
        sessionStorage.setItem(this.chaveFlash, JSON.stringify({ tipo, texto }));
    }

    consumirMensagemFlash() {
        try {
            const texto = sessionStorage.getItem(this.chaveFlash);
            if (!texto) {
                return null;
            }
            sessionStorage.removeItem(this.chaveFlash);
            return JSON.parse(texto);
        } catch (erro) {
            return null;
        }
    }
}
