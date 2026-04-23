package com.example.firebase;

public class Produto {
    private String id;
    private String nome;
    private int estoque;

    public Produto() {} // Requisito para Firestore

    public Produto(String id, String nome, int estoque) {
        this.id = id;
        this.nome = nome;
        this.estoque = estoque;
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public int getEstoque() { return estoque; }

    public void setId(String id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setEstoque(int estoque) { this.estoque = estoque; }
}
