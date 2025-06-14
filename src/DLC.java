// Polimorfismo: pode ter outras categorias de produtos
public class DLC extends Produto {
    private String jogoRelacionado;

    public DLC(String nome, double preco, String jogoRelacionado) {
        super(nome, preco);
        this.jogoRelacionado = jogoRelacionado;
    }

    @Override
    public void mostrarInfo() {
        super.mostrarInfo();
        System.out.println("Relacionado ao jogo: " + jogoRelacionado);
    }
}
