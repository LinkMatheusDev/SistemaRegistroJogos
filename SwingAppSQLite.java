import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Properties;

/**
 * Interface gráfica usando Swing + SQLite
 * Versão standalone - não depende de outras classes
 * Funciona com qualquer JDK 8+ e SQLite JDBC
 * ADAPTADA PARA O SCHEMA REAL DO BANCO EXISTENTE
 * Com funcionalidades de CRUD completo: Create, Read, Update, Delete
 */
public class SwingAppSQLite extends JFrame {
    
    private static final String DB_URL = "jdbc:sqlite:jogos.db";
    
    private JTable tabelaJogos;
    private DefaultTableModel modeloTabela;
    
    // Componentes do formulário - CAMPOS BÁSICOS E ESPECIALIZADOS
    private JTextField campoNome;
    private JTextField campoPreco;
    private JTextField campoGenero;
    private JTextField campoDesenvolvedor;
    private JTextField campoBusca;
    private JComboBox<String> comboTipoJogo;
    
    // Painéis específicos para cada tipo
    private JPanel painelCamposEspecificos;
    private CardLayout cardLayoutEspecificos;
    
    // Campos específicos para JogoAcao
    private JSpinner spinnerNivelViolencia;
    private JCheckBox checkMultijogador;
    private JComboBox<String> comboTipoControle;
    private JComboBox<Integer> comboClassificacaoEtaria;
    
    // Campos específicos para JogoRPG
    private JSpinner spinnerHorasGameplay;
    private JComboBox<String> comboTipoRPG;
    private JCheckBox checkCustomizacao;
    private JSpinner spinnerNumeroClasses;
    private JCheckBox checkHistoriaRamificada;
    
    // Campos específicos para JogoEsporte
    private JTextField campoModalidadeEsportiva;
    private JCheckBox checkLicenciamento;
    private JComboBox<String> comboModoJogo;
    private JCheckBox checkCarreira;
    private JCheckBox checkOnline;
    private JSpinner spinnerNumeroJogadores;
    
    // Campos específicos para JogoEstrategia
    private JComboBox<String> comboTipoEstrategia;
    private JSpinner spinnerComplexidade;
    private JCheckBox checkCampanha;
    private JCheckBox checkEditor;
    private JSpinner spinnerNumeroFaccoes;
    private JCheckBox checkIA;
    private JComboBox<String> comboPerspectiva;
    
    // Variável para controlar se estamos editando
    private int jogoEditandoId = -1;
    
    public SwingAppSQLite() {
        inicializarBanco();
        configurarJanela();
        inicializarComponentes();
        carregarDados();
        setVisible(true);
    }
    
