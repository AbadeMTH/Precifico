function formatarMoeda(valor) {
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(Number(valor) || 0);
}

function formatarDecimal(valor, casas = 2) {
    return (Number(valor) || 0).toLocaleString('pt-BR', {
        minimumFractionDigits: casas,
        maximumFractionDigits: casas
    });
}

function formatarPorcentagem(valor) {
    return new Intl.NumberFormat('pt-BR', { maximumFractionDigits: 2 }).format(Number(valor) || 0) + '%';
}

function escaparHtml(texto) {
    const div = document.createElement('div');
    div.textContent = texto || '';
    return div.innerHTML;
}

function obterUrlImagemProduto(imagem) {
    return (imagem && imagem.trim() !== '') ? imagem : 'img/sem-foto.jpg';
}
