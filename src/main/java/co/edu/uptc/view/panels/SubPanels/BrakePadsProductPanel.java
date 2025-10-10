package co.edu.uptc.view.panels.SubPanels;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class BrakePadsProductPanel extends JPanel {

    public BrakePadsProductPanel() {
        setLayout(new BorderLayout());
        add(new JLabel("Pastillas", SwingConstants.CENTER), BorderLayout.CENTER);

    }
}
