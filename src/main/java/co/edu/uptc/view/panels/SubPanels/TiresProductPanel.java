package co.edu.uptc.view.panels.SubPanels;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class TiresProductPanel extends JPanel {
    
    public TiresProductPanel(){
         setLayout(new BorderLayout());
        add(new JLabel("LLantas", SwingConstants.CENTER), BorderLayout.CENTER);
    }
}
