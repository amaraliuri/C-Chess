package main.model;

import java.util.ArrayList;
import java.util.List;

public abstract class Peca {
    protected String cor;
    protected Posicao posicao;
    protected boolean jaMoveu;

    public Peca(String cor, Posicao posicao) {
        this.cor = cor;
        this.posicao = posicao;
        this.jaMoveu = false;
    }

    public abstract List<Posicao> calcMovPossiveis(Tabuleiro tabuleiro);

    public void moverPara(Posicao destino) {
        this.posicao = destino;
        this.jaMoveu = true;
    }

    protected List<Posicao> calcularMovimentosLineares(Tabuleiro tabuleiro, int[][] direcoes) {
        List<Posicao> movimentos = new ArrayList<>();
        for (int[] dir : direcoes) {
            int lin = posicao.getLinha() + dir[0];
            int col = posicao.getColuna() + dir[1];

            while (lin >= 0 && lin < 8 && col >= 0 && col < 8) {
                Posicao destino = new Posicao(lin, col);
                Peca pecaDestino = tabuleiro.obterPecas(destino);

                if (pecaDestino == null) {
                    movimentos.add(destino);
                } else {
                    if (!pecaDestino.getCor().equals(this.cor)) {
                        movimentos.add(destino);
                    }
                    break;
                }
                lin += dir[0];
                col += dir[1];
            }
        }
        return movimentos;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public Posicao getPosicao() {
        return posicao;
    }

    public void setPosicao(Posicao posicao) {
        this.posicao = posicao;
    }

    public boolean isJaMoveu() {
        return jaMoveu;
    }

    public void setJaMoveu(boolean jaMoveu) {
        this.jaMoveu = jaMoveu;
    }
}