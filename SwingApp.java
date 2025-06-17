import com.sistemaregistrojogos.database.DatabaseManager;
import com.sistemaregistrojogos.database.JogoDAO;
import com.sistemaregistrojogos.model.Jogo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

/**
 * Interface gráfica simples usando Swing (nativo do Java)
 * Não precisa de dependências externas - funciona em qualquer JDK 8+
 * FORÇANDO USO DO SQLITE
 */
public class SwingApp extends JFrame {
    
    private JTable tabelaJogos;
    private DefaultTableModel modeloTabela;
    private JogoDAO jogoDAO;
    private DatabaseManager dbManager;
    
    // Componentes do formulário
    private JTextField campoNome;
    private JTextField campoPreco;
    private JTextField campoGenero;
    private JTextField campoDesenvolvedora;
    private JTextField campoPlataforma;
    private JTextField campoAno;
    private JTextField campoClassificacao;
    private JTextArea campoDescricao;
    
    public SwingApp() {
        // FORÇAR SQLITE ANTES DE QUALQUER COISA
        forceSQLiteConnection();
        
        this.dbManager = DatabaseManager.getInstance();
        this.jogoDAO = new JogoDAO();
        
        configurarJanela();
        inicializarComponentes();
        carregarDados();
        
        setVisible(true);
    }
    
    private void forceSQLiteConnection() {
        try {
            // Inicializar SQLite diretamente
            Properties props = new Properties();
            props.setProperty("foreign_keys", "true");
            props.setProperty("journal_mode", "WAL");
            props.setProperty("synchronous", "NORMAL");
            
            Connection conn = DriverManager.getConnection("jdbc:sqlite:jogos.db", props);
            
            // Criar tabela se não existir
            String createTable = """
                CREATE TABLE IF NOT EXISTS jogos (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome TEXT NOT NULL UNIQUE,
                    preco REAL NOT NULL CHECK(preco >= 0),
                    genero TEXT,
                    desenvolvedora TEXT,
                    plataforma TEXT,
                    ano_lancamento INTEGER CHECK(ano_lancamento >= 1970 AND ano_lancamento <= 2050),
                    classificacao REAL CHECK(classificacao >= 0 AND classificacao <= 10),
                    descricao TEXT,
                    data_cadastro DATETIME DEFAULT CURRENT_TIMESTAMP,
                    data_atualizacao DATETIME DEFAULT CURRENT_TIMESTAMP
                )
            """;
            
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createTable);
                System.out.println("✅ Banco SQLite inicializado com sucesso!");
            }
            
