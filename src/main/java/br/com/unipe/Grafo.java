package br.com.unipe;

import java.util.*;

public class Grafo {
    private final List<Aresta> arestas;
    private final List<Vertice> vertices;
    private boolean eDirigido;
    private int ordem;
    private int tamanho;
    private final boolean ePonderado;

    public Grafo() {
        this(false, false);
    }

    public Grafo(boolean eDirigido, boolean ePonderado) {
        this.eDirigido = eDirigido;
        this.ePonderado = ePonderado;
        arestas = new ArrayList<>();
        vertices = new ArrayList<>();
    }

    // -------------------------------------------------------------------------
    // Getters auxiliares (usados pelo LinkedInAnalyzer)
    // -------------------------------------------------------------------------

    public List<Vertice> getVertices() {
        return vertices;
    }

    public List<Aresta> getArestas() {
        return arestas;
    }

    // -------------------------------------------------------------------------
    // Construção do grafo
    // -------------------------------------------------------------------------

    public void adicionaVertices(String... nomes) {
        for (String nome : nomes) {
            vertices.add(new Vertice(nome));
            ordem++;
        }
    }

    public void addAresta(String v1, String v2) {
        arestas.add(criaAresta("", v1, v2, null));
    }

    public void addAresta(String v1, String v2, int peso) {
        arestas.add(criaAresta("", v1, v2, peso));
    }

    public void addAresta(String nome, String v1, String v2) {
        arestas.add(criaAresta(nome, v1, v2, null));
    }

    public void addAresta(String nome, String v1, String v2, int peso) {
        arestas.add(criaAresta(nome, v1, v2, peso));
    }

    private Aresta criaAresta(String nomeAresta, String nomeVertice1, String nomeVertice2, Integer peso) {
        Vertice v1 = encontraVertice(nomeVertice1).orElseThrow(
                () -> new IllegalArgumentException("Vertice " + nomeVertice1 + " não encontrado."));
        Vertice v2 = encontraVertice(nomeVertice2).orElseThrow(
                () -> new IllegalArgumentException("Vertice " + nomeVertice2 + " não encontrado."));
        if (!eDirigido) {
            infereSeGrafoEDirecionado(v1, v2);
        }
        aumentaGrauDosVertices(v1, v2);
        resolveAdjacencias(v1, v2);
        tamanho++;
        return new Aresta(nomeAresta, v1, v2, peso);
    }

    private void resolveAdjacencias(Vertice v1, Vertice v2) {
        v1.adicionaAdjacencia(v2);
        v2.adicionaAdjacente(v1);
        if (!eDirigido) {
            v1.adicionaAdjacente(v2);
            v2.adicionaAdjacencia(v1);
        }
    }

    private void aumentaGrauDosVertices(Vertice v1, Vertice v2) {
        if (eDirigido) {
            v1.aumentaOutDegree();
            v2.aumentaInDegree();
        } else {
            v1.aumentaGrau();
            v2.aumentaGrau();
        }
    }

    private void infereSeGrafoEDirecionado(Vertice v1, Vertice v2) {
        if (eSelfLoop(v1, v2)) {
            reprocessamentoParaDigrafo();
        } else {
            for (Aresta aresta : arestas) {
                if (eViaMaoDupla(v1, v2, aresta) || eArestaDuplicada(v2, v1, aresta)) {
                    reprocessamentoParaDigrafo();
                    break;
                }
            }
        }
    }

    private static boolean eArestaDuplicada(Vertice v1, Vertice v2, Aresta aresta) {
        return aresta.getVerticeOrigem().equals(v1) && aresta.getVerticeDestino().equals(v2);
    }

    private static boolean eViaMaoDupla(Vertice v1, Vertice v2, Aresta aresta) {
        return aresta.getVerticeOrigem().equals(v2) && aresta.getVerticeDestino().equals(v1);
    }

    private static boolean eSelfLoop(Vertice v1, Vertice v2) {
        return v1.getNome().equals(v2.getNome());
    }

    public Optional<Vertice> encontraVertice(String nome) {
        for (Vertice vertice : vertices) {
            if (vertice.getNome().equalsIgnoreCase(nome)) {
                return Optional.of(vertice);
            }
        }
        return Optional.empty();
    }

    private void reprocessamentoParaDigrafo() {
        eDirigido = true;
        System.out.println("Reprocessamento para digrafo necessário. O grafo agora é direcionado.");
        limpezaGrausEAdjacencias();
        recalculaGrausEAdjacencias();
    }

    private void recalculaGrausEAdjacencias() {
        arestas.forEach(aresta -> {
            Vertice origem = aresta.getVerticeOrigem();
            Vertice destino = aresta.getVerticeDestino();
            aumentaGrauDosVertices(origem, destino);
            resolveAdjacencias(origem, destino);
        });
    }

