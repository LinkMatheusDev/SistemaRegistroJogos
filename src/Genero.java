public class Genero {
    String nome;
    String descGenero;

    public Genero(String nome, String descGenero){
        this.nome = nome;
        this.descGenero = descGenero;
    }

    public String getNome(){
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescGenero(){
        return descGenero;
    }

    public void setDescGenero(String descGenero) {
        this.descGenero = descGenero;
    }

}
