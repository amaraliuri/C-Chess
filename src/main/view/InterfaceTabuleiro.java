package main.view;

import main.dao.PartidaDao;
import main.model.Acessibilidade;
import main.model.Bispo;
import main.model.Cavalo;
import main.model.ChessApiBot;
import main.model.Jogador;
import main.model.JogadorHumano;
import main.model.Movimento;
import main.model.Partida;
import main.model.Peao;
import main.model.Peca;
import main.model.Posicao;
import main.model.Rainha;
import main.model.Rei;
import main.model.Tabuleiro;
import main.model.Torre;
import main.model.Usuario;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

public class InterfaceTabuleiro extends JFrame {
    private final Usuario usuario;
    private final Partida partida;
    private final InterfaceMenu menuPai;
    private final PartidaDao partidaDao;

    private final JButton[][] botoesCasas;
    private final JLabel lblStatusTurno;
    private final JLabel lblAlertaXeque;
    private final JPanel painelPrincipal;

    private Posicao posicaoSelecionada;
    private List<Posicao> movimentosDestacados;
    private boolean bloqueadoParaIa;

    public InterfaceTabuleiro(Usuario usuario, Partida partida, InterfaceMenu menuPai) {
        this.usuario = usuario;
        this.partida = partida;
        this.menuPai = menuPai;
        this.partidaDao = new PartidaDao();
        this.botoesCasas = new JButton[8][8];
        this.movimentosDestacados = new ArrayList<>();
        this.bloqueadoParaIa = false;

        setTitle("C-Chess - Partida em Andamento");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        painelPrincipal = new JPanel(new BorderLayout(12, 12));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        JPanel painelTopo = new JPanel(new GridLayout(1, 3, 10, 0));
        lblStatusTurno = new JLabel("Turno: BRANCA", SwingConstants.LEFT);
        lblAlertaXeque = new JLabel("", SwingConstants.CENTER);
        lblAlertaXeque.setOpaque(true);

        JButton btnVoltarMenu = new JButton("Voltar ao Menu");
        btnVoltarMenu.addActionListener(_ -> {
            menuPai.setVisible(true);
            dispose();
        });

        painelTopo.add(lblStatusTurno);
        painelTopo.add(lblAlertaXeque);
        painelTopo.add(btnVoltarMenu);
        painelPrincipal.add(painelTopo, BorderLayout.NORTH);

        JPanel painelGrade = new JPanel(new GridLayout(8, 8));
        Acessibilidade acc = usuario.getAcessibilidade();
        int tamanhoCasa = acc.getTamanhoPecas() + 26;

        for (int lin = 0; lin < 8; lin++) {
            for (int col = 0; col < 8; col++) {
                final int fLin = lin;
                final int fCol = col;
                JButton btnCasa = new JButton();
                btnCasa.setPreferredSize(new Dimension(tamanhoCasa, tamanhoCasa));
                btnCasa.setFocusPainted(true);
                btnCasa.setOpaque(true);
                btnCasa.addActionListener(_ -> aoClicarCasa(fLin, fCol));
                botoesCasas[lin][col] = btnCasa;
                painelGrade.add(btnCasa);
            }
        }

        painelPrincipal.add(painelGrade, BorderLayout.CENTER);
        setContentPane(painelPrincipal);

        atualizarEstado();
        pack();
        setLocationRelativeTo(null);
    }

    private void aoClicarCasa(int linha, int coluna) {
        if (bloqueadoParaIa || !"EM_ANDAMENTO".equals(partida.getStatus())) {
            return;
        }

        Posicao clicada = new Posicao(linha, coluna);
        Tabuleiro tabuleiro = partida.getTabuleiro();
        Peca pecaClicada = tabuleiro.obterPecas(clicada);

        if (posicaoSelecionada == null) {
            selecionarPeca(clicada, pecaClicada);
            return;
        }

        if (posicaoSelecionada.equals(clicada)) {
            limparSelecao();
            atualizarEstado();
            return;
        }

        if (pecaClicada != null && pecaClicada.getCor().equals(partida.getTurnoAtual())) {
            selecionarPeca(clicada, pecaClicada);
            return;
        }

        selecionarDestino(clicada);
    }

    private void selecionarPeca(Posicao pos, Peca peca) {
        if (peca == null || !peca.getCor().equals(partida.getTurnoAtual())) {
            return;
        }
        this.posicaoSelecionada = pos;
        this.movimentosDestacados = partida.getTabuleiro().obterMovimentosLegais(pos);
        atualizarEstado();
    }

    private void selecionarDestino(Posicao destino) {
        Movimento mov = new Movimento(posicaoSelecionada, destino);
        Jogador jogadorAtual = partida.getJogadorAtual();
        if (jogadorAtual != null && jogadorAtual.getEstrategia() instanceof JogadorHumano humano) {
            humano.definirMovimento(mov);
            mov = jogadorAtual.jogar(partida.getTabuleiro());
        }

        boolean valido = partida.processarJogada(mov);
        if (!valido) {
            JOptionPane.showMessageDialog(this, "Jogada inválida! Selecione uma casa destacada.",
                    "Movimento Ilegal", JOptionPane.WARNING_MESSAGE);
            return;
        }

        limparSelecao();
        atualizarEstado();

        if ("FINALIZADA".equals(partida.getStatus())) {
            encerrarESalvarPartida();
            return;
        }

        verificarTurnoComputador();
    }

