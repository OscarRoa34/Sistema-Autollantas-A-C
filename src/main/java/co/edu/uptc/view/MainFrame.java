package co.edu.uptc.view;

import javax.swing.*;
import co.edu.uptc.view.panels.*;
import co.edu.uptc.view.utils.ViewController;
import java.awt.*;

public class MainFrame extends JFrame {
    private SidebarPanel sidebar;
    private JPanel mainContainer;
    private ViewController controller;

    public MainFrame() {
        setTitle("Autollantas A&C");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // 🔹 Quita la barra superior nativa (sin título ni botones del sistema)
        setUndecorated(true);

        setExtendedState(MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        sidebar = new SidebarPanel(null); // se asigna luego

        mainContainer = new JPanel(new BorderLayout());
        controller = new ViewController(mainContainer);
        sidebar = new SidebarPanel(controller);
        controller.showPanel(new WelcomePanel());

        HeaderPanel header = new HeaderPanel();

        // Panel derecho con header y contenido
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(header, BorderLayout.NORTH);
        rightPanel.add(mainContainer, BorderLayout.CENTER);

        // Estructura general
        JPanel mainLayout = new JPanel(new BorderLayout());
        mainLayout.add(sidebar, BorderLayout.WEST);
        mainLayout.add(rightPanel, BorderLayout.CENTER);

        add(mainLayout, BorderLayout.CENTER);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}
