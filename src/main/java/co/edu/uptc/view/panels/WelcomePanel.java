package co.edu.uptc.view.panels;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

import co.edu.uptc.view.GlobalView;
import co.edu.uptc.view.utils.PropertiesService;

public class WelcomePanel extends JPanel {

    private Image backgroundImage;
    private PropertiesService p;

    public WelcomePanel() {
        p = new PropertiesService();
        backgroundImage = new ImageIcon(p.getProperties("welcomeimage")).getImage();
        setLayout(new BorderLayout());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        BufferedImage blurred = createBlurredImage(getWidth(), getHeight(), 15);
        g2.drawImage(blurred, 0, 0, getWidth(), getHeight(), this);

        int ovalWidth = (int) (getWidth()* 1.);  
        int ovalHeight = (int) (getHeight() * 2.1);
        g2.setColor(GlobalView.WELCOME_ROUND_BACKGROUND);
        g2.fillOval(-ovalWidth / 2, -ovalHeight / 3, ovalWidth, ovalHeight);

        String text = "<html><div style='text-align:center;'>¡Bienvenido<br>al sistema!</div></html>";
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 72));
        label.setForeground(Color.WHITE);
        label.setSize(getWidth(), getHeight());
        label.setHorizontalAlignment(SwingConstants.LEFT);
        label.setVerticalAlignment(SwingConstants.CENTER);

        g2.translate(90, 0);
        label.paint(g2);
        g2.dispose();
    }

    /**
     * @param width 
     * @param height 
     * @param iterations 
     * @return
     */
    private BufferedImage createBlurredImage(int width, int height, int iterations) {
        if (width <= 0 || height <= 0) return null;

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.drawImage(backgroundImage, 0, 0, width, height, this);
        g2.dispose();

        float[] matrix = {
                1f / 9f, 1f / 9f, 1f / 9f,
                1f / 9f, 1f / 9f, 1f / 9f,
                1f / 9f, 1f / 9f, 1f / 9f
        };
        ConvolveOp op = new ConvolveOp(new Kernel(3, 3, matrix), ConvolveOp.EDGE_NO_OP, null);

        BufferedImage blurred = img;
        for (int i = 0; i < iterations; i++) {
            BufferedImage temp = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            op.filter(blurred, temp);
            blurred = temp;
        }

        return blurred;
    }
}
