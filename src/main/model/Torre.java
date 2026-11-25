// Torre.java
package main.model;

import java.util.List;

public class Torre extends Peca {

    public Torre(String cor, Posicao posicao) {
        super(cor, posicao);
    }

    @Override
    public List<Posicao> calcMovPossiveis(Tabuleiro tabuleiro) {
        int[][] direcoes = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        return calcularMovimentosLineares(tabuleiro, direcoes);
    }
}