import java.util.ArrayList;

public class Jogo {
    private String titulo;
    private String genero;
    private String desenvolvedora;
    private double preco;

    public Jogo(String titulo, String genero, String desenvolvedora, double preco){
        this.titulo = titulo;
        this.genero = genero;
        this.desenvolvedora = desenvolvedora;
        this.preco = preco;
    }

    public void buscarJogoPorTitulo(ArrayList<Jogo> listaDeJogos, String titulo){
        for (Jogo jogo : listaDeJogos){
            if(jogo.getTitulo().equalsIgnoreCase(titulo)){
                System.out.println(jogo);
                return;
            }
        }
    }

    public String toString() {
        return "Título: " + titulo + "\n" +
                "Gênero: " + genero + "\n" +
                "Desenvolvedora: " + desenvolvedora + "\n" +
                "Preço: R$" + preco;
    }


    public String getTitulo(){
        return titulo;
    }

    public void setTitulo(String titulo){
        this.titulo = titulo;
    }

    public String getGenero(){
        return genero;
    }

    public void setGenero(String genero){
        this.genero = genero;
    }

    public String getDesenvolvedora(){
        return desenvolvedora;
    }

    public void setDesenvolvedora(String desenvolvedora){
        this.desenvolvedora = desenvolvedora;
    }

    public double getPreco(){
        return preco;
    }

    public void setPreco(double preco){
        this.preco = preco;
    }


}
