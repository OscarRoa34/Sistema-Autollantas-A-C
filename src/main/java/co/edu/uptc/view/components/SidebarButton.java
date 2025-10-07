package co.edu.uptc.view.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import co.edu.uptc.view.GlobalView;

public class SidebarButton extends JButton {
    private boolean isActive = false;
    private final Color normalColor = GlobalView.ASIDE_BUTTONS_BACKGROUND;
    private final Color hoverColor = GlobalView.ASIDE_BUTTONS_BACKGROUND_HOVER;
    private final Color activeColor = GlobalView.ASIDE_BUTTONS_BACKGROUND_ACTIVE;

    public SidebarButton(String text, ImageIcon icon) {
        super(text, icon);
        setHorizontalAlignment(SwingConstants.LEFT);
        setFont(new Font("Segoe UI", Font.BOLD, 15));
        setForeground(Color.WHITE);
        setBackground(normalColor);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setIconTextGap(10);
        setMargin(new Insets(10, 15, 10, 10));
        
        setHorizontalTextPosition(SwingConstants.RIGHT);
        setVerticalTextPosition(SwingConstants.CENTER);
        setText("<html><div style='text-align: center;'>" + text + "</div></html>");
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!isActive) {
                    setBackground(hoverColor);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!isActive) {
                    setBackground(normalColor);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (!isActive) {
                    setBackground(activeColor);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (contains(e.getPoint()) && !isActive) {
                    setBackground(hoverColor);
                }
            }
        });
    }

    public void setActive(boolean active) {
        this.isActive = active;
        if (active) {
            setBackground(activeColor);
        } else {
            setBackground(normalColor);
        }
    }

    public boolean isActive() {
        return isActive;
    }
}