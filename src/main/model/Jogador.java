// Jogador.java
package main.model;

public class Jogador {
    private String nome;
    private String cor;
    private EstrategiaJogador estrategia;

    public Jogador(String nome, String cor, EstrategiaJogador estrategia) {
        this.nome = nome;
        this.cor = cor;
        this.estrategia = estrategia;
    }

    public Movimento jogar(Tabuleiro tabuleiro) {
        return estrategia.escolherJogada(tabuleiro);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public EstrategiaJogador getEstrategia() {
        return estrategia;
    }

    public void setEstrategia(EstrategiaJogador estrategia) {
        this.estrategia = estrategia;
    }
}