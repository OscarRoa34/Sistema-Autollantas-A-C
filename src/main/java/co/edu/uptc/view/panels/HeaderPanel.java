package co.edu.uptc.view.panels;
import javax.swing.*;

import co.edu.uptc.view.GlobalView;

import java.awt.*;
public class HeaderPanel extends JPanel {

    private JButton btnCerrar;

    public HeaderPanel() {
        setLayout(new BorderLayout());
        setBackground(GlobalView.HEADER_BACKGROUND); 
        setPreferredSize(new Dimension(0, 75)); 

        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);

        btnCerrar = new JButton("X");
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFocusPainted(false);
        btnCerrar.setBorderPainted(false);
        btnCerrar.setContentAreaFilled(false);
        btnCerrar.setFont(new Font("Arial", Font.BOLD, 16));
        btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnCerrar.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "¿Seguro que quieres salir?",
                    "Confirmar salida",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        content.add(btnCerrar, BorderLayout.EAST);
        add(content, BorderLayout.CENTER);
    }
}
