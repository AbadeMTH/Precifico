class SessaoLocal {
    constructor(storageLocal = localStorage, storageSessao = sessionStorage) {
        this.storageLocal = storageLocal;
        this.storageSessao = storageSessao;
        this.chaveVendedor = 'precifico_vendedor_logado';
        this.chaveFlash = 'precifico_mensagem_flash';
    }

    salvarVendedor(vendedor) {
        this.storageLocal.setItem(this.chaveVendedor, JSON.stringify(vendedor));
    }

    obterVendedor() {
        try {
            const texto = this.storageLocal.getItem(this.chaveVendedor);
            return texto ? JSON.parse(texto) : null;
        } catch (erro) {
            return null;
        }
    }

    limparVendedor() {
        this.storageLocal.removeItem(this.chaveVendedor);
    }

    salvarMensagemFlash(tipo, texto) {
        this.storageSessao.setItem(this.chaveFlash, JSON.stringify({ tipo, texto }));
    }

    consumirMensagemFlash() {
        try {
            const texto = this.storageSessao.getItem(this.chaveFlash);
            if (!texto) {
                return null;
            }
            this.storageSessao.removeItem(this.chaveFlash);
            return JSON.parse(texto);
        } catch (erro) {
            return null;
        }
    }
}
