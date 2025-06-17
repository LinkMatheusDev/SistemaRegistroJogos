package com.sistemaregistrojogos.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Gerenciador de conexão com PostgreSQL para o Sistema de Registro de Jogos
 */
public class PostgreSQLManager {
    private static final Logger logger = LoggerFactory.getLogger(PostgreSQLManager.class);
    
    private static PostgreSQLManager instance;
    private Connection connection;
    
    // Configurações do PostgreSQL
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/sistemaregistrojogos";
    private static final String DB_USER = "jogos_user";
    private static final String DB_PASSWORD = "jogos_password";
    private static final String DB_SCHEMA = "jogos_app";
    
    private PostgreSQLManager() {
        try {
            // Carregar driver PostgreSQL
            Class.forName("org.postgresql.Driver");
            conectar();
            inicializarEstrutura();
            logger.info("Conexão PostgreSQL estabelecida com sucesso");
        } catch (ClassNotFoundException e) {
            logger.error("Driver PostgreSQL não encontrado", e);
            throw new RuntimeException("Driver PostgreSQL não disponível", e);
        } catch (SQLException e) {
            logger.error("Erro ao conectar com PostgreSQL", e);
            throw new RuntimeException("Falha na conexão PostgreSQL", e);
        }
    }
    
    public static synchronized PostgreSQLManager getInstance() {
        if (instance == null) {
            instance = new PostgreSQLManager();
        }
        return instance;
    }
    
    private void conectar() throws SQLException {
        connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        connection.setAutoCommit(true);
    }
    
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            conectar();
        }
        return connection;
    }
    
    private void inicializarEstrutura() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Definir schema
            stmt.execute("SET search_path TO " + DB_SCHEMA + ", public");
            
            // Verificar se as tabelas existem
            String checkTable = """
                SELECT EXISTS (
                    SELECT FROM information_schema.tables 
                    WHERE table_schema = 'jogos_app' 
                    AND table_name = 'jogos'
                );
            """;
            
            try (var rs = stmt.executeQuery(checkTable)) {
                if (rs.next() && rs.getBoolean(1)) {
                    logger.info("Estrutura do banco PostgreSQL já existe");
                } else {
                    logger.warn("Tabelas não encontradas no schema jogos_app");
                    logger.info("Execute o Docker setup para inicializar o banco");
                }
            }
        }
    }
    
    public void fecharConexao() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                logger.info("Conexão PostgreSQL fechada");
            }
        } catch (SQLException e) {
            logger.warn("Erro ao fechar conexão PostgreSQL", e);
        }
    }
    
    public boolean testarConexao() {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeQuery("SELECT 1");
            return true;
        } catch (SQLException e) {
            logger.error("Falha no teste de conexão", e);
            return false;
        }
    }
    
    public void reconectar() throws SQLException {
        fecharConexao();
        conectar();
        logger.info("Reconectado ao PostgreSQL");
    }
} 