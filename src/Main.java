import java.util.Scanner;
import java.util.ArrayList;
public class Main {
    public static void main(String[] args) {

        ArrayList<Jogo> listaDeJogos = new ArrayList<>();
        ArrayList<Genero> listaDeGeneros = new ArrayList<>();

        Scanner entrada = new Scanner(System.in);

        int opcao = 0;

        System.out.println("*****CADASTRO DE JOGOS*****");
        while(opcao != -1) {
            System.out.println("\nDigite sua opção: \n" +
                    "1 - Adicionar jogo\n" +
                    "2 - Consultar jogo (pelo título)\n" +
                    "3 - Alterar dados de um jogo\n" +
                    "4 - Excluir dados de um jogo\n" +
                    "5 - Mostrar todos os jogos\n" +
                    "-1 - Sair");
            opcao = entrada.nextInt();
            entrada.nextLine(); // consumir a quebra de linha

            switch (opcao) {
                case 1:
                    System.out.println("Digite o Titulo do jogo: ");
                    String titulo = entrada.nextLine();

                    System.out.println("Digite o Genero: ");
                    String nomeGenero = entrada.nextLine();
                    boolean generoExiste = false;
                    Genero generoEncontrado = null;
                    String descGenero = null;
                    for (Genero g : listaDeGeneros) //descobre se o genero ja existe na listaDeGeneros{
                        if (g.getNome().equalsIgnoreCase(nomeGenero)) {//deve verificar se o genero ja foi adicionado, caso sim atribuir genero ao jogo
                            System.out.println("Gênero ja existe ");

                            generoExiste = true;
                            generoEncontrado = g;
                            break;
                        } else {
                            System.out.println("Digite a descrição do Gênero: ");
                            descGenero = entrada.nextLine();

                        }


                    Genero genero = new Genero(nomeGenero, descGenero); //cria um novo genero

                    System.out.println("Digite a Desenvolvedora:");
                    String desenvolvedora = entrada.nextLine();

                    System.out.println("Digite o preço:");
                    double preco = entrada.nextDouble();

                    Jogo jogo01 = new Jogo(titulo, genero, desenvolvedora, preco);//cria um novo jogo
                    listaDeJogos.add(jogo01); //adiciona o jogo a lista de jogos

                    System.out.println("Jogo adicionado com sucesso. ");

                    System.out.println(jogo01);

                    break;
                case 2:
                    System.out.println("Digite o jogo a ser consultado:");
                    String consulta = entrada.nextLine();
                    boolean encontrado = false;
                    for (Jogo jogo : listaDeJogos) {
                        if (jogo.getTitulo().equalsIgnoreCase(consulta)) {
                            System.out.println("Jogo encontrado: ");
                            System.out.println(jogo);
                            encontrado = true;
                            break;
                        }
                    }
                    if (!encontrado) {
                        System.out.println("Jogo não encontrado.");
                    }
                    break;
            }
        }




        }
    }
}