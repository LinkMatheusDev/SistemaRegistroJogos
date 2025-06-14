import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Cadastro cadastro = new Cadastro();
        Scanner sc = new Scanner(System.in);
        int opcao;

        do {
            System.out.println("\nMENU:");
            System.out.println("1 - Incluir novo produto");
            System.out.println("2 - Consultar produto");
            System.out.println("3 - Alterar produto");
            System.out.println("4 - Excluir produto");
            System.out.println("5 - Listar todos os produtos");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opção: ");

            opcao = sc.nextInt();
            sc.nextLine(); // limpa buffer

            switch (opcao) {
                case 1:
                    System.out.print("Nome: ");
                    String nome = sc.nextLine();
                    System.out.print("Preço: ");
                    double preco = sc.nextDouble(); sc.nextLine();
                    System.out.print("Gênero: ");
                    String genero = sc.nextLine();
                    System.out.print("Desenvolvedora: ");
                    String dev = sc.nextLine();

                    Produto j = new Jogo(nome, preco, genero, dev);
                    cadastro.incluir(j);
                    break;

                case 2:
                    System.out.print("Digite o nome do produto para consultar: ");
                    String nomeConsulta = sc.nextLine();
                    Produto encontrado = cadastro.consultar(nomeConsulta);
                    if (encontrado != null) {
                        encontrado.mostrarInfo();
                    } else {
                        System.out.println("Produto não encontrado.");
                    }
                    break;

                case 3:
                    System.out.print("Digite o nome do produto para alterar: ");
                    String nomeAlterar = sc.nextLine();
                    System.out.print("Digite o novo preço: ");
                    double novoPreco = sc.nextDouble(); sc.nextLine();
                    boolean alterado = cadastro.alterar(nomeAlterar, novoPreco);
                    System.out.println(alterado ? "Preço alterado com sucesso." : "Produto não encontrado.");
                    break;

                case 4:
                    System.out.print("Digite o nome do produto para excluir: ");
                    String nomeExcluir = sc.nextLine();
                    boolean excluido = cadastro.excluir(nomeExcluir);
                    System.out.println(excluido ? "Produto excluído com sucesso." : "Produto não encontrado.");
                    break;

                case 5:
                    cadastro.listar();
                    break;

                case 0:
                    System.out.println("Saindo do sistema...");
                    break;

                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }

        } while (opcao != 0);

        sc.close();
    }
}
