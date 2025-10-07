package co.edu.uptc.view.panels;

import javax.swing.*;
import java.awt.*;
import co.edu.uptc.view.GlobalView;
import co.edu.uptc.view.utils.PropertiesService;
import co.edu.uptc.view.utils.RoundedButton;

public class HeaderPanel extends JPanel {

    private RoundedButton btnCerrar;
    private PropertiesService p;

    public HeaderPanel() {
        p = new PropertiesService();
        setLayout(new BorderLayout());
        setBackground(GlobalView.HEADER_BACKGROUND);
        setPreferredSize(new Dimension(0, 100));

        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(23, 23, 23, 23));

        ImageIcon iconSalir = new ImageIcon(
                new ImageIcon(p.getProperties("logout"))
                        .getImage()
                        .getScaledInstance(32, 32, Image.SCALE_SMOOTH));

        btnCerrar = new RoundedButton(
                "Cerrar",
                iconSalir,
                15,
                GlobalView.CLOSE_BUTTON_BACKGROUND,
                GlobalView.CLOSE_BUTTON_BACKGROUND_HOVER, 
                null
        );

        btnCerrar.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "¿Seguro que quieres salir?",
                    "Confirmar salida",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        content.add(btnCerrar, BorderLayout.EAST);
        add(content, BorderLayout.CENTER);
    }
}