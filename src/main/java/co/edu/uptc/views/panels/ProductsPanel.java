package co.edu.uptc.views.panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.EmptyBorder;
import co.edu.uptc.view.GlobalView;
import co.edu.uptc.view.panels.SubPanels.*;
import co.edu.uptc.view.utils.PropertiesService;
import co.edu.uptc.view.utils.ViewController;

public class ProductsPanel extends JPanel {

    private final ViewController controller;
    private final PropertiesService props;

    public ProductsPanel(ViewController controller) {
        this.controller = controller;
        this.props = new PropertiesService();
        setLayout(new BorderLayout());

        JPanel gridPanel = new JPanel(new GridLayout(2, 2, 2, 2));
        gridPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        String[] categories = {"Llantas", "Lubricantes y Filtros", "Baterías", "Pastillas"};
        String[] iconProps = {"icon.tires", "icon.lubfilters", "icon.battery", "icon.brakepads"};

        JPanel[] panels = {
                new TiresProductPanel(controller),
                new LubFiltersProductPanel(controller),
                new BateryProductPanel(controller),
                new BrakePadsProductPanel(controller)
        };

        for (int i = 0; i < 4; i++) {
            JPanel colorPanel = new JPanel();
            colorPanel.setBackground(GlobalView.PRODUCTS_OPTIONS_BACKGROUND);
            colorPanel.setLayout(new GridBagLayout());

            ImageIcon icon = new ImageIcon(props.getProperties(iconProps[i]));
            Image scaledIcon = icon.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
            icon = new ImageIcon(scaledIcon);

            FadeButton btn = new FadeButton(categories[i], colorPanel);
            btn.setPreferredSize(new Dimension(310, 170)); 
            btn.setFont(new Font("Segoe UI", Font.BOLD, 30));
            btn.setIcon(icon);
            btn.setHorizontalTextPosition(SwingConstants.CENTER);
            btn.setVerticalTextPosition(SwingConstants.BOTTOM);

            final int index = i;
            btn.addActionListener(e -> controller.showPanel(panels[index]));

            colorPanel.add(btn);
            gridPanel.add(colorPanel);
        }

        add(gridPanel, BorderLayout.CENTER);
    }

    static class FadeButton extends JButton {
        private Color normalColor = GlobalView.PRODUCTS_OPTIONS_BUTTON_COLOR;
        private Color hoverColor = normalColor.darker().darker().darker();
        private Color activeColor = normalColor.darker().darker();

        private Color panelNormal = GlobalView.PRODUCTS_OPTIONS_BACKGROUND;
        private Color panelHover = GlobalView.WARNING_POPUP_BACKGROUND;

        private JPanel containerPanel;
        private Timer colorTimer;
        private int steps = 10;
        private int currentStep = 0;

        public FadeButton(String text, JPanel containerPanel) {
            super(text);
            this.containerPanel = containerPanel;

            setContentAreaFilled(false);
            setFocusPainted(false);
            setForeground(Color.WHITE);
            setBorder(new EmptyBorder(0,0,0,0));
            setBackground(normalColor);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); 

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    startFade(getBackground(), hoverColor, getPanelColor(), panelHover);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    startFade(getBackground(), normalColor, getPanelColor(), panelNormal);
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    startFade(getBackground(), activeColor, getPanelColor(), panelHover.darker());
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    startFade(getBackground(), hoverColor, getPanelColor(), panelHover);
                }
            });
        }

        private Color getPanelColor() {
            return containerPanel.getBackground();
        }

        private void startFade(Color btnFrom, Color btnTo, Color panelFrom, Color panelTo) {
            if (colorTimer != null && colorTimer.isRunning()) {
                colorTimer.stop();
            }
            currentStep = 0;

            colorTimer = new Timer(15, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    currentStep++;
                    float ratio = Math.min(1f, (float) currentStep / steps);

                    int r = (int)(btnFrom.getRed() + ratio * (btnTo.getRed() - btnFrom.getRed()));
                    int g = (int)(btnFrom.getGreen() + ratio * (btnTo.getGreen() - btnFrom.getGreen()));
                    int b = (int)(btnFrom.getBlue() + ratio * (btnTo.getBlue() - btnFrom.getBlue()));
                    setBackground(new Color(r,g,b));

                    int pr = (int)(panelFrom.getRed() + ratio * (panelTo.getRed() - panelFrom.getRed()));
                    int pg = (int)(panelFrom.getGreen() + ratio * (panelTo.getGreen() - panelFrom.getGreen()));
                    int pb = (int)(panelFrom.getBlue() + ratio * (panelTo.getBlue() - panelFrom.getBlue()));
                    containerPanel.setBackground(new Color(pr,pg,pb));

                    repaint();

                    if (ratio >= 1f) {
                        colorTimer.stop();
                    }
                }
            });
            colorTimer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            super.paintComponent(g2);
            g2.dispose();
        }
    }
}
