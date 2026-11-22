package br.com.precifico.service;

import br.com.precifico.builder.ProdutoBuilder;
import br.com.precifico.dao.IPrecificacaoDAO;
import br.com.precifico.dao.IProdutoBrindeDAO;
import br.com.precifico.dao.IProdutoDAO;
import br.com.precifico.dao.IVendedorDAO;
import br.com.precifico.dto.ProdutoRequisicao;
import br.com.precifico.dto.RespostaApi;
import br.com.precifico.model.Precificacao;
import br.com.precifico.model.Produto;
import br.com.precifico.model.SituacaoProduto;
import br.com.precifico.model.TipoBrinde;
import br.com.precifico.model.Vendedor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProdutoServiceTest {

    @Mock
    IProdutoDAO produtoDAO;

    @Mock
    IPrecificacaoDAO precificacaoDAO;

    @Mock
    IProdutoBrindeDAO produtoBrindeDAO;

    @Mock
    IVendedorDAO vendedorDAO;

    @Mock
    IPrecificacaoService precificacaoService;

    @InjectMocks
    ProdutoService produtoService;

    private Vendedor vendedorLogado;
    private Vendedor outroVendedor;

    @BeforeEach
    void inicializarMocks() {
        MockitoAnnotations.openMocks(this);
        vendedorLogado = new Vendedor(1, "Maria Silva", "maria@precifico.com", "123456", 28, "Rua A, 100", "111.222.333-44");
        outroVendedor = new Vendedor(2, "Joao Souza", "joao@precifico.com", "654321", 32, "Rua B, 200", "555.666.777-88");
    }

    private ProdutoRequisicao criarRequisicaoValida() {
        ProdutoRequisicao requisicao = new ProdutoRequisicao();
        requisicao.setCodigoProduto("  PRD-001  ");
        requisicao.setNome("  Caneca Personalizada  ");
        requisicao.setDescricao("  Caneca de ceramica 325ml  ");
        requisicao.setImagem("  https://exemplo.com/caneca.png  ");
        requisicao.setSituacao("DISPONIVEL");
        requisicao.setCusto(15.0);
        requisicao.setValorVenda(35.0);
        requisicao.setBrindes(List.of());
        return requisicao;
    }

    private Produto criarProdutoExistente(int id, Vendedor dono, SituacaoProduto situacao) {
        return new ProdutoBuilder()
                .comId(id)
                .comCodigoProduto("PRD-001")
                .comNome("Caneca Personalizada")
                .comDescricao("Caneca de ceramica 325ml")
                .comImagem("https://exemplo.com/caneca.png")
                .comSituacao(situacao)
                .comVendedor(dono)
                .constroi();
    }

    // ==================== FLUXO DE CADASTRO ====================

    @Test
    @DisplayName("Cadastro - Positivo: deve cadastrar produto válido sem brindes e retornar 201")
    void deveCadastrarProdutoValidoSemBrindesComSucesso() throws Exception {
        // Preparação
        ProdutoRequisicao requisicao = criarRequisicaoValida();

        // Execução
        RespostaApi resposta = produtoService.cadastrar(requisicao, vendedorLogado);

        // Verificação
        assertEquals(201, resposta.getStatus());
        assertTrue(resposta.isSucesso());
        assertEquals("Produto 'Caneca Personalizada' cadastrado com sucesso!", resposta.getMensagem());

        Produto produtoSalvo = (Produto) resposta.getDados();
        assertNotNull(produtoSalvo);
        assertEquals("PRD-001", produtoSalvo.getCodigoProduto());
        assertEquals("Caneca Personalizada", produtoSalvo.getNome());
        assertEquals("Caneca de ceramica 325ml", produtoSalvo.getDescricao());
        assertEquals("https://exemplo.com/caneca.png", produtoSalvo.getImagem());
        assertEquals(SituacaoProduto.DISPONIVEL, produtoSalvo.getSituacao());
        assertEquals(20.0, produtoSalvo.getPrecificacao().getLucro());
        assertTrue(produtoSalvo.getBrindes().isEmpty());

        verify(produtoDAO, times(1)).cadastrar(any());
        verify(precificacaoDAO, times(1)).cadastrar(any());
        verify(produtoBrindeDAO, times(1)).salvarBrindes(anyInt(), any());
    }

    @Test
    @DisplayName("Cadastro - Positivo: deve cadastrar produto com brindes aplicando Decorator")
    void deveCadastrarProdutoComBrindesComSucesso() throws Exception {
        // Preparação
        ProdutoRequisicao requisicao = criarRequisicaoValida();
        requisicao.setBrindes(List.of("BALA", "Adesivo"));

        // Execução
        RespostaApi resposta = produtoService.cadastrar(requisicao, vendedorLogado);

        // Verificação
        assertEquals(201, resposta.getStatus());
        assertTrue(resposta.isSucesso());

        Produto produtoSalvo = (Produto) resposta.getDados();
        assertNotNull(produtoSalvo);
        assertThat(produtoSalvo.getBrindes()).containsExactly(TipoBrinde.BALA, TipoBrinde.ADESIVO);
        assertEquals(
                "Caneca de ceramica 325ml, Brinde: Bala, Brinde: Adesivo",
                produtoSalvo.getDescricaoComBrindes()
        );

        verify(produtoDAO, times(1)).cadastrar(any());
        verify(precificacaoDAO, times(1)).cadastrar(any());
        verify(produtoBrindeDAO, times(1)).salvarBrindes(anyInt(), any());
    }

    @Test
    @DisplayName("Cadastro - Negativo: deve retornar 401 ao tentar cadastrar sem vendedor logado")
    void deveRetornar401QuandoVendedorNaoEstiverLogadoNoCadastro() throws Exception {
        // Preparação
        ProdutoRequisicao requisicao = criarRequisicaoValida();

        // Execução
        RespostaApi resposta = produtoService.cadastrar(requisicao, null);

        // Verificação
        assertEquals(401, resposta.getStatus());
        assertFalse(resposta.isSucesso());
        assertEquals("Sessão expirada. Faça login novamente.", resposta.getMensagem());
        assertNull(resposta.getDados());

        verify(produtoDAO, times(0)).cadastrar(any());
        verify(precificacaoDAO, times(0)).cadastrar(any());
        verify(produtoBrindeDAO, times(0)).salvarBrindes(anyInt(), any());
    }

    @Test
    @DisplayName("Cadastro - Negativo: deve retornar 400 quando campos obrigatórios estiverem vazios")
    void deveRetornar400QuandoCamposObrigatoriosEstiveremVaziosNoCadastro() throws Exception {
        // Preparação
        ProdutoRequisicao requisicao = criarRequisicaoValida();
        requisicao.setNome("   ");

        // Execução
        RespostaApi resposta = produtoService.cadastrar(requisicao, vendedorLogado);

        // Verificação
        assertEquals(400, resposta.getStatus());
        assertFalse(resposta.isSucesso());
        assertEquals("Por favor, preencha todos os campos obrigatórios.", resposta.getMensagem());

        verify(produtoDAO, times(0)).cadastrar(any());
        verify(precificacaoDAO, times(0)).cadastrar(any());
        verify(produtoBrindeDAO, times(0)).salvarBrindes(anyInt(), any());
    }

    @Test
    @DisplayName("Cadastro - Negativo: deve retornar 400 quando custo ou valor de venda for menor ou igual a zero")
    void deveRetornar400QuandoCustoOuVendaForInvalidoNoCadastro() throws Exception {
        // Preparação
        ProdutoRequisicao requisicao = criarRequisicaoValida();
        requisicao.setCusto(0.0);

        // Execução
        RespostaApi resposta = produtoService.cadastrar(requisicao, vendedorLogado);

        // Verificação
        assertEquals(400, resposta.getStatus());
        assertFalse(resposta.isSucesso());
        assertEquals("Os valores de custo e venda devem ser maiores que zero.", resposta.getMensagem());

        verify(produtoDAO, times(0)).cadastrar(any());
        verify(precificacaoDAO, times(0)).cadastrar(any());
        verify(produtoBrindeDAO, times(0)).salvarBrindes(anyInt(), any());
    }

    @Test
    @DisplayName("Cadastro - Negativo: deve retornar 400 quando informar brinde inválido")
    void deveRetornar400QuandoInformarBrindeInvalidoNoCadastro() throws Exception {
        // Preparação
        ProdutoRequisicao requisicao = criarRequisicaoValida();
        requisicao.setBrindes(List.of("CHOCOLATE"));

        // Execução
        RespostaApi resposta = produtoService.cadastrar(requisicao, vendedorLogado);

        // Verificação
        assertEquals(400, resposta.getStatus());
        assertFalse(resposta.isSucesso());
        assertEquals("Brinde inválido informado: CHOCOLATE", resposta.getMensagem());

        verify(produtoDAO, times(0)).cadastrar(any());
        verify(precificacaoDAO, times(0)).cadastrar(any());
        verify(produtoBrindeDAO, times(0)).salvarBrindes(anyInt(), any());
    }

    @Test
    @DisplayName("Cadastro - Negativo: deve retornar 400 quando informar brindes duplicados")
    void deveRetornar400QuandoInformarBrindesDuplicadosNoCadastro() throws Exception {
        // Preparação
        ProdutoRequisicao requisicao = criarRequisicaoValida();
        requisicao.setBrindes(List.of("BALA", "Bala"));

        // Execução
        RespostaApi resposta = produtoService.cadastrar(requisicao, vendedorLogado);

        // Verificação
        assertEquals(400, resposta.getStatus());
        assertFalse(resposta.isSucesso());
        assertEquals("Não é permitido selecionar brindes duplicados.", resposta.getMensagem());

        verify(produtoDAO, times(0)).cadastrar(any());
        verify(precificacaoDAO, times(0)).cadastrar(any());
        verify(produtoBrindeDAO, times(0)).salvarBrindes(anyInt(), any());
    }

    // ==================== FLUXO DE ATUALIZAÇÃO ====================

    @Test
    @DisplayName("Atualização - Positivo: deve atualizar produto, precificação e brindes com sucesso e retornar 200")
    void deveAtualizarProdutoComSucesso() throws Exception {
        // Preparação
        int idProduto = 10;
        Produto existente = criarProdutoExistente(idProduto, vendedorLogado, SituacaoProduto.DISPONIVEL);
        Precificacao precificacaoExistente = new Precificacao(1, idProduto, 35.0, 15.0, 20.0);

        ProdutoRequisicao novosDados = criarRequisicaoValida();
        novosDados.setNome("Caneca Premium");
        novosDados.setCusto(20.0);
        novosDados.setValorVenda(50.0);
        novosDados.setSituacao("VENDIDO");
        novosDados.setBrindes(List.of("PIRULITO"));

        when(produtoDAO.consultarPorId(any())).thenReturn(existente);
        when(precificacaoDAO.consultarPorProduto(existente)).thenReturn(precificacaoExistente);

        // Execução
        RespostaApi resposta = produtoService.atualizar(idProduto, novosDados, vendedorLogado);

        // Verificação
        assertEquals(200, resposta.getStatus());
        assertTrue(resposta.isSucesso());
        assertEquals("Produto atualizado com sucesso!", resposta.getMensagem());

        Produto produtoAtualizado = (Produto) resposta.getDados();
        assertNotNull(produtoAtualizado);
        assertEquals(idProduto, produtoAtualizado.getId());
        assertEquals("Caneca Premium", produtoAtualizado.getNome());
        assertEquals(SituacaoProduto.VENDIDO, produtoAtualizado.getSituacao());
        assertEquals(30.0, produtoAtualizado.getPrecificacao().getLucro());
        assertThat(produtoAtualizado.getBrindes()).containsExactly(TipoBrinde.PIRULITO);

        verify(produtoDAO, times(1)).consultarPorId(any());
        verify(produtoDAO, times(1)).atualizar(any());
        verify(precificacaoDAO, times(1)).atualizar(any());
        verify(produtoBrindeDAO, times(1)).substituirBrindes(anyInt(), any());
    }

    @Test
    @DisplayName("Atualização - Positivo: deve preservar a situação original quando não enviada na atualização")
    void devePreservarSituacaoOriginalQuandoNaoInformadaNaAtualizacao() throws Exception {
        // Preparação
        int idProduto = 11;
        Produto existente = criarProdutoExistente(idProduto, vendedorLogado, SituacaoProduto.VENDIDO);
        Precificacao precificacaoExistente = new Precificacao(2, idProduto, 35.0, 15.0, 20.0);

        ProdutoRequisicao novosDados = criarRequisicaoValida();
        novosDados.setSituacao(null);

        when(produtoDAO.consultarPorId(any())).thenReturn(existente);
        when(precificacaoDAO.consultarPorProduto(existente)).thenReturn(precificacaoExistente);

        // Execução
        RespostaApi resposta = produtoService.atualizar(idProduto, novosDados, vendedorLogado);

        // Verificação
        assertEquals(200, resposta.getStatus());
        Produto produtoAtualizado = (Produto) resposta.getDados();
        assertNotNull(produtoAtualizado);
        assertEquals(SituacaoProduto.VENDIDO, produtoAtualizado.getSituacao());

        verify(produtoDAO, times(1)).atualizar(any());
        verify(precificacaoDAO, times(1)).atualizar(any());
        verify(produtoBrindeDAO, times(1)).substituirBrindes(anyInt(), any());
    }

    @Test
    @DisplayName("Atualização - Positivo: deve preservar promoção e recalcular desconto quando novo valor de venda continuar maior")
    void devePreservarPromocaoQuandoNovoValorVendaContinuarMaiorQueValorPromocional() throws Exception {
        // Preparação
        int idProduto = 12;
        Produto existente = criarProdutoExistente(idProduto, vendedorLogado, SituacaoProduto.DISPONIVEL);
        Precificacao precificacaoComPromocao = new Precificacao(
                3, idProduto, 100.0, 40.0, 60.0,
                new BigDecimal("80.00"), new BigDecimal("20.00")
        );

        ProdutoRequisicao novosDados = criarRequisicaoValida();
        novosDados.setCusto(40.0);
        novosDados.setValorVenda(160.0);

        when(produtoDAO.consultarPorId(any())).thenReturn(existente);
        when(precificacaoDAO.consultarPorProduto(existente)).thenReturn(precificacaoComPromocao);

        // Execução
        RespostaApi resposta = produtoService.atualizar(idProduto, novosDados, vendedorLogado);

        // Verificação
        assertEquals(200, resposta.getStatus());
        Produto produtoAtualizado = (Produto) resposta.getDados();
        assertNotNull(produtoAtualizado);
        Precificacao novaPrecificacao = produtoAtualizado.getPrecificacao();
        assertTrue(novaPrecificacao.isEmPromocao());
        assertEquals(new BigDecimal("80.00"), novaPrecificacao.getValorPromocional());
        assertEquals(new BigDecimal("50.00"), novaPrecificacao.getPercentualDesconto());

        verify(produtoDAO, times(1)).atualizar(any());
        verify(precificacaoDAO, times(1)).atualizar(any());
    }

    @Test
    @DisplayName("Atualização - Positivo: deve remover promoção quando novo valor de venda ficar menor ou igual ao promocional")
    void deveRemoverPromocaoQuandoNovoValorVendaForMenorOuIgualAoPromocional() throws Exception {
        // Preparação
        int idProduto = 13;
        Produto existente = criarProdutoExistente(idProduto, vendedorLogado, SituacaoProduto.DISPONIVEL);
        Precificacao precificacaoComPromocao = new Precificacao(
                4, idProduto, 100.0, 40.0, 60.0,
                new BigDecimal("80.00"), new BigDecimal("20.00")
        );

        ProdutoRequisicao novosDados = criarRequisicaoValida();
        novosDados.setCusto(40.0);
        novosDados.setValorVenda(75.0);

        when(produtoDAO.consultarPorId(any())).thenReturn(existente);
        when(precificacaoDAO.consultarPorProduto(existente)).thenReturn(precificacaoComPromocao);

        // Execução
        RespostaApi resposta = produtoService.atualizar(idProduto, novosDados, vendedorLogado);

        // Verificação
        assertEquals(200, resposta.getStatus());
        Produto produtoAtualizado = (Produto) resposta.getDados();
        assertNotNull(produtoAtualizado);
        Precificacao novaPrecificacao = produtoAtualizado.getPrecificacao();
        assertFalse(novaPrecificacao.isEmPromocao());
        assertNull(novaPrecificacao.getValorPromocional());
        assertNull(novaPrecificacao.getPercentualDesconto());

        verify(produtoDAO, times(1)).atualizar(any());
        verify(precificacaoDAO, times(1)).atualizar(any());
    }

    @Test
    @DisplayName("Atualização - Negativo: deve retornar 401 ao atualizar sem vendedor logado")
    void deveRetornar401QuandoVendedorNaoEstiverLogadoNaAtualizacao() throws Exception {
        // Preparação
        ProdutoRequisicao novosDados = criarRequisicaoValida();

        // Execução
        RespostaApi resposta = produtoService.atualizar(10, novosDados, null);

        // Verificação
        assertEquals(401, resposta.getStatus());
        assertFalse(resposta.isSucesso());
        assertEquals("Acesso restrito. Faça login para continuar.", resposta.getMensagem());

        verify(produtoDAO, times(0)).consultarPorId(any());
        verify(produtoDAO, times(0)).atualizar(any());
        verify(precificacaoDAO, times(0)).atualizar(any());
        verify(produtoBrindeDAO, times(0)).substituirBrindes(anyInt(), any());
    }

    @Test
    @DisplayName("Atualização - Negativo: deve retornar 404 quando produto não existir na atualização")
    void deveRetornar404QuandoProdutoNaoExistirNaAtualizacao() throws Exception {
        // Preparação
        int idInexistente = 999;
        ProdutoRequisicao novosDados = criarRequisicaoValida();
        when(produtoDAO.consultarPorId(any())).thenReturn(null);

        // Execução
        RespostaApi resposta = produtoService.atualizar(idInexistente, novosDados, vendedorLogado);

        // Verificação
        assertEquals(404, resposta.getStatus());
        assertFalse(resposta.isSucesso());
        assertEquals("Produto não encontrado.", resposta.getMensagem());

        verify(produtoDAO, times(1)).consultarPorId(any());
        verify(produtoDAO, times(0)).atualizar(any());
        verify(precificacaoDAO, times(0)).atualizar(any());
        verify(produtoBrindeDAO, times(0)).substituirBrindes(anyInt(), any());
    }

    @Test
    @DisplayName("Atualização - Negativo: deve retornar 403 quando tentar atualizar produto de outro vendedor")
    void deveRetornar403QuandoProdutoPertencerAOutroVendedorNaAtualizacao() throws Exception {
        // Preparação
        int idProduto = 10;
        Produto produtoDeOutro = criarProdutoExistente(idProduto, outroVendedor, SituacaoProduto.DISPONIVEL);
        ProdutoRequisicao novosDados = criarRequisicaoValida();
        when(produtoDAO.consultarPorId(any())).thenReturn(produtoDeOutro);

        // Execução
        RespostaApi resposta = produtoService.atualizar(idProduto, novosDados, vendedorLogado);

        // Verificação
        assertEquals(403, resposta.getStatus());
        assertFalse(resposta.isSucesso());
        assertEquals("Permissão negada para atualizar este produto.", resposta.getMensagem());

        verify(produtoDAO, times(1)).consultarPorId(any());
        verify(produtoDAO, times(0)).atualizar(any());
        verify(precificacaoDAO, times(0)).atualizar(any());
        verify(produtoBrindeDAO, times(0)).substituirBrindes(anyInt(), any());
    }

    @Test
    @DisplayName("Atualização - Negativo: deve retornar 400 quando dados informados na atualização forem inválidos")
    void deveRetornar400QuandoDadosForemInvalidosNaAtualizacao() throws Exception {
        // Preparação
        int idProduto = 10;
        Produto existente = criarProdutoExistente(idProduto, vendedorLogado, SituacaoProduto.DISPONIVEL);
        ProdutoRequisicao dadosInvalidos = criarRequisicaoValida();
        dadosInvalidos.setValorVenda(-5.0);
        when(produtoDAO.consultarPorId(any())).thenReturn(existente);

        // Execução
        RespostaApi resposta = produtoService.atualizar(idProduto, dadosInvalidos, vendedorLogado);

        // Verificação
        assertEquals(400, resposta.getStatus());
        assertFalse(resposta.isSucesso());
        assertEquals("Os valores de custo e venda devem ser maiores que zero.", resposta.getMensagem());

        verify(produtoDAO, times(1)).consultarPorId(any());
        verify(produtoDAO, times(0)).atualizar(any());
        verify(precificacaoDAO, times(0)).atualizar(any());
        verify(produtoBrindeDAO, times(0)).substituirBrindes(anyInt(), any());
    }

    // ==================== FLUXO DE DELEÇÃO ====================

    @Test
    @DisplayName("Deleção - Positivo: deve deletar produto pertencente ao vendedor logado e retornar 200")
    void deveDeletarProdutoComSucesso() throws Exception {
        // Preparação
        int idProduto = 10;
        Produto existente = criarProdutoExistente(idProduto, vendedorLogado, SituacaoProduto.DISPONIVEL);
        when(produtoDAO.consultarPorId(any())).thenReturn(existente);

        // Execução
        RespostaApi resposta = produtoService.deletar(idProduto, vendedorLogado);

        // Verificação
        assertEquals(200, resposta.getStatus());
        assertTrue(resposta.isSucesso());
        assertEquals("Produto 'Caneca Personalizada' excluído com sucesso.", resposta.getMensagem());
        assertNull(resposta.getDados());

        verify(produtoDAO, times(1)).consultarPorId(any());
        verify(produtoDAO, times(1)).deletar(existente);
    }

    @Test
    @DisplayName("Deleção - Negativo: deve retornar 401 ao tentar deletar sem vendedor logado")
    void deveRetornar401QuandoVendedorNaoEstiverLogadoNaDelecao() throws Exception {
        // Preparação
        int idProduto = 10;

        // Execução
        RespostaApi resposta = produtoService.deletar(idProduto, null);

        // Verificação
        assertEquals(401, resposta.getStatus());
        assertFalse(resposta.isSucesso());
        assertEquals("Acesso restrito. Faça login para continuar.", resposta.getMensagem());

        verify(produtoDAO, times(0)).consultarPorId(any());
        verify(produtoDAO, times(0)).deletar(any());
    }

    @Test
    @DisplayName("Deleção - Negativo: deve retornar 404 quando produto não existir na deleção")
    void deveRetornar404QuandoProdutoNaoExistirNaDelecao() throws Exception {
        // Preparação
        int idInexistente = 999;
        when(produtoDAO.consultarPorId(any())).thenReturn(null);

        // Execução
        RespostaApi resposta = produtoService.deletar(idInexistente, vendedorLogado);

        // Verificação
        assertEquals(404, resposta.getStatus());
        assertFalse(resposta.isSucesso());
        assertEquals("Produto não encontrado.", resposta.getMensagem());

        verify(produtoDAO, times(1)).consultarPorId(any());
        verify(produtoDAO, times(0)).deletar(any());
    }

    @Test
    @DisplayName("Deleção - Negativo: deve retornar 403 quando tentar deletar produto de outro vendedor")
    void deveRetornar403QuandoProdutoPertencerAOutroVendedorNaDelecao() throws Exception {
        // Preparação
        int idProduto = 10;
        Produto produtoDeOutro = criarProdutoExistente(idProduto, outroVendedor, SituacaoProduto.DISPONIVEL);
        when(produtoDAO.consultarPorId(any())).thenReturn(produtoDeOutro);

        // Execução
        RespostaApi resposta = produtoService.deletar(idProduto, vendedorLogado);

        // Verificação
        assertEquals(403, resposta.getStatus());
        assertFalse(resposta.isSucesso());
        assertEquals("Permissão negada para excluir este produto.", resposta.getMensagem());

        verify(produtoDAO, times(1)).consultarPorId(any());
        verify(produtoDAO, times(0)).deletar(any());
    }
}
