// Rainha.java
package main.model;

import java.util.List;

public class Rainha extends Peca {

    public Rainha(String cor, Posicao posicao) {
        super(cor, posicao);
    }

    @Override
    public List<Posicao> calcMovPossiveis(Tabuleiro tabuleiro) {
        int[][] direcoes = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1},
            {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
        };
        return calcularMovimentosLineares(tabuleiro, direcoes);
    }
}