package main.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import main.model.Acessibilidade;
import main.util.ConexaoMySQL;

public class AcessibilidadeDao implements RepositoryDao<Acessibilidade> {

    @Override
    public void salvar(Acessibilidade a) {
        String sql = "INSERT INTO Chess_acessibilidade (id, contraste, tamanho_pecas) VALUES (?, ?, ?)";
        try (Connection conn = ConexaoMySQL.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, a.getId());
            stmt.setBoolean(2, a.isContraste());
            stmt.setInt(3, a.getTamanhoPecas());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar configurações de acessibilidade: " + e.getMessage(), e);
        }
    }

    @Override
    public void atualizar(Acessibilidade a) {
        String sql = "UPDATE Chess_acessibilidade SET contraste = ?, tamanho_pecas = ? WHERE id = ?";
        try (Connection conn = ConexaoMySQL.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, a.isContraste());
            stmt.setInt(2, a.getTamanhoPecas());
            stmt.setInt(3, a.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar configurações de acessibilidade: " + e.getMessage(), e);
        }
    }

    @Override
    public Acessibilidade buscarPorId(int id) {
        String sql = "SELECT id, contraste, tamanho_pecas FROM Chess_acessibilidade WHERE id = ?";
        try (Connection conn = ConexaoMySQL.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Acessibilidade(
                            rs.getInt("id"),
                            rs.getBoolean("contraste"),
                            rs.getInt("tamanho_pecas")
                    );
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar configurações de acessibilidade: " + e.getMessage(), e);
        }
    }
}