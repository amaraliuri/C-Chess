package main.view;

import br.com.ifpr.cchess.dao.PartidaDao;
import br.com.ifpr.cchess.model.Partida;
import br.com.ifpr.cchess.model.Usuario;
import java.awt.BorderLayout;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class InterfaceHistorico extends JFrame {
    private final Usuario usuario;
    private final PartidaDao partidaDao;

    public InterfaceHistorico(Usuario usuario) {
        this.usuario = usuario;
        this.partidaDao = new PartidaDao();

        setTitle("C-Chess - Histórico de Partidas");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(680, 440);
        setLocationRelativeTo(null);

        JPanel painelPrincipal = new JPanel(new BorderLayout(12, 12));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JLabel lblTitulo = new JLabel("Histórico de Partidas - " + usuario.getNome(), SwingConstants.CENTER);
        painelPrincipal.add(lblTitulo, BorderLayout.NORTH);

        String[] colunas = {"ID", "Data e Hora", "Status", "Resultado"};
        DefaultTableModel modelo = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable tabela = new JTable(modelo);
        carregarPartidas(modelo);

        painelPrincipal.add(new JScrollPane(tabela), BorderLayout.CENTER);

        JButton btnFechar = new JButton("Fechar");
        btnFechar.addActionListener(_ -> dispose());
        painelPrincipal.add(btnFechar, BorderLayout.SOUTH);

        setContentPane(painelPrincipal);
        TemaAcessivel.aplicarTema(painelPrincipal, usuario.getAcessibilidade());
        lblTitulo.setFont(TemaAcessivel.fonteTitulo(usuario.getAcessibilidade()));
    }

    private void carregarPartidas(DefaultTableModel modelo) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        try {
            List<Partida> lista = partidaDao.listarPorUsuario(usuario.getId());
            for (Partida p : lista) {
                String dataFormatada = (p.getDataHora() != null) ? p.getDataHora().format(fmt) : "-";
                modelo.addRow(new Object[]{
                        p.getId(),
                        dataFormatada,
                        p.getStatus(),
                        p.getResultado()
                });
            }
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao consultar histórico:\n" + ex.getMessage(),
                    "Erro no Banco de Dados", JOptionPane.ERROR_MESSAGE);
        }
    }
}