    private void limpezaGrausEAdjacencias() {
        vertices.forEach(vertice -> {
            vertice.resetaGraus();
            vertice.resetaAdjacenciasEAdjacentes();
        });
    }

    // -------------------------------------------------------------------------
    // Dijkstra — menor caminho ponderado
    //
    // Retorna um ResultadoDijkstra com:
    //   • distancias : custo mínimo de 'origem' até cada vértice
    //   • predecessores: mapa para reconstruir o caminho
    // -------------------------------------------------------------------------

    public ResultadoDijkstra dijkstra(String nomeOrigem) {
        Vertice origem = encontraVertice(nomeOrigem).orElseThrow(
                () -> new IllegalArgumentException("Vértice " + nomeOrigem + " não encontrado."));

        // Mapas de distância e predecessor
        Map<Vertice, Integer> dist = new HashMap<>();
        Map<Vertice, Vertice> pred = new HashMap<>();

        for (Vertice v : vertices) {
            dist.put(v, Integer.MAX_VALUE);
            pred.put(v, null);
        }
        dist.put(origem, 0);

        // Fila de prioridade: ordena pelo custo acumulado (menor primeiro)
        PriorityQueue<Vertice> fila = new PriorityQueue<>(
                Comparator.comparingInt(v -> dist.getOrDefault(v, Integer.MAX_VALUE)));
        fila.add(origem);

        Set<Vertice> visitados = new HashSet<>();

        while (!fila.isEmpty()) {
            Vertice atual = fila.poll();

            if (visitados.contains(atual)) continue;
            visitados.add(atual);

            // Percorre todas as arestas que partem de 'atual'
            // (para grafo não-dirigido, as arestas são registradas nos dois sentidos)
            for (Aresta aresta : arestas) {
                Vertice vizinho = null;
                if (aresta.getVerticeOrigem().equals(atual)) {
                    vizinho = aresta.getVerticeDestino();
                } else if (!eDirigido && aresta.getVerticeDestino().equals(atual)) {
                    vizinho = aresta.getVerticeOrigem();
                }

                if (vizinho == null || visitados.contains(vizinho)) continue;

                int peso = aresta.getPeso() != null ? aresta.getPeso() : 1;
                int novaDist = dist.get(atual) + peso;

                if (novaDist < dist.get(vizinho)) {
                    dist.put(vizinho, novaDist);
                    pred.put(vizinho, atual);
                    fila.add(vizinho); // re-insere com distância atualizada
                }
            }
        }

        return new ResultadoDijkstra(dist, pred);
    }

    // -------------------------------------------------------------------------
    // BFS — menor caminho em número de arestas (sem peso)
    // -------------------------------------------------------------------------

    public List<String> bfs(String nomeOrigem, String nomeDestino) {
        Vertice origem = encontraVertice(nomeOrigem).orElseThrow();
        Vertice destino = encontraVertice(nomeDestino).orElseThrow();

        Map<Vertice, Vertice> pred = new HashMap<>();
        Queue<Vertice> fila = new LinkedList<>();
        Set<Vertice> visitados = new HashSet<>();

        fila.add(origem);
        visitados.add(origem);
        pred.put(origem, null);

        while (!fila.isEmpty()) {
            Vertice atual = fila.poll();
            if (atual.equals(destino)) break;

            for (Vertice viz : atual.getAdjacencias()) {
                if (!visitados.contains(viz)) {
                    visitados.add(viz);
                    pred.put(viz, atual);
                    fila.add(viz);
                }
            }
        }

        if (!pred.containsKey(destino)) return Collections.emptyList();

        // Reconstrói o caminho
        List<String> caminho = new ArrayList<>();
        Vertice passo = destino;
        while (passo != null) {
            caminho.add(0, passo.getNome());
            passo = pred.get(passo);
        }
        return caminho;
    }

    // -------------------------------------------------------------------------
    // Métodos já existentes (mantidos integralmente)
    // -------------------------------------------------------------------------

    public String exibeGrausDosVertices() {
        StringBuilder graus = new StringBuilder();
        for (Vertice vertice : vertices) {
            graus.append(vertice.exibeGraus());
        }
        return graus.toString();
    }

    public String exibeAdjacencias() {
        StringBuilder adjacencias = new StringBuilder();
        for (Vertice vertice : vertices) {
            adjacencias.append("\n").append(vertice.getNome()).append(": ").append(vertice.getAdjacencias());
        }
        return adjacencias.toString();
    }

