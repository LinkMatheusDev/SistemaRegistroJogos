import java.util.Scanner;
import java.util.ArrayList;
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        ArrayList<Jogo> listaDeJogos = new ArrayList<>();
        Scanner entrada = new Scanner(System.in);
        int opcao = 0;
        System.out.println("*****CADASTRO DE JOGOS*****");
        while(opcao != -1){
            System.out.println("\nDigite sua opção: \n" +
                    "1 - Adicionar jogo\n" +
                    "2 - Consultar jogo (pelo título)\n" +
                    "3 - Alterar dados de um jogo\n" +
                    "4 - Excluir dados de um jogo\n" +
                    "5 - Mostrar todos os jogos\n" +
                    "-1 - Sair");
        opcao = entrada.nextInt();
        entrada.nextLine();

        switch (opcao) {
            case 1:
                System.out.println("Digite o Titulo do jogo: ");
                String titulo = entrada.nextLine();
                System.out.println("Digite o Genero: ");
                String genero = entrada.nextLine();
                System.out.println("Digite a Desenvolvedora:");
                String desenvolvedora = entrada.nextLine();
                System.out.println("Digite o preço:");
                double preco = entrada.nextDouble();

                Jogo jogo01 = new Jogo(titulo,genero, desenvolvedora, preco);
                listaDeJogos.add(jogo01);

                System.out.println("Jogo adicionado com sucesso. ");

                System.out.println(listaDeJogos.get(0));

                break;
            case 2:
                System.out.println("Digite o jogo a ser consultado:");



        }
    }
}
}