import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

// Classes auxiliares criadas com ajuda de inteligência artificial.
class Aresta {
    final int para;
    final int peso;
    Aresta(int para, int peso) { this.para = para; this.peso = peso; }
}

class GrafoDirecionado {
    private final int n;
    private final List<List<Aresta>> adj;

    GrafoDirecionado(int n) {
        this.n = n;
        this.adj = new ArrayList<>(n);
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
    }

    int tamanho() { return n; }

    void adicionarAresta(int de, int para, int peso) {
        if (de < 0 || de >= n || para < 0 || para >= n) throw new IllegalArgumentException("vértice inválido");
        if (peso <= 0) throw new IllegalArgumentException("peso deve ser positivo");
        adj.get(de).add(new Aresta(para, peso));
    }

    List<Aresta> vizinhos(int u) { return adj.get(u); }

    int quantidadeArestas() {
        int m = 0;
        for (var lista : adj) m += lista.size();
        return m;
    }

    void salvarEmArquivo(String caminho) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(caminho))) { 
            bw.write(n + " " + quantidadeArestas());
            bw.newLine();
            for (int u = 0; u < n; u++) {
                for (Aresta e : adj.get(u)) {
                    bw.write(u + " " + e.para + " " + e.peso);
                    bw.newLine();
                }
            }
        }
    }

    static GrafoDirecionado carregarDeArquivo(String caminho) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            String linha = br.readLine();
            if (linha == null) throw new IOException("arquivo vazio");
            String[] cab = linha.trim().split("\\s+");
            int n = Integer.parseInt(cab[0]);
            int m = Integer.parseInt(cab[1]);
            GrafoDirecionado g = new GrafoDirecionado(n);
            for (int i = 0; i < m; i++) {
                String s = br.readLine();
                if (s == null) throw new IOException("formato incorreto: arestas insuficientes");
                String[] tok = s.trim().split("\\s+");
                int u = Integer.parseInt(tok[0]);
                int v = Integer.parseInt(tok[1]);
                int w = Integer.parseInt(tok[2]);
                g.adicionarAresta(u, v, w);
            }
            return g;
        }
    }
}

class GeradoresGrafo {
    static GrafoDirecionado aleatorio(int n, double p, int pesoMin, int pesoMax) {
        GrafoDirecionado g = new GrafoDirecionado(n);
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        for (int de = 0; de < n; de++) {
            for (int para = 0; para < n; para++) {
                if (de == para) continue;
                if (rnd.nextDouble() < p) {
                    int w = rnd.nextInt(pesoMin, pesoMax + 1);
                    g.adicionarAresta(de, para, w);
                }
            }
        }
        return g;
    }

    static GrafoDirecionado dag(int n, int camadas, int arestasPorNo, int pesoMin, int pesoMax) {
        if (camadas < 2) camadas = 2;
        GrafoDirecionado g = new GrafoDirecionado(n);
        ThreadLocalRandom rnd = ThreadLocalRandom.current();

        List<List<Integer>> L = new ArrayList<>();
        for (int i = 0; i < camadas; i++) L.add(new ArrayList<>());
        for (int v = 0; v < n; v++) {
            int camada = (int) ((long) v * camadas / n);
            if (camada >= camadas) camada = camadas - 1;
            L.get(camada).add(v);
        }

        for (int i = 0; i < camadas; i++) {
            List<Integer> origem = L.get(i);
            if (origem.isEmpty()) continue;
            List<Integer> destinos = new ArrayList<>();
            for (int j = i + 1; j < camadas; j++) destinos.addAll(L.get(j));
            if (destinos.isEmpty()) continue;

            for (int u : origem) {
                Collections.shuffle(destinos, new Random(rnd.nextLong()));
                int deg = Math.min(arestasPorNo, destinos.size());
                for (int k = 0; k < deg; k++) {
                    int v = destinos.get(k);
                    int w = rnd.nextInt(pesoMin, pesoMax + 1);
                    g.adicionarAresta(u, v, w);
                }
            }
        }
        return g;
    }
}

class AcharCaminhoMinimo {
    static class No implements Comparable<No> {
        int v; int dist;
        No(int v, int dist) { this.v = v; this.dist = dist; }

        @Override
        public int compareTo(No o) { return Integer.compare(this.dist, o.dist); }
    }

    static Resultado caminhosMinimos(GrafoDirecionado g, int origem) {
        int n = g.tamanho();
        int[] dist = new int[n];
        int[] pai = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(pai, -1);
        dist[origem] = 0;

        boolean[] visitado = new boolean[n];
        PriorityQueue<No> fila = new PriorityQueue<>();
        fila.add(new No(origem, 0));

        while (!fila.isEmpty()) {
            No cur = fila.poll();
            if (visitado[cur.v]) continue;
            visitado[cur.v] = true;

            for (Aresta e : g.vizinhos(cur.v)) {
                if (visitado[e.para]) continue;
                if (dist[cur.v] != Integer.MAX_VALUE && dist[cur.v] + e.peso < dist[e.para]) {
                    dist[e.para] = dist[cur.v] + e.peso;
                    pai[e.para] = cur.v;
                    fila.add(new No(e.para, dist[e.para]));
                }
            }
        }
        return new Resultado(dist, pai);
    }

