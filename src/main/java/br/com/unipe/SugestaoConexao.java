package br.com.unipe;

/**
 * Representa uma sugestão de conexão de 2º grau.
 * Contém o nome da pessoa sugerida e quantos amigos em comum ela tem
 * com o usuário que fez a consulta.
 */
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