    private void inicializarBanco() {
        try {
            // Configurar SQLite
            Properties props = new Properties();
            props.setProperty("foreign_keys", "true");
            props.setProperty("journal_mode", "WAL");
            props.setProperty("synchronous", "NORMAL");
            
            try (Connection conn = DriverManager.getConnection(DB_URL, props)) {
                // Verificar se a tabela existe
                String checkTable = """
                    SELECT name FROM sqlite_master WHERE type='table' AND name='jogos'
                """;
                
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(checkTable)) {
                    
                    if (rs.next()) {
                        System.out.println("✅ Tabela 'jogos' encontrada!");
                        // Verificar se precisa adicionar colunas novas
                        adicionarColunasEspecializadas(conn);
                    } else {
                        // Criar tabela com schema completo para classes especializadas
                        String createTable = """
                            CREATE TABLE jogos (
                                id INTEGER PRIMARY KEY AUTOINCREMENT,
                                nome TEXT NOT NULL,
                                preco REAL NOT NULL,
                                genero TEXT,
                                desenvolvedor TEXT,
                                tipo_classe TEXT DEFAULT 'Jogo',
                                
                                -- Campos para JogoAcao
                                nivel_violencia INTEGER,
                                tem_multijogador BOOLEAN,
                                tipo_controle TEXT,
                                classificacao_etaria INTEGER,
                                
                                -- Campos para JogoRPG
                                horas_gameplay INTEGER,
                                tipo_rpg TEXT,
                                tem_customizacao BOOLEAN,
                                numero_classes INTEGER,
                                tem_historia_ramificada BOOLEAN,
                                
                                -- Campos para JogoEsporte
                                modalidade_esportiva TEXT,
                                tem_licenciamento BOOLEAN,
                                modo_jogo TEXT,
                                tem_carreira BOOLEAN,
                                tem_online BOOLEAN,
                                numero_jogadores INTEGER,
                                
                                -- Campos para JogoEstrategia
                                tipo_estrategia TEXT,
                                complexidade_aprendizado INTEGER,
                                tem_campanha BOOLEAN,
                                tem_editor BOOLEAN,
                                numero_faccoes INTEGER,
                                tem_ia BOOLEAN,
                                perspectiva TEXT,
                                
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                            )
                        """;
                        stmt.execute(createTable);
                        System.out.println("✅ Tabela 'jogos' criada com suporte a classes especializadas!");
                    }
                }
                
                System.out.println("✅ Banco SQLite inicializado com sucesso!");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Erro ao inicializar SQLite: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Erro ao inicializar banco de dados: " + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
    
    private void adicionarColunasEspecializadas(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            // Lista de colunas a adicionar se não existirem
            String[] novasColunas = {
                "ALTER TABLE jogos ADD COLUMN tipo_classe TEXT DEFAULT 'Jogo'",
                
                // JogoAcao
                "ALTER TABLE jogos ADD COLUMN nivel_violencia INTEGER",
                "ALTER TABLE jogos ADD COLUMN tem_multijogador BOOLEAN",
                "ALTER TABLE jogos ADD COLUMN tipo_controle TEXT",
                "ALTER TABLE jogos ADD COLUMN classificacao_etaria INTEGER",
                
                // JogoRPG
                "ALTER TABLE jogos ADD COLUMN horas_gameplay INTEGER",
                "ALTER TABLE jogos ADD COLUMN tipo_rpg TEXT",
                "ALTER TABLE jogos ADD COLUMN tem_customizacao BOOLEAN",
                "ALTER TABLE jogos ADD COLUMN numero_classes INTEGER",
                "ALTER TABLE jogos ADD COLUMN tem_historia_ramificada BOOLEAN",
                
                // JogoEsporte
                "ALTER TABLE jogos ADD COLUMN modalidade_esportiva TEXT",
                "ALTER TABLE jogos ADD COLUMN tem_licenciamento BOOLEAN",
                "ALTER TABLE jogos ADD COLUMN modo_jogo TEXT",
                "ALTER TABLE jogos ADD COLUMN tem_carreira BOOLEAN",
                "ALTER TABLE jogos ADD COLUMN tem_online BOOLEAN",
                "ALTER TABLE jogos ADD COLUMN numero_jogadores INTEGER",
                
                // JogoEstrategia
                "ALTER TABLE jogos ADD COLUMN tipo_estrategia TEXT",
                "ALTER TABLE jogos ADD COLUMN complexidade_aprendizado INTEGER",
                "ALTER TABLE jogos ADD COLUMN tem_campanha BOOLEAN",
                "ALTER TABLE jogos ADD COLUMN tem_editor BOOLEAN",
                "ALTER TABLE jogos ADD COLUMN numero_faccoes INTEGER",
                "ALTER TABLE jogos ADD COLUMN tem_ia BOOLEAN",
                "ALTER TABLE jogos ADD COLUMN perspectiva TEXT"
            };
            
            for (String coluna : novasColunas) {
                try {
                    stmt.execute(coluna);
                } catch (SQLException e) {
                    // Ignorar erro se coluna já existe
                    if (!e.getMessage().contains("duplicate column name")) {
                        System.err.println("Aviso ao adicionar coluna: " + e.getMessage());
                    }
                }
            }
            
            System.out.println("✅ Colunas especializadas verificadas/adicionadas!");
            
        } catch (SQLException e) {
            System.err.println("❌ Erro ao adicionar colunas especializadas: " + e.getMessage());
        }
    }
    
    private void configurarJanela() {
        setTitle("🎮 Sistema de Registro de Jogos v3.0 - Swing + SQLite (CRUD Completo)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        
        // Tentar definir look and feel nativo do sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Ignorar se não conseguir - usar padrão
        }
    }
    
    private void inicializarComponentes() {
        setLayout(new BorderLayout());
        
        // Painel principal com abas
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Aba de formulário
        JPanel painelFormulario = criarPainelFormulario();
        tabbedPane.addTab("📝 Cadastrar/Editar Jogo", painelFormulario);
        
        // Aba de lista
        JPanel painelLista = criarPainelLista();
        tabbedPane.addTab("📋 Lista de Jogos", painelLista);
        
        // Aba de busca
        JPanel painelBusca = criarPainelBusca();
        tabbedPane.addTab("🔍 Buscar Jogo", painelBusca);
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Status bar
        JLabel statusBar = new JLabel("💾 SQLite - CRUD Completo: CREATE, READ, UPDATE, DELETE");
        statusBar.setBorder(new EmptyBorder(5, 10, 5, 10));
        statusBar.setOpaque(true);
        statusBar.setBackground(new Color(240, 240, 240));
        add(statusBar, BorderLayout.SOUTH);
    }
    
    private JPanel criarPainelFormulario() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Painel principal com scroll
        JPanel painelPrincipal = new JPanel(new GridBagLayout());
        JScrollPane scrollPane = new JScrollPane(painelPrincipal);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(800, 500));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Campos básicos
        campoNome = new JTextField(30);
        campoPreco = new JTextField(30);
        campoGenero = new JTextField(30);
        campoDesenvolvedor = new JTextField(30);
        
        // Combo para tipo de jogo
        comboTipoJogo = new JComboBox<>(new String[]{"Jogo Básico", "Jogo de Ação", "Jogo de RPG", "Jogo de Esporte", "Jogo de Estratégia"});
        comboTipoJogo.addActionListener(e -> atualizarCamposEspecificos());
        
        // Layout dos campos básicos
        adicionarCampo(painelPrincipal, gbc, "Nome do Jogo:", campoNome, 0);
        adicionarCampo(painelPrincipal, gbc, "Preço (R$):", campoPreco, 1);
        adicionarCampo(painelPrincipal, gbc, "Gênero:", campoGenero, 2);
        adicionarCampo(painelPrincipal, gbc, "Desenvolvedor:", campoDesenvolvedor, 3);
        adicionarCampoCombo(painelPrincipal, gbc, "Tipo de Jogo:", comboTipoJogo, 4);
        
