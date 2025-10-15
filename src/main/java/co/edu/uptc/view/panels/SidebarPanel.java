package co.edu.uptc.view.panels;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import co.edu.uptc.view.utils.PropertiesService;
import co.edu.uptc.view.utils.ViewController;
import co.edu.uptc.view.GlobalView;
import co.edu.uptc.view.components.SidebarButton;

public class SidebarPanel extends JPanel {

    private final PropertiesService p;
    private SidebarButton activeButton;
    private final List<SidebarButton> buttons = new ArrayList<>();
    private final ViewController controller;

    public SidebarPanel(ViewController controller) {
        this.controller = controller;
        this.p = new PropertiesService();
        
        setLayout(new BorderLayout());
        setBackground(GlobalView.ASIDE_BACKGROUND);
        setPreferredSize(new Dimension(250, 0));

        add(createLogoPanel(), BorderLayout.NORTH);
        add(createButtonsPanel(), BorderLayout.CENTER);
    }

    private JPanel createLogoPanel() {
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setBackground(GlobalView.ASIDE_BACKGROUND);
        logoPanel.setPreferredSize(new Dimension(250, 180));

        JLabel logoLabel = new JLabel();
        ImageIcon logoIcon = new ImageIcon(p.getProperties("logo"));
        Image scaledLogo = logoIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        logoLabel.setIcon(new ImageIcon(scaledLogo));
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoLabel.setVerticalAlignment(SwingConstants.TOP);
        
        logoPanel.add(logoLabel, BorderLayout.CENTER);

        logoLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                controller.showPanel(new WelcomePanel());
                setActiveButton(null); 
            }
        });

        return logoPanel;
    }

    private JPanel createButtonsPanel() {
        JPanel buttonsPanel = new JPanel(new GridLayout(5, 1, 0, 25));
        buttonsPanel.setBackground(GlobalView.ASIDE_BACKGROUND);
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        SidebarButton btnProductos = createSidebarButton(
                "Gestión de Productos",
                new ImageIcon(p.getProperties("products")),
                () -> controller.showPanel(new ProductsPanel(controller))
        );

        SidebarButton btnServicios = createSidebarButton(
                "Gestión de Servicios",
                new ImageIcon(p.getProperties("services")),
                () -> controller.showPanel(new ServicesPanel())
        );

        SidebarButton btnCompras = createSidebarButton(
                "Gestión de registros de Compra",
                new ImageIcon(p.getProperties("purchase")),
                () -> controller.showPanel(new PurchasePanel())
        );

        SidebarButton btnVentas = createSidebarButton(
                "Gestión de registros de Venta",
                new ImageIcon(p.getProperties("sales")),
                () -> controller.showPanel(new SalesPanel())
        );

        SidebarButton btnReportes = createSidebarButton(
                "Gestion de Reportes y Alertas",
                new ImageIcon(p.getProperties("reports")),
                () -> controller.showPanel(new ReportsPanel())
        );

        buttonsPanel.add(btnProductos);
        buttonsPanel.add(btnServicios);
        buttonsPanel.add(btnCompras);
        buttonsPanel.add(btnVentas);
        buttonsPanel.add(btnReportes);

        buttons.add(btnProductos);
        buttons.add(btnServicios);
        buttons.add(btnCompras);
        buttons.add(btnVentas);
        buttons.add(btnReportes);
        
        return buttonsPanel;
    }

    private SidebarButton createSidebarButton(String text, ImageIcon icon, Runnable onClick) {
        SidebarButton button = new SidebarButton(text, icon);
        button.addActionListener(e -> {
            setActiveButton(button);
            onClick.run();
        });
        return button;
    }

    public void setActiveButton(SidebarButton button) {
        if (activeButton != null) {
            activeButton.setActive(false);
        }
        if (button != null) {
            button.setActive(true);
        }
        activeButton = button;
    }

    public SidebarButton getActiveButton() {
        return activeButton;
    }

    public void setActiveButtonByIndex(int index) {
        if (index >= 0 && index < buttons.size()) {
            setActiveButton(buttons.get(index));
        }
    }
}