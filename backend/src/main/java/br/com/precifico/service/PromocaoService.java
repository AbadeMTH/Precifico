package br.com.precifico.service;

import br.com.precifico.dao.IPrecificacaoDAO;
import br.com.precifico.dao.IProdutoDAO;
import br.com.precifico.dto.PromocaoRequisicao;
import br.com.precifico.dto.RespostaApi;
import br.com.precifico.model.Precificacao;
import br.com.precifico.model.Produto;
import br.com.precifico.model.Vendedor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class PromocaoService implements IPromocaoService {

    private static final BigDecimal CEM = new BigDecimal("100");
    private static final BigDecimal TOLERANCIA_CONSISTENCIA = new BigDecimal("0.02");

    private final IProdutoDAO produtoDAO;
    private final IPrecificacaoDAO precificacaoDAO;

    public PromocaoService(IProdutoDAO produtoDAO, IPrecificacaoDAO precificacaoDAO) {
        this.produtoDAO = produtoDAO;
        this.precificacaoDAO = precificacaoDAO;
    }

    @Override
    public RespostaApi aplicarPromocao(int idProduto, PromocaoRequisicao dados, Vendedor vendedorLogado) throws Exception {
        if (vendedorLogado == null) {
            return RespostaApi.erro(401, "Acesso restrito. Faça login para continuar.");
        }

        if (idProduto <= 0) {
            return RespostaApi.erro(400, "Produto inválido para promoção.");
        }

        Produto produto = buscarProdutoPorId(idProduto);
        if (produto == null) {
            return RespostaApi.erro(404, "Produto não encontrado.");
        }

        if (!produto.pertenceA(vendedorLogado)) {
            return RespostaApi.erro(403, "Permissão negada para promover este produto.");
        }

        Precificacao precificacao = precificacaoDAO.consultarPorProduto(produto);
        if (precificacao == null || precificacao.getValorVenda() <= 0) {
            return RespostaApi.erro(400, "O produto não possui um preço atual válido para promoção.");
        }

        if (dados == null || (dados.getValorPromocional() == null && dados.getPercentualDesconto() == null)) {
            return RespostaApi.erro(400, "Informe o novo valor promocional ou o percentual de desconto.");
        }

        BigDecimal valorAtual = precificacao.obterValorVendaDecimal();
        RespostaApi erroEntradas = validarEntradasBrutas(dados, valorAtual);
        if (erroEntradas != null) {
            return erroEntradas;
        }

        BigDecimal novoValorPromocional;
        BigDecimal percentualCalculado;

        if (dados.getValorPromocional() != null) {
            novoValorPromocional = dados.getValorPromocional().setScale(2, RoundingMode.HALF_UP);
            if (novoValorPromocional.compareTo(BigDecimal.ZERO) <= 0) {
                return RespostaApi.erro(400, "O novo valor promocional deve ser maior que zero.");
            }
            if (novoValorPromocional.compareTo(valorAtual) >= 0) {
                return RespostaApi.erro(400, "O novo valor promocional deve ser menor que o valor atual do produto.");
            }

            if (dados.getPercentualDesconto() != null) {
                BigDecimal valorEsperado = Precificacao.calcularValorPromocional(valorAtual, dados.getPercentualDesconto());
                if (novoValorPromocional.subtract(valorEsperado).abs().compareTo(TOLERANCIA_CONSISTENCIA) > 0) {
                    return RespostaApi.erro(400, "Os valores informados para o novo preço e o desconto percentual são inconsistentes.");
                }
            }

            percentualCalculado = Precificacao.calcularPercentualDesconto(valorAtual, novoValorPromocional);
        } else {
            BigDecimal percentualInformado = dados.getPercentualDesconto().setScale(2, RoundingMode.HALF_UP);
            novoValorPromocional = Precificacao.calcularValorPromocional(valorAtual, percentualInformado);
            if (novoValorPromocional.compareTo(BigDecimal.ZERO) <= 0
                    || novoValorPromocional.compareTo(valorAtual) >= 0) {
                return RespostaApi.erro(400, "O desconto informado gera um valor promocional inválido para este produto.");
            }
            percentualCalculado = Precificacao.calcularPercentualDesconto(valorAtual, novoValorPromocional);
        }

        if (percentualCalculado.compareTo(BigDecimal.ZERO) <= 0 || percentualCalculado.compareTo(CEM) >= 0) {
            return RespostaApi.erro(400, "O desconto percentual deve ser maior que zero e menor que 100%.");
        }

        precificacao.aplicarPromocao(novoValorPromocional, percentualCalculado);
        precificacaoDAO.atualizarPromocao(precificacao);

        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("idProduto", produto.getId());
        resultado.put("nomeProduto", produto.getNome());
        resultado.put("valorAtual", valorAtual);
        resultado.put("valorPromocional", precificacao.getValorPromocional());
        resultado.put("percentualDesconto", precificacao.getPercentualDesconto());
        resultado.put("precificacao", precificacao);

        return RespostaApi.sucesso(200, "Promoção aplicada com sucesso!", resultado);
    }

    @Override
    public RespostaApi removerPromocao(int idProduto, Vendedor vendedorLogado) throws Exception {
        if (vendedorLogado == null) {
            return RespostaApi.erro(401, "Acesso restrito. Faça login para continuar.");
        }

        if (idProduto <= 0) {
            return RespostaApi.erro(400, "Produto inválido.");
        }

        Produto produto = buscarProdutoPorId(idProduto);
        if (produto == null) {
            return RespostaApi.erro(404, "Produto não encontrado.");
        }

        if (!produto.pertenceA(vendedorLogado)) {
            return RespostaApi.erro(403, "Permissão negada para alterar a promoção deste produto.");
        }

        Precificacao precificacao = precificacaoDAO.consultarPorProduto(produto);
        if (precificacao == null) {
            return RespostaApi.erro(404, "Precificação do produto não encontrada.");
        }

        precificacao.removerPromocao();
        precificacaoDAO.atualizarPromocao(precificacao);

        return RespostaApi.sucesso(200, "Promoção removida com sucesso.", precificacao);
    }

    private RespostaApi validarEntradasBrutas(PromocaoRequisicao dados, BigDecimal valorAtual) {
        if (dados.getValorPromocional() != null) {
            if (dados.getValorPromocional().compareTo(BigDecimal.ZERO) <= 0) {
                return RespostaApi.erro(400, "O novo valor promocional deve ser maior que zero.");
            }
            if (dados.getValorPromocional().compareTo(valorAtual) >= 0) {
                return RespostaApi.erro(400, "O novo valor promocional deve ser menor que o valor atual do produto.");
            }
        }

        if (dados.getPercentualDesconto() != null) {
            if (dados.getPercentualDesconto().compareTo(BigDecimal.ZERO) <= 0
                    || dados.getPercentualDesconto().compareTo(CEM) >= 0) {
                return RespostaApi.erro(400, "O desconto percentual deve ser maior que zero e menor que 100%.");
            }
        }

        return null;
    }

    private Produto buscarProdutoPorId(int idProduto) throws Exception {
        Produto filtro = new Produto();
        filtro.setId(idProduto);
        return produtoDAO.consultarPorId(filtro);
    }
}
