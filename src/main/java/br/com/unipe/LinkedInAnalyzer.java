package br.com.unipe;

import java.util.*;


public class LinkedInAnalyzer {

    private final Grafo grafo;

    /**
     * @param grafo instância do grafo que representa a rede social
     */
    public LinkedInAnalyzer(Grafo grafo) {
        this.grafo = grafo;
    }
    /**
     * Sugere conexões de 2º grau para o usuário informado.
     *
     * Regras:
     *  - Não sugere contatos diretos (1º grau) nem o próprio usuário.
     *  - Ordena por número de amigos em comum (decrescente).
     *
     * @param nomeUsuario nome do usuário que quer receber sugestões
     * @return lista de {@link SugestaoConexao} ordenada por amigos em comum
     */
    public List<SugestaoConexao> sugerirConexoes(String nomeUsuario) {
        Vertice usuario = grafo.encontraVertice(nomeUsuario).orElseThrow(
                () -> new IllegalArgumentException("Usuário '" + nomeUsuario + "' não encontrado."));

        // Conjunto de contatos diretos (1º grau) do usuário
        Set<Vertice> contatosDiretos = new HashSet<>(usuario.getAdjacencias());

        // Mapa: candidato -> quantidade de amigos em comum
        Map<Vertice, Integer> amigosEmComum = new HashMap<>();

        // Para cada amigo direto, veja os amigos dele
        for (Vertice amigo : contatosDiretos) {
            for (Vertice candidato : amigo.getAdjacencias()) {

                // Filtra: não pode ser o próprio usuário nem contato direto
                if (candidato.equals(usuario) || contatosDiretos.contains(candidato)) {
                    continue;
                }

                amigosEmComum.merge(candidato, 1, Integer::sum);
            }
        }

        // Converte para lista de SugestaoConexao e ordena por amigos em comum (desc)
        List<SugestaoConexao> sugestoes = new ArrayList<>();
        for (Map.Entry<Vertice, Integer> entry : amigosEmComum.entrySet()) {
            sugestoes.add(new SugestaoConexao(entry.getKey().getNome(), entry.getValue()));
        }
        sugestoes.sort(Comparator.comparingInt(SugestaoConexao::getAmigosEmComum).reversed());

        return sugestoes;
    }
    /**
     * Calcula o grau de separação (número de passos) entre dois perfis.
     * Usa BFS para encontrar o caminho com o menor número de arestas.
     *
     * @param nomeOrigem  perfil de partida
     * @param nomeDestino perfil de chegada
     * @return número inteiro de passos, ou -1 se não houver conexão
     */
    public int grauDeSeparacao(String nomeOrigem, String nomeDestino) {
        grafo.encontraVertice(nomeOrigem).orElseThrow(
                () -> new IllegalArgumentException("Vértice '" + nomeOrigem + "' não encontrado."));
        grafo.encontraVertice(nomeDestino).orElseThrow(
                () -> new IllegalArgumentException("Vértice '" + nomeDestino + "' não encontrado."));

        // BFS: encontra o caminho com menor número de arestas
        List<String> caminho = grafo.bfs(nomeOrigem, nomeDestino);

        if (caminho.isEmpty()) {
            return -1; // perfis totalmente isolados entre si
        }

        // Número de passos = número de arestas = tamanho do caminho - 1
        return caminho.size() - 1;
    }
    /**
     * Encontra a rota de maior afinidade (menor custo ponderado) entre dois perfis.
     * Utiliza o algoritmo de Dijkstra implementado na classe {@link Grafo}.
     *
     * @param nomeOrigem  perfil de partida
     * @param nomeDestino perfil de chegada
     * @return {@link ResultadoRota} com o caminho e o custo total;
     *         custo = -1 e caminho vazio se os perfis forem inalcançáveis
     */
    public ResultadoRota rotaDeMaiorAfinidade(String nomeOrigem, String nomeDestino) {
        Vertice origem = grafo.encontraVertice(nomeOrigem).orElseThrow(
                () -> new IllegalArgumentException("Vértice '" + nomeOrigem + "' não encontrado."));
        Vertice destino = grafo.encontraVertice(nomeDestino).orElseThrow(
                () -> new IllegalArgumentException("Vértice '" + nomeDestino + "' não encontrado."));

        // Executa Dijkstra a partir da origem
        ResultadoDijkstra resultado = grafo.dijkstra(nomeOrigem);

        Map<Vertice, Integer> distancias = resultado.getDistancias();
        Map<Vertice, Vertice> predecessores = resultado.getPredecessores();

        // Verifica se o destino é alcançável
        if (distancias.get(destino) == Integer.MAX_VALUE) {
            return new ResultadoRota(Collections.emptyList(), -1);
        }

        // Reconstrói o caminho percorrendo os predecessores de trás para frente
        List<String> caminho = new ArrayList<>();
        Vertice passo = destino;
        while (passo != null) {
            caminho.add(0, passo.getNome());
            passo = predecessores.get(passo);
        }

        int custoTotal = distancias.get(destino);
        return new ResultadoRota(caminho, custoTotal);
    }
    /**
     * Identifica todos os grupos de pessoas conectadas entre si,
     * mas isoladas do restante da rede (componentes conexos).
     *
     * Percorre o grafo inteiro usando BFS a partir de vértices ainda não visitados.
     *
     * @return lista de grupos; cada grupo é uma lista de nomes dos usuários
     */
    public List<List<String>> mapearGruposIsolados() {
        Set<Vertice> visitados = new HashSet<>();
        List<List<String>> grupos = new ArrayList<>();

        for (Vertice vertice : grafo.getVertices()) {
            if (visitados.contains(vertice)) continue;

            // BFS para descobrir todo o componente conexo deste vértice
            List<String> grupo = new ArrayList<>();
            Queue<Vertice> fila = new LinkedList<>();

            fila.add(vertice);
            visitados.add(vertice);

            while (!fila.isEmpty()) {
                Vertice atual = fila.poll();
                grupo.add(atual.getNome());

                // Em grafo não-dirigido, adjacências guardam vizinhos nos dois sentidos
                for (Vertice vizinho : atual.getAdjacencias()) {
                    if (!visitados.contains(vizinho)) {
                        visitados.add(vizinho);
                        fila.add(vizinho);
                    }
                }
            }

            grupos.add(grupo);
        }

        return grupos;
    }
}
