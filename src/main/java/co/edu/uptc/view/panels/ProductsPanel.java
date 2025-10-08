package co.edu.uptc.view.panels;

import javax.swing.*;
import java.awt.*;

public class ProductsPanel extends JPanel {
    public ProductsPanel() {
        setLayout(new BorderLayout());
        add(new JLabel("Gestión de Productos", SwingConstants.CENTER), BorderLayout.CENTER);
    }
}
