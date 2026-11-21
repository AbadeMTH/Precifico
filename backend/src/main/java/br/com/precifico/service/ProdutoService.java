package br.com.precifico.service;

import br.com.precifico.builder.ProdutoBuilder;
import br.com.precifico.dao.IPrecificacaoDAO;
import br.com.precifico.dao.IProdutoDAO;
import br.com.precifico.dao.IVendedorDAO;
import br.com.precifico.dto.ProdutoRequisicao;
import br.com.precifico.dto.RespostaApi;
import br.com.precifico.model.Precificacao;
import br.com.precifico.model.Produto;
import br.com.precifico.model.SituacaoProduto;
import br.com.precifico.model.Vendedor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ProdutoService implements IProdutoService {

    private final IProdutoDAO produtoDAO;
    private final IPrecificacaoDAO precificacaoDAO;
    private final IVendedorDAO vendedorDAO;
    private final IPrecificacaoService precificacaoService;

    public ProdutoService(
            IProdutoDAO produtoDAO,
            IPrecificacaoDAO precificacaoDAO,
            IVendedorDAO vendedorDAO,
            IPrecificacaoService precificacaoService
    ) {
        this.produtoDAO = produtoDAO;
        this.precificacaoDAO = precificacaoDAO;
        this.vendedorDAO = vendedorDAO;
        this.precificacaoService = precificacaoService;
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

        Produto produto = construirNovoProduto(dados, vendedorLogado);
        produtoDAO.cadastrar(produto);

        Precificacao precificacao = produto.getPrecificacao();
        precificacao.setIdProduto(produto.getId());
        precificacaoDAO.cadastrar(precificacao);

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

        Produto produtoAtualizado = construirProdutoAtualizado(idProduto, dados, vendedorLogado, produtoExistente);
        produtoDAO.atualizar(produtoAtualizado);
        precificacaoDAO.atualizar(produtoAtualizado.getPrecificacao());

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
        produto.setVendedor(vendedor);
        produto.setPrecificacao(precificacao);
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

        return null;
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
            Produto produtoExistente
    ) {
        Precificacao precificacao = new Precificacao(dados.getValorVenda(), dados.getCusto());
        precificacao.setIdProduto(idProduto);

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

    private boolean estaVazio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }
}
