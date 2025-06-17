/**
 * Classe base para representar um Jogo
 * Produto principal do sistema
 */
public class Jogo {
    private int id;
    private String nome;
    private double preco;
    private String genero;
    private String desenvolvedor;
    private String dataCreacao;
    
    // Construtor padrão
    public Jogo() {}
    
    // Construtor completo
    public Jogo(int id, String nome, double preco, String genero, String desenvolvedor, String dataCreacao) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.genero = genero;
        this.desenvolvedor = desenvolvedor;
        this.dataCreacao = dataCreacao;
    }
    
    // Construtor sem ID (para novos jogos)
    public Jogo(String nome, double preco, String genero, String desenvolvedor) {
        this.nome = nome;
        this.preco = preco;
        this.genero = genero;
        this.desenvolvedor = desenvolvedor;
    }
    
    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public double getPreco() { return preco; }
    public void setPreco(double preco) { this.preco = preco; }
    
    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }
    
    public String getDesenvolvedor() { return desenvolvedor; }
    public void setDesenvolvedor(String desenvolvedor) { this.desenvolvedor = desenvolvedor; }
    
    public String getDataCreacao() { return dataCreacao; }
    public void setDataCreacao(String dataCreacao) { this.dataCreacao = dataCreacao; }
    
    // Métodos de negócio
    public String getPrecoFormatado() {
        return String.format("R$ %.2f", preco);
    }
    
    public boolean isGratuito() {
        return preco == 0.0;
    }
    
    public String getCategoria() {
        if (preco == 0.0) return "Gratuito";
        if (preco <= 29.99) return "Barato";
        if (preco <= 79.99) return "Médio";
        return "Premium";
    }
    
    @Override
    public String toString() {
        return String.format("Jogo{id=%d, nome='%s', preco=%.2f, genero='%s', desenvolvedor='%s'}", 
                           id, nome, preco, genero, desenvolvedor);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Jogo jogo = (Jogo) obj;
        return id == jogo.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
} 