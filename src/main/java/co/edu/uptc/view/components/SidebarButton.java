package co.edu.uptc.view.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import co.edu.uptc.view.GlobalView;

public class SidebarButton extends JButton {

    public SidebarButton(String text, ImageIcon icon) {
        super(text, icon);
        setHorizontalAlignment(SwingConstants.LEFT);
        setFont(new Font("Segoe UI", Font.BOLD, 15));
        setForeground(Color.WHITE);
        setBackground(GlobalView.ASIDE_BUTTONS_BACKGROUND);
        setFocusPainted(false);
        setBorderPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setIconTextGap(10);
        setMargin(new Insets(10, 15, 10, 10));
        
        setHorizontalTextPosition(SwingConstants.RIGHT);
        setVerticalTextPosition(SwingConstants.CENTER);
        setText("<html><div style='text-align: center;'>" + text + "</div></html>");
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(GlobalView.ASIDE_BUTTONS_BACKGROUND_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(GlobalView.ASIDE_BUTTONS_BACKGROUND);
            }
        });
    }
}
