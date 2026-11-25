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

public class InterfaceCadastro extends JFrame {
    private final JFrame telaAnterior;
    private final JTextField campoNome;
    private final JTextField campoLogin;
    private final JPasswordField campoSenha;
    private final UsuarioDao usuarioDao;
    private final AcessibilidadeDao acessibilidadeDao;

    public InterfaceCadastro(JFrame telaAnterior) {
        this.telaAnterior = telaAnterior;
        this.usuarioDao = new UsuarioDao();
        this.acessibilidadeDao = new AcessibilidadeDao();

        setTitle("C-Chess - Cadastrar Conta");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 420);
        setLocationRelativeTo(null);

        JPanel painelPrincipal = new JPanel(new BorderLayout(15, 15));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JLabel lblTitulo = new JLabel("Criar Nova Conta", SwingConstants.CENTER);
        painelPrincipal.add(lblTitulo, BorderLayout.NORTH);

        JPanel painelForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        painelForm.add(new JLabel("Nome:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        campoNome = new JTextField(20);
        painelForm.add(campoNome, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        painelForm.add(new JLabel("Login:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        campoLogin = new JTextField(20);
        painelForm.add(campoLogin, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        painelForm.add(new JLabel("Senha:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        campoSenha = new JPasswordField(20);
        painelForm.add(campoSenha, gbc);

        painelPrincipal.add(painelForm, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new GridBagLayout());
        GridBagConstraints gbcBtn = new GridBagConstraints();
        gbcBtn.insets = new Insets(6, 8, 6, 8);
        gbcBtn.fill = GridBagConstraints.HORIZONTAL;
        gbcBtn.weightx = 1.0;

        JButton btnSalvar = new JButton("Salvar Cadastro");
        btnSalvar.addActionListener(_ -> salvarConta());
        gbcBtn.gridx = 0;
        painelBotoes.add(btnSalvar, gbcBtn);

        JButton btnVoltar = new JButton("Voltar");
        btnVoltar.addActionListener(_ -> voltar());
        gbcBtn.gridx = 1;
        painelBotoes.add(btnVoltar, gbcBtn);

        painelPrincipal.add(painelBotoes, BorderLayout.SOUTH);

        setContentPane(painelPrincipal);
        TemaAcessivel.aplicarTema(painelPrincipal, new Acessibilidade());
        lblTitulo.setFont(TemaAcessivel.fonteTitulo(new Acessibilidade()));
    }

    private void salvarConta() {
        String nome = campoNome.getText().trim();
        String login = campoLogin.getText().trim();
        String senha = new String(campoSenha.getPassword()).trim();

        if (nome.isEmpty() || login.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos os campos são obrigatórios.", "Campos Vazios", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (usuarioDao.buscarPorLogin(login) != null) {
                JOptionPane.showMessageDialog(this, "Este login já está em uso. Escolha outro.", "Login Duplicado", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Usuario novoUsuario = new Usuario(0, nome, login, senha);
            usuarioDao.salvar(novoUsuario);

            Acessibilidade configPadrao = new Acessibilidade(novoUsuario.getId(), false, 64);
            acessibilidadeDao.salvar(configPadrao);

            JOptionPane.showMessageDialog(this, "Conta cadastrada com sucesso!", "Confirmação", JOptionPane.INFORMATION_MESSAGE);
            voltar();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao cadastrar conta:\n" + ex.getMessage(),
                    "Erro no Banco de Dados", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void voltar() {
        telaAnterior.setVisible(true);
        dispose();
    }
}