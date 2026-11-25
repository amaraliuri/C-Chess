// JogadorHumano.java
package main.model;

public class JogadorHumano implements EstrategiaJogador {
    private Movimento proximoMovimento;

    public void definirMovimento(Movimento movimento) {
        this.proximoMovimento = movimento;
    }

    @Override
    public Movimento escolherJogada(Tabuleiro tabuleiro) {
        Movimento mov = this.proximoMovimento;
        this.proximoMovimento = null;
        return mov;
    }
}