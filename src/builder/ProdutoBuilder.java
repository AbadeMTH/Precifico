package builder;

import model.Precificacao;
import model.Produto;
import model.Vendedor;

/**
 * Padrão Builder com Interface Fluente para construção de Produto
 * Conforme estudado em Aula06 - Builder (Builder_InterfaceFluente)
 */
public class ProdutoBuilder {
    private int id;
    private String codigoProduto;
    private String nome;
    private String descricao;
    private String imagem;
    private String situacao = "DISPONIVEL";
    private Vendedor vendedor;
    private Precificacao precificacao;

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

    public ProdutoBuilder comSituacao(String situacao) {
        this.situacao = situacao;
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

    public ProdutoBuilder comPrecificacao(double valorVenda, double custoProduto) {
        this.precificacao = new Precificacao(valorVenda, custoProduto);
        return this;
    }

    public Produto constroi() {
        return new Produto(id, codigoProduto, nome, descricao, imagem, situacao, vendedor, precificacao);
    }
}
