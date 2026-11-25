package main.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import main.model.Partida;
import main.util.ConexaoMySQL;

public class PartidaDao implements RepositoryDao<Partida> {

    @Override
    public void salvar(Partida p) {
        String sql = "INSERT INTO Chess_partida (usuario_id, turno_atual, status, resultado, data_hora) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexaoMySQL.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, p.getUsuario().getId());
            stmt.setString(2, p.getTurnoAtual());
            stmt.setString(3, p.getStatus());
            stmt.setString(4, p.getResultado());
            stmt.setTimestamp(5, Timestamp.valueOf(p.getDataHora()));
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    p.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar partida no histórico: " + e.getMessage(), e);
        }
    }

    @Override
    public void atualizar(Partida p) {
        String sql = "UPDATE Chess_partida SET turno_atual = ?, status = ?, resultado = ?, data_hora = ? WHERE id = ?";
        try (Connection conn = ConexaoMySQL.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, p.getTurnoAtual());
            stmt.setString(2, p.getStatus());
            stmt.setString(3, p.getResultado());
            stmt.setTimestamp(4, Timestamp.valueOf(p.getDataHora()));
            stmt.setInt(5, p.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar partida: " + e.getMessage(), e);
        }
    }

    @Override
    public Partida buscarPorId(int id) {
        String sql = "SELECT id, turno_atual, status, resultado, data_hora FROM Chess_partida WHERE id = ?";
        try (Connection conn = ConexaoMySQL.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearPartida(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar partida por ID: " + e.getMessage(), e);
        }
    }

    public List<Partida> listarPorUsuario(int usuarioId) {
        String sql = "SELECT id, turno_atual, status, resultado, data_hora FROM Chess_partida WHERE usuario_id = ? ORDER BY data_hora DESC";
        List<Partida> partidas = new ArrayList<>();

        try (Connection conn = ConexaoMySQL.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    partidas.add(mapearPartida(rs));
                }
            }
            return partidas;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar histórico de partidas: " + e.getMessage(), e);
        }
    }

    private Partida mapearPartida(ResultSet rs) throws SQLException {
        Partida p = new Partida();
        p.setId(rs.getInt("id"));
        p.setTurnoAtual(rs.getString("turno_atual"));
        p.setStatus(rs.getString("status"));
        p.setResultado(rs.getString("resultado"));
        Timestamp ts = rs.getTimestamp("data_hora");
        if (ts != null) {
            p.setDataHora(ts.toLocalDateTime());
        }
        return p;
    }
}