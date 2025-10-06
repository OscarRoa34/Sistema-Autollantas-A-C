package co.edu.uptc.view.panels;

import javax.swing.*;
import java.awt.*;
import co.edu.uptc.view.utils.ViewController;
import co.edu.uptc.view.GlobalView;
import co.edu.uptc.view.components.SidebarButton;

public class SidebarPanel extends JPanel {
    public SidebarPanel(ViewController controller) {
        setLayout(new BorderLayout());
        setBackground(GlobalView.ASIDE_BACKGROUND);
        setPreferredSize(new Dimension(250, 0)); // 🔹 Más ancho

        // ====== LOGO SUPERIOR ======
        JPanel logoPanel = new JPanel();
        logoPanel.setBackground(GlobalView.ASIDE_BACKGROUND);
        logoPanel.setPreferredSize(new Dimension(250, 230)); // 🔹 Espacio reservado para el logo

        JLabel logoLabel = new JLabel(new ImageIcon("src\\main\\resources\\Images\\Logo1.png"));
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoPanel.add(logoLabel);

        // ====== BOTONES ======
        JPanel buttonsPanel = new JPanel(new GridLayout(4, 1, 0, 30));
        buttonsPanel.setBackground(GlobalView.ASIDE_BACKGROUND);

        SidebarButton btnProductos = new SidebarButton(
                "Gestión de Productos y Servicios",
                new ImageIcon("src\\main\\resources\\Images\\products.png")
        );

        SidebarButton btnCompras = new SidebarButton(
                "Gestión de Facturas de Compra",
                new ImageIcon("src\\main\\resources\\Images\\purchase.png")
        );

        SidebarButton btnVentas = new SidebarButton(
                "Gestión de Facturas de Venta",
                new ImageIcon("src\\main\\resources\\Images\\sales.png")
        );

        btnProductos.addActionListener(e -> controller.showPanel(new ProductsPanel()));
        btnCompras.addActionListener(e -> controller.showPanel(new PurchasePanel()));
        btnVentas.addActionListener(e -> controller.showPanel(new SalesPanel()));

        buttonsPanel.add(btnProductos);
        buttonsPanel.add(btnCompras);
        buttonsPanel.add(btnVentas);

        // ====== ENSAMBLE ======
        add(logoPanel, BorderLayout.NORTH);   // 🔹 Logo arriba
        add(buttonsPanel, BorderLayout.CENTER); // 🔹 Botones más abajo
    }
}
