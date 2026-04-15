package com.example.simulado.model;

public class Filme {
    private int id;
    private String titulo;
    private String diretor;
    private int ano;
    private float nota;       // 1 a 5
    private String genero;
    private boolean viuNoCinema;

    // Construtor completo (com ID — para buscar do banco)
    public Filme(int id, String titulo, String diretor,
                 int ano, float nota, String genero, boolean viuNoCinema) {
        this.id = id;
        this.titulo = titulo;
        this.diretor = diretor;
        this.ano = ano;
        this.nota = nota;
        this.genero = genero;
        this.viuNoCinema = viuNoCinema;
    }

    // Construtor sem ID (para inserir novo)
    public Filme(String titulo, String diretor,
                 int ano, float nota, String genero, boolean viuNoCinema) {
        this(-1, titulo, diretor, ano, nota, genero, viuNoCinema);
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDiretor() { return diretor; }
    public void setDiretor(String diretor) { this.diretor = diretor; }

    public int getAno() { return ano; }
    public void setAno(int ano) { this.ano = ano; }

    public float getNota() { return nota; }
    public void setNota(float nota) { this.nota = nota; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public boolean isViuNoCinema() { return viuNoCinema; }
    public void setViuNoCinema(boolean viuNoCinema) { this.viuNoCinema = viuNoCinema; }
}