function iniciarPaginaCadastroVendedor({ sessao, authServico }) {
    verificarMensagemFlash('mensagens', sessao);

    document.getElementById('form-cadastro-vendedor').addEventListener('submit', async function (evento) {
        evento.preventDefault();

        const textoIdade = document.getElementById('idade').value.trim();
        const dadosVendedor = {
            nome: document.getElementById('nome').value,
            email: document.getElementById('email').value,
            senha: document.getElementById('senha').value,
            confirmaSenha: document.getElementById('confirmaSenha').value,
            cpf: document.getElementById('cpf').value,
            idade: textoIdade !== '' ? Number(textoIdade) : null,
            endereco: document.getElementById('endereco').value
        };

        try {
            const resposta = await authServico.cadastrar(dadosVendedor);
            if (resposta.sucesso && resposta.dados) {
                sessao.salvarVendedor(resposta.dados);
                sessao.salvarMensagemFlash('sucesso', resposta.mensagem);
                window.location.href = 'produtos.html';
                return;
            }

            exibirAlerta('mensagens', 'perigo', resposta.mensagem || 'Erro ao cadastrar vendedor.');
        } catch (erro) {
            exibirAlerta('mensagens', 'perigo', 'Erro de comunicação com o servidor Spring Boot.');
        }
    });
}

iniciarPaginaCadastroVendedor(window.PrecificoApp);