        // Criar painéis específicos com CardLayout
        criarPaineisEspecificos();
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        painelPrincipal.add(painelCamposEspecificos, gbc);
        
        painel.add(scrollPane, BorderLayout.CENTER);
        
        // Painel de botões
        JPanel painelBotoes = new JPanel(new FlowLayout());
        
        JButton botaoSalvar = new JButton("💾 Salvar Jogo");
        botaoSalvar.setPreferredSize(new Dimension(150, 40));
        botaoSalvar.setBackground(new Color(0, 150, 0));
        botaoSalvar.setForeground(Color.WHITE);
        botaoSalvar.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        botaoSalvar.addActionListener(e -> salvarOuAtualizarJogo());
        
        JButton botaoLimpar = new JButton("🧹 Limpar");
        botaoLimpar.setPreferredSize(new Dimension(150, 40));
        botaoLimpar.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        botaoLimpar.addActionListener(e -> limparFormulario());
        
        JButton botaoCancelar = new JButton("❌ Cancelar Edição");
        botaoCancelar.setPreferredSize(new Dimension(150, 40));
        botaoCancelar.setBackground(new Color(200, 100, 0));
        botaoCancelar.setForeground(Color.WHITE);
        botaoCancelar.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        botaoCancelar.addActionListener(e -> cancelarEdicao());
        
        painelBotoes.add(botaoSalvar);
        painelBotoes.add(botaoLimpar);
        painelBotoes.add(botaoCancelar);
        
        painel.add(painelBotoes, BorderLayout.SOUTH);
        
