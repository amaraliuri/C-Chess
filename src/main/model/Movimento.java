package main.model;

public class Movimento {
    private Posicao origem;
    private Posicao destino;
    private Peca pecaCapturada;

    public Movimento(Posicao origem, Posicao destino) {
        this.origem = origem;
        this.destino = destino;
    }

    public Movimento(Posicao origem, Posicao destino, Peca pecaCapturada) {
        this.origem = origem;
        this.destino = destino;
        this.pecaCapturada = pecaCapturada;
    }

    public Posicao getOrigem() {
        return origem;
    }

    public void setOrigem(Posicao origem) {
        this.origem = origem;
    }

    public Posicao getDestino() {
        return destino;
    }

    public void setDestino(Posicao destino) {
        this.destino = destino;
    }

    public Peca getPecaCapturada() {
        return pecaCapturada;
    }

    public void setPecaCapturada(Peca pecaCapturada) {
        this.pecaCapturada = pecaCapturada;
    }
}