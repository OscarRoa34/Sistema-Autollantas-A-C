package co.edu.uptc.view.panels.SubPanels;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class LubFiltersProductPanel extends JPanel {
    
    public LubFiltersProductPanel() {
        setLayout(new BorderLayout());
        add(new JLabel("Lubricantes y Filtros", SwingConstants.CENTER), BorderLayout.CENTER);
    }
}
