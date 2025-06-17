import Jogo;

package com.sistemaregistrojogos.model;
/**
 * Classe especializada para Jogos de RPG (Role Playing Game)
 * Herda de Jogo e adiciona características específicas de RPG
 */
public class JogoRPG extends Jogo {
    private int horasGameplay; // Estimativa de horas para completar
    private String tipoRPG; // "JRPG", "Western RPG", "Action RPG", "Tactical RPG"
    private boolean temCustomizacao;
    private int numeroClasses; // Número de classes/profissões disponíveis
    private boolean temHistoriaRamificada;
    
    // Construtor padrão
    public JogoRPG() {
        super();
        setGenero("RPG");
    }
    
    // Construtor completo
    public JogoRPG(int id, String nome, double preco, String desenvolvedor, String dataCreacao,
                   int horasGameplay, String tipoRPG, boolean temCustomizacao, 
                   int numeroClasses, boolean temHistoriaRamificada) {
        super(id, nome, preco, "RPG", desenvolvedor, dataCreacao);
        this.horasGameplay = horasGameplay;
        this.tipoRPG = tipoRPG;
        this.temCustomizacao = temCustomizacao;
        this.numeroClasses = numeroClasses;
        this.temHistoriaRamificada = temHistoriaRamificada;
    }
    
    // Construtor sem ID
    public JogoRPG(String nome, double preco, String desenvolvedor,
                   int horasGameplay, String tipoRPG, boolean temCustomizacao, 
                   int numeroClasses, boolean temHistoriaRamificada) {
        super(nome, preco, "RPG", desenvolvedor);
        this.horasGameplay = horasGameplay;
        this.tipoRPG = tipoRPG;
        this.temCustomizacao = temCustomizacao;
        this.numeroClasses = numeroClasses;
        this.temHistoriaRamificada = temHistoriaRamificada;
    }
    
    // Getters e Setters específicos
    public int getHorasGameplay() { return horasGameplay; }
    public void setHorasGameplay(int horasGameplay) { 
        if (horasGameplay < 0) {
            throw new IllegalArgumentException("Horas de gameplay não pode ser negativo");
        }
        this.horasGameplay = horasGameplay; 
    }
    
    public String getTipoRPG() { return tipoRPG; }
    public void setTipoRPG(String tipoRPG) { this.tipoRPG = tipoRPG; }
    
    public boolean isTemCustomizacao() { return temCustomizacao; }
    public void setTemCustomizacao(boolean temCustomizacao) { this.temCustomizacao = temCustomizacao; }
    
    public int getNumeroClasses() { return numeroClasses; }
    public void setNumeroClasses(int numeroClasses) { 
        if (numeroClasses < 0) {
            throw new IllegalArgumentException("Número de classes não pode ser negativo");
        }
        this.numeroClasses = numeroClasses; 
    }
    
    public boolean isTemHistoriaRamificada() { return temHistoriaRamificada; }
    public void setTemHistoriaRamificada(boolean temHistoriaRamificada) { this.temHistoriaRamificada = temHistoriaRamificada; }
    
    // Métodos específicos de negócio
    public String getDuracaoCategoria() {
        if (horasGameplay <= 10) return "Curto";
        if (horasGameplay <= 30) return "Médio";
        if (horasGameplay <= 60) return "Longo";
        return "Épico";
    }
    
    public double getValorPorHora() {
        if (horasGameplay == 0) return 0.0;
        return getPreco() / horasGameplay;
    }
    
    public String getComplexidade() {
        int pontos = 0;
        if (temCustomizacao) pontos += 2;
        if (numeroClasses > 5) pontos += 2;
        if (temHistoriaRamificada) pontos += 3;
        if (horasGameplay > 50) pontos += 2;
        
        if (pontos <= 3) return "Simples";
        if (pontos <= 6) return "Moderada";
        return "Complexa";
    }
    
    public boolean isRPGTradicional() {
        return "JRPG".equals(tipoRPG) || "Western RPG".equals(tipoRPG);
    }
    
    public String getRecomendacaoJogador() {
        StringBuilder rec = new StringBuilder();
        rec.append("Recomendado para: ");
        
        if (horasGameplay > 80) {
            rec.append("Jogadores hardcore, ");
        }
        if (temCustomizacao && numeroClasses > 3) {
            rec.append("Fãs de personalização, ");
        }
        if (temHistoriaRamificada) {
            rec.append("Amantes de narrativa, ");
        }
        if ("JRPG".equals(tipoRPG)) {
            rec.append("Fãs de RPG japonês, ");
        }
        
        String resultado = rec.toString();
        if (resultado.endsWith(", ")) {
            resultado = resultado.substring(0, resultado.length() - 2);
        }
        
        return resultado.isEmpty() ? "Jogadores em geral" : resultado;
    }
    
    @Override
    public String toString() {
        return String.format("JogoRPG{%s, horas=%d, tipo='%s', classes=%d, complexidade='%s'}", 
                           super.toString(), horasGameplay, tipoRPG, numeroClasses, getComplexidade());
    }
    
    // Método para obter informações específicas do RPG
    public String getInformacoesRPG() {
        StringBuilder info = new StringBuilder();
        info.append("=== INFORMAÇÕES DE RPG ===\n");
        info.append("Horas de Gameplay: ").append(horasGameplay).append("h (").append(getDuracaoCategoria()).append(")\n");
        info.append("Tipo de RPG: ").append(tipoRPG).append("\n");
        info.append("Customização: ").append(temCustomizacao ? "Sim" : "Não").append("\n");
        info.append("Número de Classes: ").append(numeroClasses).append("\n");
        info.append("História Ramificada: ").append(temHistoriaRamificada ? "Sim" : "Não").append("\n");
        info.append("Complexidade: ").append(getComplexidade()).append("\n");
        info.append("Valor por Hora: R$ ").append(String.format("%.2f", getValorPorHora())).append("\n");
        info.append(getRecomendacaoJogador()).append("\n");
        return info.toString();
    }
} 