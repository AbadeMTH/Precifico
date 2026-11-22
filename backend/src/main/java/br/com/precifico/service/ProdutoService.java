package br.com.precifico.service;

import br.com.precifico.builder.ProdutoBuilder;
import br.com.precifico.dao.IPrecificacaoDAO;
import br.com.precifico.dao.IProdutoBrindeDAO;
import br.com.precifico.dao.IProdutoDAO;
import br.com.precifico.dao.IVendedorDAO;
import br.com.precifico.decorator.CompositorBrindes;
import br.com.precifico.dto.ProdutoRequisicao;
import br.com.precifico.dto.RespostaApi;
import br.com.precifico.model.Precificacao;
import br.com.precifico.model.Produto;
import br.com.precifico.model.SituacaoProduto;
import br.com.precifico.model.TipoBrinde;
import br.com.precifico.model.Vendedor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ProdutoService implements IProdutoService {

    private final IProdutoDAO produtoDAO;
    private final IPrecificacaoDAO precificacaoDAO;
    private final IProdutoBrindeDAO produtoBrindeDAO;
    private final IVendedorDAO vendedorDAO;
    private final IPrecificacaoService precificacaoService;
    private final CompositorBrindes compositorBrindes;

    public ProdutoService(
            IProdutoDAO produtoDAO,
            IPrecificacaoDAO precificacaoDAO,
            IProdutoBrindeDAO produtoBrindeDAO,
            IVendedorDAO vendedorDAO,
            IPrecificacaoService precificacaoService
    ) {
        this.produtoDAO = produtoDAO;
        this.precificacaoDAO = precificacaoDAO;
        this.produtoBrindeDAO = produtoBrindeDAO;
        this.vendedorDAO = vendedorDAO;
        this.precificacaoService = precificacaoService;
        this.compositorBrindes = new CompositorBrindes();
    }

    @Override
    public RespostaApi listarTodos(Vendedor vendedorLogado) throws Exception {
        if (vendedorLogado == null) {
            return RespostaApi.erro(401, "Acesso restrito. Faça login para continuar.");
        }

        Vendedor vendedorCompleto = vendedorDAO.consultarPorId(vendedorLogado);
        List<Produto> listaProdutos = produtoDAO.consultarPorVendedor(vendedorLogado);

        for (Produto produto : listaProdutos) {
            preencherRelacionamentos(produto, vendedorCompleto);
        }

        return RespostaApi.sucesso(200, "Produtos listados com sucesso.", listaProdutos);
    }

    @Override
    public RespostaApi consultarPorId(int idProduto, Vendedor vendedorLogado) throws Exception {
        if (vendedorLogado == null) {
            return RespostaApi.erro(401, "Acesso restrito. Faça login para continuar.");
        }

        Produto produto = buscarProdutoCompleto(idProduto);
        if (produto == null) {
            return RespostaApi.erro(404, "Produto não encontrado.");
        }

        if (!produto.pertenceA(vendedorLogado)) {
            return RespostaApi.erro(403, "Acesso não autorizado ao produto solicitado.");
        }

        Map<String, Object> detalhes = precificacaoService.montarDetalhesComerciais(produto);
        return RespostaApi.sucesso(200, "Produto encontrado.", detalhes);
    }

    @Override
    public RespostaApi cadastrar(ProdutoRequisicao dados, Vendedor vendedorLogado) throws Exception {
        if (vendedorLogado == null) {
            return RespostaApi.erro(401, "Sessão expirada. Faça login novamente.");
        }

        RespostaApi erroValidacao = validarDados(dados);
        if (erroValidacao != null) {
            return erroValidacao;
        }

        List<TipoBrinde> brindesSelecionados = extrairBrindes(dados.getBrindes());
        Produto produto = construirNovoProduto(dados, vendedorLogado);
        compositorBrindes.aplicarNoProduto(produto, brindesSelecionados);
        produtoDAO.cadastrar(produto);

        Precificacao precificacao = produto.getPrecificacao();
        precificacao.setIdProduto(produto.getId());
        precificacaoDAO.cadastrar(precificacao);
        produtoBrindeDAO.salvarBrindes(produto.getId(), produto.getBrindes());

        String mensagem = "Produto '" + produto.getNome() + "' cadastrado com sucesso!";
        return RespostaApi.sucesso(201, mensagem, produto);
    }

    @Override
    public RespostaApi atualizar(int idProduto, ProdutoRequisicao dados, Vendedor vendedorLogado) throws Exception {
        if (vendedorLogado == null) {
            return RespostaApi.erro(401, "Acesso restrito. Faça login para continuar.");
        }

        Produto produtoExistente = buscarProdutoPorId(idProduto);
        if (produtoExistente == null) {
            return RespostaApi.erro(404, "Produto não encontrado.");
        }

        if (!produtoExistente.pertenceA(vendedorLogado)) {
            return RespostaApi.erro(403, "Permissão negada para atualizar este produto.");
        }

        RespostaApi erroValidacao = validarDados(dados);
        if (erroValidacao != null) {
            return erroValidacao;
        }

        List<TipoBrinde> brindesSelecionados = extrairBrindes(dados.getBrindes());
        Precificacao precificacaoExistente = precificacaoDAO.consultarPorProduto(produtoExistente);
        Produto produtoAtualizado = construirProdutoAtualizado(
                idProduto,
                dados,
                vendedorLogado,
                produtoExistente,
                precificacaoExistente
        );
        compositorBrindes.aplicarNoProduto(produtoAtualizado, brindesSelecionados);
        produtoDAO.atualizar(produtoAtualizado);
        precificacaoDAO.atualizar(produtoAtualizado.getPrecificacao());
        produtoBrindeDAO.substituirBrindes(idProduto, produtoAtualizado.getBrindes());

        return RespostaApi.sucesso(200, "Produto atualizado com sucesso!", produtoAtualizado);
    }

    @Override
    public RespostaApi alterarSituacao(int idProduto, Vendedor vendedorLogado) throws Exception {
        if (vendedorLogado == null) {
            return RespostaApi.erro(401, "Acesso restrito. Faça login para continuar.");
        }

        Produto produto = buscarProdutoPorId(idProduto);
        if (produto == null) {
            return RespostaApi.erro(404, "Produto não encontrado.");
        }

        if (!produto.pertenceA(vendedorLogado)) {
            return RespostaApi.erro(403, "Permissão negada para alterar este produto.");
        }

        SituacaoProduto novaSituacao = produto.alternarSituacao();
        produtoDAO.alterarSituacao(produto);

        String mensagem = "Situação do produto '" + produto.getNome() + "' alterada para " + novaSituacao + ".";
        return RespostaApi.sucesso(200, mensagem, Map.of("novaSituacao", novaSituacao));
    }

    @Override
    public RespostaApi deletar(int idProduto, Vendedor vendedorLogado) throws Exception {
        if (vendedorLogado == null) {
            return RespostaApi.erro(401, "Acesso restrito. Faça login para continuar.");
        }

        Produto produto = buscarProdutoPorId(idProduto);
        if (produto == null) {
            return RespostaApi.erro(404, "Produto não encontrado.");
        }

        if (!produto.pertenceA(vendedorLogado)) {
            return RespostaApi.erro(403, "Permissão negada para excluir este produto.");
        }

        produtoDAO.deletar(produto);

        String mensagem = "Produto '" + produto.getNome() + "' excluído com sucesso.";
        return RespostaApi.sucesso(200, mensagem, null);
    }

    private Produto buscarProdutoPorId(int idProduto) throws Exception {
        Produto filtro = new Produto();
        filtro.setId(idProduto);
        return produtoDAO.consultarPorId(filtro);
    }

    private Produto buscarProdutoCompleto(int idProduto) throws Exception {
        Produto produto = buscarProdutoPorId(idProduto);
        if (produto == null) {
            return null;
        }

        Vendedor vendedor = vendedorDAO.consultarPorId(produto.getVendedor());
        preencherRelacionamentos(produto, vendedor);
        return produto;
    }

    private void preencherRelacionamentos(Produto produto, Vendedor vendedor) throws Exception {
        Precificacao precificacao = precificacaoDAO.consultarPorProduto(produto);
        List<TipoBrinde> brindesPersistidos = produtoBrindeDAO.consultarPorProduto(produto.getId());
        produto.setVendedor(vendedor);
        produto.setPrecificacao(precificacao);
        compositorBrindes.aplicarNoProduto(produto, brindesPersistidos);
    }

    private RespostaApi validarDados(ProdutoRequisicao dados) {
        if (dados == null || estaVazio(dados.getCodigoProduto()) || estaVazio(dados.getNome())
                || estaVazio(dados.getDescricao()) || estaVazio(dados.getImagem())) {
            return RespostaApi.erro(400, "Por favor, preencha todos os campos obrigatórios.");
        }

        if (dados.getCusto() == null || dados.getValorVenda() == null) {
            return RespostaApi.erro(400, "Por favor, preencha todos os campos obrigatórios.");
        }

        if (dados.getCusto() <= 0 || dados.getValorVenda() <= 0) {
            return RespostaApi.erro(400, "Os valores de custo e venda devem ser maiores que zero.");
        }

        return validarBrindes(dados.getBrindes());
    }

    private RespostaApi validarBrindes(List<String> brindesInformados) {
        if (brindesInformados == null || brindesInformados.isEmpty()) {
            return null;
        }

        Set<TipoBrinde> vistos = new LinkedHashSet<>();
        for (String textoBrinde : brindesInformados) {
            TipoBrinde tipoBrinde = TipoBrinde.deTexto(textoBrinde);
            if (tipoBrinde == null) {
                return RespostaApi.erro(400, "Brinde inválido informado: " + textoBrinde);
            }
            if (!vistos.add(tipoBrinde)) {
                return RespostaApi.erro(400, "Não é permitido selecionar brindes duplicados.");
            }
        }

        return null;
    }

    private List<TipoBrinde> extrairBrindes(List<String> brindesInformados) {
        List<TipoBrinde> brindes = new ArrayList<>();
        if (brindesInformados == null) {
            return brindes;
        }

        for (String textoBrinde : brindesInformados) {
            TipoBrinde tipoBrinde = TipoBrinde.deTexto(textoBrinde);
            if (tipoBrinde != null && !brindes.contains(tipoBrinde)) {
                brindes.add(tipoBrinde);
            }
        }

        return brindes;
    }

    private Produto construirNovoProduto(ProdutoRequisicao dados, Vendedor vendedorLogado) {
        Precificacao precificacao = new Precificacao(dados.getValorVenda(), dados.getCusto());
        return new ProdutoBuilder()
                .comCodigoProduto(dados.getCodigoProduto().trim())
                .comNome(dados.getNome().trim())
                .comDescricao(dados.getDescricao().trim())
                .comImagem(dados.getImagem().trim())
                .comSituacao(SituacaoProduto.deTexto(dados.getSituacao()))
                .comVendedor(vendedorLogado)
                .comPrecificacao(precificacao)
                .constroi();
    }

    private Produto construirProdutoAtualizado(
            int idProduto,
            ProdutoRequisicao dados,
            Vendedor vendedorLogado,
            Produto produtoExistente,
            Precificacao precificacaoExistente
    ) {
        Precificacao precificacao = new Precificacao(dados.getValorVenda(), dados.getCusto());
        precificacao.setIdProduto(idProduto);
        preservarPromocaoSeConsistente(precificacao, precificacaoExistente);

        SituacaoProduto situacao = estaVazio(dados.getSituacao())
                ? produtoExistente.getSituacao()
                : SituacaoProduto.deTexto(dados.getSituacao());

        return new ProdutoBuilder()
                .comId(idProduto)
                .comCodigoProduto(dados.getCodigoProduto().trim())
                .comNome(dados.getNome().trim())
                .comDescricao(dados.getDescricao().trim())
                .comImagem(dados.getImagem().trim())
                .comSituacao(situacao)
                .comVendedor(vendedorLogado)
                .comPrecificacao(precificacao)
                .constroi();
    }

    private void preservarPromocaoSeConsistente(Precificacao novaPrecificacao, Precificacao precificacaoExistente) {
        if (precificacaoExistente == null || !precificacaoExistente.isEmPromocao()) {
            return;
        }

        BigDecimal novoValorVenda = novaPrecificacao.obterValorVendaDecimal();
        BigDecimal valorPromocionalAtual = precificacaoExistente.getValorPromocional();

        if (valorPromocionalAtual.compareTo(novoValorVenda) < 0) {
            BigDecimal novoPercentual = Precificacao.calcularPercentualDesconto(
                    novoValorVenda,
                    valorPromocionalAtual
            );
            if (novoPercentual.compareTo(BigDecimal.ZERO) > 0
                    && novoPercentual.compareTo(new BigDecimal("100")) < 0) {
                novaPrecificacao.aplicarPromocao(valorPromocionalAtual, novoPercentual);
            }
        }
    }

    private boolean estaVazio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }
}
