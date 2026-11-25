package main.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ConexaoMySQL {

    private static final Map<String, String> ENV = carregarEnv();

    private static Map<String, String> carregarEnv() {
        Map<String, String> mapa = new HashMap<>();
        Path caminhoEnv = Path.of(".env");
        if (Files.exists(caminhoEnv)) {
            try {
                for (String linha : Files.readAllLines(caminhoEnv)) {
                    String trimmed = linha.trim();
                    if (trimmed.isEmpty() || trimmed.startsWith("#") || !trimmed.contains("=")) {
                        continue;
                    }
                    int idx = trimmed.indexOf('=');
                    String chave = trimmed.substring(0, idx).trim();
                    String valor = trimmed.substring(idx + 1).trim();
                    mapa.put(chave, valor);
                }
            } catch (IOException e) {
                System.err.println("Aviso: não foi possível ler o arquivo .env: " + e.getMessage());
            }
        }
        return mapa;
    }

    public static String getVariavelObrigatoria(String chave) {
        String valor = ENV.get(chave);
        if (valor == null || valor.isBlank()) {
            valor = System.getenv(chave);
        }
        if (valor == null || valor.isBlank()) {
            throw new IllegalStateException(
                    "Variável obrigatória '" + chave + "' não definida. Configure-a no arquivo .env na raiz do projeto."
            );
        }
        return valor;
    }

    public static Connection conectar() throws SQLException {
        String host = getVariavelObrigatoria("DB_HOST");
        String port = getVariavelObrigatoria("DB_PORT");
        String dbName = getVariavelObrigatoria("DB_NAME");
        String user = getVariavelObrigatoria("DB_USER");
        String password = getVariavelObrigatoria("DB_PASSWORD");

        String url = String.format(
                "jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=America/Sao_Paulo&allowPublicKeyRetrieval=true",
                host, port, dbName
        );

        return DriverManager.getConnection(url, user, password);
    }
}