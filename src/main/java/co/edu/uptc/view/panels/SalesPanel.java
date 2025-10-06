package co.edu.uptc.view.panels;
import javax.swing.*;
import java.awt.*;

public class SalesPanel extends JPanel {
    public SalesPanel() {
        setLayout(new BorderLayout());
        add(new JLabel("Gestión de Facturas de Venta", SwingConstants.CENTER), BorderLayout.CENTER);
    }
}
