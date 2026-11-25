// Rei.java
package main.model;

import java.util.ArrayList;
import java.util.List;

public class Rei extends Peca {

    public Rei(String cor, Posicao posicao) {
        super(cor, posicao);
    }

    @Override
    public List<Posicao> calcMovPossiveis(Tabuleiro tabuleiro) {
        List<Posicao> movimentos = new ArrayList<>();
        int[][] passos = {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1},           {0, 1},
            {1, -1},  {1, 0},  {1, 1}
        };

        for (int[] passo : passos) {
            Posicao destino = new Posicao(posicao.getLinha() + passo[0], posicao.getColuna() + passo[1]);
            if (destino.isValida()) {
                Peca alvo = tabuleiro.obterPecas(destino);
                if (alvo == null || !alvo.getCor().equals(this.cor)) {
                    movimentos.add(destino);
                }
            }
        }
        return movimentos;
    }
}