package com.sistemaregistrojogos;

import com.sistemaregistrojogos.database.DatabaseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;

public class Main extends Application {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    @Override
    public void start(Stage stage) throws Exception {
        try {
            // Inicializar banco de dados
            DatabaseManager.getInstance().initializeDatabase();
            
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/fxml/main-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
            
            // Aplicar CSS
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            
            stage.setTitle("Sistema de Registro de Jogos");
            stage.setScene(scene);
            stage.setMinWidth(800);
            stage.setMinHeight(600);
            
            // Adicionar ícone (opcional)
            try {
                stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/icon.png")));
            } catch (Exception e) {
                logger.warn("Não foi possível carregar o ícone da aplicação");
            }
            
            stage.show();
            
            logger.info("Aplicação iniciada com sucesso");
            
        } catch (Exception e) {
            logger.error("Erro ao iniciar aplicação", e);
            throw e;
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        try {
            DatabaseManager.getInstance().closeConnection();
        } catch (Exception e) {
            logger.error("Erro ao fechar conexão com banco", e);
        }
        logger.info("Aplicação finalizada");
    }

    public static void main(String[] args) {
        launch();
    }
} 