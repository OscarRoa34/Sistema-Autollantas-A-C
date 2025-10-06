package co.edu.uptc.view.panels;
import javax.swing.*;
import java.awt.*;

public class PurchasePanel extends JPanel {
    public PurchasePanel() {
        setLayout(new BorderLayout());
        add(new JLabel("Gestión de Facturas de Compra", SwingConstants.CENTER), BorderLayout.CENTER);
    }
}
