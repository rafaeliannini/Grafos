import java.io.*;
import java.util.*;

public class Grafo {
    private int numVertices;
    private List<Integer>[] adjacentes;

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
}
