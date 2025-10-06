package co.edu.uptc.view.panels;

import javax.swing.*;
import java.awt.*;

public class WelcomePanel extends JPanel {
    public WelcomePanel() {
        setLayout(new BorderLayout());
        JLabel lbl = new JLabel("¡Bienvenido al sistema!", SwingConstants.CENTER);
        lbl.setFont(new Font("Arial", Font.BOLD, 28));
        add(lbl, BorderLayout.CENTER);
    }
}
