class HttpClient {
    constructor(urlBase) {
        this.urlBase = urlBase;
    }

    async requisitar(caminho, metodo, corpo = null) {
        const opcoes = {
            method: metodo,
            credentials: 'include',
            headers: {}
        };

        if (corpo !== null) {
            opcoes.headers['Content-Type'] = 'application/json;charset=UTF-8';
            opcoes.body = JSON.stringify(corpo);
        }

        const resposta = await fetch(this.urlBase + caminho, opcoes);
        const json = await this.lerJsonSeguro(resposta);

        return {
            status: resposta.status,
            ok: resposta.ok,
            sucesso: Boolean(json.sucesso),
            mensagem: json.mensagem || '',
            dados: json.dados !== undefined ? json.dados : null
        };
    }

    async lerJsonSeguro(resposta) {
        try {
            return await resposta.json();
        } catch (erro) {
            return {};
        }
    }

    get(caminho) {
        return this.requisitar(caminho, 'GET');
    }

    post(caminho, corpo = null) {
        return this.requisitar(caminho, 'POST', corpo);
    }

    put(caminho, corpo) {
        return this.requisitar(caminho, 'PUT', corpo);
    }

    patch(caminho, corpo = null) {
        return this.requisitar(caminho, 'PATCH', corpo);
    }

    delete(caminho) {
        return this.requisitar(caminho, 'DELETE');
    }
}
