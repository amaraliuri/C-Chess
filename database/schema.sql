CREATE DATABASE IF NOT EXISTS cchess
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE cchess;

CREATE TABLE IF NOT EXISTS Chess_usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    login VARCHAR(50) NOT NULL UNIQUE,
    senha VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS Chess_acessibilidade (
    id INT PRIMARY KEY,
    contraste BOOLEAN NOT NULL DEFAULT FALSE,
    tamanho_pecas INT NOT NULL DEFAULT 64,
    CONSTRAINT fk_acessibilidade_usuario
        FOREIGN KEY (id) REFERENCES Chess_usuario(id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Chess_partida (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    turno_atual VARCHAR(20) NOT NULL,
    status VARCHAR(30) NOT NULL,
    resultado VARCHAR(50) NOT NULL,
    data_hora DATETIME NOT NULL,
    CONSTRAINT fk_partida_usuario
        FOREIGN KEY (usuario_id) REFERENCES Chess_usuario(id)
        ON DELETE CASCADE
);