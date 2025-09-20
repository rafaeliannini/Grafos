import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println();
            System.out.println("Escolha o arquivo:");
            System.out.println("1 - graph-test-8-1.txt");
            System.out.println("2 - graph-test-100-1.txt");
            System.out.println("3 - graph-test-50000-1.txt");
            System.out.println("4 - Digitar o nome do arquivo");
            System.out.print("Opção: ");
            
            int opcao = scanner.nextInt();
            scanner.nextLine();
            
            String arquivo;
            switch (opcao) {
                case 1 -> arquivo = "graph-test-8-1.txt";
                case 2 -> arquivo = "graph-test-100-1.txt";
                case 3 -> arquivo = "graph-test-50000-1.txt";
                case 4 -> {
                    System.out.print("Digite o nome do arquivo: ");
                    arquivo = scanner.nextLine().trim();
                }
                default -> {
                    System.out.println("Opção inválida!");
                    scanner.close();
                    return;
                }
            }

            Grafo grafo = new Grafo();
            grafo.lerGrafo(arquivo);

            System.out.print("Digite o número do vértice que deseja observar: ");
            int vertice = scanner.nextInt();

            if (vertice < 1 || vertice > grafo.getNumVertices()) {
                System.out.println("Vértice inválido! Deve estar entre 1 e " + grafo.getNumVertices());
            } else {
                System.out.println();
                System.out.println("Grau de saída de " + vertice + ": " + grafo.grauSaida(vertice));
                System.out.println("Grau de entrada de " + vertice + ": " + grafo.grauEntrada(vertice));
                System.out.println("Sucessores: " + grafo.ListaSucessores(vertice));
                System.out.println("Predecessores: " + grafo.ListaPredecessores(vertice));

                grafo.buscaProfundidade();

                System.out.println();
                System.out.println("Classificação das arestas que saem do vértice " + vertice + ":");

                grafo.classificarArestasDoVertice(vertice);
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo: " + e.getMessage());
        }
    }
}
