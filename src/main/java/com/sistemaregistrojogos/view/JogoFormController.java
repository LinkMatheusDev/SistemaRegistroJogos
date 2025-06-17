package com.sistemaregistrojogos.view;

import com.sistemaregistrojogos.database.JogoDAO;
import com.sistemaregistrojogos.model.Jogo;
import com.sistemaregistrojogos.util.AlertUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class JogoFormController implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(JogoFormController.class);
    
    private Stage dialogStage;
    private Jogo jogo;
    private JogoDAO jogoDAO;
    private boolean isEditMode = false;
    private Runnable onSaveCallback;

    @FXML private Label lblTitulo;
    @FXML private TextField txtNome;
    @FXML private TextField txtPreco;
    @FXML private ComboBox<String> cmbGenero;
    @FXML private TextField txtDesenvolvedora;
    @FXML private ComboBox<String> cmbPlataforma;
    @FXML private Spinner<Integer> spnAno;
    @FXML private Slider sldClassificacao;
    @FXML private Label lblClassificacao;
    @FXML private TextArea txtDescricao;
    @FXML private Button btnSalvar;
    @FXML private Button btnCancelar;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupFormControls();
        setupValidation();
    }

    private void setupFormControls() {
        // Configurar ComboBox de gêneros
        cmbGenero.setItems(FXCollections.observableArrayList(
            "Ação", "Aventura", "RPG", "Estratégia", "Simulação", 
            "Esporte", "Corrida", "Puzzle", "FPS", "MMORPG", "Indie", "Terror",
            "Plataforma", "Musical", "Educativo", "Casual"
        ));

        // Configurar ComboBox de plataformas
        cmbPlataforma.setItems(FXCollections.observableArrayList(
            "PC", "PlayStation 5", "PlayStation 4", "Xbox Series X/S", "Xbox One",
            "Nintendo Switch", "Mobile", "Web", "Linux", "Mac", "Steam Deck"
        ));

        // Configurar Spinner de ano
        int currentYear = LocalDate.now().getYear();
        SpinnerValueFactory<Integer> anoValueFactory = 
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1970, currentYear + 5, currentYear);
        spnAno.setValueFactory(anoValueFactory);
        spnAno.setEditable(true);

        // Configurar Slider de classificação
        sldClassificacao.valueProperty().addListener((obs, oldVal, newVal) -> {
            lblClassificacao.setText(String.format("%.1f", newVal.doubleValue()));
        });
        
        // Valor inicial do slider
        sldClassificacao.setValue(0.0);
        lblClassificacao.setText("0.0");
    }

    private void setupValidation() {
        // Validação de preço - apenas números e vírgula/ponto
        txtPreco.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.matches("\\d*([,.]\\d{0,2})?")) {
                txtPreco.setText(oldText);
            }
        });

        // Listener para habilitar/desabilitar botão salvar
        txtNome.textProperty().addListener((obs, oldText, newText) -> updateSaveButtonState());
        txtPreco.textProperty().addListener((obs, oldText, newText) -> updateSaveButtonState());
        
        updateSaveButtonState();
    }

    private void updateSaveButtonState() {
        boolean isValid = !txtNome.getText().trim().isEmpty() && 
                         !txtPreco.getText().trim().isEmpty() &&
                         isValidPrice(txtPreco.getText().trim());
        btnSalvar.setDisable(!isValid);
    }

    private boolean isValidPrice(String priceText) {
        try {
            String normalizedPrice = priceText.replace(',', '.');
            double price = Double.parseDouble(normalizedPrice);
            return price >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @FXML
    private void handleSalvar() {
        try {
            if (validarCampos()) {
                if (jogo == null) {
                    jogo = new Jogo();
                }

                // Preencher dados do jogo
                jogo.setNome(txtNome.getText().trim());
                
                String precoText = txtPreco.getText().trim().replace(',', '.');
                jogo.setPreco(Double.parseDouble(precoText));
                
                jogo.setGenero(cmbGenero.getValue());
                jogo.setDesenvolvedora(txtDesenvolvedora.getText().trim());
                jogo.setPlataforma(cmbPlataforma.getValue());
                jogo.setAnoLancamento(spnAno.getValue());
                jogo.setClassificacao(sldClassificacao.getValue());
                jogo.setDescricao(txtDescricao.getText().trim());

                // Salvar no banco de dados
                boolean sucesso;
                if (isEditMode) {
                    sucesso = jogoDAO.atualizar(jogo);
                } else {
                    sucesso = jogoDAO.inserir(jogo);
                }

                if (sucesso) {
                    if (onSaveCallback != null) {
                        onSaveCallback.run();
                    }
                    
                    String mensagem = isEditMode ? "Jogo atualizado com sucesso!" : "Jogo cadastrado com sucesso!";
                    AlertUtil.showInfo("Sucesso", "Operação realizada", mensagem);
                    
                    dialogStage.close();
                    
                } else {
                    String erro = isEditMode ? "Erro ao atualizar jogo" : "Erro ao cadastrar jogo";
                    AlertUtil.showError("Erro", erro, "Verifique se o nome do jogo já não existe.");
                }
            }
            
        } catch (NumberFormatException e) {
            AlertUtil.showError("Erro", "Valor inválido", "Por favor, insira um preço válido.");
        } catch (Exception e) {
            logger.error("Erro ao salvar jogo", e);
            AlertUtil.showException("Erro", "Erro inesperado", "Ocorreu um erro ao salvar o jogo.", e);
        }
    }

    @FXML
    private void handleCancelar() {
        dialogStage.close();
    }

    private boolean validarCampos() {
        StringBuilder erros = new StringBuilder();

        // Validar nome
        if (txtNome.getText().trim().isEmpty()) {
            erros.append("- Nome é obrigatório\n");
        }

        // Validar preço
        if (txtPreco.getText().trim().isEmpty()) {
            erros.append("- Preço é obrigatório\n");
        } else if (!isValidPrice(txtPreco.getText().trim())) {
            erros.append("- Preço deve ser um valor numérico válido\n");
        }

        if (erros.length() > 0) {
            AlertUtil.showWarning("Campos obrigatórios", "Por favor, corrija os seguintes erros:", erros.toString());
            return false;
        }

        return true;
    }

    public void setJogo(Jogo jogo) {
        this.jogo = jogo;
        this.isEditMode = (jogo != null);
        
        if (isEditMode) {
            lblTitulo.setText("Editar Jogo");
            preencherCampos();
        } else {
            lblTitulo.setText("Novo Jogo");
            limparCampos();
        }
    }

    private void preencherCampos() {
        if (jogo != null) {
            txtNome.setText(jogo.getNome());
            txtPreco.setText(String.format("%.2f", jogo.getPreco()).replace('.', ','));
            cmbGenero.setValue(jogo.getGenero());
            txtDesenvolvedora.setText(jogo.getDesenvolvedora());
            cmbPlataforma.setValue(jogo.getPlataforma());
            
            if (jogo.getAnoLancamento() > 0) {
                spnAno.getValueFactory().setValue(jogo.getAnoLancamento());
            }
            
            sldClassificacao.setValue(jogo.getClassificacao());
            txtDescricao.setText(jogo.getDescricao());
        }
    }

    private void limparCampos() {
        txtNome.clear();
        txtPreco.clear();
        cmbGenero.setValue(null);
        txtDesenvolvedora.clear();
        cmbPlataforma.setValue(null);
        spnAno.getValueFactory().setValue(LocalDate.now().getYear());
        sldClassificacao.setValue(0.0);
        txtDescricao.clear();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setJogoDAO(JogoDAO jogoDAO) {
        this.jogoDAO = jogoDAO;
    }

    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }

    // Método estático para abrir o diálogo
    public static void showDialog(Jogo jogo, JogoDAO jogoDAO, Runnable onSaveCallback) throws IOException {
        FXMLLoader loader = new FXMLLoader(JogoFormController.class.getResource("/fxml/jogo-form.fxml"));
        Stage dialogStage = new Stage();
        
        Scene scene = new Scene(loader.load(), 500, 650);
        scene.getStylesheets().add(JogoFormController.class.getResource("/css/style.css").toExternalForm());
        
        dialogStage.setScene(scene);
        dialogStage.setTitle(jogo == null ? "Novo Jogo" : "Editar Jogo");
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setResizable(false);

        JogoFormController controller = loader.getController();
        controller.setDialogStage(dialogStage);
        controller.setJogoDAO(jogoDAO);
        controller.setOnSaveCallback(onSaveCallback);
        controller.setJogo(jogo);

        dialogStage.showAndWait();
    }
} 