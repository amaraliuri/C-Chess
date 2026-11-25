package main.view;

import main.dao.AcessibilidadeDao;
import main.model.Acessibilidade;
import main.model.Usuario;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

public class InterfaceAcessibilidade extends JDialog {
    private final Usuario usuario;
    private final InterfaceMenu menuPai;
    private final AcessibilidadeDao acessibilidadeDao;
    private final JPanel painelPrincipal;
    private final JCheckBox chkAltoContraste;
    private final JSlider sliderTamanho;
    private final JLabel lblPreviewPeca;

    public InterfaceAcessibilidade(Usuario usuario, InterfaceMenu menuPai) {
        super(menuPai, "Configurar Acessibilidade", true);
        this.usuario = usuario;
        this.menuPai = menuPai;
        this.acessibilidadeDao = new AcessibilidadeDao();

        Acessibilidade atual = usuario.getAcessibilidade();

        setSize(540, 440);
        setLocationRelativeTo(menuPai);

        painelPrincipal = new JPanel(new BorderLayout(16, 16));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JLabel lblTitulo = new JLabel("Acessibilidade Visual", SwingConstants.CENTER);
        painelPrincipal.add(lblTitulo, BorderLayout.NORTH);

        JPanel painelControles = new JPanel(new GridLayout(4, 1, 10, 10));

        chkAltoContraste = new JCheckBox("Ativar Modo de Alto Contraste");
        chkAltoContraste.setSelected(atual.isContraste());
        chkAltoContraste.addActionListener(_ -> atualizarPreview());
        painelControles.add(chkAltoContraste);

        JLabel lblSlider = new JLabel("Tamanho das Peças e Textos (44px a 96px):");
        painelControles.add(lblSlider);

        sliderTamanho = new JSlider(44, 96, atual.getTamanhoPecas());
        sliderTamanho.setMajorTickSpacing(13);
        sliderTamanho.setPaintTicks(true);
        sliderTamanho.setPaintLabels(true);
        sliderTamanho.addChangeListener(_ -> atualizarPreview());
        painelControles.add(sliderTamanho);

        lblPreviewPeca = new JLabel("Pré-visualização: ♔ ♕ ♖ ♗ ♘ ♙", SwingConstants.CENTER);
        painelControles.add(lblPreviewPeca);

        painelPrincipal.add(painelControles, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new GridLayout(1, 2, 12, 0));
        JButton btnSalvar = new JButton("Salvar e Aplicar");
        btnSalvar.addActionListener(_ -> salvarConfiguracoes());
        painelBotoes.add(btnSalvar);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(_ -> dispose());
        painelBotoes.add(btnCancelar);

        painelPrincipal.add(painelBotoes, BorderLayout.SOUTH);

        setContentPane(painelPrincipal);
        atualizarPreview();
    }

    private void atualizarPreview() {
        Acessibilidade preview = new Acessibilidade(
                usuario.getId(),
                chkAltoContraste.isSelected(),
                sliderTamanho.getValue()
        );
        preview.aplicarConfig();
        TemaAcessivel.aplicarTema(painelPrincipal, preview);
        lblPreviewPeca.setFont(TemaAcessivel.fontePeca(preview));
    }

    private void salvarConfiguracoes() {
        Acessibilidade config = usuario.getAcessibilidade();
        config.setContraste(chkAltoContraste.isSelected());
        config.setTamanhoPecas(sliderTamanho.getValue());
        config.aplicarConfig();

        try {
            acessibilidadeDao.atualizar(config);
            menuPai.atualizarTema();
            JOptionPane.showMessageDialog(this, "Preferências salvas com sucesso!", "Acessibilidade", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar preferências:\n" + ex.getMessage(),
                    "Erro no Banco de Dados", JOptionPane.ERROR_MESSAGE);
        }
    }
}