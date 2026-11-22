class PromocaoServico {
    constructor(httpClient) {
        this.httpClient = httpClient;
    }

    promover(id, dadosPromocao) {
        return this.httpClient.put('/api/produtos/' + encodeURIComponent(id) + '/promocao', dadosPromocao);
    }

    removerPromocao(id) {
        return this.httpClient.delete('/api/produtos/' + encodeURIComponent(id) + '/promocao');
    }
}
