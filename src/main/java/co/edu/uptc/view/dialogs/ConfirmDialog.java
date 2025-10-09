package co.edu.uptc.view.dialogs;

import javax.swing.*;
import java.awt.*;

import co.edu.uptc.view.GlobalView;

public class ConfirmDialog extends JDialog {

    private boolean confirmed = false;

    public ConfirmDialog(Frame parent, String message, String title) {
        super(parent, title, true);
        setUndecorated(true);
        setLayout(new BorderLayout());
        setSize(440, 240);
        setLocationRelativeTo(parent);
        setBackground(new Color(0, 0, 0, 0));

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int arc = 25;
                int width = getWidth();
                int height = getHeight();

                g2.setColor(GlobalView.DIALOG_BACKGROUND);
                g2.fillRoundRect(0, 0, width, height, arc, arc);

                g2.setStroke(new BasicStroke(6f));
                g2.setColor(GlobalView.ASIDE_BACKGROUND);
                g2.drawRoundRect(1, 1, width - 2, height - 2, arc, arc);

                g2.dispose();
            }

            @Override
            public boolean isOpaque() {
                return false; 
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        mainPanel.setOpaque(false);

        JLabel lblMessage = new JLabel("<html><div style='text-align: center;'>" + message + "</div></html>");
        lblMessage.setFont(new Font("Segoe UI", Font.PLAIN, 35));
        lblMessage.setForeground(Color.BLACK);
        lblMessage.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 10));
        buttonPanel.setOpaque(false);

        JButton btnYes = createStyledButton("Aceptar", GlobalView.CONFIRM_BUTTON_BACKGROUND);
        JButton btnNo = createStyledButton("Cancelar", GlobalView.CANCEL_BUTTON_BACKGROUND);  

        btnYes.addActionListener(e -> {
            confirmed = true;
            dispose();
        });

        btnNo.addActionListener(e -> {
            confirmed = false;
            dispose();
        });

        buttonPanel.add(btnYes);
        buttonPanel.add(btnNo);

        mainPanel.add(lblMessage, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JButton createStyledButton(String text, Color baseColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 20));
        button.setForeground(Color.WHITE);
        button.setBackground(baseColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(null);
        button.setMargin(new Insets(6, 20, 6, 20));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(baseColor.darker());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(baseColor);
            }
        });

        return button;
    }

    public static boolean showConfirmDialog(Frame parent, String message, String title) {
        ConfirmDialog dialog = new ConfirmDialog(parent, message, title);
        dialog.setVisible(true);
        return dialog.confirmed;
    }
}
