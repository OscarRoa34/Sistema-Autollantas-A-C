package co.edu.uptc.view.panels;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import co.edu.uptc.view.GlobalView;
import co.edu.uptc.view.utils.PropertiesService;
import co.edu.uptc.view.utils.RoundedButton;

public class HeaderPanel extends JPanel {

    private RoundedButton btnCerrar;
    private JLabel lblFechaHora;
    private PropertiesService p;
    @SuppressWarnings("deprecation")
    private final SimpleDateFormat formato =
            new SimpleDateFormat("'Hoy es' EEEE d 'de' MMMM 'de' yyyy, h:mma", new Locale("es", "ES"));

    public HeaderPanel() {
        p = new PropertiesService();
        setLayout(new BorderLayout());
        setBackground(GlobalView.HEADER_BACKGROUND);
        setPreferredSize(new Dimension(0, 100));

        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(23, 23, 23, 23));

        lblFechaHora = new JLabel(formatFecha());
        lblFechaHora.setForeground(Color.WHITE);
        lblFechaHora.setFont(new Font("SansSerif", Font.BOLD, 20));
        content.add(lblFechaHora, BorderLayout.WEST);

        Timer timer = new Timer(60000, e -> lblFechaHora.setText(formatFecha()));
        timer.setInitialDelay(0);
        timer.start();

        ImageIcon iconSalir = new ImageIcon(
                new ImageIcon(p.getProperties("logout"))
                        .getImage()
                        .getScaledInstance(32, 32, Image.SCALE_SMOOTH));

        btnCerrar = new RoundedButton(
                "Cerrar",
                iconSalir,
                15,
                GlobalView.CLOSE_BUTTON_BACKGROUND,
                GlobalView.CLOSE_BUTTON_BACKGROUND_HOVER,
                null
        );

        btnCerrar.addActionListener(e -> {
            boolean confirm = co.edu.uptc.view.dialogs.ConfirmDialog.showConfirmDialog(
                    null,
                    "¿Está seguro de querer cerrar la aplicación?",
                    "Confirmar salida"
            );

            if (confirm) {
                System.exit(0);
            }
        });

        content.add(btnCerrar, BorderLayout.EAST);
        add(content, BorderLayout.CENTER);
    }

    private String formatFecha() {
        String texto = formato.format(new Date()).toLowerCase();
        return "H" + texto.substring(1);
    }
}
