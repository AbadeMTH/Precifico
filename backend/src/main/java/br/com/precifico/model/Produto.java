package br.com.precifico.model;

import br.com.precifico.decorator.ProdutoDecoravel;

import java.util.ArrayList;
import java.util.List;

public class Produto implements ProdutoDecoravel {
    private int id;
    private String codigoProduto;
    private String nome;
    private String descricao;
    private String imagem;
    private SituacaoProduto situacao;
    private Vendedor vendedor;
    private Precificacao precificacao;
    private List<TipoBrinde> brindes;
    private String descricaoComBrindes;

    public Produto() {
        this.situacao = SituacaoProduto.DISPONIVEL;
        this.brindes = new ArrayList<>();
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
        this(id, codigoProduto, nome, descricao, imagem, situacao, vendedor, precificacao, new ArrayList<>(), nome);
    }

    public Produto(
            int id,
            String codigoProduto,
            String nome,
            String descricao,
            String imagem,
            SituacaoProduto situacao,
            Vendedor vendedor,
            Precificacao precificacao,
            List<TipoBrinde> brindes,
            String descricaoComBrindes
    ) {
        this.id = id;
        this.codigoProduto = codigoProduto;
        this.nome = nome;
        this.descricao = descricao;
        this.imagem = imagem;
        this.situacao = situacao != null ? situacao : SituacaoProduto.DISPONIVEL;
        this.vendedor = vendedor;
        this.precificacao = precificacao;
        this.brindes = brindes != null ? new ArrayList<>(brindes) : new ArrayList<>();
        this.descricaoComBrindes = descricaoComBrindes != null ? descricaoComBrindes : nome;
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

    @Override
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

    @Override
    public List<TipoBrinde> getBrindes() {
        return brindes != null ? brindes : new ArrayList<>();
    }

    public void setBrindes(List<TipoBrinde> brindes) {
        this.brindes = brindes != null ? new ArrayList<>(brindes) : new ArrayList<>();
    }

    public String getDescricaoComBrindes() {
        return descricaoComBrindes;
    }

    public void setDescricaoComBrindes(String descricaoComBrindes) {
        this.descricaoComBrindes = descricaoComBrindes;
    }
}
