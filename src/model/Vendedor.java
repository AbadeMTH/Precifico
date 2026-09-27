package model;

public class Vendedor {
    private int id;
    private String nome;
    private String email;
    private String senha;
    private int idade;
    private String endereco;
    private String cpf;

    public Vendedor() {
    }

    public Vendedor(int id, String nome, String email, String senha, int idade, String endereco, String cpf) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.idade = idade;
        this.endereco = endereco;
        this.cpf = cpf;
    }

    public Vendedor(String nome, String email, String senha, int idade, String endereco, String cpf) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.idade = idade;
        this.endereco = endereco;
        this.cpf = cpf;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return this.senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public int getIdade() {
        return this.idade;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }

    public String getEndereco() {
        return this.endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getCpf() {
        return this.cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
}
