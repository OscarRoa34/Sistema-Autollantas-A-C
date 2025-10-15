package co.edu.uptc.view.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import co.edu.uptc.view.GlobalView;

public class SidebarButton extends JButton {
    private boolean isActive = false;
    private final Color normalColor = GlobalView.ASIDE_BUTTONS_BACKGROUND;
    private final Color hoverColor = GlobalView.ASIDE_BUTTONS_HOVER_COLOR;
    private final Color activeColor = GlobalView.ASIDE_BUTTONS_ACTIVE_BACKGROUND;

    public SidebarButton(String text, ImageIcon icon) {
        super("<html><div style='text-align: center; display: flex; align-items: center; justify-content: center;'>"
                + "<span style='display: inline-block; vertical-align: middle;'>" + text + "</span></div></html>",
                icon);
        setFont(GlobalView.BUTTON_FONT.deriveFont(Font.BOLD, 15f));
        setForeground(GlobalView.LIGHT_TEXT_COLOR);
        setBackground(normalColor);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setHorizontalAlignment(SwingConstants.CENTER);
        setHorizontalTextPosition(SwingConstants.RIGHT);
        setVerticalTextPosition(SwingConstants.CENTER);
        setIconTextGap(12);
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
        setBackground(active ? activeColor : normalColor);
        setForeground(active ? GlobalView.LIGHT_TEXT_COLOR : GlobalView.LIGHT_TEXT_COLOR);
    }

    public boolean isActive() {
        return isActive;
    }
}