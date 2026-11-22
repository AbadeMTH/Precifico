function iniciarPaginaProdutos({ sessao, authServico, produtoServico }) {
    const vendedor = exigirAutenticacao(sessao);
    configurarBotaoSair('btn-sair', sessao, authServico);
    verificarMensagemFlash('mensagens', sessao);

    const container = document.getElementById('container-produtos');

    function montarLinhaProduto(produto) {
        const metricas = calcularMetricasExibicaoProduto(produto.precificacao);
        const disponivel = String(produto.situacao).toUpperCase() === 'DISPONIVEL';
        const urlFoto = obterUrlImagemProduto(produto.imagem);
        const precoHtml = metricas.emPromocao
            ? `
                <small class="preco-antigo">DE R$ ${formatarDecimal(metricas.venda, 2)}</small>
                <strong class="preco-promocional">PARA R$ ${formatarDecimal(metricas.valorPromocional, 2)}</strong>
                <span class="selo selo-promocao">-${formatarDecimal(metricas.percentualDesconto, 2)}%</span>
            `
            : `<strong>R$ ${formatarDecimal(metricas.venda, 2)}</strong>`;
        const brindesHtml = renderizarSelosBrindes(produto.brindes);

        return `
            <tr>
                <td>
                    <img src="${escaparHtml(urlFoto)}" alt="${escaparHtml(produto.nome)}" class="miniatura"
                         onerror="this.onerror=null; this.src='img/sem-foto.jpg';">
                </td>
                <td>
                    <strong>${escaparHtml(produto.codigoProduto)}</strong>
                </td>
                <td>
                    <strong>${escaparHtml(produto.nome)}</strong>
                    <small style="max-width: 280px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">${escaparHtml(produto.descricao)}</small>
                    ${brindesHtml}
                </td>
                <td>
                    R$ ${formatarDecimal(metricas.custo, 2)}
                </td>
                <td>
                    ${precoHtml}
                </td>
                <td>
                    <strong style="color: ${metricas.lucro >= 0 ? 'var(--cor-marca)' : '#9a382c'};">
                        R$ ${formatarDecimal(metricas.lucro, 2)}
                    </strong>
                    <small>${formatarDecimal(metricas.margem, 1)}% sobre o custo</small>
                </td>
                <td>
                    <span class="selo ${disponivel ? 'selo-disponivel' : 'selo-vendido'}">
                        ${escaparHtml(produto.situacao)}
                    </span>
                </td>
                <td style="text-align: right; white-space: nowrap;">
                    <a href="detalhe_produto.html?id=${produto.id}" class="botao botao-pequeno" title="Visualizar Detalhes e Precificação">Visualizar</a>
                    <a href="formulario_produto.html?id=${produto.id}" class="botao botao-pequeno" title="Editar Produto">Editar</a>
                    <a href="promover_produto.html?id=${produto.id}" class="botao botao-pequeno botao-promocao" title="Criar ou alterar promoção">Promover produto</a>
                    <button type="button" class="botao botao-pequeno" data-acao="situacao" data-id="${produto.id}" title="Alternar Disponível / Vendido">
                        ${disponivel ? 'Marcar Vendido' : 'Reativar'}
                    </button>
                    <button type="button" class="botao botao-pequeno botao-perigo" data-acao="excluir" data-id="${produto.id}" data-nome="${escaparHtml(produto.nome)}" title="Excluir Produto">
                        Excluir
                    </button>
                </td>
            </tr>
        `;
    }

    async function carregarProdutos() {
        try {
            const resposta = await produtoServico.listarTodos();
            if (tratarNaoAutorizado(resposta, sessao)) {
                return;
            }

            if (!resposta.sucesso) {
                exibirAlerta('mensagens', 'perigo', resposta.mensagem || 'Não foi possível carregar os produtos.');
                return;
            }

            const listaProdutos = resposta.dados || [];
            if (listaProdutos.length === 0) {
                container.innerHTML = `
                    <div class="painel-vazio">
                        <h2 style="margin-bottom: 8px;">Nenhum produto cadastrado ainda</h2>
                        <a href="formulario_produto.html" class="botao botao-primario" style="margin-top: 10px; text-decoration:none;">Cadastrar primeiro produto →</a>
                    </div>
                `;
                return;
            }

            const linhasHtml = listaProdutos.map(montarLinhaProduto).join('');
            container.innerHTML = `
                <div class="tabela-responsiva">
                    <table>
                        <thead>
                            <tr>
                                <th style="width: 70px;">Foto</th>
                                <th>Código</th>
                                <th>Produto / Descrição</th>
                                <th>Custo</th>
                                <th>Preço de Venda</th>
                                <th>Lucro Estimado</th>
                                <th>Situação</th>
                                <th style="text-align: right;">Ações</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${linhasHtml}
                        </tbody>
                    </table>
                </div>
            `;
        } catch (erro) {
            exibirAlerta('mensagens', 'perigo', 'Falha ao conectar com a API do servidor.');
        }
    }

    async function alternarSituacao(id) {
        try {
            const resposta = await produtoServico.alterarSituacao(id);
            if (tratarNaoAutorizado(resposta, sessao)) {
                return;
            }

            if (resposta.sucesso) {
                exibirAlerta('mensagens', 'sucesso', resposta.mensagem);
                await carregarProdutos();
                return;
            }

            exibirAlerta('mensagens', 'perigo', resposta.mensagem || 'Erro ao alterar situação do produto.');
        } catch (erro) {
            exibirAlerta('mensagens', 'perigo', 'Erro ao alterar situação do produto.');
        }
    }

    async function excluirProduto(id, nome) {
        if (!confirm(`Tem certeza que deseja excluir o produto ${nome}?`)) {
            return;
        }
        try {
            const resposta = await produtoServico.deletar(id);
            if (tratarNaoAutorizado(resposta, sessao)) {
                return;
            }

            if (resposta.sucesso) {
                exibirAlerta('mensagens', 'sucesso', resposta.mensagem);
                await carregarProdutos();
                return;
            }

            exibirAlerta('mensagens', 'perigo', resposta.mensagem || 'Erro ao excluir o produto.');
        } catch (erro) {
            exibirAlerta('mensagens', 'perigo', 'Erro ao excluir o produto.');
        }
    }

    container.addEventListener('click', function (evento) {
        const botao = evento.target.closest('button[data-acao]');
        if (!botao) {
            return;
        }

        const acao = botao.dataset.acao;
        const id = Number(botao.dataset.id);
        if (acao === 'situacao') {
            alternarSituacao(id);
        } else if (acao === 'excluir') {
            excluirProduto(id, botao.dataset.nome || '');
        }
    });

    if (vendedor) {
        carregarProdutos();
    }
}

iniciarPaginaProdutos(window.PrecificoApp);
