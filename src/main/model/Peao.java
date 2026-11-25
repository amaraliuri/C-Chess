// Peao.java
package main.model;

import java.util.ArrayList;
import java.util.List;

public class Peao extends Peca {

    public Peao(String cor, Posicao posicao) {
        super(cor, posicao);
    }

    @Override
    public List<Posicao> calcMovPossiveis(Tabuleiro tabuleiro) {
        List<Posicao> movimentos = new ArrayList<>();
        int direcao = "BRANCA".equals(cor) ? -1 : 1;
        int linhaInicial = "BRANCA".equals(cor) ? 6 : 1;

        int linFrente = posicao.getLinha() + direcao;
        int colAtual = posicao.getColuna();

        Posicao umPasso = new Posicao(linFrente, colAtual);
        if (umPasso.isValida() && tabuleiro.obterPecas(umPasso) == null) {
            movimentos.add(umPasso);

            if (!jaMoveu && posicao.getLinha() == linhaInicial) {
                Posicao doisPassos = new Posicao(posicao.getLinha() + (2 * direcao), colAtual);
                if (doisPassos.isValida() && tabuleiro.obterPecas(doisPassos) == null) {
                    movimentos.add(doisPassos);
                }
            }
        }

        int[] diagCols = {colAtual - 1, colAtual + 1};
        for (int dc : diagCols) {
            Posicao diag = new Posicao(linFrente, dc);
            if (diag.isValida()) {
                Peca alvo = tabuleiro.obterPecas(diag);
                if (alvo != null && !alvo.getCor().equals(this.cor)) {
                    movimentos.add(diag);
                }
            }
        }

        return movimentos;
    }
}