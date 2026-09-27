<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Produto" %>
<%@ page import="model.Vendedor" %>
<%@ page import="model.Precificacao" %>
<%
    Vendedor vendedorLogado = (Vendedor) session.getAttribute("vendedorLogado");
    if (vendedorLogado == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    Produto produto = (Produto) request.getAttribute("produto");
    boolean modoEdicao = (produto != null && produto.getId() > 0);
    Precificacao prec = (produto != null) ? produto.getPrecificacao() : null;

    double custoPadrao = prec != null ? prec.getCustoProduto() : 0.0;
    double vendaPadrao = prec != null ? prec.getValorVenda() : 0.0;
    double margemPadrao = (custoPadrao > 0 && vendaPadrao > 0) ? ((vendaPadrao - custoPadrao) / custoPadrao) * 100.0 : 25.0;
%>
<!doctype html>
<html lang="pt-BR">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Precifico • <%= modoEdicao ? "Editar Produto" : "Novo Produto" %></title>
    <link rel="stylesheet" href="css/estilo.css">
</head>
<body>
<header>
    <div class="logotipo">Precifico</div>
    <div class="cabecalho-usuario">
        <span>Vendedor: <strong><%= vendedorLogado.getNome() %></strong></span>
        <a href="ControleProduto?btnop=ConsultarTodos" class="botao botao-pequeno" style="text-decoration:none;">Meus Produtos</a>
    </div>
</header>

<main>
    <button class="voltar" onclick="window.location.href='ControleProduto?btnop=ConsultarTodos'">← Voltar para lista de produtos</button>

    <div class="cabecalho-secao">
        <div>
            <h1><%= modoEdicao ? "Editar Informações do Produto" : "Novo Produto" %></h1>
        </div>
    </div>

    <% 
        String msgErro = (String) request.getAttribute("msgErro");
        if (msgErro != null && !msgErro.trim().isEmpty()) { 
    %>
        <div class="aviso perigo" style="margin-bottom: 24px;"><%= msgErro %></div>
    <% } %>

    <form id="form-produto" action="ControleProduto" method="POST" novalidate>
        <input type="hidden" name="btnop" value="<%= modoEdicao ? "Atualizar" : "Cadastrar" %>">
        <% if (modoEdicao) { %>
            <input type="hidden" name="id" value="<%= produto.getId() %>">
        <% } %>

        <div class="grade">
            <div class="painel">
                <h2>1. Informações do produto</h2>
                <div class="campos">
                    <div class="campo">
                        <label for="codigoProduto">Código / SKU *</label>
                        <input id="codigoProduto" name="codigoProduto" required maxlength="50" 
                               placeholder="Ex.: PRD-101" 
                               value="<%= modoEdicao ? produto.getCodigoProduto() : "" %>" autofocus>
                    </div>

                    <div class="campo">
                        <label for="situacao">Situação</label>
                        <select id="situacao" name="situacao">
                            <option value="DISPONIVEL" <%= (modoEdicao && "DISPONIVEL".equalsIgnoreCase(produto.getSituacao())) ? "selected" : "" %>>Disponível</option>
                            <option value="VENDIDO" <%= (modoEdicao && "VENDIDO".equalsIgnoreCase(produto.getSituacao())) ? "selected" : "" %>>Vendido</option>
                        </select>
                    </div>

                    <div class="campo linha-inteira">
                        <label for="nome">Nome do produto *</label>
                        <input id="nome" name="nome" required maxlength="100" 
                               placeholder="Ex.: Roda de carro esportiva aro 17" 
                               value="<%= modoEdicao ? produto.getNome() : "" %>">
                    </div>

                    <div class="campo linha-inteira">
                        <label for="descricao">Descrição detalhada *</label>
                        <textarea id="descricao" name="descricao" required maxlength="2000" 
                                  placeholder="Descreva as características, estado de conservação e especificações."><%= modoEdicao ? produto.getDescricao() : "" %></textarea>
                    </div>

                    <div class="campo linha-inteira">
                        <label for="imagem">URL da foto do produto (JPEG, PNG, WebP) *</label>
                        <input id="imagem" name="imagem" type="url" required maxlength="500" 
                               placeholder="https://exemplo.com/fotos/roda.jpg"
                               value="<%= modoEdicao ? produto.getImagem() : "" %>">
                        <small>Informe o link direto para a imagem do produto na internet.</small>


                    </div>
                </div>

                <h2 style="margin-top: 32px;">2. Custo e percentual de lucro desejado</h2>
                <div class="campos">
                    <div class="campo">
                        <label for="custo">Valor de custo (R$) *</label>
                        <input id="custo" name="custo" type="number" min="0.01" step="0.01" required 
                               placeholder="Ex.: 800.00" 
                               value="<%= modoEdicao && custoPadrao > 0 ? String.format(java.util.Locale.US, "%.2f", custoPadrao) : "" %>">
                        <small>Valor de compra ou custo de fabricação.</small>
                    </div>

                    <div class="campo">
                        <label for="margem">Acréscimo sobre o custo (%) *</label>
                        <input id="margem" type="number" min="0" step="0.01" required 
                               placeholder="Ex.: 25" 
                               value="<%= modoEdicao && margemPadrao >= 0 ? String.format(java.util.Locale.US, "%.2f", margemPadrao) : "25" %>">
                        <small>Percentual de lucro desejado sobre o valor de custo.</small>
                    </div>
                </div>
            </div>

            <div class="pilha">
                <div class="painel cartao-preco">
                    <div class="sobretitulo">SUGESTÃO AUTOMÁTICA</div>
                    <div class="preco-destaque" id="sugestao">R$ —</div>
                    <p id="formula">Preencha o custo e o percentual para calcular a sugestão.</p>
                    <button type="button" id="usar-preco" class="botao" style="width: 100%;">Usar preço sugerido</button>
                    <small style="display:block; margin-top: 14px; color: var(--cor-atenuada);">Fórmula: Custo + (Custo × Percentual ÷ 100)</small>
                </div>

                <div class="painel">
                    <h2>3. Defina seu preço de venda</h2>
                    <div class="campo">
                        <label for="venda">Preço de venda final (R$) *</label>
                        <input id="venda" name="valorVenda" type="number" min="0.01" step="0.01" required 
                               placeholder="Ex.: 1000.00" 
                               value="<%= modoEdicao && vendaPadrao > 0 ? String.format(java.util.Locale.US, "%.2f", vendaPadrao) : "" %>">
                        <small>Você pode aceitar a sugestão automática ou definir livremente o preço final.</small>
                    </div>

                    <div class="metricas">
                        <div>
                            <small>Lucro estimado</small>
                            <strong id="lucro">R$ —</strong>
                        </div>
                        <div>
                            <small>Resultado sobre o custo</small>
                            <strong id="margem-efetiva">—%</strong>
                        </div>
                    </div>

                    <div id="aviso-preco" class="aviso" aria-live="polite">
                        Seu resultado aparecerá aqui.
                    </div>
                </div>
            </div>
        </div>

        <div class="acoes">
            <button type="button" onclick="window.location.href='ControleProduto?btnop=ConsultarTodos'">Cancelar</button>
            <button type="submit" class="primario"><%= modoEdicao ? "Salvar alterações" : "Salvar produto" %></button>
        </div>
    </form>
</main>

<script>
    // Função auxiliar para obter elementos do HTML pelo ID
    const $ = id => document.getElementById(id);

    // Formatadores para exibição de valores em Real (R$) e Porcentagem (%)
    const formatarMoeda = valor => new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(valor);
    const formatarPorcentagem = valor => new Intl.NumberFormat('pt-BR', { maximumFractionDigits: 2 }).format(valor) + '%';

    let precoSugeridoAtual = 0.0;

    // Lê os números digitados nos campos do formulário
    function obterValores() {
        return {
            custo: $('custo').value === '' ? NaN : Number($('custo').value),
            margem: $('margem').value === '' ? NaN : Number($('margem').value),
            venda: $('venda').value === '' ? NaN : Number($('venda').value)
        };
    }

    // Executa a requisição assíncrona ao backend Java (Fetch API) diretamente ao digitar
    async function simularPrecificacao() {
        let { custo, margem, venda } = obterValores();
        let custoValido = Number.isFinite(custo) && custo > 0;
        let margemValida = Number.isFinite(margem) && margem >= 0;
        let vendaValida = Number.isFinite(venda) && venda > 0;

        if (!custoValido) {
            $('sugestao').textContent = 'R$ —';
            $('formula').textContent = 'Preencha o custo e o percentual para calcular a sugestão.';
            $('usar-preco').disabled = true;
            $('lucro').textContent = 'R$ —';
            $('margem-efetiva').textContent = '—%';
            $('aviso-preco').textContent = 'Preencha os valores para visualizar a análise de resultado.';
            $('aviso-preco').className = 'aviso';
            precoSugeridoAtual = 0.0;
            return;
        }

        // URL para a Action SimularProdutoAction no Java
        const url = 'ControleProduto?btnop=Simular' +
            '&custo=' + encodeURIComponent(custo) +
            '&margem=' + encodeURIComponent(margemValida ? margem : 0) +
            '&valorVenda=' + encodeURIComponent(vendaValida ? venda : 0);

        try {
            // Chamada direta via Fetch API (sem debounce, resposta imediata do servidor)
            const resposta = await fetch(url);
            const dados = await resposta.json();

            if (dados && dados.valido) {
                // Atualiza o Preço Sugerido com base no cálculo da classe Precificacao
                if (margemValida && dados.precoSugerido > 0) {
                    precoSugeridoAtual = dados.precoSugerido;
                    $('sugestao').textContent = formatarMoeda(dados.precoSugerido);
                    $('formula').textContent = formatarMoeda(custo) + ' de custo + ' + formatarPorcentagem(margem) + ' de margem desejada.';
                    $('usar-preco').disabled = false;
                } else {
                    $('sugestao').textContent = 'R$ —';
                    $('formula').textContent = 'Preencha o percentual para calcular a sugestão.';
                    $('usar-preco').disabled = true;
                }

                // Atualiza Lucro, Margem Efetiva e Diagnóstico Comercial
                if (vendaValida) {
                    $('lucro').textContent = formatarMoeda(dados.lucro);
                    $('lucro').style.color = dados.lucro >= 0 ? 'var(--cor-marca)' : '#9a382c';
                    $('margem-efetiva').textContent = formatarPorcentagem(dados.margemEfetiva);
                    $('aviso-preco').textContent = dados.diagnostico;
                    $('aviso-preco').className = 'aviso ' + (dados.estiloAviso || '');
                } else {
                    $('lucro').textContent = 'R$ —';
                    $('margem-efetiva').textContent = '—%';
                    $('aviso-preco').textContent = 'Defina o preço de venda para visualizar o diagnóstico.';
                    $('aviso-preco').className = 'aviso';
                }
            }
        } catch (erro) {
            console.error('Falha ao comunicar com o servidor Java para simulação:', erro);
        }
    }

    // Ao digitar em qualquer um dos campos, chama a simulação diretamente (sem debounce)
    ['custo', 'margem', 'venda'].forEach(id => $(id).addEventListener('input', simularPrecificacao));

    // Botão de atalho para preencher o preço sugerido no campo de venda
    $('usar-preco').addEventListener('click', () => {
        if (precoSugeridoAtual > 0) {
            $('venda').value = precoSugeridoAtual.toFixed(2);
            simularPrecificacao();
        }
    });

    // Executa a simulação inicial ao abrir a tela
    simularPrecificacao();
</script>
</body>
</html>