    private void verificarTurnoComputador() {
        Jogador proximo = partida.getJogadorAtual();
        if (proximo == null || !(proximo.getEstrategia() instanceof ChessApiBot)) {
            return;
        }

        bloqueadoParaIa = true;
        lblStatusTurno.setText("Turno: PRETA (Calculando IA...)");

        SwingWorker<Movimento, Void> worker = new SwingWorker<>() {
            @Override
            protected Movimento doInBackground() {
                return proximo.jogar(partida.getTabuleiro());
            }

            @Override
            protected void done() {
                bloqueadoParaIa = false;
                try {
                    Movimento movBot = get();
                    boolean executado = partida.processarJogada(movBot);
                    if (!executado) {
                        JOptionPane.showMessageDialog(InterfaceTabuleiro.this,
                                "A Chess-API retornou um lance inválido para a posição atual.",
                                "Aviso da IA", JOptionPane.WARNING_MESSAGE);
                    }
                    atualizarEstado();
                    if ("FINALIZADA".equals(partida.getStatus())) {
                        encerrarESalvarPartida();
                    }
                } catch (Exception ex) {
                    atualizarEstado();
                    JOptionPane.showMessageDialog(InterfaceTabuleiro.this,
                            "Não foi possível obter a jogada do computador.\nVerifique sua conexão com a internet.\nDetalhe: " + ex.getCause().getMessage(),
                            "Falha na Chess-API", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void encerrarESalvarPartida() {
        try {
            partidaDao.salvar(partida);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this,
                    "Partida finalizada, mas houve erro ao salvar no histórico:\n" + ex.getMessage(),
                    "Erro de Persistência", JOptionPane.ERROR_MESSAGE);
        }

        String vencedor = "VITORIA_BRANCAS".equals(partida.getResultado()) ? "Brancas" : "Pretas";
        JOptionPane.showMessageDialog(this,
                "Xeque-mate! Vitória das peças " + vencedor + ".\nPartida registrada no histórico.",
                "Fim de Partida", JOptionPane.INFORMATION_MESSAGE);
    }

    private void limparSelecao() {
        this.posicaoSelecionada = null;
        this.movimentosDestacados.clear();
    }

    public void atualizarEstado() {
        Acessibilidade acc = usuario.getAcessibilidade();
        TemaAcessivel.aplicarTema(painelPrincipal, acc);

        Tabuleiro tabuleiro = partida.getTabuleiro();
        String turno = partida.getTurnoAtual();
        lblStatusTurno.setText("Turno: " + turno);

        boolean emXeque = tabuleiro.verifXeque(turno);
        if (emXeque && "EM_ANDAMENTO".equals(partida.getStatus())) {
            lblAlertaXeque.setText("ALERTA: REI EM XEQUE!");
            lblAlertaXeque.setBackground(TemaAcessivel.destaqueXeque());
            lblAlertaXeque.setForeground(Color.WHITE);
        } else {
            lblAlertaXeque.setText("");
            lblAlertaXeque.setBackground(TemaAcessivel.fundoPrincipal(acc));
        }

        for (int lin = 0; lin < 8; lin++) {
            for (int col = 0; col < 8; col++) {
                Posicao pos = new Posicao(lin, col);
                JButton btn = botoesCasas[lin][col];
                Peca peca = tabuleiro.obterPecas(pos);

                boolean casaClara = (lin + col) % 2 == 0;
                Color corFundo = casaClara ? TemaAcessivel.casaClara(acc) : TemaAcessivel.casaEscura(acc);

                if (posicaoSelecionada != null && posicaoSelecionada.equals(pos)) {
                    corFundo = TemaAcessivel.destaqueOrigem(acc);
                    btn.setBorder(BorderFactory.createLineBorder(Color.BLACK, 4));
                } else if (movimentosDestacados.contains(pos)) {
                    corFundo = TemaAcessivel.destaqueMovimento(acc);
                    btn.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
                } else if (emXeque && peca instanceof Rei && peca.getCor().equals(turno)) {
                    corFundo = TemaAcessivel.destaqueXeque();
                    btn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 4));
                } else {
                    btn.setBorder(BorderFactory.createLineBorder(
                            acc.isContraste() ? Color.WHITE : new Color(90, 70, 50), 1));
                }

                btn.setBackground(corFundo);
                btn.setFont(TemaAcessivel.fontePeca(acc));

                if (peca != null) {
                    btn.setText(obterSimboloUnicode(peca));
                    btn.setForeground("BRANCA".equals(peca.getCor())
                            ? TemaAcessivel.pecaBranca(acc)
                            : TemaAcessivel.pecaPreta(acc));
                } else if (movimentosDestacados.contains(pos)) {
                    btn.setText("•");
                    btn.setForeground(Color.BLACK);
                } else {
                    btn.setText("");
                }
            }
        }
    }

    private String obterSimboloUnicode(Peca peca) {
        return switch (peca) {
            case Rei _ -> "♚";
            case Rainha _ -> "♛";
            case Torre _ -> "♜";
            case Bispo _ -> "♝";
            case Cavalo _ -> "♞";
            case Peao _ -> "♟";
            default -> "?";
        };
    }
}