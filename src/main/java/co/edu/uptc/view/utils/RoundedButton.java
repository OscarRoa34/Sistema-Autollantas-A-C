package co.edu.uptc.view.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter; 
import java.awt.event.MouseEvent;

public class RoundedButton extends JButton {
    private final int radius;
    private final Color bg;
    private final Color hoverBg;
    private final Color borderColor;
    private boolean hover = false;

    public RoundedButton(String text, Icon icon, int radius, Color bg, Color hoverBg, Color borderColor) {
        super(text, icon);
        this.radius = radius;
        this.bg = bg;
        this.hoverBg = hoverBg;
        this.borderColor = borderColor;

        setContentAreaFilled(false);
        setOpaque(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(Color.WHITE);
        setHorizontalTextPosition(SwingConstants.RIGHT);
        setIconTextGap(8);
        setFont(new Font("Arial", Font.BOLD, 14));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
            public void mouseExited(MouseEvent e)  { hover = false; repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color fill = hover ? hoverBg : bg;
        g2.setColor(fill);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

        if (borderColor != null) {
            g2.setColor(borderColor);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        }

        g2.dispose();
        super.paintComponent(g); // dibuja texto e ícono sobre el fondo redondeado
    }
}