    public String exibeAdjacentes() {
        StringBuilder adjacencias = new StringBuilder();
        for (Vertice vertice : vertices) {
            adjacencias.append("\n").append(vertice.getNome()).append(": ").append(vertice.getAdjacentes());
        }
        return adjacencias.toString();
    }

    public void exibeMatrizAdjacencia() {
        List<Vertice> verticesOrdenados = vertices.stream().sorted(Comparator.comparing(Vertice::getNome)).toList();
        StringBuilder matriz = new StringBuilder("\nMatriz de Adjacência\n\t");
        verticesOrdenados.forEach(v -> matriz.append(v.getNome()).append("\t"));
        matriz.append("\n");
        for (Vertice vertice : verticesOrdenados) {
            matriz.append(vertice.getNome()).append("\t");
            List<Vertice> adjacencias = vertice.getAdjacencias();
            for (Vertice outroVertice : verticesOrdenados) {
                matriz.append(adjacencias.contains(outroVertice) ? "1" : "0").append("\t");
            }
            matriz.append("\n");
        }
        System.out.println(matriz);
    }

    public void exibeMatrizIncidencia() {
        List<Vertice> verticesOrdenados = vertices.stream().sorted(Comparator.comparing(Vertice::getNome)).toList();
        StringBuilder matriz = new StringBuilder("\nMatriz de Incidência\n\t");
        arestas.forEach(a -> matriz.append(a.getNome()).append("\t"));
        matriz.append("\n");
        for (Vertice vertice : verticesOrdenados) {
            matriz.append(vertice.getNome()).append("\t");
            for (Aresta aresta : arestas) {
                Vertice origem = aresta.getVerticeOrigem();
                Vertice destino = aresta.getVerticeDestino();
                String valor;
                if (origem.equals(vertice) && destino.equals(vertice)) {
                    valor = " 2";
                } else if (origem.equals(vertice)) {
                    valor = eDirigido ? "-1" : "1";
                } else if (destino.equals(vertice)) {
                    valor = " 1";
                } else {
                    valor = " 0";
                }
                matriz.append(valor).append("\t");
            }
            matriz.append("\n");
        }
        System.out.println(matriz);
    }

    public List<String> dfsIterativo(String origem, String destino) {
        Vertice verticeOrigem = encontraVertice(origem).orElseThrow(
                () -> new IllegalArgumentException("Vertice " + origem + " não encontrado."));
        Vertice verticeDestino = destino == null ? null
                : encontraVertice(destino).orElseThrow(
                        () -> new IllegalArgumentException("Vertice " + destino + " não encontrado."));

        Stack<Vertice> pilha = new Stack<>();
        List<Vertice> visitados = new ArrayList<>();
        StringBuilder percurso = new StringBuilder("Percurso = ");

        visitados.add(verticeOrigem);
        pilha.push(verticeOrigem);
        percurso.append(verticeOrigem.getNome()).append(", ");

        while (!pilha.isEmpty()) {
            Vertice atual = pilha.peek();
            if (atual.equals(verticeDestino)) break;

            List<Vertice> adjacencias = atual.getAdjacencias();
            List<Vertice> adjacenciasOrdenadas = adjacencias.stream()
                    .sorted(Comparator.comparing(Vertice::getNome)).toList();
            Optional<Vertice> proximo = adjacenciasOrdenadas.stream()
                    .filter(a -> !visitados.contains(a)).findFirst();

            if (proximo.isPresent()) {
                Vertice adjacencia = proximo.get();
                visitados.add(adjacencia);
                percurso.append(adjacencia.getNome()).append(", ");
                pilha.push(adjacencia);
            } else {
                pilha.pop();
            }
        }

        System.out.println(percurso);
        return visitados.stream().map(Vertice::getNome).toList();
    }

    public List<String> dfsRecursivo(String origem, String destino, List<Vertice> visitados) {
        final List<Vertice> visitadosAtual = visitados != null ? visitados : new ArrayList<>();
        Vertice v = encontraVertice(origem).orElseThrow(
                () -> new IllegalArgumentException("Vertice " + origem + " não encontrado."));
        visitadosAtual.add(v);

        if (origem.equals(destino)) {
            return visitadosAtual.stream().map(Vertice::getNome).toList();
        }

        for (Vertice adj : v.getAdjacencias()) {
            if (visitadosAtual.contains(adj)) continue;
            dfsRecursivo(adj.getNome(), destino, visitadosAtual);
            if (destino != null && visitadosAtual.stream().anyMatch(x -> x.getNome().equals(destino))) {
                return visitadosAtual.stream().map(Vertice::getNome).toList();
            }
        }
        return visitadosAtual.stream().map(Vertice::getNome).toList();
    }

