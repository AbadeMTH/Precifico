package br.com.precifico.service;

import br.com.precifico.dto.RespostaApi;
import br.com.precifico.dto.SimulacaoRequisicao;
import br.com.precifico.model.Precificacao;
import br.com.precifico.model.Produto;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class PrecificacaoService implements IPrecificacaoService {

    private static final double MARGEM_PADRAO_DIAGNOSTICO = 25.0;

    @Override
    public RespostaApi simular(SimulacaoRequisicao requisicao) {
        if (requisicao == null) {
            return RespostaApi.erro(400, "Valores numéricos inválidos.");
        }

        double custo = requisicao.getCusto() != null ? requisicao.getCusto() : 0.0;
        double margem = requisicao.getMargem() != null ? requisicao.getMargem() : 0.0;
        double valorVenda = requisicao.getValorVenda() != null ? requisicao.getValorVenda() : 0.0;

        if (custo < 0 || margem < 0 || valorVenda < 0) {
            return RespostaApi.erro(400, "Valores numéricos inválidos.");
        }

        Map<String, Object> simulacao = calcularSimulacao(custo, margem, valorVenda);
        return RespostaApi.sucesso(200, "Simulação calculada com sucesso.", simulacao);
    }

    @Override
    public Map<String, Object> montarDetalhesComerciais(Produto produto) {
        double margemEfetiva = 0.0;
        double precoSugerido = 0.0;
        String diagnosticoComercial = "Sem precificação cadastrada.";

        if (produto.possuiPrecificacao()) {
            Precificacao precificacao = produto.getPrecificacao();
            margemEfetiva = precificacao.getMargemEfetiva();
            precoSugerido = precificacao.calcularPrecoSugerido(MARGEM_PADRAO_DIAGNOSTICO);
            diagnosticoComercial = precificacao.classificarPreco(MARGEM_PADRAO_DIAGNOSTICO);
        }

        Map<String, Object> detalhes = new LinkedHashMap<>();
        detalhes.put("produto", produto);
        detalhes.put("margemEfetiva", margemEfetiva);
        detalhes.put("precoSugerido", precoSugerido);
        detalhes.put("diagnosticoComercial", diagnosticoComercial);
        return detalhes;
    }

    private Map<String, Object> calcularSimulacao(double custo, double margem, double valorVenda) {
        Precificacao precificacao = new Precificacao(valorVenda, custo);
        boolean possuiCusto = custo > 0;
        boolean possuiVenda = possuiCusto && valorVenda > 0;

        double precoSugerido = possuiCusto ? precificacao.calcularPrecoSugerido(margem) : 0.0;
        double lucro = possuiVenda ? precificacao.calcularLucro() : 0.0;
        double margemEfetiva = possuiVenda ? precificacao.getMargemEfetiva() : 0.0;
        String diagnostico = possuiVenda ? precificacao.classificarPreco(margem) : "";

        Map<String, Object> simulacao = new LinkedHashMap<>();
        simulacao.put("precoSugerido", precoSugerido);
        simulacao.put("lucro", lucro);
        simulacao.put("margemEfetiva", margemEfetiva);
        simulacao.put("diagnostico", diagnostico);
        return simulacao;
    }
}
