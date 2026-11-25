package main.model;

import main.util.ConexaoMySQL;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.json.JSONObject;

public class ChessApiBot implements EstrategiaJogador {
    private String endpointUrl;

    public ChessApiBot() {
        this.endpointUrl = ConexaoMySQL.getVariavelObrigatoria("CHESS_API_URL");
    }

    public ChessApiBot(String endpointUrl) {
        this.endpointUrl = endpointUrl;
    }

    @Override
    public Movimento escolherJogada(Tabuleiro tabuleiro) {
        String fen = tabuleiro.gerarFen();
        String jsonResposta = consultarApiRest(fen);

        JSONObject obj = new JSONObject(jsonResposta);
        if (obj.has("error") && !obj.isNull("error")) {
            throw new RuntimeException("Erro retornado pela Chess-API: " + obj.getString("error"));
        }

        String from = obj.getString("from");
        String to = obj.getString("to");

        Posicao origem = Posicao.fromNotacaoAlgebraica(from);
        Posicao destino = Posicao.fromNotacaoAlgebraica(to);
        return new Movimento(origem, destino);
    }

    public String consultarApiRest(String fen) {
        try {
            JSONObject payload = new JSONObject();
            payload.put("fen", fen);

            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpointUrl))
                    .timeout(Duration.ofSeconds(15))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException("Falha HTTP na Chess-API. Código: " + response.statusCode());
            }

            return response.body();
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível obter jogada da Chess-API: " + e.getMessage(), e);
        }
    }

    public String getEndpointUrl() {
        return endpointUrl;
    }

    public void setEndpointUrl(String endpointUrl) {
        this.endpointUrl = endpointUrl;
    }
}