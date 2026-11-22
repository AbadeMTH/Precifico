package br.com.precifico.controller;

import br.com.precifico.dto.ProdutoRequisicao;
import br.com.precifico.dto.PromocaoRequisicao;
import br.com.precifico.dto.RespostaApi;
import br.com.precifico.dto.SimulacaoRequisicao;
import br.com.precifico.model.Vendedor;
import br.com.precifico.service.IPrecificacaoService;
import br.com.precifico.service.IProdutoService;
import br.com.precifico.service.IPromocaoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/produtos")
public class ControleProduto {

    private final IProdutoService produtoService;
    private final IPrecificacaoService precificacaoService;
    private final IPromocaoService promocaoService;

    public ControleProduto(
            IProdutoService produtoService,
            IPrecificacaoService precificacaoService,
            IPromocaoService promocaoService
    ) {
        this.produtoService = produtoService;
        this.precificacaoService = precificacaoService;
        this.promocaoService = promocaoService;
    }

    @GetMapping
    public ResponseEntity<RespostaApi> listarTodos(HttpServletRequest requisicaoHttp) {
        try {
            Vendedor vendedorLogado = obterVendedorLogado(requisicaoHttp);
            RespostaApi resposta = produtoService.listarTodos(vendedorLogado);
            return montarResposta(resposta);
        } catch (Exception erro) {
            return montarErroInterno(erro);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<RespostaApi> consultarPorId(@PathVariable int id, HttpServletRequest requisicaoHttp) {
        try {
            Vendedor vendedorLogado = obterVendedorLogado(requisicaoHttp);
            RespostaApi resposta = produtoService.consultarPorId(id, vendedorLogado);
            return montarResposta(resposta);
        } catch (Exception erro) {
            return montarErroInterno(erro);
        }
    }

    @PostMapping
    public ResponseEntity<RespostaApi> cadastrar(
            @RequestBody ProdutoRequisicao dados,
            HttpServletRequest requisicaoHttp
    ) {
        try {
            Vendedor vendedorLogado = obterVendedorLogado(requisicaoHttp);
            RespostaApi resposta = produtoService.cadastrar(dados, vendedorLogado);
            return montarResposta(resposta);
        } catch (Exception erro) {
            return montarErroInterno(erro);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<RespostaApi> atualizar(
            @PathVariable int id,
            @RequestBody ProdutoRequisicao dados,
            HttpServletRequest requisicaoHttp
    ) {
        try {
            Vendedor vendedorLogado = obterVendedorLogado(requisicaoHttp);
            RespostaApi resposta = produtoService.atualizar(id, dados, vendedorLogado);
            return montarResposta(resposta);
        } catch (Exception erro) {
            return montarErroInterno(erro);
        }
    }

    @PatchMapping("/{id}/situacao")
    public ResponseEntity<RespostaApi> alterarSituacao(@PathVariable int id, HttpServletRequest requisicaoHttp) {
        try {
            Vendedor vendedorLogado = obterVendedorLogado(requisicaoHttp);
            RespostaApi resposta = produtoService.alterarSituacao(id, vendedorLogado);
            return montarResposta(resposta);
        } catch (Exception erro) {
            return montarErroInterno(erro);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RespostaApi> deletar(@PathVariable int id, HttpServletRequest requisicaoHttp) {
        try {
            Vendedor vendedorLogado = obterVendedorLogado(requisicaoHttp);
            RespostaApi resposta = produtoService.deletar(id, vendedorLogado);
            return montarResposta(resposta);
        } catch (Exception erro) {
            return montarErroInterno(erro);
        }
    }

    @PostMapping("/simular")
    public ResponseEntity<RespostaApi> simular(@RequestBody SimulacaoRequisicao dados) {
        try {
            RespostaApi resposta = precificacaoService.simular(dados);
            return montarResposta(resposta);
        } catch (Exception erro) {
            return montarErroInterno(erro);
        }
    }

    @PutMapping("/{id}/promocao")
    public ResponseEntity<RespostaApi> aplicarPromocao(
            @PathVariable int id,
            @RequestBody PromocaoRequisicao dados,
            HttpServletRequest requisicaoHttp
    ) {
        try {
            Vendedor vendedorLogado = obterVendedorLogado(requisicaoHttp);
            RespostaApi resposta = promocaoService.aplicarPromocao(id, dados, vendedorLogado);
            return montarResposta(resposta);
        } catch (Exception erro) {
            return montarErroInterno(erro);
        }
    }

    @DeleteMapping("/{id}/promocao")
    public ResponseEntity<RespostaApi> removerPromocao(
            @PathVariable int id,
            HttpServletRequest requisicaoHttp
    ) {
        try {
            Vendedor vendedorLogado = obterVendedorLogado(requisicaoHttp);
            RespostaApi resposta = promocaoService.removerPromocao(id, vendedorLogado);
            return montarResposta(resposta);
        } catch (Exception erro) {
            return montarErroInterno(erro);
        }
    }

    private ResponseEntity<RespostaApi> montarResposta(RespostaApi resposta) {
        return ResponseEntity.status(resposta.getStatus()).body(resposta);
    }

    private ResponseEntity<RespostaApi> montarErroInterno(Exception erro) {
        RespostaApi respostaErro = RespostaApi.erro(500, "Ocorreu um erro no processamento: " + erro.getMessage());
        return ResponseEntity.status(500).body(respostaErro);
    }

    private Vendedor obterVendedorLogado(HttpServletRequest requisicaoHttp) {
        HttpSession sessao = requisicaoHttp.getSession(false);
        if (sessao == null) {
            return null;
        }
        return (Vendedor) sessao.getAttribute("vendedorLogado");
    }
}
