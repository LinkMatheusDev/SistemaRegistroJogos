package com.sistemaregistrojogos.database;

import com.sistemaregistrojogos.model.Jogo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JogoDAO {
    private static final Logger logger = LoggerFactory.getLogger(JogoDAO.class);
    private final DatabaseManager dbManager;

    public JogoDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public boolean inserir(Jogo jogo) {
        String sql = """
            INSERT INTO jogos (nome, preco, genero, desenvolvedora, plataforma, 
                             ano_lancamento, classificacao, descricao, data_cadastro, data_atualizacao)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            LocalDateTime agora = LocalDateTime.now();
            jogo.setDataCadastro(agora);
            jogo.setDataAtualizacao(agora);

            pstmt.setString(1, jogo.getNome());
            pstmt.setDouble(2, jogo.getPreco());
            pstmt.setString(3, jogo.getGenero());
            pstmt.setString(4, jogo.getDesenvolvedora());
            pstmt.setString(5, jogo.getPlataforma());
            pstmt.setInt(6, jogo.getAnoLancamento());
            pstmt.setDouble(7, jogo.getClassificacao());
            pstmt.setString(8, jogo.getDescricao());
            pstmt.setTimestamp(9, Timestamp.valueOf(agora));
            pstmt.setTimestamp(10, Timestamp.valueOf(agora));

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        jogo.setId(generatedKeys.getInt(1));
                    }
                }
                logger.info("Jogo inserido com sucesso: {}", jogo.getNome());
                return true;
            }

        } catch (SQLException e) {
            logger.error("Erro ao inserir jogo: {}", jogo.getNome(), e);
        }
        return false;
    }

    public Optional<Jogo> buscarPorId(int id) {
        String sql = """
            SELECT id, nome, preco, genero, desenvolvedora, plataforma, 
                   ano_lancamento, classificacao, descricao, data_cadastro, data_atualizacao
            FROM jogos WHERE id = ?
        """;

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(criarJogoDoResultSet(rs));
            }

        } catch (SQLException e) {
            logger.error("Erro ao buscar jogo por ID: {}", id, e);
        }
        return Optional.empty();
    }

    public Optional<Jogo> buscarPorNome(String nome) {
        String sql = """
            SELECT id, nome, preco, genero, desenvolvedora, plataforma, 
                   ano_lancamento, classificacao, descricao, data_cadastro, data_atualizacao
            FROM jogos WHERE LOWER(nome) = LOWER(?)
        """;

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nome);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(criarJogoDoResultSet(rs));
            }

        } catch (SQLException e) {
            logger.error("Erro ao buscar jogo por nome: {}", nome, e);
        }
        return Optional.empty();
    }

    public List<Jogo> listarTodos() {
        List<Jogo> jogos = new ArrayList<>();
        String sql = """
            SELECT id, nome, preco, genero, desenvolvedora, plataforma, 
                   ano_lancamento, classificacao, descricao, data_cadastro, data_atualizacao
            FROM jogos ORDER BY nome
        """;

        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                jogos.add(criarJogoDoResultSet(rs));
            }

            logger.debug("Listados {} jogos", jogos.size());

        } catch (SQLException e) {
            logger.error("Erro ao listar jogos", e);
        }
        return jogos;
    }

    public List<Jogo> buscarPorGenero(String genero) {
        List<Jogo> jogos = new ArrayList<>();
        String sql = """
            SELECT id, nome, preco, genero, desenvolvedora, plataforma, 
                   ano_lancamento, classificacao, descricao, data_cadastro, data_atualizacao
            FROM jogos WHERE LOWER(genero) LIKE LOWER(?) ORDER BY nome
        """;

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + genero + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                jogos.add(criarJogoDoResultSet(rs));
            }

        } catch (SQLException e) {
            logger.error("Erro ao buscar jogos por gênero: {}", genero, e);
        }
        return jogos;
    }

    public boolean atualizar(Jogo jogo) {
        String sql = """
            UPDATE jogos SET nome = ?, preco = ?, genero = ?, desenvolvedora = ?, 
                           plataforma = ?, ano_lancamento = ?, classificacao = ?, 
                           descricao = ?, data_atualizacao = ?
            WHERE id = ?
        """;

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            LocalDateTime agora = LocalDateTime.now();
            jogo.setDataAtualizacao(agora);

            pstmt.setString(1, jogo.getNome());
            pstmt.setDouble(2, jogo.getPreco());
            pstmt.setString(3, jogo.getGenero());
            pstmt.setString(4, jogo.getDesenvolvedora());
            pstmt.setString(5, jogo.getPlataforma());
            pstmt.setInt(6, jogo.getAnoLancamento());
            pstmt.setDouble(7, jogo.getClassificacao());
            pstmt.setString(8, jogo.getDescricao());
            pstmt.setTimestamp(9, Timestamp.valueOf(agora));
            pstmt.setInt(10, jogo.getId());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Jogo atualizado com sucesso: {}", jogo.getNome());
                return true;
            }

        } catch (SQLException e) {
            logger.error("Erro ao atualizar jogo: {}", jogo.getNome(), e);
        }
        return false;
    }

    public boolean excluir(int id) {
        String sql = "DELETE FROM jogos WHERE id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                logger.info("Jogo excluído com sucesso. ID: {}", id);
                return true;
            }

        } catch (SQLException e) {
            logger.error("Erro ao excluir jogo. ID: {}", id, e);
        }
        return false;
    }

    public int contarJogos() {
        String sql = "SELECT COUNT(*) FROM jogos";

        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            logger.error("Erro ao contar jogos", e);
        }
        return 0;
    }

    private Jogo criarJogoDoResultSet(ResultSet rs) throws SQLException {
        return new Jogo(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getDouble("preco"),
                rs.getString("genero"),
                rs.getString("desenvolvedora"),
                rs.getString("plataforma"),
                rs.getInt("ano_lancamento"),
                rs.getDouble("classificacao"),
                rs.getString("descricao"),
                rs.getTimestamp("data_cadastro").toLocalDateTime(),
                rs.getTimestamp("data_atualizacao").toLocalDateTime()
        );
    }
} 