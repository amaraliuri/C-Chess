package main.dao;

public interface RepositoryDao<T> {
    void salvar(T entidade);
    void atualizar(T entidade);
    T buscarPorId(int id);
}