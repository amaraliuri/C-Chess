package main.model;

import java.util.Objects;

public class Posicao {
    private int linha;
    private int coluna;

    public Posicao(int linha, int coluna) {
        this.linha = linha;
        this.coluna = coluna;
    }

    public String toNotacaoAlgebraica() {
        char colChar = (char) ('a' + coluna);
        int linNum = 8 - linha;
        return "" + colChar + linNum;
    }

    public static Posicao fromNotacaoAlgebraica(String notacao) {
        if (notacao == null || notacao.length() < 2) {
            throw new IllegalArgumentException("Notação algébrica inválida: " + notacao);
        }
        char colChar = Character.toLowerCase(notacao.charAt(0));
        int linNum = Character.getNumericValue(notacao.charAt(1));
        int coluna = colChar - 'a';
        int linha = 8 - linNum;
        return new Posicao(linha, coluna);
    }

    public boolean isValida() {
        return linha >= 0 && linha < 8 && coluna >= 0 && coluna < 8;
    }

    public int getLinha() {
        return linha;
    }

    public void setLinha(int linha) {
        this.linha = linha;
    }

    public int getColuna() {
        return coluna;
    }

    public void setColuna(int coluna) {
        this.coluna = coluna;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Posicao posicao)) return false;
        return linha == posicao.linha && coluna == posicao.coluna;
    }

    @Override
    public int hashCode() {
        return Objects.hash(linha, coluna);
    }

    @Override
    public String toString() {
        return toNotacaoAlgebraica();
    }
}