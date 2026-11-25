package main.view;

import br.com.ifpr.cchess.model.ChessApiBot;
import br.com.ifpr.cchess.model.Jogador;
import br.com.ifpr.cchess.model.JogadorHumano;
import br.com.ifpr.cchess.model.Partida;
import br.com.ifpr.cchess.model.Usuario;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class InterfaceMenu extends JFrame {
    private final Usuario usuario;
    private final JPanel painelPrincipal;
    private final JLabel lblBoasVindas;

    public InterfaceMenu(Usuario usuario) {
        this.usuario = usuario;

        setTitle("C-Chess - Menu Principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(560, 500);
        setLocationRelativeTo(null);

        painelPrincipal = new JPanel(new BorderLayout(16, 16));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        lblBoasVindas = new JLabel("Bem-vindo(a), " + usuario.getNome(), SwingConstants.CENTER);
        painelPrincipal.add(lblBoasVindas, BorderLayout.NORTH);

        JPanel painelOpcoes = new JPanel(new GridLayout(5, 1, 12, 12));

        JButton btnPvP = new JButton("Iniciar Partida Local (PvP)");
        btnPvP.addActionListener(_ -> iniciarPartida(false));
        painelOpcoes.add(btnPvP);

        JButton btnPvE = new JButton("Iniciar Partida contra Computador (PvE)");
        btnPvE.addActionListener(_ -> iniciarPartida(true));
        painelOpcoes.add(btnPvE);

        JButton btnAcessibilidade = new JButton("Configurar Acessibilidade");
        btnAcessibilidade.addActionListener(_ -> new InterfaceAcessibilidade(usuario, this).setVisible(true));
        painelOpcoes.add(btnAcessibilidade);

        JButton btnHistorico = new JButton("Consultar Histórico de Partidas");
        btnHistorico.addActionListener(_ -> new InterfaceHistorico(usuario).setVisible(true));
        painelOpcoes.add(btnHistorico);

        JButton btnSair = new JButton("Sair");
        btnSair.addActionListener(_ -> {
            new InterfaceLogin().setVisible(true);
            dispose();
        });
        painelOpcoes.add(btnSair);

        painelPrincipal.add(painelOpcoes, BorderLayout.CENTER);
        setContentPane(painelPrincipal);

        atualizarTema();
    }

    public void atualizarTema() {
        TemaAcessivel.aplicarTema(painelPrincipal, usuario.getAcessibilidade());
        lblBoasVindas.setFont(TemaAcessivel.fonteTitulo(usuario.getAcessibilidade()));
        revalidate();
        repaint();
    }

    private void iniciarPartida(boolean contraComputador) {
        Jogador brancas = new Jogador(usuario.getNome(), "BRANCA", new JogadorHumano());
        Jogador pretas = contraComputador
                ? new Jogador("Chess-API (IA)", "PRETA", new ChessApiBot())
                : new Jogador("Jogador 2", "PRETA", new JogadorHumano());

        Partida partida = new Partida(usuario, brancas, pretas);
        partida.iniciarPartida();

        new InterfaceTabuleiro(usuario, partida, this).setVisible(true);
        setVisible(false);
    }
}