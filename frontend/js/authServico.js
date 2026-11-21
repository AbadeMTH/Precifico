class AuthServico {
    constructor(httpClient) {
        this.httpClient = httpClient;
    }

    entrar(credenciais) {
        return this.httpClient.post('/api/auth/login', credenciais);
    }

    cadastrar(dadosVendedor) {
        return this.httpClient.post('/api/vendedores', dadosVendedor);
    }

    sair() {
        return this.httpClient.post('/api/auth/logout');
    }
}
