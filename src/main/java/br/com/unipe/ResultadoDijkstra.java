package br.com.unipe;

import java.util.Map;

/**
 * Encapsula o resultado do algoritmo de Dijkstra:
 *  - distancias   : custo mínimo acumulado da origem até cada vértice
 *  - predecessores: mapa para reconstrução do caminho
 */
public class ResultadoDijkstra {
    private final Map<Vertice, Integer> distancias;
    private final Map<Vertice, Vertice> predecessores;

    public ResultadoDijkstra(Map<Vertice, Integer> distancias, Map<Vertice, Vertice> predecessores) {
        this.distancias = distancias;
        this.predecessores = predecessores;
    }

    public Map<Vertice, Integer> getDistancias() {
        return distancias;
    }

    public Map<Vertice, Vertice> getPredecessores() {
        return predecessores;
    }
}
