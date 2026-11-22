function iniciarPaginaLogin({ sessao, authServico }) {
    verificarMensagemFlash('mensagens', sessao);

    document.getElementById('form-login').addEventListener('submit', async function (evento) {
        evento.preventDefault();
        const email = document.getElementById('email').value;
        const senha = document.getElementById('senha').value;

        try {
            const resposta = await authServico.entrar({ email, senha });

            if (resposta.sucesso && resposta.dados) {
                sessao.salvarVendedor(resposta.dados);
                sessao.salvarMensagemFlash('sucesso', resposta.mensagem);
                window.location.href = 'produtos.html';
                return;
            }

            exibirAlerta('mensagens', 'perigo', resposta.mensagem || 'Não foi possível realizar o login.');
        } catch (erro) {
            exibirAlerta('mensagens', 'perigo', 'Erro de conexão com o servidor Spring Boot (verifique se está rodando na porta 8080).');
        }
    });
}

iniciarPaginaLogin(window.PrecificoApp);
