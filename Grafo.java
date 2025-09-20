import java.io.*;
import java.util.*;

public class Grafo {
    private int numVertices;
    private List<Integer>[] adjacentes;

    private Set<Pair> arestasPai = new HashSet<>();
    private boolean[] visitado;
    private int[] descoberta, termino;
    private int tempo;

    // Classe de pares desenvolvida com ajuda de IA
    private static class Pair {
        final int u, v;
        Pair(int u, int v) { this.u = u; this.v = v; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Pair)) return false;
            Pair p = (Pair) o;
            return u == p.u && v == p.v;
        }

        @Override
        public int hashCode() {
            return Objects.hash(u, v);
        }
    }

    public int getNumVertices(){
        return numVertices;
    }

    public void lerGrafo(String nomeArquivo) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(nomeArquivo))) {
            String[] primeiraLinha = br.readLine().trim().split("\\s+");
            numVertices = Integer.parseInt(primeiraLinha[0]);

            adjacentes = new ArrayList[numVertices + 1];
            for (int i = 1; i <= numVertices; i++) {
                adjacentes[i] = new ArrayList<>();
            }

            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String[] partes = linha.trim().split("\\s+");
                int origem = Integer.parseInt(partes[0]);
                int destino = Integer.parseInt(partes[1]);

                adjacentes[origem].add(destino);
            }

            for (int i = 1; i <= numVertices; i++) {
                Collections.sort(adjacentes[i]);
            }
        }
    }

    public int grauSaida(int v) {
        return adjacentes[v].size();
    }

    public int grauEntrada(int v) {
        int grau = 0;
        for (int u = 1; u <= numVertices; u++) {
            for (int vizinho : adjacentes[u]) {
                if (vizinho == v) {
                    grau++;
                }
            }
        }
        return grau;
    }

    public List<Integer> ListaSucessores(int v) {
        return adjacentes[v];
    }

    public List<Integer> ListaPredecessores(int v) {
        List<Integer> predecessores = new ArrayList<>();
        for (int u = 1; u <= numVertices; u++) {
            for (int vizinho : adjacentes[u]) {
                if (vizinho == v) {
                    predecessores.add(u);
                }
            }
        }
        return predecessores;
    }

    public void buscaProfundidade() {
        visitado = new boolean[numVertices + 1];
        descoberta = new int[numVertices + 1];
        termino = new int[numVertices + 1];
        tempo = 0;

        for (int v = 1; v <= numVertices; v++) {
            if (!visitado[v]) {
                buscaProfundidadeIterativa(v);
            }
        }
    }

    // Iterativa pois a recursiva estourava a JVM com o grafo de 1 milhão de arestas
    private void buscaProfundidadeIterativa(int raiz) {
        // Pilha e iteradores para simular funcionamento de chamada recursiva, e fazer a busca em ordem lexicográfica
        Deque<Integer> pilha = new ArrayDeque<>();
        Deque<Iterator<Integer>> iteradores = new ArrayDeque<>();

        pilha.push(raiz);
        descoberta[raiz] = ++tempo;
        visitado[raiz] = true;
        iteradores.push(adjacentes[raiz].iterator());

        while (!pilha.isEmpty()) {
            int u = pilha.peek();
            Iterator<Integer> it = iteradores.peek();

            if (it.hasNext()) {
                int v = it.next();
                if (!visitado[v]) {
                    System.out.println("Aresta de árvore: " + u + " -> " + v);
                    arestasPai.add(new Pair(u, v));

                    visitado[v] = true;
                    descoberta[v] = ++tempo;

                    pilha.push(v);
                    iteradores.push(adjacentes[v].iterator());
                }
            } else {
                termino[u] = ++tempo;
                pilha.pop();
                iteradores.pop();
            }
        }
    }

    public void classificarArestasDoVertice(int u) {
        for (int v : adjacentes[u]) {
        Pair aresta = new Pair(u, v);
            if (arestasPai.contains(aresta)) {
                System.out.println("Aresta (" + u + " -> " + v + "): Árvore");
            } else if (descoberta[v] < descoberta[u] && termino[v] > termino[u]) {
                System.out.println("Aresta (" + u + " -> " + v + "): Retorno");
            } else if (descoberta[u] < descoberta[v] && termino[v] < termino[u]) {
                System.out.println("Aresta (" + u + " -> " + v + "): Avanço");
            } else {
                System.out.println("Aresta (" + u + " -> " + v + "): Cruzamento");
            }
        }
    }

}
