package br.com.precifico.controller;

import br.com.precifico.dto.CadastroVendedorRequisicao;
import br.com.precifico.dto.LoginRequisicao;
import br.com.precifico.dto.RespostaApi;
import br.com.precifico.service.IVendedorService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ControleVendedor {

    private final IVendedorService vendedorService;

    public ControleVendedor(IVendedorService vendedorService) {
        this.vendedorService = vendedorService;
    }

    @PostMapping("/api/vendedores")
    public ResponseEntity<RespostaApi> cadastrar(
            @RequestBody CadastroVendedorRequisicao dados,
            HttpServletRequest requisicaoHttp
    ) {
        try {
            RespostaApi resposta = vendedorService.cadastrar(dados);
            registrarSessaoSeAutenticado(resposta, requisicaoHttp);
            return montarResposta(resposta);
        } catch (Exception erro) {
            return montarErroInterno(erro);
        }
    }

    @PostMapping("/api/auth/login")
    public ResponseEntity<RespostaApi> entrar(
            @RequestBody LoginRequisicao dados,
            HttpServletRequest requisicaoHttp
    ) {
        try {
            RespostaApi resposta = vendedorService.entrar(dados);
            registrarSessaoSeAutenticado(resposta, requisicaoHttp);
            return montarResposta(resposta);
        } catch (Exception erro) {
            return montarErroInterno(erro);
        }
    }

    @PostMapping("/api/auth/logout")
    public ResponseEntity<RespostaApi> sair(HttpServletRequest requisicaoHttp) {
        HttpSession sessao = requisicaoHttp.getSession(false);
        if (sessao != null) {
            sessao.invalidate();
        }

        RespostaApi resposta = vendedorService.sair();
        return montarResposta(resposta);
    }

    private ResponseEntity<RespostaApi> montarResposta(RespostaApi resposta) {
        return ResponseEntity.status(resposta.getStatus()).body(resposta);
    }

    private ResponseEntity<RespostaApi> montarErroInterno(Exception erro) {
        RespostaApi respostaErro = RespostaApi.erro(500, "Ocorreu um erro no processamento: " + erro.getMessage());
        return ResponseEntity.status(500).body(respostaErro);
    }

    private void registrarSessaoSeAutenticado(RespostaApi resposta, HttpServletRequest requisicaoHttp) {
        if (!resposta.isSucesso() || resposta.getDados() == null) {
            return;
        }
        HttpSession sessao = requisicaoHttp.getSession(true);
        sessao.setAttribute("vendedorLogado", resposta.getDados());
    }
}
