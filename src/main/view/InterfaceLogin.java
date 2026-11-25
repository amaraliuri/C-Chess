package main.view;

import br.com.ifpr.cchess.dao.AcessibilidadeDao;
import br.com.ifpr.cchess.dao.UsuarioDao;
import br.com.ifpr.cchess.model.Acessibilidade;
import br.com.ifpr.cchess.model.Usuario;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class InterfaceLogin extends JFrame {
    private final JTextField campoLogin;
    private final JPasswordField campoSenha;
    private final UsuarioDao usuarioDao;
    private final AcessibilidadeDao acessibilidadeDao;

    public InterfaceLogin() {
        this.usuarioDao = new UsuarioDao();
        this.acessibilidadeDao = new AcessibilidadeDao();

        setTitle("C-Chess - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 380);
        setLocationRelativeTo(null);

        JPanel painelPrincipal = new JPanel(new BorderLayout(15, 15));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JLabel lblTitulo = new JLabel("C-Chess | Acesso ao Sistema", SwingConstants.CENTER);
        lblTitulo.setFont(TemaAcessivel.fonteTitulo(new Acessibilidade()));
        painelPrincipal.add(lblTitulo, BorderLayout.NORTH);

        JPanel painelForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        painelForm.add(new JLabel("Login:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        campoLogin = new JTextField(18);
        painelForm.add(campoLogin, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        painelForm.add(new JLabel("Senha:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        campoSenha = new JPasswordField(18);
        painelForm.add(campoSenha, gbc);

        painelPrincipal.add(painelForm, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new GridBagLayout());
        GridBagConstraints gbcBtn = new GridBagConstraints();
        gbcBtn.insets = new Insets(6, 8, 6, 8);
        gbcBtn.fill = GridBagConstraints.HORIZONTAL;
        gbcBtn.weightx = 1.0;

        JButton btnEntrar = new JButton("Entrar");
        btnEntrar.addActionListener(_ -> realizarLogin());
        gbcBtn.gridx = 0;
        painelBotoes.add(btnEntrar, gbcBtn);

        JButton btnCadastrar = new JButton("Cadastrar Conta");
        btnCadastrar.addActionListener(_ -> abrirCadastro());
        gbcBtn.gridx = 1;
        painelBotoes.add(btnCadastrar, gbcBtn);

        painelPrincipal.add(painelBotoes, BorderLayout.SOUTH);

        setContentPane(painelPrincipal);
        getRootPane().setDefaultButton(btnEntrar);
        TemaAcessivel.aplicarTema(painelPrincipal, new Acessibilidade());
        lblTitulo.setFont(TemaAcessivel.fonteTitulo(new Acessibilidade()));
    }

    private void realizarLogin() {
        String login = campoLogin.getText().trim();
        String senha = new String(campoSenha.getPassword());

        if (login.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha login e senha.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Usuario usuario = usuarioDao.buscarPorLogin(login);
            if (usuario == null || !usuario.autenticar(login, senha)) {
                JOptionPane.showMessageDialog(this, "Login ou senha inválidos.", "Erro de Autenticação", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Acessibilidade config = acessibilidadeDao.buscarPorId(usuario.getId());
            if (config == null) {
                config = new Acessibilidade(usuario.getId(), false, 64);
                acessibilidadeDao.salvar(config);
            }
            config.aplicarConfig();
            usuario.setAcessibilidade(config);

            new InterfaceMenu(usuario).setVisible(true);
            dispose();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Falha ao conectar com o banco de dados:\n" + ex.getMessage(),
                    "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirCadastro() {
        new InterfaceCadastro(this).setVisible(true);
        setVisible(false);
    }
}