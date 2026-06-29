package br.com.unipe;

public class Aresta {
    private String nome;
    private Vertice verticeOrigem;
    private Vertice verticeDestino;
    private Integer peso;

    public Aresta(String nome, Vertice verticeOrigem, Vertice verticeDestino, Integer peso) {
        this.nome = nome;
        this.verticeOrigem = verticeOrigem;
        this.verticeDestino = verticeDestino;
        this.peso = peso;
    }

    public Aresta(String nome, Vertice verticeOrigem, Vertice verticeDestino) {
        this(nome, verticeOrigem, verticeDestino, null);
    }

    public Aresta(Vertice verticeOrigem, Vertice verticeDestino, Integer peso) {
        this(null, verticeOrigem, verticeDestino, peso);
    }

    public Aresta(Vertice verticeOrigem, Vertice verticeDestino) {
        this(null, verticeOrigem, verticeDestino, null);
    }

    public String getNome() {
        return nome != null ? nome : "";
    }

    public Vertice getVerticeOrigem() {
        return verticeOrigem;
    }

    public Vertice getVerticeDestino() {
        return verticeDestino;
    }

    public Integer getPeso() {
        return peso;
    }

    public void setNome(String nome) { this.nome = nome; }
    public void setVerticeOrigem(Vertice v) { this.verticeOrigem = v; }
    public void setVerticeDestino(Vertice v) { this.verticeDestino = v; }
    public void setPeso(Integer peso) { this.peso = peso; }

    @Override
    public String toString() {
        return "\n" + getNome() + "{" + verticeOrigem.getNome() + "," + verticeDestino.getNome() + "}";
    }
}
