package co.edu.uptc.view.utils;

import javax.swing.*;
import java.awt.*;

public class ViewController {
    private final JPanel container;

    public ViewController(JPanel container) {
        this.container = container;
        container.setLayout(new BorderLayout());
    }

    public void showPanel(JPanel newPanel) {
        container.removeAll();
        container.add(newPanel, BorderLayout.CENTER);
        container.revalidate();
        container.repaint();
    }
}
