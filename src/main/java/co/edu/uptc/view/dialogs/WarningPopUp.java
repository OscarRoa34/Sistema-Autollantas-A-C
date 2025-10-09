package co.edu.uptc.view.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import co.edu.uptc.view.GlobalView;
import co.edu.uptc.view.utils.PropertiesService;

public class WarningPopUp extends JWindow {

    private float opacity = 0f;

    public WarningPopUp(Frame parent, String title, String message) {
        super(parent);
        setBackground(new Color(0, 0, 0, 0));

        PropertiesService p = new PropertiesService();

        JPanel panel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(GlobalView.WARNING_POPUP_BACKGROUND);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }

            @Override
            public boolean isOpaque() {
                return false;
            }
        };
        panel.setOpaque(false);
        setContentPane(panel);

        ImageIcon icon = new ImageIcon(p.getProperties("warning-icon"));
        Image scaled = icon.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
        JLabel iconLabel = new JLabel(new ImageIcon(scaled));
        iconLabel.setBounds(25, 35, 48, 48);
        panel.add(iconLabel);

        JLabel lblText = new JLabel(
            "<html><div style='color:white; font-family:Segoe UI; line-height:1.25; width:240px;'>" +
            "<b style='font-size:16px;'>" + title + "</b><br>" +
            "<span style='font-size:13px;'>" + ajustarTexto(message, 10) + "</span></div></html>"
        );
        lblText.setForeground(Color.WHITE);
        lblText.setBounds(90, 25, 240, 70);
        panel.add(lblText);

        JButton btnClose = new JButton("X");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnClose.setForeground(Color.WHITE);
        btnClose.setFocusPainted(false);
        btnClose.setBorderPainted(false);
        btnClose.setContentAreaFilled(false);
        btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> fadeOutAndClose());
        btnClose.setBounds(300, 0, 50, 50);
        panel.add(btnClose);

        setSize(360, 120);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = screenSize.width - getWidth() - 20;
        int y = 120;
        setLocation(x, y);

        fadeIn();

        Timer timer = new Timer(3000, e -> fadeOutAndClose());
        timer.setRepeats(false);
        timer.start();
    }

    private String ajustarTexto(String texto, int maxCharsPorLinea) {
        if (texto.length() <= maxCharsPorLinea) return texto;
        StringBuilder sb = new StringBuilder();
        String[] palabras = texto.split(" ");
        int count = 0;
        for (String palabra : palabras) {
            sb.append(palabra).append(" ");
            count += palabra.length() + 1;
            if (count > maxCharsPorLinea) {
                sb.append("<br>");
                count = 0;
            }
        }
        return sb.toString().trim();
    }

    private void fadeIn() {
        setOpacity(0f);
        setVisible(true);
        Timer fadeTimer = new Timer(30, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                opacity += 0.05f;
                if (opacity >= 1f) {
                    opacity = 1f;
                    ((Timer) e.getSource()).stop();
                }
                setOpacity(opacity);
            }
        });
        fadeTimer.start();
    }

    private void fadeOutAndClose() {
        Timer fadeTimer = new Timer(30, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                opacity -= 0.05f;
                if (opacity <= 0f) {
                    opacity = 0f;
                    ((Timer) e.getSource()).stop();
                    dispose();
                }
                setOpacity(opacity);
            }
        });
        fadeTimer.start();
    }

    public static void showWarningPopup(Frame parent, String title, String message) {
        new WarningPopUp(parent, title, message);
    }
}