    public int encontraComprimentoCaminho(String... caminho) {
        if (!ePonderado) return caminho.length - 1;
        int comprimento = 0;
        List<Aresta> arestasPercorridas = new ArrayList<>();
        for (int i = 0; i < caminho.length - 1; i++) {
            int indiceAtual = i;
            Vertice origem = encontraVertice(caminho[indiceAtual]).orElseThrow(
                    () -> new IllegalArgumentException("Vertice " + caminho[indiceAtual] + " não encontrado."));
            Vertice destino = encontraVertice(caminho[indiceAtual + 1]).orElseThrow(
                    () -> new IllegalArgumentException("Vertice " + caminho[indiceAtual + 1] + " não encontrado."));
            Optional<Aresta> aresta = arestas.stream()
                    .filter(a -> a.getVerticeOrigem().equals(origem) && a.getVerticeDestino().equals(destino))
                    .findFirst();
            if (aresta.isPresent()) {
                if (arestasPercorridas.contains(aresta.get()))
                    throw new IllegalArgumentException("Aresta repetida!");
                arestasPercorridas.add(aresta.get());
                comprimento += aresta.get().getPeso();
            }
        }
        return comprimento;
    }

    public boolean eConexo() {
        for (Vertice v : vertices)
            if (v.getInDegree() == 0 || v.getOutDegree() == 0) return false;
        for (Vertice v : vertices) {
            List<String> caminho = dfsIterativo(v.getNome(), null);
            if (caminho.size() < vertices.size()) return false;
        }
        return true;
    }

    public List<String> greedySearch(String nomeVerticeOrigem, String nomeVerticeDestino) {
        List<Vertice> verticesVisitados = new ArrayList<>();
        int comprimentoCaminho = 0;

        Vertice verticeOrigem = encontraVertice(nomeVerticeOrigem).orElseThrow();
        Vertice verticeDestino = encontraVertice(nomeVerticeDestino).orElseThrow();

        verticesVisitados.add(verticeOrigem);
        Vertice atual = verticeOrigem;

        while (!atual.equals(verticeDestino)) {
            Vertice verticeAlvo = atual;
            List<Vertice> adjacencias = verticeAlvo.getAdjacencias();
            if (adjacencias == null || adjacencias.isEmpty()) {
                System.out.println("Caminho não encontrado. Busca falhou em: " + atual.getNome());
                return null;
            }
            List<Aresta> arestasVizinhas = new ArrayList<>();
            for (Vertice vizinho : adjacencias) {
                if (!verticesVisitados.contains(vizinho)) {
                    arestasVizinhas.addAll(obtemArestasParaVizinho(verticeAlvo, vizinho));
                }
            }
            if (arestasVizinhas.isEmpty()) {
                System.out.println("Caminho não encontrado. Busca falhou em: " + atual.getNome());
                return null;
            }
            Aresta melhorAresta = arestasVizinhas.stream()
                    .min(Comparator.comparing(Aresta::getPeso)).orElseThrow();
            comprimentoCaminho += melhorAresta.getPeso() != null ? melhorAresta.getPeso() : 0;
            atual = obtemVerticeOposto(melhorAresta, verticeAlvo);
            verticesVisitados.add(atual);
            System.out.println("Percorrendo aresta " + melhorAresta.getNome() +
                    " (peso " + melhorAresta.getPeso() + ") para o vértice " + atual.getNome());
        }

        List<String> nomesVisitados = verticesVisitados.stream().map(Vertice::getNome).toList();
        System.out.println("Destino " + verticeDestino.getNome() + " encontrado!");
        System.out.println("Caminho: " + String.join(" -> ", nomesVisitados));
        System.out.println("Comprimento do caminho: " + comprimentoCaminho);
        return nomesVisitados;
    }

    private List<Aresta> obtemArestasParaVizinho(Vertice atual, Vertice vizinho) {
        return arestas.stream()
                .filter(a -> (a.getVerticeOrigem().equals(atual) && a.getVerticeDestino().equals(vizinho)) ||
                        (!eDirigido && a.getVerticeDestino().equals(atual) && a.getVerticeOrigem().equals(vizinho)))
                .toList();
    }

    private Vertice obtemVerticeOposto(Aresta aresta, Vertice vertice) {
        return aresta.getVerticeOrigem().equals(vertice) ? aresta.getVerticeDestino() : aresta.getVerticeOrigem();
    }

    @Override
    public String toString() {
        return """
                direcionado = %s,
                ordem = %d,
                tamanho = %d,
                vertices = %s,
                arestas = %s,
                graus = %s,
                adjacencias = %s,
                adjacentes = %s
                }""".formatted(eDirigido ? "sim" : "não", ordem, tamanho, vertices, arestas,
                exibeGrausDosVertices(), exibeAdjacencias(), exibeAdjacentes());
    }
}
