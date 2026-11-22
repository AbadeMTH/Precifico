function arredondarDuasCasas(valor) {
    return Math.round((valor + Number.EPSILON) * 100) / 100;
}

function converterParaCentavos(valor) {
    return Math.round(arredondarDuasCasas(valor) * 100);
}

function calcularDescontoPorValor(valorAtual, novoValor) {
    const novoValorArredondado = arredondarDuasCasas(novoValor);
    return arredondarDuasCasas(((valorAtual - novoValorArredondado) / valorAtual) * 100);
}

function calcularValorPorDesconto(valorAtual, desconto) {
    const descontoArredondado = arredondarDuasCasas(desconto);
    return arredondarDuasCasas(valorAtual * (1 - (descontoArredondado / 100)));
}

function validarPromocao(valorAtual, novoValor, desconto) {
    if (!Number.isFinite(valorAtual) || valorAtual <= 0) {
        return { valido: false, mensagem: 'O produto não possui um preço atual válido para promoção.' };
    }

    if ((Number.isFinite(novoValor) && novoValor < 0) || (Number.isFinite(desconto) && desconto < 0)) {
        return { valido: false, mensagem: 'Não são permitidos valores negativos.' };
    }

    if (Number.isFinite(novoValor)) {
        const novoValorCheck = arredondarDuasCasas(novoValor);
        if (novoValorCheck <= 0) {
            return { valido: false, mensagem: 'O novo valor promocional deve ser maior que zero.' };
        }
        if (converterParaCentavos(novoValorCheck) >= converterParaCentavos(valorAtual)) {
            return { valido: false, mensagem: 'O novo valor promocional deve ser menor que o valor atual (DE).' };
        }
    }

    if (Number.isFinite(desconto)) {
        const descontoCheck = arredondarDuasCasas(desconto);
        if (descontoCheck <= 0 || descontoCheck >= 100) {
            return { valido: false, mensagem: 'O desconto percentual deve ser maior que zero e menor que 100%.' };
        }
    }

    if (!Number.isFinite(novoValor) || !Number.isFinite(desconto)) {
        return { valido: false, mensagem: 'Informe o novo valor promocional ou o percentual de desconto.' };
    }

    return {
        valido: true,
        novoValor: arredondarDuasCasas(novoValor),
        desconto: arredondarDuasCasas(desconto)
    };
}

function calcularResumoPromocao(valorAtual, custoProduto, novoValor) {
    return {
        economia: arredondarDuasCasas(valorAtual - novoValor),
        lucroPromocional: arredondarDuasCasas(novoValor - custoProduto)
    };
}

function calcularMetricasExibicaoProduto(precificacao) {
    const dados = precificacao || {};
    const custo = dados.custoProduto || 0.0;
    const venda = dados.valorVenda || 0.0;
    const emPromocao = Boolean(dados.emPromocao && dados.valorPromocional != null);
    const valorPromocional = emPromocao ? Number(dados.valorPromocional) : null;
    const percentualDesconto = emPromocao ? Number(dados.percentualDesconto || 0) : null;
    const lucro = emPromocao && dados.lucroPromocional != null
        ? Number(dados.lucroPromocional)
        : (dados.lucro || 0.0);
    const margem = emPromocao && custo > 0
        ? ((lucro / custo) * 100.0)
        : (dados.margemEfetiva || 0.0);

    return {
        custo: custo,
        venda: venda,
        emPromocao: emPromocao,
        valorPromocional: valorPromocional,
        percentualDesconto: percentualDesconto,
        lucro: lucro,
        margem: margem
    };
}
