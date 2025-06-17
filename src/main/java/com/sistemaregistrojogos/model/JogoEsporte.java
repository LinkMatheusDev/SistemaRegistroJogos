import Jogo;

package com.sistemaregistrojogos.model;
/**
 * Classe especializada para Jogos de Esporte
 * Herda de Jogo e adiciona características específicas de jogos esportivos
 */
public class JogoEsporte extends Jogo {
    private String modalidadeEsportiva; // "Futebol", "Basquete", "Tênis", "Corrida", etc.
    private boolean temLicenciamento; // Se tem licenças oficiais (FIFA, NBA, etc.)
    private String modoJogo; // "Simulação", "Arcade", "Híbrido"
    private boolean temCarreira;
    private boolean temOnline;
    private int numeroJogadores; // Máximo de jogadores simultâneos
    
    // Construtor padrão
    public JogoEsporte() {
        super();
        setGenero("Esporte");
    }
    
    // Construtor completo
    public JogoEsporte(int id, String nome, double preco, String desenvolvedor, String dataCreacao,
                       String modalidadeEsportiva, boolean temLicenciamento, String modoJogo,
                       boolean temCarreira, boolean temOnline, int numeroJogadores) {
        super(id, nome, preco, "Esporte", desenvolvedor, dataCreacao);
        this.modalidadeEsportiva = modalidadeEsportiva;
        this.temLicenciamento = temLicenciamento;
        this.modoJogo = modoJogo;
        this.temCarreira = temCarreira;
        this.temOnline = temOnline;
        this.numeroJogadores = numeroJogadores;
    }
    
    // Construtor sem ID
    public JogoEsporte(String nome, double preco, String desenvolvedor,
                       String modalidadeEsportiva, boolean temLicenciamento, String modoJogo,
                       boolean temCarreira, boolean temOnline, int numeroJogadores) {
        super(nome, preco, "Esporte", desenvolvedor);
        this.modalidadeEsportiva = modalidadeEsportiva;
        this.temLicenciamento = temLicenciamento;
        this.modoJogo = modoJogo;
        this.temCarreira = temCarreira;
        this.temOnline = temOnline;
        this.numeroJogadores = numeroJogadores;
    }
    
    // Getters e Setters específicos
    public String getModalidadeEsportiva() { return modalidadeEsportiva; }
    public void setModalidadeEsportiva(String modalidadeEsportiva) { this.modalidadeEsportiva = modalidadeEsportiva; }
    
    public boolean isTemLicenciamento() { return temLicenciamento; }
    public void setTemLicenciamento(boolean temLicenciamento) { this.temLicenciamento = temLicenciamento; }
    
    public String getModoJogo() { return modoJogo; }
    public void setModoJogo(String modoJogo) { this.modoJogo = modoJogo; }
    
    public boolean isTemCarreira() { return temCarreira; }
    public void setTemCarreira(boolean temCarreira) { this.temCarreira = temCarreira; }
    
    public boolean isTemOnline() { return temOnline; }
    public void setTemOnline(boolean temOnline) { this.temOnline = temOnline; }
    
    public int getNumeroJogadores() { return numeroJogadores; }
    public void setNumeroJogadores(int numeroJogadores) { 
        if (numeroJogadores < 1) {
            throw new IllegalArgumentException("Número de jogadores deve ser pelo menos 1");
        }
        this.numeroJogadores = numeroJogadores; 
    }
    
    // Métodos específicos de negócio
    public String getTipoExperiencia() {
        if ("Simulação".equals(modoJogo)) {
            return "Realista e desafiadora";
        } else if ("Arcade".equals(modoJogo)) {
            return "Divertida e casual";
        } else {
            return "Equilibrada";
        }
    }
    
    public boolean isParaJogadorCasual() {
        return "Arcade".equals(modoJogo) && !temCarreira;
    }
    
    public boolean isParaJogadorSerio() {
        return "Simulação".equals(modoJogo) && temLicenciamento && temCarreira;
    }
    
    public String getRecomendacaoMultiplayer() {
        if (numeroJogadores == 1) {
            return "Apenas single player";
        } else if (numeroJogadores <= 4) {
            return "Ideal para jogos em família";
        } else if (numeroJogadores <= 12) {
            return "Ótimo para grupos de amigos";
        } else {
            return "Suporte a grandes partidas online";
        }
    }
    
    public double getValorCompetitivo() {
        double pontos = 0.0;
        if (temLicenciamento) pontos += 2.0;
        if ("Simulação".equals(modoJogo)) pontos += 2.0;
        if (temOnline) pontos += 1.5;
        if (temCarreira) pontos += 1.0;
        if (numeroJogadores > 8) pontos += 0.5;
        
        return pontos; // Máximo 7.0
    }
    
    public String getNivelCompetitivo() {
        double valor = getValorCompetitivo();
        if (valor <= 2.0) return "Casual";
        if (valor <= 4.0) return "Intermediário";
        if (valor <= 6.0) return "Competitivo";
        return "Profissional";
    }
    
    public boolean isAtualAnual() {
        // Modalidades que geralmente têm versões anuais
        String[] esportesAnuais = {"Futebol", "Basquete", "Hockey", "Football Americano"};
        for (String esporte : esportesAnuais) {
            if (esporte.equalsIgnoreCase(modalidadeEsportiva)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        return String.format("JogoEsporte{%s, modalidade='%s', modo='%s', licenciado=%b, online=%b}", 
                           super.toString(), modalidadeEsportiva, modoJogo, temLicenciamento, temOnline);
    }
    
    // Método para obter informações específicas do jogo de esporte
    public String getInformacoesEsporte() {
        StringBuilder info = new StringBuilder();
        info.append("=== INFORMAÇÕES DE ESPORTE ===\n");
        info.append("Modalidade: ").append(modalidadeEsportiva).append("\n");
        info.append("Licenciamento Oficial: ").append(temLicenciamento ? "Sim" : "Não").append("\n");
        info.append("Modo de Jogo: ").append(modoJogo).append(" (").append(getTipoExperiencia()).append(")\n");
        info.append("Modo Carreira: ").append(temCarreira ? "Sim" : "Não").append("\n");
        info.append("Multiplayer Online: ").append(temOnline ? "Sim" : "Não").append("\n");
        info.append("Máx. Jogadores: ").append(numeroJogadores).append(" (").append(getRecomendacaoMultiplayer()).append(")\n");
        info.append("Nível Competitivo: ").append(getNivelCompetitivo()).append(" (").append(String.format("%.1f", getValorCompetitivo())).append("/7.0)\n");
        info.append("Atualização Anual: ").append(isAtualAnual() ? "Provavelmente" : "Não usual").append("\n");
        
        if (isParaJogadorCasual()) {
            info.append("✅ Recomendado para jogadores casuais\n");
        }
        if (isParaJogadorSerio()) {
            info.append("✅ Recomendado para jogadores sérios\n");
        }
        
        return info.toString();
    }
} 