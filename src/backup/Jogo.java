public class Jogo extends Produto {
    private String genero;
    private String desenvolvedora;

    public Jogo(String nome, double preco, String genero, String desenvolvedora) {
        super(nome, preco);
        this.genero = genero;
        this.desenvolvedora = desenvolvedora;
    }

    @Override
    public void mostrarInfo() {
        super.mostrarInfo();
        System.out.println("Gênero: " + genero);
        System.out.println("Desenvolvedora: " + desenvolvedora);
    }
}
