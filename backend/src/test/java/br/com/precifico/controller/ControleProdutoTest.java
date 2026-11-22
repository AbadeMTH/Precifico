package br.com.precifico.controller;

import br.com.precifico.builder.ProdutoBuilder;
import br.com.precifico.dao.BancoTesteUtil;
import br.com.precifico.dao.IPrecificacaoDAO;
import br.com.precifico.dao.IProdutoBrindeDAO;
import br.com.precifico.dao.IProdutoDAO;
import br.com.precifico.dao.IVendedorDAO;
import br.com.precifico.database.IConexaoFactory;
import br.com.precifico.dto.ProdutoRequisicao;
import br.com.precifico.model.Precificacao;
import br.com.precifico.model.Produto;
import br.com.precifico.model.SituacaoProduto;
import br.com.precifico.model.TipoBrinde;
import br.com.precifico.model.Vendedor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ControleProdutoTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

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

    private Vendedor vendedorLogado;
    private Vendedor outroVendedor;
    private MockHttpSession sessaoAutenticada;

    @BeforeEach
    void prepararAmbienteTeste() throws Exception {
        BancoTesteUtil.inicializarBanco(conexaoFactory);

        Vendedor vendedor1 = new Vendedor("Maria Silva", "maria@precifico.com", "123456", 28, "Rua A, 100", "111.222.333-44");
        Vendedor vendedor2 = new Vendedor("Joao Souza", "joao@precifico.com", "654321", 32, "Rua B, 200", "555.666.777-88");
        vendedorDAO.cadastrar(vendedor1);
        vendedorDAO.cadastrar(vendedor2);

        vendedorLogado = vendedorDAO.consultarPorEmail(vendedor1);
        outroVendedor = vendedorDAO.consultarPorEmail(vendedor2);

        sessaoAutenticada = new MockHttpSession();
        sessaoAutenticada.setAttribute("vendedorLogado", vendedorLogado);
    }

    private ProdutoRequisicao criarRequisicaoValida() {
        ProdutoRequisicao requisicao = new ProdutoRequisicao();
        requisicao.setCodigoProduto("PRD-001");
        requisicao.setNome("Caneca Personalizada");
        requisicao.setDescricao("Caneca de ceramica 325ml");
        requisicao.setImagem("https://exemplo.com/caneca.png");
        requisicao.setSituacao("DISPONIVEL");
        requisicao.setCusto(15.0);
        requisicao.setValorVenda(35.0);
        requisicao.setBrindes(List.of("BALA"));
        return requisicao;
    }

    private Produto cadastrarProdutoNoBanco(Vendedor dono) throws Exception {
        Produto produto = new ProdutoBuilder()
                .comCodigoProduto("PRD-BASE")
                .comNome("Caneca Personalizada")
                .comDescricao("Caneca de ceramica 325ml")
                .comImagem("https://exemplo.com/caneca.png")
                .comSituacao(SituacaoProduto.DISPONIVEL)
                .comVendedor(dono)
                .constroi();
        produtoDAO.cadastrar(produto);

        Precificacao precificacao = new Precificacao(35.0, 15.0);
        precificacao.setIdProduto(produto.getId());
        precificacaoDAO.cadastrar(precificacao);

        produtoBrindeDAO.salvarBrindes(produto.getId(), List.of(TipoBrinde.BALA));
        return produto;
    }

    // ==================== POST /api/produtos (CADASTRO) ====================

    @Test
    @DisplayName("POST /api/produtos - Positivo: deve retornar 201 ao cadastrar produto autenticado")
    void deveRetornar201AoCadastrarProdutoComSucesso() throws Exception {
        // Preparação
        ProdutoRequisicao requisicao = criarRequisicaoValida();

        // Execução e Verificação
        mockMvc.perform(post("/api/produtos")
                        .session(sessaoAutenticada)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requisicao)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sucesso").value(true))
                .andExpect(jsonPath("$.mensagem").value("Produto 'Caneca Personalizada' cadastrado com sucesso!"))
                .andExpect(jsonPath("$.dados.nome").value("Caneca Personalizada"));
    }

    @Test
    @DisplayName("POST /api/produtos - Negativo: deve retornar 401 ao cadastrar sem sessão")
    void deveRetornar401AoCadastrarSemSessao() throws Exception {
        // Preparação
        ProdutoRequisicao requisicao = criarRequisicaoValida();

        // Execução e Verificação
        mockMvc.perform(post("/api/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requisicao)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.sucesso").value(false))
                .andExpect(jsonPath("$.mensagem").value("Sessão expirada. Faça login novamente."));
    }

    @Test
    @DisplayName("POST /api/produtos - Negativo: deve retornar 400 quando validação falhar no cadastro")
    void deveRetornar400QuandoDadosInvalidosNoCadastro() throws Exception {
        // Preparação
        ProdutoRequisicao requisicaoInvalida = criarRequisicaoValida();
        requisicaoInvalida.setNome("");

        // Execução e Verificação
        mockMvc.perform(post("/api/produtos")
                        .session(sessaoAutenticada)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requisicaoInvalida)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.sucesso").value(false))
                .andExpect(jsonPath("$.mensagem").value("Por favor, preencha todos os campos obrigatórios."));
    }

    // ==================== PUT /api/produtos/{id} (ATUALIZAÇÃO) ====================

    @Test
    @DisplayName("PUT /api/produtos/{id} - Positivo: deve retornar 200 ao atualizar produto existente")
    void deveRetornar200AoAtualizarProdutoComSucesso() throws Exception {
        // Preparação
        Produto produtoSalvo = cadastrarProdutoNoBanco(vendedorLogado);
        ProdutoRequisicao requisicao = criarRequisicaoValida();
        requisicao.setNome("Caneca Atualizada");
        requisicao.setValorVenda(45.0);

        // Execução e Verificação
        mockMvc.perform(put("/api/produtos/{id}", produtoSalvo.getId())
                        .session(sessaoAutenticada)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requisicao)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sucesso").value(true))
                .andExpect(jsonPath("$.mensagem").value("Produto atualizado com sucesso!"))
                .andExpect(jsonPath("$.dados.id").value(produtoSalvo.getId()))
                .andExpect(jsonPath("$.dados.nome").value("Caneca Atualizada"));
    }

    @Test
    @DisplayName("PUT /api/produtos/{id} - Negativo: deve retornar 404 ao atualizar produto com ID inexistente")
    void deveRetornar404AoAtualizarProdutoInexistente() throws Exception {
        // Preparação
        ProdutoRequisicao requisicao = criarRequisicaoValida();

        // Execução e Verificação
        mockMvc.perform(put("/api/produtos/{id}", 999)
                        .session(sessaoAutenticada)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requisicao)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.sucesso").value(false))
                .andExpect(jsonPath("$.mensagem").value("Produto não encontrado."));
    }

    @Test
    @DisplayName("PUT /api/produtos/{id} - Negativo: deve retornar 403 ao atualizar produto de outro vendedor")
    void deveRetornar403AoAtualizarProdutoSemPermissao() throws Exception {
        // Preparação
        Produto produtoOutroVendedor = cadastrarProdutoNoBanco(outroVendedor);
        ProdutoRequisicao requisicao = criarRequisicaoValida();

        // Execução e Verificação
        mockMvc.perform(put("/api/produtos/{id}", produtoOutroVendedor.getId())
                        .session(sessaoAutenticada)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requisicao)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.sucesso").value(false))
                .andExpect(jsonPath("$.mensagem").value("Permissão negada para atualizar este produto."));
    }

    @Test
    @DisplayName("PUT /api/produtos/{id} - Negativo: deve retornar 401 ao atualizar sem sessão")
    void deveRetornar401AoAtualizarSemSessao() throws Exception {
        // Preparação
        Produto produtoSalvo = cadastrarProdutoNoBanco(vendedorLogado);
        ProdutoRequisicao requisicao = criarRequisicaoValida();

        // Execução e Verificação
        mockMvc.perform(put("/api/produtos/{id}", produtoSalvo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requisicao)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.sucesso").value(false))
                .andExpect(jsonPath("$.mensagem").value("Acesso restrito. Faça login para continuar."));
    }

    // ==================== DELETE /api/produtos/{id} (DELEÇÃO) ====================

    @Test
    @DisplayName("DELETE /api/produtos/{id} - Positivo: deve retornar 200 ao deletar produto existente")
    void deveRetornar200AoDeletarProdutoComSucesso() throws Exception {
        // Preparação
        Produto produtoSalvo = cadastrarProdutoNoBanco(vendedorLogado);

        // Execução e Verificação
        mockMvc.perform(delete("/api/produtos/{id}", produtoSalvo.getId())
                        .session(sessaoAutenticada))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sucesso").value(true))
                .andExpect(jsonPath("$.mensagem").value("Produto 'Caneca Personalizada' excluído com sucesso."));
    }

    @Test
    @DisplayName("DELETE /api/produtos/{id} - Negativo: deve retornar 404 ao deletar produto com ID inexistente")
    void deveRetornar404AoDeletarProdutoInexistente() throws Exception {
        // Preparação
        int idInexistente = 999;

        // Execução e Verificação
        mockMvc.perform(delete("/api/produtos/{id}", idInexistente)
                        .session(sessaoAutenticada))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.sucesso").value(false))
                .andExpect(jsonPath("$.mensagem").value("Produto não encontrado."));
    }

    @Test
    @DisplayName("DELETE /api/produtos/{id} - Negativo: deve retornar 403 ao deletar produto de outro vendedor")
    void deveRetornar403AoDeletarProdutoSemPermissao() throws Exception {
        // Preparação
        Produto produtoOutroVendedor = cadastrarProdutoNoBanco(outroVendedor);

        // Execução e Verificação
        mockMvc.perform(delete("/api/produtos/{id}", produtoOutroVendedor.getId())
                        .session(sessaoAutenticada))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.sucesso").value(false))
                .andExpect(jsonPath("$.mensagem").value("Permissão negada para excluir este produto."));
    }

    @Test
    @DisplayName("DELETE /api/produtos/{id} - Negativo: deve retornar 401 ao deletar sem sessão")
    void deveRetornar401AoDeletarSemSessao() throws Exception {
        // Preparação
        Produto produtoSalvo = cadastrarProdutoNoBanco(vendedorLogado);

        // Execução e Verificação
        mockMvc.perform(delete("/api/produtos/{id}", produtoSalvo.getId()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.sucesso").value(false))
                .andExpect(jsonPath("$.mensagem").value("Acesso restrito. Faça login para continuar."));
    }
}