            conn.close();
            
        } catch (SQLException e) {
            System.err.println("❌ Erro ao inicializar SQLite: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void configurarJanela() {
        setTitle("🎮 Sistema de Registro de Jogos v2.0 - Swing");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
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
        JLabel statusBar = new JLabel("💾 Usando SQLite - Interface Swing Nativa");
        statusBar.setBorder(new EmptyBorder(5, 10, 5, 10));
        statusBar.setOpaque(true);
        statusBar.setBackground(new Color(240, 240, 240));
        add(statusBar, BorderLayout.SOUTH);
    }
    
    private JPanel criarPainelFormulario() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Campos do formulário
        campoNome = new JTextField(20);
        campoPreco = new JTextField(20);
        campoGenero = new JTextField(20);
        campoDesenvolvedora = new JTextField(20);
        campoPlataforma = new JTextField(20);
        campoAno = new JTextField(20);
        campoClassificacao = new JTextField(20);
        campoDescricao = new JTextArea(3, 20);
        campoDescricao.setLineWrap(true);
        campoDescricao.setWrapStyleWord(true);
        
        // Layout dos campos
        adicionarCampo(painel, gbc, "Nome:", campoNome, 0);
        adicionarCampo(painel, gbc, "Preço (R$):", campoPreco, 1);
        adicionarCampo(painel, gbc, "Gênero:", campoGenero, 2);
        adicionarCampo(painel, gbc, "Desenvolvedora:", campoDesenvolvedora, 3);
        adicionarCampo(painel, gbc, "Plataforma:", campoPlataforma, 4);
        adicionarCampo(painel, gbc, "Ano de Lançamento:", campoAno, 5);
        adicionarCampo(painel, gbc, "Classificação (0-10):", campoClassificacao, 6);
        
        // Descrição (área de texto)
        gbc.gridx = 0;
        gbc.gridy = 7;
        painel.add(new JLabel("Descrição:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        painel.add(new JScrollPane(campoDescricao), gbc);
        
        // Botão salvar
        JButton botaoSalvar = new JButton("💾 Salvar Jogo");
        botaoSalvar.setPreferredSize(new Dimension(150, 35));
        botaoSalvar.addActionListener(e -> salvarJogo());
        
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        painel.add(botaoSalvar, gbc);
        
        return painel;
    }
    
    private void adicionarCampo(JPanel painel, GridBagConstraints gbc, String label, JTextField campo, int linha) {
        gbc.gridx = 0;
        gbc.gridy = linha;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.weighty = 0;
        painel.add(new JLabel(label), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        painel.add(campo, gbc);
    }
    
    private JPanel criarPainelLista() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Modelo da tabela
        String[] colunas = {"ID", "Nome", "Preço", "Gênero", "Desenvolvedora", "Plataforma", "Ano", "Classificação"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabela somente leitura
            }
        };
        
        tabelaJogos = new JTable(modeloTabela);
        tabelaJogos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaJogos.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(tabelaJogos);
        painel.add(scrollPane, BorderLayout.CENTER);
        
        // Painel de botões
        JPanel painelBotoes = new JPanel(new FlowLayout());
        
        JButton botaoAtualizar = new JButton("🔄 Atualizar");
        botaoAtualizar.addActionListener(e -> carregarDados());
        painelBotoes.add(botaoAtualizar);
        
        JButton botaoExcluir = new JButton("🗑️ Excluir");
        botaoExcluir.addActionListener(e -> excluirJogoSelecionado());
        painelBotoes.add(botaoExcluir);
        
        painel.add(painelBotoes, BorderLayout.SOUTH);
        
        return painel;
    }
    
    private void carregarDados() {
        try {
            // Forçar reconexão SQLite
            forceSQLiteConnection();
            
            List<Jogo> jogos = jogoDAO.listarTodos();
            
            // Limpar tabela
            modeloTabela.setRowCount(0);
            
            // Adicionar jogos à tabela
            for (Jogo jogo : jogos) {
                Object[] linha = {
                    jogo.getId(),
                    jogo.getNome(),
                    String.format("R$ %.2f", jogo.getPreco()),
                    jogo.getGenero(),
                    jogo.getDesenvolvedora(),
                    jogo.getPlataforma(),
                    jogo.getAnoLancamento(),
                    String.format("%.1f", jogo.getClassificacao())
                };
                modeloTabela.addRow(linha);
            }
            
            System.out.println("✅ Carregados " + jogos.size() + " jogos");
            
        } catch (Exception e) {
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
            
            // Criar jogo
            Jogo jogo = new Jogo();
            jogo.setNome(campoNome.getText().trim());
            jogo.setPreco(Double.parseDouble(campoPreco.getText().trim()));
            jogo.setGenero(campoGenero.getText().trim());
            jogo.setDesenvolvedora(campoDesenvolvedora.getText().trim());
            jogo.setPlataforma(campoPlataforma.getText().trim());
            
            // Campos opcionais
            if (!campoAno.getText().trim().isEmpty()) {
                jogo.setAnoLancamento(Integer.parseInt(campoAno.getText().trim()));
            }
            
            if (!campoClassificacao.getText().trim().isEmpty()) {
                jogo.setClassificacao(Double.parseDouble(campoClassificacao.getText().trim()));
            }
            
            jogo.setDescricao(campoDescricao.getText().trim());
            
            // Salvar
            if (jogoDAO.inserir(jogo)) {
                JOptionPane.showMessageDialog(this, "Jogo salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                limparFormulario();
                carregarDados();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao salvar jogo!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Verifique os valores numéricos!", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void excluirJogoSelecionado() {
        int linhaSelecionada = tabelaJogos.getSelectedRow();
        
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um jogo para excluir!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (Integer) modeloTabela.getValueAt(linhaSelecionada, 0);
        String nome = (String) modeloTabela.getValueAt(linhaSelecionada, 1);
        
        int confirmacao = JOptionPane.showConfirmDialog(
            this, 
            "Tem certeza que deseja excluir o jogo \"" + nome + "\"?", 
            "Confirmar Exclusão", 
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirmacao == JOptionPane.YES_OPTION) {
            if (jogoDAO.excluir(id)) {
                JOptionPane.showMessageDialog(this, "Jogo excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarDados();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao excluir jogo!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void limparFormulario() {
        campoNome.setText("");
        campoPreco.setText("");
        campoGenero.setText("");
        campoDesenvolvedora.setText("");
        campoPlataforma.setText("");
        campoAno.setText("");
        campoClassificacao.setText("");
        campoDescricao.setText("");
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
            new SwingApp();
        });
    }
} 