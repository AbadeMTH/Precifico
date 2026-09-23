package model;

public class Vendedor {
    private int id;
    private String nome;
    private String email;
    private String senha;

    public Vendedor(String nome, String email, String senha){
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    public String getNome(){
        return this.nome;
    }

    public String getEmail(){
        return this.email;
    }

    public String getSenha(){
        return this.senha;
    }
}
