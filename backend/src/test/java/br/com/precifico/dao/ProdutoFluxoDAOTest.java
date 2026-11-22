package br.com.precifico.dao;

import br.com.precifico.builder.ProdutoBuilder;
import br.com.precifico.database.IConexaoFactory;
import br.com.precifico.model.Precificacao;
import br.com.precifico.model.Produto;
import br.com.precifico.model.SituacaoProduto;
import br.com.precifico.model.TipoBrinde;
import br.com.precifico.model.Vendedor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ProdutoFluxoDAOTest {

    @Autowired
    IConexaoFactory conexaoFactory;

    @Autowired
    IVendedorDAO vendedorDAO;

    @Autowired
    IProdutoDAO produtoDAO;

    @Autowired
    IPrecificacaoDAO precificacaoDAO;

    @Autowired
    IProdutoBrindeDAO produtoBrindeDAO;

    private Vendedor vendedorPersistido;

    @BeforeEach
    void prepararBancoH2() throws Exception {
        BancoTesteUtil.inicializarBanco(conexaoFactory);

        Vendedor vendedor = new Vendedor(
                "Ana Vendedora",
                "ana@precifico.com",
                "senha123",
                30,
                "Av. Central, 500",
                "123.456.789-00"
        );
        vendedorDAO.cadastrar(vendedor);
        vendedorPersistido = vendedorDAO.consultarPorEmail(vendedor);
    }

    private Produto construirProduto(String codigo, String nome, Vendedor vendedor) {
        return new ProdutoBuilder()
                .comCodigoProduto(codigo)
                .comNome(nome)
                .comDescricao("Descricao do produto " + nome)
                .comImagem("https://exemplo.com/imagem.png")
                .comSituacao(SituacaoProduto.DISPONIVEL)
                .comVendedor(vendedor)
                .constroi();
    }

    private Produto persistirProdutoCompleto(String codigo, String nome, double custo, double venda, List<TipoBrinde> brindes)
            throws Exception {
        Produto produto = construirProduto(codigo, nome, vendedorPersistido);
        produtoDAO.cadastrar(produto);

        Precificacao precificacao = new Precificacao(venda, custo);
        precificacao.setIdProduto(produto.getId());
        precificacaoDAO.cadastrar(precificacao);

        produtoBrindeDAO.salvarBrindes(produto.getId(), brindes);
        return produto;
    }

    // ==================== FLUXO DE CADASTRO NO H2 ====================

    @Test
    @DisplayName("Cadastro DAO - Positivo: deve cadastrar produto, precificação e brindes e recuperá-los no H2")
    void deveCadastrarProdutoPrecificacaoEBrindesComSucesso() throws Exception {
        // Preparação
        Produto produto = construirProduto("COD-100", "Camiseta Basica", vendedorPersistido);
        Precificacao precificacao = new Precificacao(79.90, 30.00);
        List<TipoBrinde> brindes = List.of(TipoBrinde.BALA, TipoBrinde.ADESIVO);

        // Execução
        produtoDAO.cadastrar(produto);
        precificacao.setIdProduto(produto.getId());
        precificacaoDAO.cadastrar(precificacao);
        produtoBrindeDAO.salvarBrindes(produto.getId(), brindes);

        Produto produtoEncontrado = produtoDAO.consultarPorId(produto);
        Precificacao precificacaoEncontrada = precificacaoDAO.consultarPorProduto(produto);
        List<TipoBrinde> brindesEncontrados = produtoBrindeDAO.consultarPorProduto(produto.getId());

        // Verificação
        assertTrue(produto.getId() > 0);
        assertNotNull(produtoEncontrado);
        assertEquals("COD-100", produtoEncontrado.getCodigoProduto());
        assertEquals("Camiseta Basica", produtoEncontrado.getNome());
        assertEquals(SituacaoProduto.DISPONIVEL, produtoEncontrado.getSituacao());
        assertEquals(vendedorPersistido.getId(), produtoEncontrado.obterIdVendedor());

        assertNotNull(precificacaoEncontrada);
        assertEquals(79.90, precificacaoEncontrada.getValorVenda());
        assertEquals(30.00, precificacaoEncontrada.getCustoProduto());
        assertEquals(49.90, precificacaoEncontrada.getLucro());

        assertThat(brindesEncontrados).containsExactly(TipoBrinde.BALA, TipoBrinde.ADESIVO);
    }

    @Test
    @DisplayName("Cadastro DAO - Negativo: deve lançar SQLException ao cadastrar produto com vendedor inexistente")
    void deveLancarSQLExceptionQuandoVendedorNaoExistirNoBanco() {
        // Preparação
        Vendedor vendedorInexistente = new Vendedor();
        vendedorInexistente.setId(9999);
        Produto produtoInvalido = construirProduto("COD-ERR", "Produto Orfao", vendedorInexistente);

        // Execução e Verificação
        assertThrows(SQLException.class, () -> produtoDAO.cadastrar(produtoInvalido));
    }

    // ==================== FLUXO DE ATUALIZAÇÃO NO H2 ====================

    @Test
    @DisplayName("Atualização DAO - Positivo: deve atualizar produto, precificação e substituir brindes no H2")
    void deveAtualizarProdutoPrecificacaoEBrindesComSucesso() throws Exception {
        // Preparação
        Produto produto = persistirProdutoCompleto("COD-200", "Mochila Escolar", 50.0, 120.0, List.of(TipoBrinde.BALA));

        produto.setCodigoProduto("COD-200-REV");
        produto.setNome("Mochila Executiva");
        produto.setDescricao("Mochila reforcada para notebook");
        produto.setImagem("https://exemplo.com/mochila-nova.png");
        produto.setSituacao(SituacaoProduto.VENDIDO);

        Precificacao novaPrecificacao = new Precificacao(180.0, 70.0);
        novaPrecificacao.setIdProduto(produto.getId());
        novaPrecificacao.aplicarPromocao(new BigDecimal("150.00"), new BigDecimal("16.67"));

        List<TipoBrinde> novosBrindes = List.of(TipoBrinde.PIRULITO, TipoBrinde.ADESIVO);

        // Execução
        produtoDAO.atualizar(produto);
        precificacaoDAO.atualizar(novaPrecificacao);
        produtoBrindeDAO.substituirBrindes(produto.getId(), novosBrindes);

        Produto produtoAtualizado = produtoDAO.consultarPorId(produto);
        Precificacao precificacaoAtualizada = precificacaoDAO.consultarPorProduto(produto);
        List<TipoBrinde> brindesAtualizados = produtoBrindeDAO.consultarPorProduto(produto.getId());

        // Verificação
        assertNotNull(produtoAtualizado);
        assertEquals("COD-200-REV", produtoAtualizado.getCodigoProduto());
        assertEquals("Mochila Executiva", produtoAtualizado.getNome());
        assertEquals("Mochila reforcada para notebook", produtoAtualizado.getDescricao());
        assertEquals(SituacaoProduto.VENDIDO, produtoAtualizado.getSituacao());

        assertNotNull(precificacaoAtualizada);
        assertEquals(180.0, precificacaoAtualizada.getValorVenda());
        assertEquals(70.0, precificacaoAtualizada.getCustoProduto());
        assertEquals(110.0, precificacaoAtualizada.getLucro());
        assertEquals(new BigDecimal("150.00"), precificacaoAtualizada.getValorPromocional());
        assertEquals(new BigDecimal("16.67"), precificacaoAtualizada.getPercentualDesconto());

        assertThat(brindesAtualizados).containsExactly(TipoBrinde.PIRULITO, TipoBrinde.ADESIVO);
    }

    @Test
    @DisplayName("Atualização DAO - Negativo: atualizar produto com ID inexistente não deve alterar registros existentes")
    void naoDeveAlterarProdutosExistentesAoAtualizarIdInexistente() throws Exception {
        // Preparação
        Produto produtoExistente = persistirProdutoCompleto("COD-201", "Caderno Capa Dura", 10.0, 25.0, List.of());
        Produto produtoInexistente = construirProduto("COD-999", "Nome Inexistente", vendedorPersistido);
        produtoInexistente.setId(9999);

        // Execução
        produtoDAO.atualizar(produtoInexistente);
        Produto produtoOriginal = produtoDAO.consultarPorId(produtoExistente);
        Produto consultaInexistente = produtoDAO.consultarPorId(produtoInexistente);

        // Verificação
        assertNotNull(produtoOriginal);
        assertEquals("Caderno Capa Dura", produtoOriginal.getNome());
        assertNull(consultaInexistente);
    }

    // ==================== FLUXO DE DELEÇÃO NO H2 ====================

    @Test
    @DisplayName("Deleção DAO - Positivo: deve deletar produto e remover precificação e brindes em cascata no H2")
    void deveDeletarProdutoERemoverDependenciasEmCascata() throws Exception {
        // Preparação
        Produto produto = persistirProdutoCompleto(
                "COD-300",
                "Garrafa Termica",
                25.0,
                65.0,
                List.of(TipoBrinde.BALA, TipoBrinde.PIRULITO)
        );

        // Execução
        produtoDAO.deletar(produto);

        Produto produtoAposExclusao = produtoDAO.consultarPorId(produto);
        Precificacao precificacaoAposExclusao = precificacaoDAO.consultarPorProduto(produto);
        List<TipoBrinde> brindesAposExclusao = produtoBrindeDAO.consultarPorProduto(produto.getId());

        // Verificação
        assertNull(produtoAposExclusao);
        assertNull(precificacaoAposExclusao);
        assertTrue(brindesAposExclusao.isEmpty());
    }

    @Test
    @DisplayName("Deleção DAO - Negativo: deletar ID inexistente não deve afetar produtos já cadastrados")
    void naoDeveRemoverProdutosExistentesAoDeletarIdInexistente() throws Exception {
        // Preparação
        Produto produtoExistente = persistirProdutoCompleto("COD-301", "Agenda 2026", 12.0, 30.0, List.of(TipoBrinde.ADESIVO));
        Produto filtroInexistente = new Produto();
        filtroInexistente.setId(8888);

        // Execução
        produtoDAO.deletar(filtroInexistente);
        Produto produtoMantido = produtoDAO.consultarPorId(produtoExistente);

        // Verificação
        assertNotNull(produtoMantido);
        assertEquals("Agenda 2026", produtoMantido.getNome());
    }
}
