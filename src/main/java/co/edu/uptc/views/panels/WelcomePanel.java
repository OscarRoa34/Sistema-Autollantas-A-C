package co.edu.uptc.views.panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import co.edu.uptc.view.GlobalView;
import co.edu.uptc.view.utils.PropertiesService;

public class WelcomePanel extends JPanel {

    private final Image backgroundImage;
    private BufferedImage cachedBlurredImage;
    private final PropertiesService p;

    public WelcomePanel() {
        this.p = new PropertiesService();
        this.backgroundImage = new ImageIcon(p.getProperties("welcomeimage")).getImage();
        
        setLayout(new BorderLayout());

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                invalidateBlurredImageCache();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawBlurredBackground(g2);
        drawOverlay(g2);
        drawWelcomeText(g2);

        g2.dispose();
    }

    private void drawBlurredBackground(Graphics2D g2) {
        if (cachedBlurredImage == null || 
            cachedBlurredImage.getWidth() != getWidth() || 
            cachedBlurredImage.getHeight() != getHeight()) {
            
            cachedBlurredImage = generateBlurredImage(getWidth(), getHeight());
        }
        if (cachedBlurredImage != null) {
            g2.drawImage(cachedBlurredImage, 0, 0, this);
        }
    }

    private void drawOverlay(Graphics2D g2) {
        int ovalWidth = (int) (getWidth() * 1.0);
        int ovalHeight = (int) (getHeight() * 2.1);
        int x = -ovalWidth / 2;
        int y = -ovalHeight / 3;
        
        g2.setColor(GlobalView.WELCOME_ROUND_BACKGROUND);
        g2.fillOval(x, y, ovalWidth, ovalHeight);
    }

    @SuppressWarnings("unused")
    private void drawWelcomeText(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        String line1 = "¡Bienvenido";
        String line2 = "al sistema!";
        
        Font welcomeFont = GlobalView.TITLE_FONT.deriveFont(Font.BOLD, 72f);
        g2.setFont(welcomeFont);
        g2.setColor(GlobalView.LIGHT_TEXT_COLOR);

        FontMetrics fm = g2.getFontMetrics();
        int line1Width = fm.stringWidth(line1);
        int line2Width = fm.stringWidth(line2);
        int lineHeight = fm.getHeight();
        
        int x1 = 90;
        int x2 = 90;
        int y1 = (getHeight() - lineHeight) / 2;
        int y2 = y1 + lineHeight;

        g2.drawString(line1, x1, y1);
        g2.drawString(line2, x2, y2);
    }

    private void invalidateBlurredImageCache() {
        cachedBlurredImage = null;
        repaint();
    }

    private BufferedImage generateBlurredImage(int width, int height) {
        if (width <= 0 || height <= 0 || backgroundImage == null) {
            return null;
        }

        BufferedImage sourceImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = sourceImage.createGraphics();
        g2.drawImage(backgroundImage, 0, 0, width, height, this);
        g2.dispose();

        float[] matrix = new float[9];
        for (int i = 0; i < 9; i++) {
            matrix[i] = 1.0f / 9.0f;
        }
        Kernel kernel = new Kernel(3, 3, matrix);
        ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        
        BufferedImage blurred = sourceImage;
        int iterations = 12; 
        for (int i = 0; i < iterations; i++) {
            BufferedImage temp = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            op.filter(blurred, temp);
            blurred = temp;
        }
        return blurred;
    }
}