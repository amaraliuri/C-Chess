// Bispo.java
package main.model;

import java.util.List;

public class Bispo extends Peca {

    public Bispo(String cor, Posicao posicao) {
        super(cor, posicao);
    }

    @Override
    public List<Posicao> calcMovPossiveis(Tabuleiro tabuleiro) {
        int[][] direcoes = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        return calcularMovimentosLineares(tabuleiro, direcoes);
    }
}