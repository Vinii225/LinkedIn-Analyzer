package br.com.unipe;

public class SugestaoConexao {
    private final String nome;
    private final int amigosEmComum;

    public SugestaoConexao(String nome, int amigosEmComum) {
        this.nome = nome;
        this.amigosEmComum = amigosEmComum;
    }

    public String getNome() {
        return nome;
    }

    public int getAmigosEmComum() {
        return amigosEmComum;
    }

    @Override
    public String toString() {
        return nome + " (" + amigosEmComum + " amigo(s) em comum)";
    }
}
