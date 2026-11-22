function iniciarPaginaDetalheProduto({ sessao, produtoServico }) {
    const vendedor = exigirAutenticacao(sessao);
    const paramsUrl = new URLSearchParams(window.location.search);
    const idProduto = paramsUrl.get('id');

    if (!idProduto) {
        window.location.href = 'produtos.html';
        return;
    }

    async function carregarDetalhes() {
        try {
            const resposta = await produtoServico.consultarPorId(idProduto);
            if (tratarNaoAutorizado(resposta, sessao)) {
                return;
            }

            const detalhes = resposta.dados;
            const produto = detalhes ? detalhes.produto : null;
            if (!resposta.sucesso || !produto) {
                sessao.salvarMensagemFlash('perigo', resposta.mensagem || 'Produto não encontrado.');
                window.location.href = 'produtos.html';
                return;
            }

            const precificacao = produto.precificacao || {};
            const custo = precificacao.custoProduto || 0.0;
            const venda = precificacao.valorVenda || 0.0;
            const lucro = precificacao.lucro || 0.0;
            const margem = detalhes.margemEfetiva !== undefined ? detalhes.margemEfetiva : (precificacao.margemEfetiva || 0.0);
            const diagnostico = detalhes.diagnosticoComercial || 'Sem precificação cadastrada.';
            const disponivel = String(produto.situacao).toUpperCase() === 'DISPONIVEL';

            document.title = `Precifico • Detalhes de ${produto.nome}`;
            document.getElementById('detalhe-nome').textContent = produto.nome;

            const selo = document.getElementById('detalhe-selo');
            selo.textContent = produto.situacao;
            selo.className = `selo ${disponivel ? 'selo-disponivel' : 'selo-vendido'}`;

            const imagem = document.getElementById('detalhe-imagem');
            imagem.src = obterUrlImagemProduto(produto.imagem);
            imagem.alt = produto.nome;

            document.getElementById('detalhe-codigo').textContent = produto.codigoProduto;
            document.getElementById('detalhe-descricao').textContent = produto.descricao;
            document.getElementById('detalhe-brindes').innerHTML = renderizarSelosBrindes(produto.brindes, 'Nenhum brinde');

            const linkImagem = document.getElementById('detalhe-url-imagem');
            linkImagem.href = produto.imagem || '#';
            linkImagem.textContent = produto.imagem || '—';

            document.getElementById('detalhe-custo').textContent = `R$ ${formatarDecimal(custo, 2)}`;
            document.getElementById('detalhe-venda').textContent = `R$ ${formatarDecimal(venda, 2)}`;

            const elementoLucro = document.getElementById('detalhe-lucro');
            elementoLucro.textContent = `R$ ${formatarDecimal(lucro, 2)}`;
            elementoLucro.style.color = lucro >= 0 ? 'var(--cor-marca)' : '#9a382c';

            document.getElementById('detalhe-margem').textContent = `${formatarDecimal(margem, 1)}%`;

            const boxPromocao = document.getElementById('box-promocao');
            if (precificacao.emPromocao && precificacao.valorPromocional != null) {
                boxPromocao.style.display = 'block';
                document.getElementById('detalhe-promocao').textContent =
                    `DE R$ ${formatarDecimal(venda, 2)} PARA R$ ${formatarDecimal(precificacao.valorPromocional, 2)} (${formatarDecimal(precificacao.percentualDesconto, 2)}% de desconto)`;
            } else {
                boxPromocao.style.display = 'none';
            }

            const boxDiagnostico = document.getElementById('box-diagnostico');
            boxDiagnostico.className = `aviso ${lucro >= 0 ? 'sucesso' : 'perigo'}`;
            document.getElementById('detalhe-diagnostico').textContent = diagnostico;

            document.getElementById('btn-editar').href = `formulario_produto.html?id=${produto.id}`;
            document.getElementById('btn-promover').href = `promover_produto.html?id=${produto.id}`;

            const btnSituacao = document.getElementById('btn-situacao');
            btnSituacao.textContent = disponivel ? 'Alterar para Vendido' : 'Reativar Produto';
            btnSituacao.onclick = async function () {
                const respostaSituacao = await produtoServico.alterarSituacao(produto.id);
                if (tratarNaoAutorizado(respostaSituacao, sessao)) {
                    return;
                }
                if (respostaSituacao.sucesso) {
                    sessao.salvarMensagemFlash('sucesso', respostaSituacao.mensagem);
                    window.location.href = 'produtos.html';
                    return;
                }
                exibirAlerta('mensagens', 'perigo', respostaSituacao.mensagem || 'Erro ao alterar situação do produto.');
            };

            const btnExcluir = document.getElementById('btn-excluir');
            btnExcluir.onclick = async function () {
                if (!confirm('Deseja realmente excluir este produto?')) {
                    return;
                }
                const respostaExclusao = await produtoServico.deletar(produto.id);
                if (tratarNaoAutorizado(respostaExclusao, sessao)) {
                    return;
                }
                if (respostaExclusao.sucesso) {
                    sessao.salvarMensagemFlash('sucesso', respostaExclusao.mensagem);
                    window.location.href = 'produtos.html';
                    return;
                }
                exibirAlerta('mensagens', 'perigo', respostaExclusao.mensagem || 'Erro ao excluir o produto.');
            };
        } catch (erro) {
            exibirAlerta('mensagens', 'perigo', 'Erro ao carregar os detalhes do produto.');
        }
    }

    if (vendedor) {
        carregarDetalhes();
    }
}

iniciarPaginaDetalheProduto(window.PrecificoApp);
