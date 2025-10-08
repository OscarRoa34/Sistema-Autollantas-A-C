package co.edu.uptc.view.panels;

import javax.swing.*;
import java.awt.*;


public class ServicesPanel extends JPanel {
    
    public ServicesPanel(){
        setLayout(new BorderLayout());
        add(new JLabel("Gestión de Servicios", SwingConstants.CENTER), BorderLayout.CENTER);
    }

}
