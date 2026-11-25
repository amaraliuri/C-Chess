// Cavalo.java
package main.model;

import java.util.ArrayList;
import java.util.List;

public class Cavalo extends Peca {

    public Cavalo(String cor, Posicao posicao) {
        super(cor, posicao);
    }

    @Override
    public List<Posicao> calcMovPossiveis(Tabuleiro tabuleiro) {
        List<Posicao> movimentos = new ArrayList<>();
        int[][] saltos = {
            {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
            {1, -2}, {1, 2}, {2, -1}, {2, 1}
        };

        for (int[] salto : saltos) {
            Posicao destino = new Posicao(posicao.getLinha() + salto[0], posicao.getColuna() + salto[1]);
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