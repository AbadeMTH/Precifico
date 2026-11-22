class ProdutoServico {
    constructor(httpClient) {
        this.httpClient = httpClient;
    }

    listarTodos() {
        return this.httpClient.get('/api/produtos');
    }

    consultarPorId(id) {
        return this.httpClient.get('/api/produtos/' + encodeURIComponent(id));
    }

    cadastrar(dadosProduto) {
        return this.httpClient.post('/api/produtos', dadosProduto);
    }

    atualizar(id, dadosProduto) {
        return this.httpClient.put('/api/produtos/' + encodeURIComponent(id), dadosProduto);
    }

    alterarSituacao(id) {
        return this.httpClient.patch('/api/produtos/' + encodeURIComponent(id) + '/situacao');
    }

    deletar(id) {
        return this.httpClient.delete('/api/produtos/' + encodeURIComponent(id));
    }

    simular(dadosSimulacao) {
        return this.httpClient.post('/api/produtos/simular', dadosSimulacao);
    }
}
