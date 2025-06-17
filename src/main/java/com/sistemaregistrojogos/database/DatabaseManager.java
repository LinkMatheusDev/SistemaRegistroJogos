package com.sistemaregistrojogos.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Properties;

public class DatabaseManager {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
    
    // Configuração PostgreSQL (Docker)
    private static final String DATABASE_URL = "jdbc:postgresql://localhost:5432/sistemaregistrojogos";
    private static final String DB_USER = "jogos_user";
    private static final String DB_PASSWORD = "jogos_password";
    private static final String DB_SCHEMA = "jogos_app";
    
    // Fallback SQLite se PostgreSQL não estiver disponível
    private static final String SQLITE_URL = "jdbc:sqlite:jogos.db";
    
    private static DatabaseManager instance;
    private Connection connection;
    private boolean usingPostgreSQL = false;

    private DatabaseManager() {
        // Singleton pattern
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public void initializeDatabase() throws SQLException {
        try {
            // Tentar PostgreSQL primeiro
            if (tryPostgreSQL()) {
                usingPostgreSQL = true;
                logger.info("Conectado ao PostgreSQL (Docker) com sucesso");
            } else {
                // Fallback para SQLite
                initializeSQLite();
                usingPostgreSQL = false;
                logger.info("Usando SQLite como fallback");
            }
            
        } catch (SQLException e) {
            logger.error("Erro ao inicializar banco de dados", e);
            throw e;
        }
    }
    
    private boolean tryPostgreSQL() {
        try {
            // Carregar driver PostgreSQL
            Class.forName("org.postgresql.Driver");
            
            connection = DriverManager.getConnection(DATABASE_URL, DB_USER, DB_PASSWORD);
            connection.setAutoCommit(true);
            
            // Configurar schema
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("SET search_path TO " + DB_SCHEMA + ", public");
            }
            
            // Verificar se as tabelas existem
            if (!checkPostgreSQLTables()) {
                logger.warn("Tabelas PostgreSQL não encontradas. Execute o Docker setup.");
            }
            
            return true;
            
        } catch (Exception e) {
            logger.warn("PostgreSQL não disponível, usando SQLite: " + e.getMessage());
            return false;
        }
    }
    
    private boolean checkPostgreSQLTables() {
        try (Statement stmt = connection.createStatement()) {
            String query = """
                SELECT EXISTS (
                    SELECT FROM information_schema.tables 
                    WHERE table_schema = 'jogos_app' AND table_name = 'jogos'
                )
            """;
            
            try (ResultSet rs = stmt.executeQuery(query)) {
                if (rs.next()) {
                    return rs.getBoolean(1);
                }
            }
        } catch (SQLException e) {
            logger.warn("Erro ao verificar tabelas PostgreSQL", e);
        }
        return false;
    }

    private void initializeSQLite() throws SQLException {
        Properties props = new Properties();
        props.setProperty("foreign_keys", "true");
        props.setProperty("journal_mode", "WAL");
        props.setProperty("synchronous", "NORMAL");
        
        connection = DriverManager.getConnection(SQLITE_URL, props);
        connection.setAutoCommit(true);
        
        // Criar tabelas SQLite se não existirem
        createSQLiteTables();
    }

    private void createSQLiteTables() throws SQLException {
        String createJogosTable = """
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

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createJogosTable);
            logger.debug("Tabela jogos SQLite criada/verificada com sucesso");
        }
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            initializeDatabase();
        }
        return connection;
    }

    public boolean isUsingPostgreSQL() {
        return usingPostgreSQL;
    }

    public String getDatabaseType() {
        return usingPostgreSQL ? "PostgreSQL (Docker)" : "SQLite (Local)";
    }

    public synchronized void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    logger.info("Conexão com banco de dados fechada");
                }
            } catch (SQLException e) {
                logger.error("Erro ao fechar conexão com banco de dados", e);
            } finally {
                connection = null;
            }
        }
    }

    // Método para contar registros
    public int countRecords() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM jogos")) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Erro ao contar registros", e);
        }
        return 0;
    }

    // Método para verificar conexão
    public boolean testConnection() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeQuery("SELECT 1");
            return true;
        } catch (SQLException e) {
            logger.error("Falha no teste de conexão", e);
            return false;
        }
    }

    // Método para executar backup do banco de dados
    public void backupDatabase(String backupPath) {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            String backupSQL = "VACUUM INTO '" + backupPath + "'";
            stmt.execute(backupSQL);
            logger.info("Backup do banco de dados criado em: " + backupPath);
        } catch (SQLException e) {
            logger.error("Erro ao criar backup do banco de dados", e);
        }
    }

    // Método para verificar integridade do banco
    public boolean checkDatabaseIntegrity() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("PRAGMA integrity_check")) {
            
            if (rs.next()) {
                String result = rs.getString(1);
                boolean isOk = "ok".equals(result);
                logger.info("Verificação de integridade do banco: " + result);
                return isOk;
            }
        } catch (SQLException e) {
            logger.error("Erro ao verificar integridade do banco de dados", e);
        }
        return false;
    }

    // Método para limpar dados de teste
    public void clearTestData() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM jogos WHERE nome LIKE '%Test%' OR nome LIKE '%Teste%'");
            logger.info("Dados de teste removidos");
        } catch (SQLException e) {
            logger.error("Erro ao limpar dados de teste", e);
        }
    }
} 