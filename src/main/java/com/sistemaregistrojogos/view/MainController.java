package com.sistemaregistrojogos.view;

import com.sistemaregistrojogos.database.JogoDAO;
import com.sistemaregistrojogos.model.Jogo;
import com.sistemaregistrojogos.util.AlertUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    private JogoDAO jogoDAO;
    private ObservableList<Jogo> jogosObservableList;

    // Componentes FXML
    @FXML private Button btnNovo;
    @FXML private Button btnEditar;
    @FXML private Button btnExcluir;
    @FXML private Button btnBuscar;
    @FXML private Button btnLimpar;
    
    @FXML private TextField txtBusca;
    @FXML private ComboBox<String> cmbGenero;
    
    @FXML private TableView<Jogo> tableJogos;
    @FXML private TableColumn<Jogo, Integer> colId;
    @FXML private TableColumn<Jogo, String> colNome;
    @FXML private TableColumn<Jogo, String> colPreco;
    @FXML private TableColumn<Jogo, String> colGenero;
    @FXML private TableColumn<Jogo, String> colDesenvolvedora;
    @FXML private TableColumn<Jogo, String> colPlataforma;
    @FXML private TableColumn<Jogo, Integer> colAno;
    @FXML private TableColumn<Jogo, String> colClassificacao;
    
    @FXML private Label lblStatus;
    @FXML private Label lblTotalJogos;
    
    // Painel de detalhes
    @FXML private Label lblDetalheNome;
    @FXML private Label lblDetalhePreco;
    @FXML private Label lblDetalheGenero;
    @FXML private Label lblDetalheDesenvolvedora;
    @FXML private Label lblDetalhePlataforma;
    @FXML private Label lblDetalheAno;
    @FXML private Label lblDetalheClassificacao;
    @FXML private Label lblDetalheDescricao;
    @FXML private Label lblDetalheDataCadastro;
    @FXML private Label lblDetalheDataAtualizacao;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            // Inicializar DAO
            jogoDAO = new JogoDAO();
            jogosObservableList = FXCollections.observableArrayList();
            
            // Configurar tabela
            setupTableColumns();
            setupTableSelectionListener();
            
            // Configurar ComboBox de gêneros
            setupGeneroComboBox();
            
            // Configurar listeners
            setupEventListeners();
            
            // Carregar dados iniciais
            carregarJogos();
            
            logger.info("Interface inicializada com sucesso");
            
        } catch (Exception e) {
            logger.error("Erro ao inicializar interface", e);
            AlertUtil.showError("Erro", "Erro ao inicializar aplicação", e.getMessage());
        }
    }

    private void setupTableColumns() {
        // Configurar colunas da tabela
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colGenero.setCellValueFactory(new PropertyValueFactory<>("genero"));
        colDesenvolvedora.setCellValueFactory(new PropertyValueFactory<>("desenvolvedora"));
        colPlataforma.setCellValueFactory(new PropertyValueFactory<>("plataforma"));
        colAno.setCellValueFactory(new PropertyValueFactory<>("anoLancamento"));
        
        // Formatação customizada para preço
        colPreco.setCellValueFactory(cellData -> 
            new SimpleStringProperty(String.format("R$ %.2f", cellData.getValue().getPreco())));
        
        // Formatação customizada para classificação
        colClassificacao.setCellValueFactory(cellData -> {
            double classificacao = cellData.getValue().getClassificacao();
            return new SimpleStringProperty(classificacao > 0 ? String.format("%.1f", classificacao) : "-");
        });
        
        // Definir tabela como editável
        tableJogos.setItems(jogosObservableList);
        tableJogos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void setupTableSelectionListener() {
        tableJogos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                exibirDetalhesJogo(newSelection);
                btnEditar.setDisable(false);
                btnExcluir.setDisable(false);
            } else {
                limparDetalhes();
                btnEditar.setDisable(true);
                btnExcluir.setDisable(true);
            }
        });
    }

    private void setupGeneroComboBox() {
        // Lista de gêneros comuns
        cmbGenero.setItems(FXCollections.observableArrayList(
            "Todos", "Ação", "Aventura", "RPG", "Estratégia", "Simulação", 
            "Esporte", "Corrida", "Puzzle", "FPS", "MMORPG", "Indie", "Terror"
        ));
        
        cmbGenero.setValue("Todos");
        cmbGenero.setOnAction(e -> filtrarPorGenero());
    }

    private void setupEventListeners() {
        // Listener para busca em tempo real
        txtBusca.textProperty().addListener((obs, oldText, newText) -> {
            if (newText != null && newText.trim().isEmpty()) {
                carregarJogos();
            }
        });
        
        // Enter no campo de busca
        txtBusca.setOnAction(e -> handleBuscar());
    }

    @FXML
    private void handleNovo() {
        try {
            JogoFormController.showDialog(null, jogoDAO, this::carregarJogos);
        } catch (Exception e) {
            logger.error("Erro ao abrir formulário de novo jogo", e);
            AlertUtil.showError("Erro", "Erro ao abrir formulário", e.getMessage());
        }
    }

    @FXML
    private void handleEditar() {
        Jogo jogoSelecionado = tableJogos.getSelectionModel().getSelectedItem();
        if (jogoSelecionado != null) {
            try {
                JogoFormController.showDialog(jogoSelecionado, jogoDAO, this::carregarJogos);
            } catch (Exception e) {
                logger.error("Erro ao abrir formulário de edição", e);
                AlertUtil.showError("Erro", "Erro ao abrir formulário", e.getMessage());
            }
        }
    }

    @FXML
    private void handleExcluir() {
        Jogo jogoSelecionado = tableJogos.getSelectionModel().getSelectedItem();
        if (jogoSelecionado != null) {
            Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacao.setTitle("Confirmar Exclusão");
            confirmacao.setHeaderText("Excluir Jogo");
            confirmacao.setContentText("Tem certeza que deseja excluir o jogo '" + 
                                     jogoSelecionado.getNome() + "'?");
            
            Optional<ButtonType> resultado = confirmacao.showAndWait();
            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                try {
                    if (jogoDAO.excluir(jogoSelecionado.getId())) {
                        carregarJogos();
                        atualizarStatus("Jogo excluído com sucesso!");
                        logger.info("Jogo excluído: {}", jogoSelecionado.getNome());
                    } else {
                        AlertUtil.showError("Erro", "Falha ao excluir", "Não foi possível excluir o jogo.");
                    }
                } catch (Exception e) {
                    logger.error("Erro ao excluir jogo", e);
                    AlertUtil.showError("Erro", "Erro ao excluir jogo", e.getMessage());
                }
            }
        }
    }

    @FXML
    private void handleBuscar() {
        String termoBusca = txtBusca.getText().trim();
        if (!termoBusca.isEmpty()) {
            try {
                Optional<Jogo> resultado = jogoDAO.buscarPorNome(termoBusca);
                if (resultado.isPresent()) {
                    jogosObservableList.clear();
                    jogosObservableList.add(resultado.get());
                    atualizarContadores();
                    atualizarStatus("Encontrado 1 jogo");
                } else {
                    jogosObservableList.clear();
                    atualizarContadores();
                    atualizarStatus("Nenhum jogo encontrado com o nome: " + termoBusca);
                }
            } catch (Exception e) {
                logger.error("Erro ao buscar jogo", e);
                AlertUtil.showError("Erro", "Erro na busca", e.getMessage());
            }
        } else {
            carregarJogos();
        }
    }

    @FXML
    private void handleLimpar() {
        txtBusca.clear();
        cmbGenero.setValue("Todos");
        carregarJogos();
        atualizarStatus("Filtros limpos");
    }

    private void filtrarPorGenero() {
        String generoSelecionado = cmbGenero.getValue();
        if (generoSelecionado != null && !generoSelecionado.equals("Todos")) {
            try {
                List<Jogo> jogosFiltrados = jogoDAO.buscarPorGenero(generoSelecionado);
                jogosObservableList.clear();
                jogosObservableList.addAll(jogosFiltrados);
                atualizarContadores();
                atualizarStatus("Filtrado por gênero: " + generoSelecionado);
            } catch (Exception e) {
                logger.error("Erro ao filtrar por gênero", e);
                AlertUtil.showError("Erro", "Erro no filtro", e.getMessage());
            }
        } else if ("Todos".equals(generoSelecionado)) {
            carregarJogos();
        }
    }

    private void carregarJogos() {
        try {
            List<Jogo> jogos = jogoDAO.listarTodos();
            jogosObservableList.clear();
            jogosObservableList.addAll(jogos);
            atualizarContadores();
            atualizarStatus("Jogos carregados com sucesso");
            
        } catch (Exception e) {
            logger.error("Erro ao carregar jogos", e);
            AlertUtil.showError("Erro", "Erro ao carregar dados", e.getMessage());
        }
    }

    private void exibirDetalhesJogo(Jogo jogo) {
        lblDetalheNome.setText(jogo.getNome());
        lblDetalhePreco.setText(String.format("R$ %.2f", jogo.getPreco()));
        lblDetalheGenero.setText(jogo.getGenero() != null ? jogo.getGenero() : "-");
        lblDetalheDesenvolvedora.setText(jogo.getDesenvolvedora() != null ? jogo.getDesenvolvedora() : "-");
        lblDetalhePlataforma.setText(jogo.getPlataforma() != null ? jogo.getPlataforma() : "-");
        lblDetalheAno.setText(jogo.getAnoLancamento() > 0 ? String.valueOf(jogo.getAnoLancamento()) : "-");
        lblDetalheClassificacao.setText(jogo.getClassificacao() > 0 ? String.format("%.1f", jogo.getClassificacao()) : "-");
        lblDetalheDescricao.setText(jogo.getDescricao() != null ? jogo.getDescricao() : "-");
        lblDetalheDataCadastro.setText(jogo.getDataCadastro() != null ? jogo.getDataCadastro().format(dateFormatter) : "-");
        lblDetalheDataAtualizacao.setText(jogo.getDataAtualizacao() != null ? jogo.getDataAtualizacao().format(dateFormatter) : "-");
    }

    private void limparDetalhes() {
        lblDetalheNome.setText("-");
        lblDetalhePreco.setText("-");
        lblDetalheGenero.setText("-");
        lblDetalheDesenvolvedora.setText("-");
        lblDetalhePlataforma.setText("-");
        lblDetalheAno.setText("-");
        lblDetalheClassificacao.setText("-");
        lblDetalheDescricao.setText("-");
        lblDetalheDataCadastro.setText("-");
        lblDetalheDataAtualizacao.setText("-");
    }

    private void atualizarContadores() {
        int total = jogosObservableList.size();
        lblTotalJogos.setText("Total: " + total + " jogo" + (total != 1 ? "s" : ""));
    }

    private void atualizarStatus(String mensagem) {
        lblStatus.setText(mensagem);
    }
} 