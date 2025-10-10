package co.edu.uptc.view.panels.SubPanels;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class BateryProductPanel extends JPanel {
    
    public BateryProductPanel(){
        setLayout(new BorderLayout());
        add(new JLabel("Baterias", SwingConstants.CENTER), BorderLayout.CENTER);
    }
}
