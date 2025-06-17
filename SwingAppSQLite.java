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
 */
public class SwingAppSQLite extends JFrame {
    
    private static final String DB_URL = "jdbc:sqlite:jogos.db";
    
    private JTable tabelaJogos;
    private DefaultTableModel modeloTabela;
    
    // Componentes do formulário - APENAS CAMPOS QUE EXISTEM
    private JTextField campoNome;
    private JTextField campoPreco;
    private JTextField campoGenero;
    private JTextField campoDesenvolvedor;
    
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
                    } else {
                        // Criar tabela com schema mínimo se não existir
                        String createTable = """
                            CREATE TABLE jogos (
                                id INTEGER PRIMARY KEY AUTOINCREMENT,
                                nome TEXT NOT NULL,
                                preco REAL NOT NULL,
                                genero TEXT,
                                desenvolvedor TEXT,
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                            )
                        """;
                        stmt.execute(createTable);
                        System.out.println("✅ Tabela 'jogos' criada!");
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
    
    private void configurarJanela() {
        setTitle("🎮 Sistema de Registro de Jogos v2.0 - Swing + SQLite (Schema Simples)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
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
        tabbedPane.addTab("📝 Cadastrar Jogo", painelFormulario);
        
        // Aba de lista
        JPanel painelLista = criarPainelLista();
        tabbedPane.addTab("📋 Lista de Jogos", painelLista);
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Status bar
        JLabel statusBar = new JLabel("💾 SQLite - Schema Simples: ID, Nome, Preço, Gênero, Desenvolvedor");
        statusBar.setBorder(new EmptyBorder(5, 10, 5, 10));
        statusBar.setOpaque(true);
        statusBar.setBackground(new Color(240, 240, 240));
        add(statusBar, BorderLayout.SOUTH);
    }
    
    private JPanel criarPainelFormulario() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Campos do formulário - APENAS OS QUE EXISTEM
        campoNome = new JTextField(30);
        campoPreco = new JTextField(30);
        campoGenero = new JTextField(30);
        campoDesenvolvedor = new JTextField(30);
        
        // Layout dos campos
        adicionarCampo(painel, gbc, "Nome do Jogo:", campoNome, 0);
        adicionarCampo(painel, gbc, "Preço (R$):", campoPreco, 1);
        adicionarCampo(painel, gbc, "Gênero:", campoGenero, 2);
        adicionarCampo(painel, gbc, "Desenvolvedor:", campoDesenvolvedor, 3);
        
        // Painel de botões
        JPanel painelBotoes = new JPanel(new FlowLayout());
        
        JButton botaoSalvar = new JButton("💾 Salvar Jogo");
        botaoSalvar.setPreferredSize(new Dimension(150, 40));
        botaoSalvar.setBackground(new Color(0, 150, 0));
        botaoSalvar.setForeground(Color.WHITE);
        botaoSalvar.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        botaoSalvar.addActionListener(e -> salvarJogo());
        
        JButton botaoLimpar = new JButton("🧹 Limpar");
        botaoLimpar.setPreferredSize(new Dimension(150, 40));
        botaoLimpar.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        botaoLimpar.addActionListener(e -> limparFormulario());
        
        painelBotoes.add(botaoSalvar);
        painelBotoes.add(botaoLimpar);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        painel.add(painelBotoes, gbc);
        
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
    
    private JPanel criarPainelLista() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Modelo da tabela - APENAS COLUNAS QUE EXISTEM
        String[] colunas = {"ID", "Nome", "Preço", "Gênero", "Desenvolvedor", "Data Criação"};
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
        tabelaJogos.getColumnModel().getColumn(1).setPreferredWidth(250); // Nome
        tabelaJogos.getColumnModel().getColumn(2).setPreferredWidth(100); // Preço
        tabelaJogos.getColumnModel().getColumn(3).setPreferredWidth(120); // Gênero
        tabelaJogos.getColumnModel().getColumn(4).setPreferredWidth(150); // Desenvolvedor
        tabelaJogos.getColumnModel().getColumn(5).setPreferredWidth(150); // Data
        
        JScrollPane scrollPane = new JScrollPane(tabelaJogos);
        painel.add(scrollPane, BorderLayout.CENTER);
        
        // Painel de botões
        JPanel painelBotoes = new JPanel(new FlowLayout());
        
        JButton botaoAtualizar = new JButton("🔄 Atualizar");
        botaoAtualizar.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        botaoAtualizar.addActionListener(e -> carregarDados());
        painelBotoes.add(botaoAtualizar);
        
        JButton botaoExcluir = new JButton("🗑️ Excluir");
        botaoExcluir.setBackground(new Color(200, 0, 0));
        botaoExcluir.setForeground(Color.WHITE);
        botaoExcluir.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        botaoExcluir.addActionListener(e -> excluirJogoSelecionado());
        painelBotoes.add(botaoExcluir);
        
        painel.add(painelBotoes, BorderLayout.SOUTH);
        
        return painel;
    }
    
    private void carregarDados() {
        try {
            // SQL APENAS COM COLUNAS QUE EXISTEM
            String sql = """
                SELECT id, nome, preco, genero, desenvolvedor, created_at
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
    
    private void salvarJogo() {
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
            
            // SQL APENAS COM COLUNAS QUE EXISTEM
            String sql = """
                INSERT INTO jogos (nome, preco, genero, desenvolvedor)
                VALUES (?, ?, ?, ?)
            """;
            
            try (Connection conn = DriverManager.getConnection(DB_URL);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                pstmt.setString(1, campoNome.getText().trim());
                pstmt.setDouble(2, Double.parseDouble(campoPreco.getText().trim()));
                pstmt.setString(3, campoGenero.getText().trim());
                pstmt.setString(4, campoDesenvolvedor.getText().trim());
                
                int rowsAffected = pstmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, 
                        "✅ Jogo salvo com sucesso!", 
                        "Sucesso", 
                        JOptionPane.INFORMATION_MESSAGE);
                    limparFormulario();
                    carregarDados();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "❌ Erro ao salvar jogo!", 
                        "Erro", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Preço deve ser um número válido!", 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            System.err.println("❌ Erro ao salvar: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Erro ao salvar: " + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
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