package main.model;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import main.util.ConexaoMySQL;
import org.json.JSONObject;

public class ChessApiBot implements EstrategiaJogador {
    private static final String FALLBACK_API_URL = "https://stockfish.online/api/s/v2.php";
    private String endpointUrl;
    private final HttpClient client;

    public ChessApiBot() {
        this.endpointUrl = ConexaoMySQL.getVariavelObrigatoria("CHESS_API_URL");
        this.client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(6))
                .build();
    }

    public ChessApiBot(String endpointUrl) {
        this();
        this.endpointUrl = endpointUrl;
    }

    @Override
    public Movimento escolherJogada(Tabuleiro tabuleiro) {
        String fen = tabuleiro.gerarFen();

        // 1ª tentativa: API principal (chess-api.com)
        try {
            String jsonResposta = consultarApiRest(fen);
            JSONObject obj = new JSONObject(jsonResposta);
            if (!obj.has("error") || obj.isNull("error")) {
                Posicao origem = Posicao.fromNotacaoAlgebraica(obj.getString("from"));
                Posicao destino = Posicao.fromNotacaoAlgebraica(obj.getString("to"));
                return new Movimento(origem, destino);
            }
        } catch (Exception e) {
            System.err.println("Aviso: Chess-API indisponível (" + e.getMessage() + "). Tentando API secundária...");
        }

        // 2ª tentativa: API REST secundária (stockfish.online)
        try {
            return consultarStockfishOnline(fen);
        } catch (Exception e) {
            System.err.println("Aviso: API secundária indisponível (" + e.getMessage() + "). Usando lance local...");
        }

        // 3ª tentativa (contingência offline): lance legal local
        return escolherLanceLocalContingencia(tabuleiro);
    }

    public String consultarApiRest(String fen) {
        try {
            JSONObject payload = new JSONObject();
            payload.put("fen", fen);
            payload.put("depth", 10);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpointUrl.trim()))
                    .timeout(Duration.ofSeconds(8))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException("HTTP " + response.statusCode());
            }

            return response.body();
        } catch (Exception e) {
            String motivo = (e.getMessage() != null && !e.getMessage().isBlank())
                    ? e.getMessage()
                    : e.getClass().getSimpleName();
            throw new RuntimeException(motivo, e);
        }
    }

    private Movimento consultarStockfishOnline(String fen) throws Exception {
        String fenCodificado = URLEncoder.encode(fen, StandardCharsets.UTF_8);
        URI uri = URI.create(FALLBACK_API_URL + "?fen=" + fenCodificado + "&depth=10");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .timeout(Duration.ofSeconds(8))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException("HTTP " + response.statusCode());
        }

        JSONObject obj = new JSONObject(response.body());
        if (!obj.optBoolean("success", false)) {
            throw new RuntimeException("Resposta sem sucesso da Stockfish Online");
        }

        // Formato retornado: "bestmove e7e5 ponder g1f3"
        String bestMoveLine = obj.getString("bestmove");
        String[] partes = bestMoveLine.trim().split("\\s+");
        if (partes.length < 2 || partes[1].length() < 4) {
            throw new RuntimeException("Formato de lance inesperado: " + bestMoveLine);
        }

        String lance = partes[1];
        Posicao origem = Posicao.fromNotacaoAlgebraica(lance.substring(0, 2));
        Posicao destino = Posicao.fromNotacaoAlgebraica(lance.substring(2, 4));
        return new Movimento(origem, destino);
    }

    private Movimento escolherLanceLocalContingencia(Tabuleiro tabuleiro) {
        List<Movimento> lancesLegais = new ArrayList<>();
        Peca[][] pecas = tabuleiro.getPecas();

        for (int lin = 0; lin < 8; lin++) {
            for (int col = 0; col < 8; col++) {
                Peca p = pecas[lin][col];
                if (p != null && "PRETA".equals(p.getCor())) {
                    for (Posicao destino : tabuleiro.obterMovimentosLegais(p.getPosicao())) {
                        lancesLegais.add(new Movimento(p.getPosicao(), destino));
                    }
                }
            }
        }

        if (lancesLegais.isEmpty()) {
            throw new RuntimeException("Nenhum movimento legal disponível para o computador.");
        }

        return lancesLegais.get(new Random().nextInt(lancesLegais.size()));
    }

    public String getEndpointUrl() {
        return endpointUrl;
    }

    public void setEndpointUrl(String endpointUrl) {
        this.endpointUrl = endpointUrl;
    }
}