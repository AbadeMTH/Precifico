function iniciarPaginaPromoverProduto({ sessao, produtoServico, promocaoServico }) {
    const vendedor = exigirAutenticacao(sessao);
    verificarMensagemFlash('mensagens', sessao);

    const paramsUrl = new URLSearchParams(window.location.search);
    const idProduto = paramsUrl.get('id');

    let valorAtualOriginal = 0;
    let custoProduto = 0;
    let atualizandoCampos = false;

    function obterCampo(id) {
        return document.getElementById(id);
    }

    function atualizarPrevia(novoValor, desconto) {
        const feedback = obterCampo('feedback-validacao');
        const seloDesconto = obterCampo('resumo-desconto');
        const textoPara = obterCampo('resumo-para');
        const textoEconomia = obterCampo('resumo-economia');
        const metricaEconomia = obterCampo('metrica-economia');
        const metricaLucro = obterCampo('metrica-lucro');

        const campoNovoVazio = obterCampo('valor-promocional').value.trim() === '';
        const campoDescontoVazio = obterCampo('percentual-desconto').value.trim() === '';

        if (campoNovoVazio && campoDescontoVazio) {
            seloDesconto.style.display = 'none';
            textoPara.textContent = 'R$ —';
            textoEconomia.textContent = 'Preencha o novo valor ou o percentual para simular a promoção.';
            metricaEconomia.textContent = 'R$ —';
            metricaLucro.textContent = 'R$ —';
            metricaLucro.style.color = 'var(--cor-texto)';
            feedback.className = 'aviso';
            feedback.textContent = 'Informe o novo valor promocional ou o percentual de desconto para visualizar o cálculo.';
            return;
        }

        const validacao = validarPromocao(valorAtualOriginal, novoValor, desconto);
        if (!validacao.valido) {
            seloDesconto.style.display = 'none';
            textoPara.textContent = 'R$ —';
            textoEconomia.textContent = 'Ajuste os valores para aplicar a promoção.';
            metricaEconomia.textContent = 'R$ —';
            metricaLucro.textContent = 'R$ —';
            metricaLucro.style.color = 'var(--cor-texto)';
            feedback.className = 'aviso perigo';
            feedback.textContent = validacao.mensagem;
            return;
        }

        const resumo = calcularResumoPromocao(valorAtualOriginal, custoProduto, validacao.novoValor);

        seloDesconto.style.display = 'inline-block';
        seloDesconto.textContent = '-' + formatarDecimal(validacao.desconto, 2) + '%';
        textoPara.textContent = formatarMoeda(validacao.novoValor);
        textoEconomia.textContent = `O produto passará de ${formatarMoeda(valorAtualOriginal)} para ${formatarMoeda(validacao.novoValor)}.`;
        metricaEconomia.textContent = formatarMoeda(resumo.economia);
        metricaLucro.textContent = formatarMoeda(resumo.lucroPromocional);
        metricaLucro.style.color = resumo.lucroPromocional >= 0 ? 'var(--cor-marca)' : '#9a382c';

        if (custoProduto > 0 && resumo.lucroPromocional < 0) {
            feedback.className = 'aviso atencao';
            feedback.textContent = 'Atenção: o preço promocional está abaixo do valor de custo cadastrado. Você ainda pode salvar a promoção.';
        } else {
            feedback.className = 'aviso sucesso';
            feedback.textContent = `Promoção válida: ${formatarDecimal(validacao.desconto, 2)}% de desconto sobre o valor atual.`;
        }
    }

    function aoAlterarNovoValor() {
        if (atualizandoCampos) {
            return;
        }
        atualizandoCampos = true;

        const textoNovoValor = obterCampo('valor-promocional').value.trim();
        if (textoNovoValor === '') {
            obterCampo('percentual-desconto').value = '';
            atualizarPrevia(NaN, NaN);
            atualizandoCampos = false;
            return;
        }

        const novoValor = Number(textoNovoValor);
        if (!Number.isFinite(novoValor) || novoValor <= 0 || novoValor >= valorAtualOriginal) {
            obterCampo('percentual-desconto').value = '';
            atualizarPrevia(novoValor, NaN);
            atualizandoCampos = false;
            return;
        }

        const novoValorArredondado = arredondarDuasCasas(novoValor);
        const descontoCalculado = calcularDescontoPorValor(valorAtualOriginal, novoValorArredondado);
        obterCampo('percentual-desconto').value = descontoCalculado.toFixed(2);
        atualizarPrevia(novoValorArredondado, descontoCalculado);
        atualizandoCampos = false;
    }

    function aoAlterarDesconto() {
        if (atualizandoCampos) {
            return;
        }
        atualizandoCampos = true;

        const textoDesconto = obterCampo('percentual-desconto').value.trim();
        if (textoDesconto === '') {
            obterCampo('valor-promocional').value = '';
            atualizarPrevia(NaN, NaN);
            atualizandoCampos = false;
            return;
        }

        const desconto = Number(textoDesconto);
        if (!Number.isFinite(desconto) || desconto <= 0 || desconto >= 100) {
            obterCampo('valor-promocional').value = '';
            atualizarPrevia(NaN, desconto);
            atualizandoCampos = false;
            return;
        }

        const descontoArredondado = arredondarDuasCasas(desconto);
        const novoValorCalculado = calcularValorPorDesconto(valorAtualOriginal, descontoArredondado);
        obterCampo('valor-promocional').value = novoValorCalculado.toFixed(2);
        atualizarPrevia(novoValorCalculado, descontoArredondado);
        atualizandoCampos = false;
    }

    async function carregarProduto() {
        if (!idProduto || !/^\d+$/.test(idProduto)) {
            sessao.salvarMensagemFlash('perigo', 'Produto inválido para criação de promoção.');
            window.location.href = 'produtos.html';
            return;
        }

        try {
            const resposta = await produtoServico.consultarPorId(idProduto);
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
            valorAtualOriginal = arredondarDuasCasas(Number(precificacao.valorVenda || 0));
            custoProduto = arredondarDuasCasas(Number(precificacao.custoProduto || 0));

            if (valorAtualOriginal <= 0) {
                sessao.salvarMensagemFlash('perigo', 'O produto não possui preço de venda válido para promoção.');
                window.location.href = 'produtos.html';
                return;
            }

            obterCampo('produto-imagem').src = obterUrlImagemProduto(produto.imagem);
            obterCampo('produto-imagem').alt = produto.nome;
            obterCampo('produto-codigo').textContent = 'Código: ' + produto.codigoProduto;
            obterCampo('produto-nome').textContent = produto.nome;
            obterCampo('produto-custo').textContent = 'Custo cadastrado: ' + formatarMoeda(custoProduto);

            obterCampo('valor-atual').value = formatarMoeda(valorAtualOriginal);
            obterCampo('resumo-de').textContent = 'DE ' + formatarMoeda(valorAtualOriginal);
            obterCampo('valor-promocional').max = arredondarDuasCasas(valorAtualOriginal - 0.01).toFixed(2);

            if (precificacao.emPromocao && precificacao.valorPromocional != null) {
                const valorPromocionalAtual = arredondarDuasCasas(Number(precificacao.valorPromocional));
                const descontoAtual = arredondarDuasCasas(Number(precificacao.percentualDesconto));
                obterCampo('valor-promocional').value = valorPromocionalAtual.toFixed(2);
                obterCampo('percentual-desconto').value = descontoAtual.toFixed(2);
                obterCampo('btn-remover-promocao').style.display = 'inline-flex';
                atualizarPrevia(valorPromocionalAtual, descontoAtual);
            } else {
                atualizarPrevia(NaN, NaN);
            }
        } catch (erro) {
            exibirAlerta('mensagens', 'perigo', 'Erro ao carregar os dados do produto.');
        }
    }

    obterCampo('valor-promocional').addEventListener('input', aoAlterarNovoValor);
    obterCampo('percentual-desconto').addEventListener('input', aoAlterarDesconto);

    obterCampo('form-promocao').addEventListener('submit', async function (evento) {
        evento.preventDefault();
        exibirAlerta('mensagens', '', '');

        const textoNovoValor = obterCampo('valor-promocional').value.trim();
        const textoDesconto = obterCampo('percentual-desconto').value.trim();
        const novoValor = textoNovoValor !== '' ? Number(textoNovoValor) : NaN;
        const desconto = textoDesconto !== '' ? Number(textoDesconto) : NaN;

        const validacao = validarPromocao(valorAtualOriginal, novoValor, desconto);
        if (!validacao.valido) {
            exibirAlerta('mensagens', 'perigo', validacao.mensagem);
            return;
        }

        try {
            const resposta = await promocaoServico.promover(idProduto, {
                valorPromocional: validacao.novoValor,
                percentualDesconto: validacao.desconto
            });

            if (tratarNaoAutorizado(resposta, sessao)) {
                return;
            }

            if (resposta.sucesso) {
                sessao.salvarMensagemFlash('sucesso', resposta.mensagem || 'Promoção aplicada com sucesso!');
                window.location.href = 'produtos.html';
                return;
            }

            exibirAlerta('mensagens', 'perigo', resposta.mensagem || 'Não foi possível salvar a promoção.');
        } catch (erro) {
            exibirAlerta('mensagens', 'perigo', 'Erro ao comunicar com o servidor para salvar a promoção.');
        }
    });

    obterCampo('btn-remover-promocao').addEventListener('click', async function () {
        if (!confirm('Deseja remover a promoção ativa deste produto?')) {
            return;
        }
        try {
            const resposta = await promocaoServico.removerPromocao(idProduto);
            if (tratarNaoAutorizado(resposta, sessao)) {
                return;
            }
            if (resposta.sucesso) {
                sessao.salvarMensagemFlash('sucesso', resposta.mensagem || 'Promoção removida com sucesso.');
                window.location.href = 'produtos.html';
                return;
            }
            exibirAlerta('mensagens', 'perigo', resposta.mensagem || 'Não foi possível remover a promoção.');
        } catch (erro) {
            exibirAlerta('mensagens', 'perigo', 'Erro ao comunicar com o servidor para remover a promoção.');
        }
    });

    if (vendedor) {
        carregarProduto();
    }
}

iniciarPaginaPromoverProduto(window.PrecificoApp);
