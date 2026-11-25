package main.dao;

import br.com.ifpr.cchess.model.Acessibilidade;
import br.com.ifpr.cchess.util.ConexaoMySQL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AcessibilidadeDao implements RepositoryDao<Acessibilidade> {

    @Override
    public void salvar(Acessibilidade a) {
        String sql = "INSERT INTO acessibilidade (id, contraste, tamanho_pecas) VALUES (?, ?, ?)";
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
        String sql = "UPDATE acessibilidade SET contraste = ?, tamanho_pecas = ? WHERE id = ?";
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
        String sql = "SELECT id, contraste, tamanho_pecas FROM acessibilidade WHERE id = ?";
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