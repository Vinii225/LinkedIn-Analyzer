package br.com.unipe;

import java.util.List;

/**
 * LinkedInApp — ponto de entrada da aplicação.
 *
 * Configura o cenário de testes sugerido no enunciado e exercita
 * todas as cinco missões do LinkedInAnalyzer.
 */
public class LinkedInApp {

    public static void main(String[] args) {

        // =====================================================================
        // 1. Monta a rede social como um grafo não-dirigido e ponderado
        // =====================================================================

        Grafo grafo = new Grafo(false, true);

        // Rede principal
        grafo.adicionaVertices("Ana", "Bruno", "Carlos", "Daniela", "Eduardo", "Fernanda");

        // Grupos isolados
        grafo.adicionaVertices("Gabriel", "Hugo");   // Grupo isolado 1
        grafo.adicionaVertices("Igor", "Juliana");   // Grupo isolado 2

        // Conexões com seus respectivos pesos de afinidade
        grafo.addAresta("Ana",     "Bruno",    1);  // trabalham muito próximos
        grafo.addAresta("Ana",     "Carlos",   2);
        grafo.addAresta("Ana",     "Daniela",  8);
        grafo.addAresta("Bruno",   "Eduardo",  1);
        grafo.addAresta("Carlos",  "Eduardo",  1);
        grafo.addAresta("Daniela", "Fernanda", 5);
        grafo.addAresta("Eduardo", "Fernanda", 1);
        grafo.addAresta("Gabriel", "Hugo",     1);  // grupo isolado 1
        grafo.addAresta("Igor",    "Juliana",  1);  // grupo isolado 2

        // =====================================================================
        // 2. Inicializa o analisador
        // =====================================================================

        LinkedInAnalyzer analyzer = new LinkedInAnalyzer(grafo);

        // =====================================================================
        // Missão 2 — Sugestão de Conexões para "Ana"
        // =====================================================================

        System.out.println("=".repeat(60));
        System.out.println("MISSÃO 2 — Sugestão de Conexões para Ana");
        System.out.println("=".repeat(60));

        List<SugestaoConexao> sugestoes = analyzer.sugerirConexoes("Ana");

        if (sugestoes.isEmpty()) {
            System.out.println("Nenhuma sugestão encontrada.");
        } else {
            sugestoes.forEach(s -> System.out.println("  -> " + s));
        }

        // =====================================================================
        // Missão 3 — Grau de Separação
        // =====================================================================

        System.out.println();
        System.out.println("=".repeat(60));
        System.out.println("MISSÃO 3 — Grau de Separação");
        System.out.println("=".repeat(60));

        imprimeSeparacao(analyzer, "Ana",     "Fernanda");
        imprimeSeparacao(analyzer, "Ana",     "Bruno");
        imprimeSeparacao(analyzer, "Ana",     "Eduardo");
        imprimeSeparacao(analyzer, "Ana",     "Gabriel");   // isolados → -1

        // =====================================================================
        // Missão 4 — Rota de Maior Afinidade
        // =====================================================================

        System.out.println();
        System.out.println("=".repeat(60));
        System.out.println("MISSÃO 4 — Rota de Maior Afinidade");
        System.out.println("=".repeat(60));

        // Caso principal do enunciado: Ana -> Fernanda
        // Rota esperada: Ana -> Bruno -> Eduardo -> Fernanda (custo 3)
        // (e NÃO Ana -> Daniela -> Fernanda, custo 13)
        imprimeRota(analyzer, "Ana",     "Fernanda");
        imprimeRota(analyzer, "Ana",     "Carlos");
        imprimeRota(analyzer, "Bruno",   "Daniela");
        imprimeRota(analyzer, "Ana",     "Gabriel");   // inalcançável → custo -1

        // =====================================================================
        // Missão 5 — Mapear Grupos Isolados (Sub-redes)
        // =====================================================================

        System.out.println();
        System.out.println("=".repeat(60));
        System.out.println("MISSÃO 5 — Grupos Isolados (Sub-redes)");
        System.out.println("=".repeat(60));

        List<List<String>> grupos = analyzer.mapearGruposIsolados();

        for (int i = 0; i < grupos.size(); i++) {
            System.out.println("  Sub-rede " + (i + 1) + ": " + grupos.get(i));
        }

        System.out.println();
        System.out.println("Total de sub-redes identificadas: " + grupos.size());
    }

    // -------------------------------------------------------------------------
    // Métodos auxiliares de exibição
    // -------------------------------------------------------------------------

    private static void imprimeSeparacao(LinkedInAnalyzer analyzer, String origem, String destino) {
        int grau = analyzer.grauDeSeparacao(origem, destino);
        if (grau == -1) {
            System.out.println("  " + origem + " <-> " + destino
                    + ": sem conexão (perfis isolados)");
        } else {
            System.out.println("  " + origem + " <-> " + destino
                    + ": " + grau + " passo(s)");
        }
    }

    private static void imprimeRota(LinkedInAnalyzer analyzer, String origem, String destino) {
        ResultadoRota rota = analyzer.rotaDeMaiorAfinidade(origem, destino);
        System.out.println("  " + origem + " -> " + destino + ": " + rota);
    }
}
