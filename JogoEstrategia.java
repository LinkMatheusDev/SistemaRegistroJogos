/**
 * Classe especializada para Jogos de Estratégia
 * Herda de Jogo e adiciona características específicas de jogos de estratégia
 */
public class JogoEstrategia extends Jogo {
    private String tipoEstrategia; // "RTS", "TBS", "4X", "Tower Defense", "Grand Strategy"
    private int complexidadeAprendizado; // 1-10 (dificuldade para aprender)
    private boolean temCampanha;
    private boolean temEditor; // Editor de mapas/cenários
    private int numeroFaccoes; // Número de civilizações/facções diferentes
    private boolean temIA; // Se tem inteligência artificial
    private String perspectiva; // "Top-down", "Isométrica", "3D"
    
    // Construtor padrão
    public JogoEstrategia() {
        super();
        setGenero("Estratégia");
    }
    
    // Construtor completo
    public JogoEstrategia(int id, String nome, double preco, String desenvolvedor, String dataCreacao,
                          String tipoEstrategia, int complexidadeAprendizado, boolean temCampanha,
                          boolean temEditor, int numeroFaccoes, boolean temIA, String perspectiva) {
        super(id, nome, preco, "Estratégia", desenvolvedor, dataCreacao);
        this.tipoEstrategia = tipoEstrategia;
        this.complexidadeAprendizado = complexidadeAprendizado;
        this.temCampanha = temCampanha;
        this.temEditor = temEditor;
        this.numeroFaccoes = numeroFaccoes;
        this.temIA = temIA;
        this.perspectiva = perspectiva;
    }
    
    // Construtor sem ID
    public JogoEstrategia(String nome, double preco, String desenvolvedor,
                          String tipoEstrategia, int complexidadeAprendizado, boolean temCampanha,
                          boolean temEditor, int numeroFaccoes, boolean temIA, String perspectiva) {
        super(nome, preco, "Estratégia", desenvolvedor);
        this.tipoEstrategia = tipoEstrategia;
        this.complexidadeAprendizado = complexidadeAprendizado;
        this.temCampanha = temCampanha;
        this.temEditor = temEditor;
        this.numeroFaccoes = numeroFaccoes;
        this.temIA = temIA;
        this.perspectiva = perspectiva;
    }
    
    // Getters e Setters específicos
    public String getTipoEstrategia() { return tipoEstrategia; }
    public void setTipoEstrategia(String tipoEstrategia) { this.tipoEstrategia = tipoEstrategia; }
    
    public int getComplexidadeAprendizado() { return complexidadeAprendizado; }
    public void setComplexidadeAprendizado(int complexidadeAprendizado) { 
        if (complexidadeAprendizado < 1 || complexidadeAprendizado > 10) {
            throw new IllegalArgumentException("Complexidade deve estar entre 1 e 10");
        }
        this.complexidadeAprendizado = complexidadeAprendizado; 
    }
    
    public boolean isTemCampanha() { return temCampanha; }
    public void setTemCampanha(boolean temCampanha) { this.temCampanha = temCampanha; }
    
    public boolean isTemEditor() { return temEditor; }
    public void setTemEditor(boolean temEditor) { this.temEditor = temEditor; }
    
    public int getNumeroFaccoes() { return numeroFaccoes; }
    public void setNumeroFaccoes(int numeroFaccoes) { 
        if (numeroFaccoes < 1) {
            throw new IllegalArgumentException("Deve ter pelo menos 1 facção");
        }
        this.numeroFaccoes = numeroFaccoes; 
    }
    
    public boolean isTemIA() { return temIA; }
    public void setTemIA(boolean temIA) { this.temIA = temIA; }
    
    public String getPerspectiva() { return perspectiva; }
    public void setPerspectiva(String perspectiva) { this.perspectiva = perspectiva; }
    
    // Métodos específicos de negócio
    public String getNivelComplexidade() {
        if (complexidadeAprendizado <= 3) return "Iniciante";
        if (complexidadeAprendizado <= 6) return "Intermediário";
        if (complexidadeAprendizado <= 8) return "Avançado";
        return "Expert";
    }
    
    public boolean isTempoReal() {
        return "RTS".equals(tipoEstrategia);
    }
    
    public boolean isBaseadoTurnos() {
        return "TBS".equals(tipoEstrategia) || "4X".equals(tipoEstrategia);
    }
    