    static List<Integer> reconstruirCaminho(int destino, int[] pai) {
        List<Integer> caminho = new ArrayList<>();
        if (destino < 0 || destino >= pai.length) return caminho;
        int cur = destino;
        while (cur != -1) {
            caminho.add(cur);
            cur = pai[cur];
        }
        Collections.reverse(caminho);
        return caminho;
    }

    static class Resultado {
        final int[] distancias;
        final int[] pais;
        Resultado(int[] d, int[] p) { this.distancias = d; this.pais = p; }
    }
}

public class Principal {
    public static void main(String[] args) {
        int[] tamanhos = {10, 100, 1000, 10000};
        int pesoMin = 1, pesoMax = 20;
        double probAresta = 0.12;
        int camadas = 4;
        int arestasPorNo = 3;

        String pastaSaida = "grafos/";
        new File(pastaSaida).mkdirs(); 
        
        System.out.println();
        System.out.print("Deseja gerar novos grafos? (s/n) ");
        String resposta;
        try (Scanner scanner = new Scanner(System.in)) {
            resposta = scanner.nextLine().trim().toLowerCase();
        }

        if (resposta.equals("s")) {
            for (int n : tamanhos) {
                GrafoDirecionado gAleat = GeradoresGrafo.aleatorio(n, probAresta, pesoMin, pesoMax);
                GrafoDirecionado gDag = GeradoresGrafo.dag(n, camadas, arestasPorNo, pesoMin, pesoMax);

                String arqAleat = pastaSaida + "grafo_aleatorio_n" + n + ".txt";
                String arqDag = pastaSaida + "grafo_dag_n" + n + ".txt";
                try {
                    gAleat.salvarEmArquivo(arqAleat); 
                    gDag.salvarEmArquivo(arqDag);
                } catch (IOException e) {
                    System.err.println("Falha ao salvar grafos n=" + n + ": " + e.getMessage());
                }
            }
        } else {
            System.out.println("Usando grafos existentes na pasta '" + pastaSaida + "'.");
        }

        for (int n : tamanhos) {
            String arqAleat = pastaSaida + "grafo_aleatorio_n" + n + ".txt";
            String arqDag = pastaSaida + "grafo_dag_n" + n + ".txt";

            double tempoTotalAleat = 0.0;
            double tempoTotalDag = 0.0;

            try {
                GrafoDirecionado g1 = GrafoDirecionado.carregarDeArquivo(arqAleat);
                GrafoDirecionado g2 = GrafoDirecionado.carregarDeArquivo(arqDag);

                int origem = 0;
                int[] destinos = escolherDestinos(n);

                System.out.println("=== Tamanho n=" + n + " ===");
                
                System.out.println("[Tipo A] Aleatório esparso: V=" + n + ", E=" + g1.quantidadeArestas());
                tempoTotalAleat += executarTeste(g1, origem, destinos);
                System.out.printf("Tempo total [Tipo A] Aleatório: %.3f ms%n", tempoTotalAleat);
                System.out.println();

                System.out.println("[Tipo B] DAG em camadas: V=" + n + ", E=" + g2.quantidadeArestas());
                tempoTotalDag += executarTeste(g2, origem, destinos);
                System.out.printf("Tempo total [Tipo B] DAG: %.3f ms%n", tempoTotalDag);
                System.out.println();

            } catch (IOException e) {
                System.err.println("Falha ao carregar grafos n=" + n + ": " + e.getMessage());
            }
        }
    }

    static int[] escolherDestinos(int n) {
        List<Integer> pool = new ArrayList<>();
        for (int i = 1; i < n; i++) pool.add(i);
        Collections.shuffle(pool);
        int k = Math.min(3, pool.size());
        int[] ds = new int[k];
        for (int i = 0; i < k; i++) ds[i] = pool.get(i);
        return ds;
    }

    static double executarTeste(GrafoDirecionado g, int origem, int[] destinos) {
        long inicio = System.nanoTime();
        AcharCaminhoMinimo.Resultado res = AcharCaminhoMinimo.caminhosMinimos(g, origem);
        long fim = System.nanoTime();
        double duracaoMs = (fim - inicio) / 1_000_000.0;
        double duracaoTotal = 0.0;

        for (int d : destinos) {
            if (res.distancias[d] == Integer.MAX_VALUE) {
                System.out.println("  origem:" + origem + " -> destino:" + d + ": inatingível");
            } else {
                List<Integer> caminho = AcharCaminhoMinimo.reconstruirCaminho(d, res.pais);
                System.out.println("  origem:" + origem + " -> destino:" + d + ": dist=" + res.distancias[d] + " caminho=" + caminho);
            }
            duracaoTotal += duracaoMs;
        }
        return duracaoTotal;
    }
}
