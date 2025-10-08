package co.edu.uptc.view.panels;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class ReportsPanel extends JPanel {

    public ReportsPanel(){
        setLayout(new BorderLayout());
        add(new JLabel("Gestión de Reportes y alertas", SwingConstants.CENTER), BorderLayout.CENTER);
    }
    
}
