package br.com.unipe;

import java.util.List;

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
