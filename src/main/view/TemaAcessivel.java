package main.view;

import main.model.Acessibilidade;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.JTableHeader;

public class TemaAcessivel {

    public static Color fundoPrincipal(Acessibilidade acc) {
        return acc.isContraste() ? new Color(10, 10, 10) : new Color(245, 246, 248);
    }

    public static Color textoPrincipal(Acessibilidade acc) {
        return acc.isContraste() ? new Color(255, 255, 255) : new Color(30, 34, 40);
    }

    public static Color fundoBotao(Acessibilidade acc) {
        return acc.isContraste() ? new Color(255, 214, 0) : new Color(33, 97, 140);
    }

    public static Color textoBotao(Acessibilidade acc) {
        return acc.isContraste() ? Color.BLACK : Color.WHITE;
    }

    public static Color casaClara(Acessibilidade acc) {
        return acc.isContraste() ? new Color(52, 52, 52) : new Color(238, 216, 181);
    }

    public static Color casaEscura(Acessibilidade acc) {
        return acc.isContraste() ? new Color(18, 18, 18) : new Color(166, 123, 86);
    }

    public static Color pecaBranca(Acessibilidade acc) {
        return Color.WHITE;
    }

    public static Color pecaPreta(Acessibilidade acc) {
        return acc.isContraste() ? new Color(255, 214, 0) : new Color(20, 20, 20);
    }

    public static Color destaqueOrigem(Acessibilidade acc) {
        return acc.isContraste() ? new Color(0, 229, 255) : new Color(246, 246, 105);
    }

    public static Color destaqueMovimento(Acessibilidade acc) {
        return acc.isContraste() ? new Color(0, 230, 118) : new Color(130, 204, 102);
    }

    public static Color destaqueXeque() {
        return new Color(220, 38, 38);
    }

    public static int tamanhoFonteBase(Acessibilidade acc) {
        int ref = (acc != null) ? acc.getTamanhoPecas() : 64;
        return Math.max(14, Math.round(ref * 0.25f));
    }

    public static Font fonteTexto(Acessibilidade acc, boolean negrito) {
        int tamanho = tamanhoFonteBase(acc);
        return new Font("SansSerif", negrito ? Font.BOLD : Font.PLAIN, tamanho);
    }

    public static Font fonteTitulo(Acessibilidade acc) {
        int tamanho = Math.round(tamanhoFonteBase(acc) * 1.45f);
        return new Font("SansSerif", Font.BOLD, tamanho);
    }

    public static Font fontePeca(Acessibilidade acc) {
        int tamanho = (acc != null) ? acc.getTamanhoPecas() : 64;
        return new Font("Segoe UI Symbol", Font.PLAIN, tamanho);
    }

    public static void aplicarTema(Container container, Acessibilidade acc) {
        Acessibilidade config = (acc != null) ? acc : new Acessibilidade();
        Color bg = fundoPrincipal(config);
        Color fg = textoPrincipal(config);
        Font fontNormal = fonteTexto(config, false);
        Font fontBold = fonteTexto(config, true);

        if (container instanceof JComponent jc) {
            jc.setBackground(bg);
            jc.setForeground(fg);
        }

        for (Component comp : container.getComponents()) {
            if (comp instanceof JPanel || comp instanceof JScrollPane) {
                comp.setBackground(bg);
                comp.setForeground(fg);
            } else if (comp instanceof JLabel lbl) {
                lbl.setForeground(fg);
                if (lbl.getFont() != null && lbl.getFont().isBold()) {
                    lbl.setFont(fontBold);
                } else {
                    lbl.setFont(fontNormal);
                }
            } else if (comp instanceof JButton btn) {
                btn.setBackground(fundoBotao(config));
                btn.setForeground(textoBotao(config));
                btn.setFont(fontBold);
                btn.setFocusPainted(true);
                if (config.isContraste()) {
                    btn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
                }
            } else if (comp instanceof JTextField || comp instanceof JPasswordField) {
                comp.setBackground(config.isContraste() ? Color.BLACK : Color.WHITE);
                comp.setForeground(fg);
                comp.setFont(fontNormal);
                if (comp instanceof JComponent jc) {
                    jc.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(config.isContraste() ? new Color(255, 214, 0) : Color.GRAY, 2),
                            BorderFactory.createEmptyBorder(6, 8, 6, 8)
                    ));
                }
            } else if (comp instanceof JCheckBox || comp instanceof JSlider) {
                comp.setBackground(bg);
                comp.setForeground(fg);
                comp.setFont(fontBold);
            } else if (comp instanceof JTable table) {
                table.setBackground(config.isContraste() ? Color.BLACK : Color.WHITE);
                table.setForeground(fg);
                table.setFont(fontNormal);
                table.setRowHeight(tamanhoFonteBase(config) + 14);
                table.setGridColor(config.isContraste() ? new Color(255, 214, 0) : Color.LIGHT_GRAY);
                JTableHeader header = table.getTableHeader();
                if (header != null) {
                    header.setBackground(fundoBotao(config));
                    header.setForeground(textoBotao(config));
                    header.setFont(fontBold);
                }
            }

            if (comp instanceof Container child) {
                aplicarTema(child, config);
            }
        }
    }
}