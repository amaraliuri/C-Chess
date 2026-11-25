// Partida.java
package main.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Partida {
    private int id;
    private String turnoAtual;
    private String status;
    private String resultado;
    private LocalDateTime dataHora;
    private Tabuleiro tabuleiro;
    private List<Jogador> jogadores;
    private Usuario usuario;

    public Partida() {
        this.tabuleiro = new Tabuleiro();
        this.jogadores = new ArrayList<>(2);
        this.dataHora = LocalDateTime.now();
    }

    public Partida(Usuario usuario, Jogador jogadorBranco, Jogador jogadorPreto) {
        this();
        this.usuario = usuario;
        this.jogadores.add(jogadorBranco);
        this.jogadores.add(jogadorPreto);
    }

    public void iniciarPartida() {
        this.tabuleiro.inicializarPecas();
        this.turnoAtual = "BRANCA";
        this.status = "EM_ANDAMENTO";
        this.resultado = "EM_ANDAMENTO";
        this.dataHora = LocalDateTime.now();
    }

    public boolean processarJogada(Movimento mov) {
        if (!"EM_ANDAMENTO".equals(this.status) || mov == null) {
            return false;
        }

        Peca peca = tabuleiro.obterPecas(mov.getOrigem());
        if (peca == null || !peca.getCor().equals(this.turnoAtual)) {
            return false;
        }

        List<Posicao> movimentosLegais = tabuleiro.obterMovimentosLegais(mov.getOrigem());
        if (!movimentosLegais.contains(mov.getDestino())) {
            return false;
        }

        tabuleiro.executarMov(mov);

        String adversario = "BRANCA".equals(this.turnoAtual) ? "PRETA" : "BRANCA";
        if (tabuleiro.verifXequeMate(adversario)) {
            this.resultado = "BRANCA".equals(this.turnoAtual) ? "VITORIA_BRANCAS" : "VITORIA_PRETAS";
            finalizarPartida();
        } else {
            trocarTurno();
        }

        return true;
    }

    public void trocarTurno() {
        this.turnoAtual = "BRANCA".equals(this.turnoAtual) ? "PRETA" : "BRANCA";
    }

    public void finalizarPartida() {
        this.status = "FINALIZADA";
        this.dataHora = LocalDateTime.now();
    }

    public Jogador getJogadorAtual() {
        for (Jogador j : jogadores) {
            if (j.getCor().equals(turnoAtual)) {
                return j;
            }
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTurnoAtual() {
        return turnoAtual;
    }

    public void setTurnoAtual(String turnoAtual) {
        this.turnoAtual = turnoAtual;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public Tabuleiro getTabuleiro() {
        return tabuleiro;
    }

    public void setTabuleiro(Tabuleiro tabuleiro) {
        this.tabuleiro = tabuleiro;
    }

    public List<Jogador> getJogadores() {
        return jogadores;
    }

    public void setJogadores(List<Jogador> jogadores) {
        this.jogadores = jogadores;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}