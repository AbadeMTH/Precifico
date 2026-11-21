package br.com.precifico.model;

public class Produto {
    private int id;
    private String codigoProduto;
    private String nome;
    private String descricao;
    private String imagem;
    private SituacaoProduto situacao;
    private Vendedor vendedor;
    private Precificacao precificacao;

    public Produto() {
        this.situacao = SituacaoProduto.DISPONIVEL;
    }

    public Produto(
            int id,
            String codigoProduto,
            String nome,
            String descricao,
            String imagem,
            SituacaoProduto situacao,
            Vendedor vendedor,
            Precificacao precificacao
    ) {
        this.id = id;
        this.codigoProduto = codigoProduto;
        this.nome = nome;
        this.descricao = descricao;
        this.imagem = imagem;
        this.situacao = situacao != null ? situacao : SituacaoProduto.DISPONIVEL;
        this.vendedor = vendedor;
        this.precificacao = precificacao;
    }

    public boolean pertenceA(Vendedor outroVendedor) {
        if (this.vendedor == null || outroVendedor == null) {
            return false;
        }
        return this.vendedor.getId() == outroVendedor.getId();
    }

    public SituacaoProduto alternarSituacao() {
        SituacaoProduto situacaoAtual = this.situacao != null ? this.situacao : SituacaoProduto.DISPONIVEL;
        this.situacao = situacaoAtual.alternar();
        return this.situacao;
    }

    public boolean possuiPrecificacao() {
        return this.precificacao != null;
    }

    public int obterIdVendedor() {
        if (this.vendedor == null) {
            return 0;
        }
        return this.vendedor.getId();
    }

    public String obterNomeSituacao() {
        SituacaoProduto situacaoAtual = this.situacao != null ? this.situacao : SituacaoProduto.DISPONIVEL;
        return situacaoAtual.name();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigoProduto() {
        return codigoProduto;
    }

    public void setCodigoProduto(String codigoProduto) {
        this.codigoProduto = codigoProduto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public SituacaoProduto getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoProduto situacao) {
        this.situacao = situacao != null ? situacao : SituacaoProduto.DISPONIVEL;
    }

    public Vendedor getVendedor() {
        return vendedor;
    }

    public void setVendedor(Vendedor vendedor) {
        this.vendedor = vendedor;
    }

    public Precificacao getPrecificacao() {
        return precificacao;
    }

    public void setPrecificacao(Precificacao precificacao) {
        this.precificacao = precificacao;
    }
}
