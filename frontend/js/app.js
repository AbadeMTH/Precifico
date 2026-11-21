function obterUrlBaseApi() {
    const host = window.location.hostname || 'localhost';
    return 'http://' + host + ':8080';
}

function criarAplicacao() {
    const urlBaseApi = obterUrlBaseApi();
    const httpClient = new HttpClient(urlBaseApi);
    const sessao = new SessaoLocal();
    const authServico = typeof AuthServico !== 'undefined' ? new AuthServico(httpClient) : null;
    const produtoServico = typeof ProdutoServico !== 'undefined' ? new ProdutoServico(httpClient) : null;

    return {
        httpClient: httpClient,
        sessao: sessao,
        authServico: authServico,
        produtoServico: produtoServico
    };
}

window.PrecificoApp = criarAplicacao();
