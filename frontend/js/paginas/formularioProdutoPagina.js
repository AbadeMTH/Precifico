function iniciarPaginaFormularioProduto({ sessao, produtoServico }) {
    const vendedor = exigirAutenticacao(sessao);
    verificarMensagemFlash('mensagens', sessao);
    renderizarOpcoesBrindes('secao-brindes');

    function obterCampo(id) {
        return document.getElementById(id);
    }

    const paramsUrl = new URLSearchParams(window.location.search);
    const idEdicao = paramsUrl.get('id');
    const modoEdicao = Boolean(idEdicao);

    let precoSugeridoAtual = 0.0;

    function obterValores() {
        return {
            custo: obterCampo('custo').value === '' ? NaN : Number(obterCampo('custo').value),
            margem: obterCampo('margem').value === '' ? NaN : Number(obterCampo('margem').value),
            venda: obterCampo('venda').value === '' ? NaN : Number(obterCampo('venda').value)
        };
    }

    function limparSimulacao() {
        obterCampo('sugestao').textContent = 'R$ —';
        obterCampo('formula').textContent = 'Preencha o custo e o percentual para calcular a sugestão.';
        obterCampo('usar-preco').disabled = true;
        obterCampo('lucro').textContent = 'R$ —';
        obterCampo('margem-efetiva').textContent = '—%';
        obterCampo('aviso-preco').textContent = 'Preencha os valores para visualizar a análise de resultado.';
        obterCampo('aviso-preco').className = 'aviso';
        precoSugeridoAtual = 0.0;
    }

    async function simularPrecificacao() {
        const { custo, margem, venda } = obterValores();
        const custoValido = Number.isFinite(custo) && custo > 0;
        const margemValida = Number.isFinite(margem) && margem >= 0;
        const vendaValida = Number.isFinite(venda) && venda > 0;

        if (!custoValido) {
            limparSimulacao();
            return;
        }

        try {
            const resposta = await produtoServico.simular({
                custo: custo,
                margem: margemValida ? margem : 0,
                valorVenda: vendaValida ? venda : 0
            });

            if (!resposta.sucesso || !resposta.dados) {
                return;
            }

            const simulacao = resposta.dados;
            if (margemValida && simulacao.precoSugerido > 0) {
                precoSugeridoAtual = simulacao.precoSugerido;
                obterCampo('sugestao').textContent = formatarMoeda(simulacao.precoSugerido);
                obterCampo('formula').textContent = formatarMoeda(custo) + ' de custo + ' + formatarPorcentagem(margem) + ' de margem desejada.';
                obterCampo('usar-preco').disabled = false;
            } else {
                obterCampo('sugestao').textContent = 'R$ —';
                obterCampo('formula').textContent = 'Preencha o percentual para calcular a sugestão.';
                obterCampo('usar-preco').disabled = true;
            }

            if (vendaValida) {
                const estiloAviso = classificarEstiloAviso(custo, venda, simulacao.diagnostico);
                obterCampo('lucro').textContent = formatarMoeda(simulacao.lucro);
                obterCampo('lucro').style.color = simulacao.lucro >= 0 ? 'var(--cor-marca)' : '#9a382c';
                obterCampo('margem-efetiva').textContent = formatarPorcentagem(simulacao.margemEfetiva);
                obterCampo('aviso-preco').textContent = simulacao.diagnostico;
                obterCampo('aviso-preco').className = 'aviso ' + estiloAviso;
            } else {
                obterCampo('lucro').textContent = 'R$ —';
                obterCampo('margem-efetiva').textContent = '—%';
                obterCampo('aviso-preco').textContent = 'Defina o preço de venda para visualizar o diagnóstico.';
                obterCampo('aviso-preco').className = 'aviso';
            }
        } catch (erro) {
            console.error('Falha ao comunicar com o servidor Java para simulação:', erro);
        }
    }

    async function carregarProdutoParaEdicao() {
        if (!modoEdicao) {
            simularPrecificacao();
            return;
        }

        document.title = 'Precifico • Editar Produto';
        obterCampo('titulo-pagina').textContent = 'Editar Informações do Produto';
        obterCampo('btn-salvar').textContent = 'Salvar alterações';
        obterCampo('id-produto').value = idEdicao;

        try {
            const resposta = await produtoServico.consultarPorId(idEdicao);
            if (tratarNaoAutorizado(resposta, sessao)) {
                return;
            }

            const produto = resposta.dados ? resposta.dados.produto : null;
            if (!resposta.sucesso || !produto) {
                sessao.salvarMensagemFlash('perigo', resposta.mensagem || 'Produto não encontrado.');
                window.location.href = 'produtos.html';
                return;
            }

            const precificacao = produto.precificacao || {};
            const custo = precificacao.custoProduto || 0.0;
            const venda = precificacao.valorVenda || 0.0;
            const margemPadrao = (custo > 0 && venda > 0) ? ((venda - custo) / custo) * 100.0 : 25.0;

            obterCampo('codigoProduto').value = produto.codigoProduto || '';
            obterCampo('situacao').value = (produto.situacao || 'DISPONIVEL').toUpperCase();
            obterCampo('nome').value = produto.nome || '';
            obterCampo('descricao').value = produto.descricao || '';
            obterCampo('imagem').value = produto.imagem || '';
            obterCampo('custo').value = custo > 0 ? custo.toFixed(2) : '';
            obterCampo('margem').value = margemPadrao >= 0 ? margemPadrao.toFixed(2) : '25';
            obterCampo('venda').value = venda > 0 ? venda.toFixed(2) : '';
            preencherBrindesSelecionados(produto.brindes || []);

            await simularPrecificacao();
        } catch (erro) {
            exibirAlerta('mensagens', 'perigo', 'Erro ao carregar dados do produto para edição.');
        }
    }

    ['custo', 'margem', 'venda'].forEach(function (idCampo) {
        obterCampo(idCampo).addEventListener('input', simularPrecificacao);
    });

    obterCampo('usar-preco').addEventListener('click', function () {
        if (precoSugeridoAtual > 0) {
            obterCampo('venda').value = precoSugeridoAtual.toFixed(2);
            simularPrecificacao();
        }
    });

    obterCampo('form-produto').addEventListener('submit', async function (evento) {
        evento.preventDefault();

        const custoTexto = obterCampo('custo').value.trim();
        const vendaTexto = obterCampo('venda').value.trim();
        const dadosProduto = {
            codigoProduto: obterCampo('codigoProduto').value,
            situacao: obterCampo('situacao').value,
            nome: obterCampo('nome').value,
            descricao: obterCampo('descricao').value,
            imagem: obterCampo('imagem').value,
            custo: custoTexto !== '' ? Number(custoTexto) : null,
            valorVenda: vendaTexto !== '' ? Number(vendaTexto) : null,
            brindes: obterBrindesSelecionados()
        };

        try {
            const resposta = modoEdicao
                ? await produtoServico.atualizar(idEdicao, dadosProduto)
                : await produtoServico.cadastrar(dadosProduto);

            if (tratarNaoAutorizado(resposta, sessao)) {
                return;
            }

            if (resposta.sucesso) {
                sessao.salvarMensagemFlash('sucesso', resposta.mensagem);
                window.location.href = 'produtos.html';
                return;
            }

            exibirAlerta('mensagens', 'perigo', resposta.mensagem || 'Não foi possível salvar o produto.');
        } catch (erro) {
            exibirAlerta('mensagens', 'perigo', 'Erro ao comunicar com o servidor Spring Boot.');
        }
    });

    if (vendedor) {
        carregarProdutoParaEdicao();
    }
}

iniciarPaginaFormularioProduto(window.PrecificoApp);