        return painel;
    }
    
    private void criarPaineisEspecificos() {
        cardLayoutEspecificos = new CardLayout();
        painelCamposEspecificos = new JPanel(cardLayoutEspecificos);
        painelCamposEspecificos.setBorder(BorderFactory.createTitledBorder("Campos Específicos"));
        
        // Painel vazio para Jogo básico
        JPanel painelVazio = new JPanel();
        painelVazio.add(new JLabel("Nenhum campo específico para Jogo básico"));
        painelCamposEspecificos.add(painelVazio, "Jogo");
        
        // Painel para JogoAcao
        painelCamposEspecificos.add(criarPainelJogoAcao(), "JogoAcao");
        
        // Painel para JogoRPG
        painelCamposEspecificos.add(criarPainelJogoRPG(), "JogoRPG");
        
        // Painel para JogoEsporte
        painelCamposEspecificos.add(criarPainelJogoEsporte(), "JogoEsporte");
        
        // Painel para JogoEstrategia
        painelCamposEspecificos.add(criarPainelJogoEstrategia(), "JogoEstrategia");
    }
    
    private JPanel criarPainelJogoAcao() {
        JPanel painel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Inicializar componentes
        spinnerNivelViolencia = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        checkMultijogador = new JCheckBox();
        comboTipoControle = new JComboBox<>(new String[]{"Keyboard", "Controller", "Both"});
        comboClassificacaoEtaria = new JComboBox<>(new Integer[]{0, 10, 12, 14, 16, 18});
        
        adicionarCampoSpinner(painel, gbc, "Nível de Violência (1-10):", spinnerNivelViolencia, 0);
        adicionarCampoCheckbox(painel, gbc, "Tem Multijogador:", checkMultijogador, 1);
        adicionarCampoCombo(painel, gbc, "Tipo de Controle:", comboTipoControle, 2);
        adicionarCampoCombo(painel, gbc, "Classificação Etária:", comboClassificacaoEtaria, 3);
        
        return painel;
    }
    
    private JPanel criarPainelJogoRPG() {
        JPanel painel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        spinnerHorasGameplay = new JSpinner(new SpinnerNumberModel(0, 0, 500, 1));
        comboTipoRPG = new JComboBox<>(new String[]{"JRPG", "Western RPG", "Action RPG", "Tactical RPG"});
        checkCustomizacao = new JCheckBox();
        spinnerNumeroClasses = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
        checkHistoriaRamificada = new JCheckBox();
        
        adicionarCampoSpinner(painel, gbc, "Horas de Gameplay:", spinnerHorasGameplay, 0);
        adicionarCampoCombo(painel, gbc, "Tipo de RPG:", comboTipoRPG, 1);
        adicionarCampoCheckbox(painel, gbc, "Tem Customização:", checkCustomizacao, 2);
        adicionarCampoSpinner(painel, gbc, "Número de Classes:", spinnerNumeroClasses, 3);
        adicionarCampoCheckbox(painel, gbc, "História Ramificada:", checkHistoriaRamificada, 4);
        
        return painel;
    }
    
    private JPanel criarPainelJogoEsporte() {
        JPanel painel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        campoModalidadeEsportiva = new JTextField(20);
        checkLicenciamento = new JCheckBox();
        comboModoJogo = new JComboBox<>(new String[]{"Simulação", "Arcade", "Híbrido"});
        checkCarreira = new JCheckBox();
        checkOnline = new JCheckBox();
        spinnerNumeroJogadores = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        
        adicionarCampo(painel, gbc, "Modalidade Esportiva:", campoModalidadeEsportiva, 0);
        adicionarCampoCheckbox(painel, gbc, "Tem Licenciamento:", checkLicenciamento, 1);
        adicionarCampoCombo(painel, gbc, "Modo de Jogo:", comboModoJogo, 2);
        adicionarCampoCheckbox(painel, gbc, "Tem Carreira:", checkCarreira, 3);
        adicionarCampoCheckbox(painel, gbc, "Tem Online:", checkOnline, 4);
        adicionarCampoSpinner(painel, gbc, "Número de Jogadores:", spinnerNumeroJogadores, 5);
        
        return painel;
    }
    
    private JPanel criarPainelJogoEstrategia() {
        JPanel painel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        comboTipoEstrategia = new JComboBox<>(new String[]{"RTS", "TBS", "4X", "Tower Defense", "Grand Strategy"});
        spinnerComplexidade = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        checkCampanha = new JCheckBox();
        checkEditor = new JCheckBox();
        spinnerNumeroFaccoes = new JSpinner(new SpinnerNumberModel(1, 1, 50, 1));
        checkIA = new JCheckBox();
        comboPerspectiva = new JComboBox<>(new String[]{"Top-down", "Isométrica", "3D"});
        
        adicionarCampoCombo(painel, gbc, "Tipo de Estratégia:", comboTipoEstrategia, 0);
        adicionarCampoSpinner(painel, gbc, "Complexidade (1-10):", spinnerComplexidade, 1);
        adicionarCampoCheckbox(painel, gbc, "Tem Campanha:", checkCampanha, 2);
        adicionarCampoCheckbox(painel, gbc, "Tem Editor:", checkEditor, 3);
        adicionarCampoSpinner(painel, gbc, "Número de Facções:", spinnerNumeroFaccoes, 4);
        adicionarCampoCheckbox(painel, gbc, "Tem IA:", checkIA, 5);
        adicionarCampoCombo(painel, gbc, "Perspectiva:", comboPerspectiva, 6);
        
        return painel;
    }
    
    private void atualizarCamposEspecificos() {
        String tipoAmigavel = (String) comboTipoJogo.getSelectedItem();
        String tipoClasse = converterParaNomeClasse(tipoAmigavel);
        cardLayoutEspecificos.show(painelCamposEspecificos, tipoClasse);
    }
    
    private String converterParaNomeClasse(String tipoAmigavel) {
        switch (tipoAmigavel) {
            case "Jogo de Ação": return "JogoAcao";
            case "Jogo de RPG": return "JogoRPG";
            case "Jogo de Esporte": return "JogoEsporte";
            case "Jogo de Estratégia": return "JogoEstrategia";
            default: return "Jogo";
        }
    }
    
    private String converterParaNomeAmigavel(String tipoClasse) {
        switch (tipoClasse) {
            case "JogoAcao": return "Jogo de Ação";
            case "JogoRPG": return "Jogo de RPG";
            case "JogoEsporte": return "Jogo de Esporte";
            case "JogoEstrategia": return "Jogo de Estratégia";
            default: return "Jogo Básico";
        }
    }
    
    private JPanel criarPainelBusca() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Painel superior com busca
        JPanel painelBuscaSuper = new JPanel(new FlowLayout());
        painelBuscaSuper.add(new JLabel("🔍 Buscar por Nome:"));
        
        campoBusca = new JTextField(20);
        painelBuscaSuper.add(campoBusca);
        
        JButton botaoBuscar = new JButton("Buscar");
        botaoBuscar.addActionListener(e -> buscarJogoPorNome());
        painelBuscaSuper.add(botaoBuscar);
        
        painel.add(painelBuscaSuper, BorderLayout.NORTH);
        
        // Área de resultado
        JTextArea areaResultado = new JTextArea(15, 50);
        areaResultado.setEditable(false);
        areaResultado.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        areaResultado.setBorder(BorderFactory.createTitledBorder("Resultado da Busca"));
        
        JScrollPane scrollResultado = new JScrollPane(areaResultado);
        painel.add(scrollResultado, BorderLayout.CENTER);
        
        return painel;
    }
    
    private void adicionarCampo(JPanel painel, GridBagConstraints gbc, String label, JTextField campo, int linha) {
        gbc.gridx = 0;
        gbc.gridy = linha;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.weighty = 0;
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        painel.add(labelComponent, gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        campo.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        painel.add(campo, gbc);
    }
    
    private void adicionarCampoCombo(JPanel painel, GridBagConstraints gbc, String label, JComboBox combo, int linha) {
        gbc.gridx = 0;
        gbc.gridy = linha;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.weighty = 0;
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        painel.add(labelComponent, gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        combo.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        painel.add(combo, gbc);
    }
    
    private void adicionarCampoSpinner(JPanel painel, GridBagConstraints gbc, String label, JSpinner spinner, int linha) {
        gbc.gridx = 0;
        gbc.gridy = linha;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.weighty = 0;
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        painel.add(labelComponent, gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        spinner.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        painel.add(spinner, gbc);
    }
    
    private void adicionarCampoCheckbox(JPanel painel, GridBagConstraints gbc, String label, JCheckBox check, int linha) {
        gbc.gridx = 0;
        gbc.gridy = linha;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.weighty = 0;
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        painel.add(labelComponent, gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        check.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        painel.add(check, gbc);
    }
    
    private void carregarDados() {
        try {
            // SQL incluindo tipo_classe
            String sql = """
                SELECT id, nome, preco, genero, desenvolvedor, tipo_classe, created_at
                FROM jogos ORDER BY nome
            """;
            
            try (Connection conn = DriverManager.getConnection(DB_URL);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                
                // Limpar tabela
                modeloTabela.setRowCount(0);
                
                // Adicionar jogos à tabela
                while (rs.next()) {
                    Object[] linha = {
                        rs.getInt("id"),
                        rs.getString("nome") != null ? rs.getString("nome") : "",
                        String.format("R$ %.2f", rs.getDouble("preco")),
                        rs.getString("genero") != null ? rs.getString("genero") : "",
                        rs.getString("desenvolvedor") != null ? rs.getString("desenvolvedor") : "",
                        rs.getString("tipo_classe") != null ? rs.getString("tipo_classe") : "Jogo",
                        rs.getString("created_at") != null ? rs.getString("created_at") : ""
                    };
                    modeloTabela.addRow(linha);
                }
                
                System.out.println("✅ Carregados " + modeloTabela.getRowCount() + " jogos");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Erro ao carregar dados: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Erro ao carregar dados: " + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void salvarOuAtualizarJogo() {
        try {
            // Validar campos obrigatórios
            if (campoNome.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nome é obrigatório!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (campoPreco.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preço é obrigatório!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String tipoAmigavel = (String) comboTipoJogo.getSelectedItem();
            String tipoJogo = converterParaNomeClasse(tipoAmigavel);
            
            // Criar instância da classe apropriada
            Jogo jogo = criarInstanciaJogo(tipoJogo);
            
            if (jogo == null) {
                JOptionPane.showMessageDialog(this, "Erro ao criar instância do jogo!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Preencher dados básicos
            jogo.setNome(campoNome.getText().trim());
            jogo.setPreco(Double.parseDouble(campoPreco.getText().trim()));
            jogo.setGenero(campoGenero.getText().trim());
            jogo.setDesenvolvedor(campoDesenvolvedor.getText().trim());
            
            // Salvar no banco
            salvarJogoNoBanco(jogo, tipoJogo);
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Preço deve ser um número válido!", 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            System.err.println("❌ Erro ao salvar/atualizar: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Erro ao salvar/atualizar: " + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private Jogo criarInstanciaJogo(String tipoJogo) {
        switch (tipoJogo) {
            case "JogoAcao":
                JogoAcao jogoAcao = new JogoAcao();
                jogoAcao.setNivelViolencia((Integer) spinnerNivelViolencia.getValue());
                jogoAcao.setTemMultijogador(checkMultijogador.isSelected());
                jogoAcao.setTipoControle((String) comboTipoControle.getSelectedItem());
                jogoAcao.setClassificacaoEtaria((Integer) comboClassificacaoEtaria.getSelectedItem());
                return jogoAcao;
                
            case "JogoRPG":
                JogoRPG jogoRPG = new JogoRPG();
                jogoRPG.setHorasGameplay((Integer) spinnerHorasGameplay.getValue());
                jogoRPG.setTipoRPG((String) comboTipoRPG.getSelectedItem());
                jogoRPG.setTemCustomizacao(checkCustomizacao.isSelected());
                jogoRPG.setNumeroClasses((Integer) spinnerNumeroClasses.getValue());
                jogoRPG.setTemHistoriaRamificada(checkHistoriaRamificada.isSelected());
                return jogoRPG;
                
            case "JogoEsporte":
                JogoEsporte jogoEsporte = new JogoEsporte();
                jogoEsporte.setModalidadeEsportiva(campoModalidadeEsportiva.getText().trim());
                jogoEsporte.setTemLicenciamento(checkLicenciamento.isSelected());
                jogoEsporte.setModoJogo((String) comboModoJogo.getSelectedItem());
                jogoEsporte.setTemCarreira(checkCarreira.isSelected());
                jogoEsporte.setTemOnline(checkOnline.isSelected());
                jogoEsporte.setNumeroJogadores((Integer) spinnerNumeroJogadores.getValue());
                return jogoEsporte;
                
            case "JogoEstrategia":
                JogoEstrategia jogoEstrategia = new JogoEstrategia();
                jogoEstrategia.setTipoEstrategia((String) comboTipoEstrategia.getSelectedItem());
                jogoEstrategia.setComplexidadeAprendizado((Integer) spinnerComplexidade.getValue());
                jogoEstrategia.setTemCampanha(checkCampanha.isSelected());
                jogoEstrategia.setTemEditor(checkEditor.isSelected());
                jogoEstrategia.setNumeroFaccoes((Integer) spinnerNumeroFaccoes.getValue());
                jogoEstrategia.setTemIA(checkIA.isSelected());
                jogoEstrategia.setPerspectiva((String) comboPerspectiva.getSelectedItem());
                return jogoEstrategia;
                
            default:
                return new Jogo(); // Jogo básico
        }
    }
    
    private void salvarJogoNoBanco(Jogo jogo, String tipoJogo) throws SQLException {
        String sql;
        if (jogoEditandoId == -1) {
            // Inserir novo jogo
            sql = """
                INSERT INTO jogos (nome, preco, genero, desenvolvedor, tipo_classe,
                                 nivel_violencia, tem_multijogador, tipo_controle, classificacao_etaria,
                                 horas_gameplay, tipo_rpg, tem_customizacao, numero_classes, tem_historia_ramificada,
                                 modalidade_esportiva, tem_licenciamento, modo_jogo, tem_carreira, tem_online, numero_jogadores,
                                 tipo_estrategia, complexidade_aprendizado, tem_campanha, tem_editor, numero_faccoes, tem_ia, perspectiva)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        } else {
            // Atualizar jogo existente
            sql = """
                UPDATE jogos SET nome = ?, preco = ?, genero = ?, desenvolvedor = ?, tipo_classe = ?,
                               nivel_violencia = ?, tem_multijogador = ?, tipo_controle = ?, classificacao_etaria = ?,
                               horas_gameplay = ?, tipo_rpg = ?, tem_customizacao = ?, numero_classes = ?, tem_historia_ramificada = ?,
                               modalidade_esportiva = ?, tem_licenciamento = ?, modo_jogo = ?, tem_carreira = ?, tem_online = ?, numero_jogadores = ?,
                               tipo_estrategia = ?, complexidade_aprendizado = ?, tem_campanha = ?, tem_editor = ?, numero_faccoes = ?, tem_ia = ?, perspectiva = ?
                WHERE id = ?
            """;
        }
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Parâmetros básicos
            pstmt.setString(1, jogo.getNome());
            pstmt.setDouble(2, jogo.getPreco());
            pstmt.setString(3, jogo.getGenero());
            pstmt.setString(4, jogo.getDesenvolvedor());
            pstmt.setString(5, tipoJogo);
            
            // Parâmetros específicos - usar valores das instâncias específicas
            if (jogo instanceof JogoAcao) {
                JogoAcao ja = (JogoAcao) jogo;
                pstmt.setInt(6, ja.getNivelViolencia());
                pstmt.setBoolean(7, ja.isTemMultijogador());
                pstmt.setString(8, ja.getTipoControle());
                pstmt.setInt(9, ja.getClassificacaoEtaria());
            } else {
                pstmt.setNull(6, Types.INTEGER);
                pstmt.setNull(7, Types.BOOLEAN);
                pstmt.setNull(8, Types.VARCHAR);
                pstmt.setNull(9, Types.INTEGER);
            }
            
            if (jogo instanceof JogoRPG) {
                JogoRPG jr = (JogoRPG) jogo;
                pstmt.setInt(10, jr.getHorasGameplay());
                pstmt.setString(11, jr.getTipoRPG());
                pstmt.setBoolean(12, jr.isTemCustomizacao());
                pstmt.setInt(13, jr.getNumeroClasses());
                pstmt.setBoolean(14, jr.isTemHistoriaRamificada());
            } else {
                pstmt.setNull(10, Types.INTEGER);
                pstmt.setNull(11, Types.VARCHAR);
                pstmt.setNull(12, Types.BOOLEAN);
                pstmt.setNull(13, Types.INTEGER);
                pstmt.setNull(14, Types.BOOLEAN);
            }
            
            if (jogo instanceof JogoEsporte) {
                JogoEsporte je = (JogoEsporte) jogo;
                pstmt.setString(15, je.getModalidadeEsportiva());
                pstmt.setBoolean(16, je.isTemLicenciamento());
                pstmt.setString(17, je.getModoJogo());
                pstmt.setBoolean(18, je.isTemCarreira());
                pstmt.setBoolean(19, je.isTemOnline());
                pstmt.setInt(20, je.getNumeroJogadores());
            } else {
                pstmt.setNull(15, Types.VARCHAR);
                pstmt.setNull(16, Types.BOOLEAN);
                pstmt.setNull(17, Types.VARCHAR);
                pstmt.setNull(18, Types.BOOLEAN);
                pstmt.setNull(19, Types.BOOLEAN);
                pstmt.setNull(20, Types.INTEGER);
            }
            
            if (jogo instanceof JogoEstrategia) {
                JogoEstrategia jest = (JogoEstrategia) jogo;
                pstmt.setString(21, jest.getTipoEstrategia());
                pstmt.setInt(22, jest.getComplexidadeAprendizado());
                pstmt.setBoolean(23, jest.isTemCampanha());
                pstmt.setBoolean(24, jest.isTemEditor());
                pstmt.setInt(25, jest.getNumeroFaccoes());
                pstmt.setBoolean(26, jest.isTemIA());
                pstmt.setString(27, jest.getPerspectiva());
            } else {
                pstmt.setNull(21, Types.VARCHAR);
                pstmt.setNull(22, Types.INTEGER);
                pstmt.setNull(23, Types.BOOLEAN);
                pstmt.setNull(24, Types.BOOLEAN);
                pstmt.setNull(25, Types.INTEGER);
                pstmt.setNull(26, Types.BOOLEAN);
                pstmt.setNull(27, Types.VARCHAR);
            }
            
            if (jogoEditandoId != -1) {
                pstmt.setInt(28, jogoEditandoId);
            }
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                String mensagem = jogoEditandoId == -1 ? "✅ Jogo salvo com sucesso!" : "✅ Jogo atualizado com sucesso!";
                JOptionPane.showMessageDialog(this, 
                    mensagem, 
                    "Sucesso", 
                    JOptionPane.INFORMATION_MESSAGE);
                limparFormulario();
                carregarDados();
                jogoEditandoId = -1;
            } else {
                String mensagem = jogoEditandoId == -1 ? "❌ Erro ao salvar jogo!" : "❌ Erro ao atualizar jogo!";
                JOptionPane.showMessageDialog(this, 
                    mensagem, 
                    "Erro", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void excluirJogoSelecionado() {
        int linhaSelecionada = tabelaJogos.getSelectedRow();
        
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, 
                "Selecione um jogo para excluir!", 
                "Aviso", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (Integer) modeloTabela.getValueAt(linhaSelecionada, 0);
        String nome = (String) modeloTabela.getValueAt(linhaSelecionada, 1);
        
        int confirmacao = JOptionPane.showConfirmDialog(
            this, 
            "Tem certeza que deseja excluir o jogo \"" + nome + "\"?", 
            "Confirmar Exclusão", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirmacao == JOptionPane.YES_OPTION) {
            try {
                String sql = "DELETE FROM jogos WHERE id = ?";
                
                try (Connection conn = DriverManager.getConnection(DB_URL);
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    
                    pstmt.setInt(1, id);
                    int rowsAffected = pstmt.executeUpdate();
                    
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, 
                            "✅ Jogo excluído com sucesso!", 
                            "Sucesso", 
                            JOptionPane.INFORMATION_MESSAGE);
                        carregarDados();
                    } else {
                        JOptionPane.showMessageDialog(this, 
                            "❌ Erro ao excluir jogo!", 
                            "Erro", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
                
            } catch (SQLException e) {
                System.err.println("❌ Erro ao excluir: " + e.getMessage());
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, 
                    "Erro ao excluir: " + e.getMessage(), 
                    "Erro", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void limparFormulario() {
        campoNome.setText("");
        campoPreco.setText("");
        campoGenero.setText("");
        campoDesenvolvedor.setText("");
        comboTipoJogo.setSelectedIndex(0);
        
        // Limpar campos específicos de JogoAcao
        if (spinnerNivelViolencia != null) spinnerNivelViolencia.setValue(1);
        if (checkMultijogador != null) checkMultijogador.setSelected(false);
        if (comboTipoControle != null) comboTipoControle.setSelectedIndex(0);
        if (comboClassificacaoEtaria != null) comboClassificacaoEtaria.setSelectedIndex(0);
        
        // Limpar campos específicos de JogoRPG
        if (spinnerHorasGameplay != null) spinnerHorasGameplay.setValue(0);
        if (comboTipoRPG != null) comboTipoRPG.setSelectedIndex(0);
        if (checkCustomizacao != null) checkCustomizacao.setSelected(false);
        if (spinnerNumeroClasses != null) spinnerNumeroClasses.setValue(1);
        if (checkHistoriaRamificada != null) checkHistoriaRamificada.setSelected(false);
        
        // Limpar campos específicos de JogoEsporte
        if (campoModalidadeEsportiva != null) campoModalidadeEsportiva.setText("");
        if (checkLicenciamento != null) checkLicenciamento.setSelected(false);
        if (comboModoJogo != null) comboModoJogo.setSelectedIndex(0);
        if (checkCarreira != null) checkCarreira.setSelected(false);
        if (checkOnline != null) checkOnline.setSelected(false);
        if (spinnerNumeroJogadores != null) spinnerNumeroJogadores.setValue(1);
        
        // Limpar campos específicos de JogoEstrategia
        if (comboTipoEstrategia != null) comboTipoEstrategia.setSelectedIndex(0);
        if (spinnerComplexidade != null) spinnerComplexidade.setValue(1);
        if (checkCampanha != null) checkCampanha.setSelected(false);
        if (checkEditor != null) checkEditor.setSelected(false);
        if (spinnerNumeroFaccoes != null) spinnerNumeroFaccoes.setValue(1);
        if (checkIA != null) checkIA.setSelected(false);
        if (comboPerspectiva != null) comboPerspectiva.setSelectedIndex(0);
        
        // Atualizar painel de campos específicos
        atualizarCamposEspecificos();
    }
    
    private void buscarJogoPorNome() {
        String nomeTexto = campoBusca.getText().trim();
        
        if (nomeTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite um nome para buscar!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            String sql = "SELECT id, nome, preco, genero, desenvolvedor, created_at FROM jogos WHERE nome LIKE ? ORDER BY nome";
            
            try (Connection conn = DriverManager.getConnection(DB_URL);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                pstmt.setString(1, "%" + nomeTexto + "%"); // Busca parcial com LIKE
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    // Encontrar a área de resultado na aba de busca
                    JTabbedPane tabbedPane = (JTabbedPane) getContentPane().getComponent(0);
                    JPanel painelBusca = (JPanel) tabbedPane.getComponentAt(2); // Terceira aba
                    JScrollPane scrollPane = (JScrollPane) painelBusca.getComponent(1);
                    JTextArea areaResultado = (JTextArea) scrollPane.getViewport().getView();
                    
                    StringBuilder resultado = new StringBuilder();
                    int contador = 0;
                    
                    resultado.append("🎮 BUSCA POR NOME: \"").append(nomeTexto).append("\"\n");
                    resultado.append("=" + "=".repeat(50)).append("\n\n");
                    
                    while (rs.next()) {
                        contador++;
                        resultado.append("🎯 JOGO ").append(contador).append(":\n");
                        resultado.append("ID: ").append(rs.getInt("id")).append("\n");
                        resultado.append("Nome: ").append(rs.getString("nome")).append("\n");
                        resultado.append("Preço: R$ ").append(String.format("%.2f", rs.getDouble("preco"))).append("\n");
                        resultado.append("Gênero: ").append(rs.getString("genero") != null ? rs.getString("genero") : "N/A").append("\n");
                        resultado.append("Desenvolvedor: ").append(rs.getString("desenvolvedor") != null ? rs.getString("desenvolvedor") : "N/A").append("\n");
                        resultado.append("Data de Criação: ").append(rs.getString("created_at")).append("\n");
                        resultado.append("-".repeat(50)).append("\n\n");
                    }
                    
                    if (contador == 0) {
                        resultado.append("❌ Nenhum jogo encontrado com o nome: \"").append(nomeTexto).append("\"\n");
                        resultado.append("💡 Dica: Tente uma busca parcial (ex: 'war' para 'Call of Duty: Modern Warfare')");
                    } else {
                        resultado.append("✅ Total encontrado: ").append(contador).append(" jogo(s)");
                    }
                    
                    areaResultado.setText(resultado.toString());
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Erro ao buscar: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Erro ao buscar: " + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void editarJogoSelecionado() {
        int linhaSelecionada = tabelaJogos.getSelectedRow();
        
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, 
                "Selecione um jogo para editar!", 
                "Aviso", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Obter dados da linha selecionada
        int id = (Integer) modeloTabela.getValueAt(linhaSelecionada, 0);
        String nome = (String) modeloTabela.getValueAt(linhaSelecionada, 1);
        String precoStr = (String) modeloTabela.getValueAt(linhaSelecionada, 2);
        String genero = (String) modeloTabela.getValueAt(linhaSelecionada, 3);
        String desenvolvedor = (String) modeloTabela.getValueAt(linhaSelecionada, 4);
        
        // Remover formatação do preço (R$ XX,XX)
        String precoLimpo = precoStr.replace("R$ ", "").replace(",", ".");
        
        // Preencher campos do formulário
        campoNome.setText(nome);
        campoPreco.setText(precoLimpo);
        campoGenero.setText(genero);
        campoDesenvolvedor.setText(desenvolvedor);
        
        // Definir modo de edição
        jogoEditandoId = id;
        
        // Mudar para a aba de formulário
        JTabbedPane tabbedPane = (JTabbedPane) getContentPane().getComponent(0);
        tabbedPane.setSelectedIndex(0);
        
        JOptionPane.showMessageDialog(this, 
            "✏️ Modo de edição ativado para: " + nome, 
            "Edição", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void cancelarEdicao() {
        if (jogoEditandoId != -1) {
            jogoEditandoId = -1;
            limparFormulario();
            JOptionPane.showMessageDialog(this, 
                "❌ Edição cancelada!", 
                "Cancelado", 
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Não há edição em andamento!", 
                "Aviso", 
                JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private JPanel criarPainelLista() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Modelo da tabela - Incluir tipo de classe
        String[] colunas = {"ID", "Nome", "Preço", "Gênero", "Desenvolvedor", "Tipo", "Data Criação"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabela somente leitura
            }
        };
        
        tabelaJogos = new JTable(modeloTabela);
        tabelaJogos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaJogos.setRowHeight(30);
        tabelaJogos.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        
        // Configurar larguras das colunas
        tabelaJogos.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        tabelaJogos.getColumnModel().getColumn(1).setPreferredWidth(200); // Nome
        tabelaJogos.getColumnModel().getColumn(2).setPreferredWidth(80);  // Preço
        tabelaJogos.getColumnModel().getColumn(3).setPreferredWidth(100); // Gênero
        tabelaJogos.getColumnModel().getColumn(4).setPreferredWidth(130); // Desenvolvedor
        tabelaJogos.getColumnModel().getColumn(5).setPreferredWidth(120); // Tipo
        tabelaJogos.getColumnModel().getColumn(6).setPreferredWidth(150); // Data
        
        JScrollPane scrollPane = new JScrollPane(tabelaJogos);
        painel.add(scrollPane, BorderLayout.CENTER);
        
        // Painel de botões
        JPanel painelBotoes = new JPanel(new FlowLayout());
        
        JButton botaoAtualizar = new JButton("🔄 Atualizar");
        botaoAtualizar.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        botaoAtualizar.addActionListener(e -> carregarDados());
        painelBotoes.add(botaoAtualizar);
        
        JButton botaoEditar = new JButton("✏️ Editar");
        botaoEditar.setBackground(new Color(0, 100, 200));
        botaoEditar.setForeground(Color.WHITE);
        botaoEditar.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        botaoEditar.addActionListener(e -> editarJogoSelecionado());
        painelBotoes.add(botaoEditar);
        
        JButton botaoExcluir = new JButton("🗑️ Excluir");
        botaoExcluir.setBackground(new Color(200, 0, 0));
        botaoExcluir.setForeground(Color.WHITE);
        botaoExcluir.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        botaoExcluir.addActionListener(e -> excluirJogoSelecionado());
        painelBotoes.add(botaoExcluir);
        
        painel.add(painelBotoes, BorderLayout.SOUTH);
        
        return painel;
    }
    
    public static void main(String[] args) {
        // Configurar look and feel antes de criar a interface
        SwingUtilities.invokeLater(() -> {
            try {
                // Tentar usar look nativo do sistema
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Usar padrão se não conseguir
            }
            
            // Criar e mostrar a interface
            new SwingAppSQLite();
        });
    }
} 