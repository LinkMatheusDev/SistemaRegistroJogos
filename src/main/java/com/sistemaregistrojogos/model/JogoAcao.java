import Jogo;

package com.sistemaregistrojogos.model;
/**
 * Classe especializada para Jogos de Ação
 * Herda de Jogo e adiciona características específicas
 */
public class JogoAcao extends Jogo {
    private int nivelViolencia; // 1-10
    private boolean temMultijogador;
    private String tipoControle; // "Keyboard", "Controller", "Both"
    private int classificacaoEtaria; // 0, 10, 12, 14, 16, 18
    
    // Construtor padrão
    public JogoAcao() {
        super();
        setGenero("Ação");
    }
    
    // Construtor completo
    public JogoAcao(int id, String nome, double preco, String desenvolvedor, String dataCreacao,
                    int nivelViolencia, boolean temMultijogador, String tipoControle, int classificacaoEtaria) {
        super(id, nome, preco, "Ação", desenvolvedor, dataCreacao);
        this.nivelViolencia = nivelViolencia;
        this.temMultijogador = temMultijogador;
        this.tipoControle = tipoControle;
        this.classificacaoEtaria = classificacaoEtaria;
    }
    
    // Construtor sem ID
    public JogoAcao(String nome, double preco, String desenvolvedor,
                    int nivelViolencia, boolean temMultijogador, String tipoControle, int classificacaoEtaria) {
        super(nome, preco, "Ação", desenvolvedor);
        this.nivelViolencia = nivelViolencia;
        this.temMultijogador = temMultijogador;
        this.tipoControle = tipoControle;
        this.classificacaoEtaria = classificacaoEtaria;
    }
    
    // Getters e Setters específicos
    public int getNivelViolencia() { return nivelViolencia; }
    public void setNivelViolencia(int nivelViolencia) { 
        if (nivelViolencia < 1 || nivelViolencia > 10) {
            throw new IllegalArgumentException("Nível de violência deve estar entre 1 e 10");
        }
        this.nivelViolencia = nivelViolencia; 
    }
    
    public boolean isTemMultijogador() { return temMultijogador; }
    public void setTemMultijogador(boolean temMultijogador) { this.temMultijogador = temMultijogador; }
    
    public String getTipoControle() { return tipoControle; }
    public void setTipoControle(String tipoControle) { this.tipoControle = tipoControle; }
    
    public int getClassificacaoEtaria() { return classificacaoEtaria; }
    public void setClassificacaoEtaria(int classificacaoEtaria) { 
        int[] idades = {0, 10, 12, 14, 16, 18};
        boolean valido = false;
        for (int idade : idades) {
            if (idade == classificacaoEtaria) {
                valido = true;
                break;
            }
        }
        if (!valido) {
            throw new IllegalArgumentException("Classificação etária deve ser: 0, 10, 12, 14, 16 ou 18");
        }
        this.classificacaoEtaria = classificacaoEtaria; 
    }
    
    // Métodos específicos de negócio
    public boolean isAdequadoParaIdade(int idade) {
        return idade >= classificacaoEtaria;
    }
    
    public String getNivelViolenciaDescricao() {
        if (nivelViolencia <= 3) return "Baixa";
        if (nivelViolencia <= 6) return "Moderada";
        if (nivelViolencia <= 8) return "Alta";
        return "Extrema";
    }
    
    public String getTipoJogo() {
        return temMultijogador ? "Ação Multijogador" : "Ação Single Player";
    }
    
    public boolean precisaControleEspecial() {
        return "Controller".equals(tipoControle);
    }
    
    @Override
    public String toString() {
        return String.format("JogoAcao{%s, violencia=%d, multijogador=%b, controle='%s', idade=%d+}", 
                           super.toString(), nivelViolencia, temMultijogador, tipoControle, classificacaoEtaria);
    }
    
    // Método para obter informações específicas do jogo de ação
    public String getInformacoesAcao() {
        StringBuilder info = new StringBuilder();
        info.append("=== INFORMAÇÕES DE AÇÃO ===\n");
        info.append("Nível de Violência: ").append(nivelViolencia).append("/10 (").append(getNivelViolenciaDescricao()).append(")\n");
        info.append("Multijogador: ").append(temMultijogador ? "Sim" : "Não").append("\n");
        info.append("Tipo de Controle: ").append(tipoControle).append("\n");
        info.append("Classificação Etária: ").append(classificacaoEtaria).append("+ anos\n");
        info.append("Tipo: ").append(getTipoJogo()).append("\n");
        return info.toString();
    }
} 