package main;

import main.view.InterfaceLogin;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new InterfaceLogin().setVisible(true);
        });
    }
}