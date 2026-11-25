package main.model;

import java.util.ArrayList;
import java.util.List;

public class Tabuleiro {
    private Peca[][] pecas;
    private String turnoFen;

    public Tabuleiro() {
        this.pecas = new Peca[8][8];
        this.turnoFen = "w";
    }

    public void inicializarPecas() {
        this.pecas = new Peca[8][8];
        this.turnoFen = "w";

        // Peças Pretas (linhas 0 e 1)
        pecas[0][0] = new Torre("PRETA", new Posicao(0, 0));
        pecas[0][1] = new Cavalo("PRETA", new Posicao(0, 1));
        pecas[0][2] = new Bispo("PRETA", new Posicao(0, 2));
        pecas[0][3] = new Rainha("PRETA", new Posicao(0, 3));
        pecas[0][4] = new Rei("PRETA", new Posicao(0, 4));
        pecas[0][5] = new Bispo("PRETA", new Posicao(0, 5));
        pecas[0][6] = new Cavalo("PRETA", new Posicao(0, 6));
        pecas[0][7] = new Torre("PRETA", new Posicao(0, 7));

        for (int col = 0; col < 8; col++) {
            pecas[1][col] = new Peao("PRETA", new Posicao(1, col));
        }

        // Peças Brancas (linhas 6 e 7)
        for (int col = 0; col < 8; col++) {
            pecas[6][col] = new Peao("BRANCA", new Posicao(6, col));
        }

        pecas[7][0] = new Torre("BRANCA", new Posicao(7, 0));
        pecas[7][1] = new Cavalo("BRANCA", new Posicao(7, 1));
        pecas[7][2] = new Bispo("BRANCA", new Posicao(7, 2));
        pecas[7][3] = new Rainha("BRANCA", new Posicao(7, 3));
        pecas[7][4] = new Rei("BRANCA", new Posicao(7, 4));
        pecas[7][5] = new Bispo("BRANCA", new Posicao(7, 5));
        pecas[7][6] = new Cavalo("BRANCA", new Posicao(7, 6));
        pecas[7][7] = new Torre("BRANCA", new Posicao(7, 7));
    }

    public Peca obterPecas(Posicao pos) {
        if (pos == null || !pos.isValida()) {
            return null;
        }
        return pecas[pos.getLinha()][pos.getColuna()];
    }

    public void executarMov(Movimento mov) {
        Posicao origem = mov.getOrigem();
        Posicao destino = mov.getDestino();
        Peca peca = obterPecas(origem);

        if (peca == null) {
            return;
        }

        Peca capturada = obterPecas(destino);
        if (capturada != null) {
            mov.setPecaCapturada(capturada);
        }

        pecas[origem.getLinha()][origem.getColuna()] = null;
        peca.moverPara(destino);

        // Promoção de peão ao atingir a última fileira
        if (peca instanceof Peao && (destino.getLinha() == 0 || destino.getLinha() == 7)) {
            peca = new Rainha(peca.getCor(), destino);
            peca.setJaMoveu(true);
        }

        pecas[destino.getLinha()][destino.getColuna()] = peca;
        this.turnoFen = "BRANCA".equals(peca.getCor()) ? "b" : "w";
    }

    public List<Posicao> obterMovimentosLegais(Posicao origem) {
        List<Posicao> legais = new ArrayList<>();
        Peca peca = obterPecas(origem);
        if (peca == null) {
            return legais;
        }

        List<Posicao> candidatos = peca.calcMovPossiveis(this);
        for (Posicao destino : candidatos) {
            if (!simularDeixaEmXeque(origem, destino, peca.getCor())) {
                legais.add(destino);
            }
        }
        return legais;
    }

    private boolean simularDeixaEmXeque(Posicao origem, Posicao destino, String cor) {
        Peca pecaOrigem = pecas[origem.getLinha()][origem.getColuna()];
        Peca pecaDestino = pecas[destino.getLinha()][destino.getColuna()];
        Posicao posOriginal = pecaOrigem.getPosicao();

        pecas[destino.getLinha()][destino.getColuna()] = pecaOrigem;
        pecas[origem.getLinha()][origem.getColuna()] = null;
        pecaOrigem.setPosicao(destino);

        boolean emXeque = verifXeque(cor);

        pecaOrigem.setPosicao(posOriginal);
        pecas[origem.getLinha()][origem.getColuna()] = pecaOrigem;
        pecas[destino.getLinha()][destino.getColuna()] = pecaDestino;

        return emXeque;
    }

    public boolean verifXeque(String cor) {
        Posicao posRei = localizarRei(cor);
        if (posRei == null) {
            return false;
        }

        for (int lin = 0; lin < 8; lin++) {
            for (int col = 0; col < 8; col++) {
                Peca p = pecas[lin][col];
                if (p != null && !p.getCor().equals(cor)) {
                    if (p.calcMovPossiveis(this).contains(posRei)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean verifXequeMate(String cor) {
        if (!verifXeque(cor)) {
            return false;
        }

        for (int lin = 0; lin < 8; lin++) {
            for (int col = 0; col < 8; col++) {
                Peca p = pecas[lin][col];
                if (p != null && p.getCor().equals(cor)) {
                    if (!obterMovimentosLegais(p.getPosicao()).isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private Posicao localizarRei(String cor) {
        for (int lin = 0; lin < 8; lin++) {
            for (int col = 0; col < 8; col++) {
                Peca p = pecas[lin][col];
                if (p instanceof Rei && p.getCor().equals(cor)) {
                    return p.getPosicao();
                }
            }
        }
        return null;
    }

    public String gerarFen() {
        StringBuilder fen = new StringBuilder();

        for (int lin = 0; lin < 8; lin++) {
            int vazias = 0;
            for (int col = 0; col < 8; col++) {
                Peca p = pecas[lin][col];
                if (p == null) {
                    vazias++;
                } else {
                    if (vazias > 0) {
                        fen.append(vazias);
                        vazias = 0;
                    }
                    fen.append(obterCaractereFen(p));
                }
            }
            if (vazias > 0) {
                fen.append(vazias);
            }
            if (lin < 7) {
                fen.append('/');
            }
        }

        fen.append(" ").append(turnoFen).append(" - - 0 1");
        return fen.toString();
    }

    private char obterCaractereFen(Peca p) {
        char simbolo = switch (p) {
            case Peao _ -> 'p';
            case Torre _ -> 'r';
            case Cavalo _ -> 'n';
            case Bispo _ -> 'b';
            case Rainha _ -> 'q';
            case Rei _ -> 'k';
            default -> '?';
        };
        return "BRANCA".equals(p.getCor()) ? Character.toUpperCase(simbolo) : simbolo;
    }

    public Peca[][] getPecas() {
        return pecas;
    }
}