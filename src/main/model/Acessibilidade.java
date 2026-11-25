// Acessibilidade.java
package main.model;

public class Acessibilidade {
    private int id;
    private boolean contraste;
    private int tamanhoPecas;

    public Acessibilidade() {
        this.contraste = false;
        this.tamanhoPecas = 64;
    }

    public Acessibilidade(int id, boolean contraste, int tamanhoPecas) {
        this.id = id;
        this.contraste = contraste;
        this.tamanhoPecas = tamanhoPecas;
    }

    public void aplicarConfig() {
        if (this.tamanhoPecas < 44) {
            this.tamanhoPecas = 44;
        } else if (this.tamanhoPecas > 96) {
            this.tamanhoPecas = 96;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isContraste() {
        return contraste;
    }

    public void setContraste(boolean contraste) {
        this.contraste = contraste;
    }

    public int getTamanhoPecas() {
        return tamanhoPecas;
    }

    public void setTamanhoPecas(int tamanhoPecas) {
        this.tamanhoPecas = tamanhoPecas;
    }
}