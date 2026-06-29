package br.com.unipe;

import java.util.List;

/**
 * Encapsula o resultado da Missão 4 (Rota de Maior Afinidade):
 *  - caminho : sequência ordenada de nomes da rota ótima
 *  - custo   : soma dos pesos das arestas percorridas (-1 se inalcançável)
 */
public class ResultadoRota {
    private final List<String> caminho;
    private final int custo;

    public ResultadoRota(List<String> caminho, int custo) {
        this.caminho = caminho;
        this.custo = custo;
    }

    public List<String> getCaminho() {
        return caminho;
    }

    public int getCusto() {
        return custo;
    }

    @Override
    public String toString() {
        if (custo == -1) {
            return "Sem rota disponível entre os perfis.";
        }
        return "Rota: " + String.join(" -> ", caminho) + " | Custo total: " + custo;
    }
}
