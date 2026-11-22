package br.com.precifico.builder;

import br.com.precifico.model.Precificacao;
import br.com.precifico.model.Produto;
import br.com.precifico.model.SituacaoProduto;
import br.com.precifico.model.TipoBrinde;
import br.com.precifico.model.Vendedor;

import java.util.ArrayList;
import java.util.List;

public class ProdutoBuilder {
    private int id;
    private String codigoProduto;
    private String nome;
    private String descricao;
    private String imagem;
    private SituacaoProduto situacao = SituacaoProduto.DISPONIVEL;
    private Vendedor vendedor;
    private Precificacao precificacao;
    private List<TipoBrinde> brindes = new ArrayList<>();
    private String descricaoComBrindes;

    public ProdutoBuilder comId(int id) {
        this.id = id;
        return this;
    }

    public ProdutoBuilder comCodigoProduto(String codigoProduto) {
        this.codigoProduto = codigoProduto;
        return this;
    }

    public ProdutoBuilder comNome(String nome) {
        this.nome = nome;
        return this;
    }

    public ProdutoBuilder comDescricao(String descricao) {
        this.descricao = descricao;
        return this;
    }

    public ProdutoBuilder comImagem(String imagem) {
        this.imagem = imagem;
        return this;
    }

    public ProdutoBuilder comSituacao(SituacaoProduto situacao) {
        this.situacao = situacao != null ? situacao : SituacaoProduto.DISPONIVEL;
        return this;
    }

    public ProdutoBuilder comVendedor(Vendedor vendedor) {
        this.vendedor = vendedor;
        return this;
    }

    public ProdutoBuilder comPrecificacao(Precificacao precificacao) {
        this.precificacao = precificacao;
        return this;
    }

    public ProdutoBuilder comBrindes(List<TipoBrinde> brindes) {
        this.brindes = brindes != null ? new ArrayList<>(brindes) : new ArrayList<>();
        return this;
    }

    public ProdutoBuilder comDescricaoComBrindes(String descricaoComBrindes) {
        this.descricaoComBrindes = descricaoComBrindes;
        return this;
    }

    public Produto constroi() {
        return new Produto(
                id,
                codigoProduto,
                nome,
                descricao,
                imagem,
                situacao,
                vendedor,
                precificacao,
                brindes,
                descricaoComBrindes
        );
    }
}