    public String getTempoJogoEstimado() {
        if ("Tower Defense".equals(tipoEstrategia)) return "30min - 2h por partida";
        if ("RTS".equals(tipoEstrategia)) return "1-3h por partida";
        if ("TBS".equals(tipoEstrategia)) return "2-6h por partida";
        if ("4X".equals(tipoEstrategia)) return "10-50h por campanha";
        if ("Grand Strategy".equals(tipoEstrategia)) return "50-200h por campanha";
        return "Variável";
    }
    
    public double getValorReplayability() {
        double pontos = 0.0;
        if (temEditor) pontos += 2.0;
        if (numeroFaccoes > 5) pontos += 1.5;
        if (numeroFaccoes > 10) pontos += 1.0; // Bonus extra
        if (temIA && complexidadeAprendizado > 6) pontos += 1.5;
        if ("4X".equals(tipoEstrategia) || "Grand Strategy".equals(tipoEstrategia)) pontos += 2.0;
        if (!temCampanha) pontos -= 1.0; // Penaliza se não tem campanha
        
        return Math.max(0.0, pontos); // Não pode ser negativo
    }
    
    public String getReplayabilityCategoria() {
        double valor = getValorReplayability();
        if (valor <= 2.0) return "Baixa";
        if (valor <= 4.0) return "Média";
        if (valor <= 6.0) return "Alta";
        return "Infinita";
    }
    
    public boolean isRecomendadoParaIniciantes() {
        return complexidadeAprendizado <= 4 && temCampanha;
    }
    
    public boolean isRecomendadoParaVeteranos() {
        return complexidadeAprendizado >= 7 && (numeroFaccoes >= 5 || temEditor);
    }
    
    public String getRecomendacaoPublico() {
        StringBuilder rec = new StringBuilder();
        
        if (isRecomendadoParaIniciantes()) {
            rec.append("Iniciantes em estratégia, ");
        }
        if (isRecomendadoParaVeteranos()) {
            rec.append("Veteranos experientes, ");
        }
        if (temEditor) {
            rec.append("Criadores de conteúdo, ");
        }
        if ("4X".equals(tipoEstrategia) || "Grand Strategy".equals(tipoEstrategia)) {
            rec.append("Fãs de jogos longos, ");
        }
        if ("RTS".equals(tipoEstrategia)) {
            rec.append("Jogadores que gostam de ação rápida, ");
        }
        if (numeroFaccoes > 8) {
            rec.append("Jogadores que gostam de variedade, ");
        }
        
        String resultado = rec.toString();
        if (resultado.endsWith(", ")) {
            resultado = resultado.substring(0, resultado.length() - 2);
        }
        
        return resultado.isEmpty() ? "Amantes de estratégia em geral" : resultado;
    }
    
    @Override
    public String toString() {
        return String.format("JogoEstrategia{%s, tipo='%s', complexidade=%d, facções=%d, replayabilidade='%s'}", 
                           super.toString(), tipoEstrategia, complexidadeAprendizado, numeroFaccoes, getReplayabilityCategoria());
    }
    
    // Método para obter informações específicas do jogo de estratégia
    public String getInformacoesEstrategia() {
        StringBuilder info = new StringBuilder();
        info.append("=== INFORMAÇÕES DE ESTRATÉGIA ===\n");
        info.append("Tipo: ").append(tipoEstrategia).append(isTempoReal() ? " (Tempo Real)" : isBaseadoTurnos() ? " (Baseado em Turnos)" : "").append("\n");
        info.append("Complexidade: ").append(complexidadeAprendizado).append("/10 (").append(getNivelComplexidade()).append(")\n");
        info.append("Campanha: ").append(temCampanha ? "Sim" : "Não").append("\n");
        info.append("Editor de Mapas: ").append(temEditor ? "Sim" : "Não").append("\n");
        info.append("Número de Facções: ").append(numeroFaccoes).append("\n");
        info.append("IA Disponível: ").append(temIA ? "Sim" : "Não").append("\n");
        info.append("Perspectiva: ").append(perspectiva).append("\n");
        info.append("Tempo Estimado: ").append(getTempoJogoEstimado()).append("\n");
        info.append("Replayabilidade: ").append(getReplayabilityCategoria()).append(" (").append(String.format("%.1f", getValorReplayability())).append("/8.0)\n");
        info.append("Recomendado para: ").append(getRecomendacaoPublico()).append("\n");
        
        if (isRecomendadoParaIniciantes()) {
            info.append("✅ Bom para iniciantes\n");
        }
        if (isRecomendadoParaVeteranos()) {
            info.append("✅ Desafiador para veteranos\n");
        }
        
        return info.toString();
    }
} 