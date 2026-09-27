package model;

public class Produto {
    private int id;
    private String codigoProduto;
    private String nome;
    private String descricao;
    private String imagem;
    private String situacao;
    private Vendedor vendedor;
    private Precificacao precificacao;

    public Produto() {
        this.situacao = "DISPONIVEL";
    }

    public Produto(int id, String codigoProduto, String nome, String descricao, String imagem, String situacao, Vendedor vendedor, Precificacao precificacao) {
        this.id = id;
        this.codigoProduto = codigoProduto;
        this.nome = nome;
        this.descricao = descricao;
        this.imagem = imagem;
        this.situacao = situacao != null ? situacao : "DISPONIVEL";
        this.vendedor = vendedor;
        this.precificacao = precificacao;
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

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
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
