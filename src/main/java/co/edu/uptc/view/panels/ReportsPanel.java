package co.edu.uptc.view.panels;

import javax.swing.*;
import javax.swing.border.*;

import org.json.JSONArray;
import org.json.JSONObject;
import com.toedter.calendar.JDateChooser;
import co.edu.uptc.view.GlobalView;
import co.edu.uptc.view.utils.PropertiesService;
import java.awt.*;
import java.util.Date;

public class ReportsPanel extends JPanel {

    private JList<JSONObject> alertList;
    private DefaultListModel<JSONObject> alertListModel;
    private JComboBox<String> reportTypeComboBox;
    private JPanel parametersPanel;
    private CardLayout cardLayout;
    private JDateChooser startDateChooser, endDateChooser;
    private JSpinner topNSpinner;
    private PropertiesService p;

    public ReportsPanel() {
        this.p = new PropertiesService();
        initComponents();
        loadAlertsData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(20, 0));
        setBackground(GlobalView.GENERAL_BACKGROUND);
        setBorder(new EmptyBorder(15, 20, 20, 20));

        // --- Título Principal ---
        JLabel title = new JLabel("Gestión de Reportes y Alertas", SwingConstants.CENTER);
        title.setFont(GlobalView.TITLE_FONT);
        title.setForeground(GlobalView.TEXT_COLOR);
        title.setBorder(new EmptyBorder(0, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        // --- Panel Contenedor Principal (2 columnas) ---
        JPanel mainContentPanel = new JPanel(new GridLayout(1, 2, 25, 0));
        mainContentPanel.setOpaque(false);

        mainContentPanel.add(createAlertsPanel());
        mainContentPanel.add(createReportsPanel());

        add(mainContentPanel, BorderLayout.CENTER);
    }

    private JPanel createAlertsPanel() {
        JPanel alertsContainer = new JPanel(new BorderLayout(0, 10));
        alertsContainer.setOpaque(false);
        alertsContainer.setBorder(new TitledBorder(
            new LineBorder(GlobalView.BORDER_COLOR, 1, true),
            " Alertas de Inventario ",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            GlobalView.TABLE_HEADER_FONT,
            GlobalView.TEXT_COLOR
        ));

        alertListModel = new DefaultListModel<>();
        alertList = new JList<>(alertListModel);
        alertList.setCellRenderer(new AlertListCellRenderer());
        alertList.setBackground(GlobalView.GENERAL_BACKGROUND_LIGHT);
        alertList.setSelectionBackground(GlobalView.TABLE_SELECTION_BACKGROUND);
        alertList.setFixedCellHeight(70);

        JScrollPane scrollPane = new JScrollPane(alertList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        alertsContainer.add(scrollPane, BorderLayout.CENTER);

        return alertsContainer;
    }

    private JPanel createReportsPanel() {
        JPanel reportsContainer = new JPanel(new BorderLayout(0, 15));
        reportsContainer.setOpaque(false);
        reportsContainer.setBorder(new TitledBorder(
            new LineBorder(GlobalView.BORDER_COLOR, 1, true),
            " Generación de Reportes ",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            GlobalView.TABLE_HEADER_FONT,
            GlobalView.TEXT_COLOR
        ));
        reportsContainer.setBorder(new CompoundBorder(reportsContainer.getBorder(), new EmptyBorder(10, 15, 15, 15)));

        // --- Panel de Configuración (Superior) ---
        JPanel configPanel = new JPanel();
        configPanel.setLayout(new BoxLayout(configPanel, BoxLayout.Y_AXIS));
        configPanel.setOpaque(false);

        JLabel reportTypeLabel = new JLabel("Seleccione el tipo de reporte:");
        reportTypeLabel.setFont(GlobalView.TABLE_BODY_FONT.deriveFont(Font.BOLD));
        reportTypeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        String[] reportTypes = {"Reporte de Ventas", "Valoración de Inventario", "Productos Más Vendidos"};
        reportTypeComboBox = new JComboBox<>(reportTypes);
        reportTypeComboBox.setFont(GlobalView.TEXT_FIELD_FONT);
        reportTypeComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        reportTypeComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        configPanel.add(reportTypeLabel);
        configPanel.add(Box.createVerticalStrut(5));
        configPanel.add(reportTypeComboBox);
        configPanel.add(Box.createVerticalStrut(20));

        // --- Panel de Parámetros Dinámicos ---
        parametersPanel = createReportParametersPanel();
        configPanel.add(parametersPanel);

        reportTypeComboBox.addActionListener(e -> updateParametersPanel());
        updateParametersPanel(); // Llamada inicial

        reportsContainer.add(configPanel, BorderLayout.NORTH);
        
        // --- Botón de Generar Reporte ---
        JButton generateButton = new JButton("Generar Reporte");
        generateButton.setFont(new Font(GlobalView.BUTTON_FONT.getFamily(), Font.BOLD, 16));
        generateButton.setIcon(createIcon(p.getProperties("report"), 22, 22));
        generateButton.setIconTextGap(15);
        generateButton.setBackground(GlobalView.CONFIRM_BUTTON_BACKGROUND);
        generateButton.setForeground(Color.WHITE);
        generateButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        generateButton.setPreferredSize(new Dimension(0, 50));
        generateButton.addActionListener(e -> generateReport());
        
        reportsContainer.add(generateButton, BorderLayout.SOUTH);
        
        return reportsContainer;
    }
    
    private JPanel createReportParametersPanel() {
        JPanel panel = new JPanel(new CardLayout());
        panel.setOpaque(false);
        this.cardLayout = (CardLayout) panel.getLayout();

        // 1. Panel para Rango de Fechas
        JPanel dateRangePanel = new JPanel(new GridLayout(2, 2, 5, 5));
        dateRangePanel.setOpaque(false);
        startDateChooser = new JDateChooser();
        endDateChooser = new JDateChooser();
        startDateChooser.setDate(new Date());
        endDateChooser.setDate(new Date());
        dateRangePanel.add(new JLabel("Fecha de Inicio:") {{ setFont(GlobalView.TABLE_BODY_FONT); }});
        dateRangePanel.add(startDateChooser);
        dateRangePanel.add(new JLabel("Fecha de Fin:") {{ setFont(GlobalView.TABLE_BODY_FONT); }});
        dateRangePanel.add(endDateChooser);
        
        // 2. Panel para Límite (Top N)
        JPanel topNPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        topNPanel.setOpaque(false);
        topNSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
        topNSpinner.setFont(GlobalView.TEXT_FIELD_FONT);
        topNPanel.add(new JLabel("Mostrar Top:") {{ setFont(GlobalView.TABLE_BODY_FONT); }});
        topNPanel.add(topNSpinner);

        // 3. Panel Vacío
        JPanel emptyPanel = new JPanel();
        emptyPanel.setOpaque(false);

        panel.add(dateRangePanel, "DATES");
        panel.add(emptyPanel, "EMPTY");
        panel.add(topNPanel, "SPINNER");

        return panel;
    }

    private void updateParametersPanel() {
        int selectedIndex = reportTypeComboBox.getSelectedIndex();
        switch (selectedIndex) {
            case 0: // Reporte de Ventas
                cardLayout.show(parametersPanel, "DATES");
                break;
            case 1: // Valoración de Inventario
                cardLayout.show(parametersPanel, "EMPTY");
                break;
            case 2: // Productos Más Vendidos
                cardLayout.show(parametersPanel, "SPINNER");
                break;
        }
    }

    private void loadAlertsData() {
        String jsonText = """
        [
            {"level": "CRITICAL_STOCK", "message": "Batería Gold 12V (MAC) solo tiene 2 unidades restantes."},
            {"level": "LOW_STOCK", "message": "Aceite Sintético 5W-30 (Mobil 1) tiene 8 unidades."},
            {"level": "CRITICAL_STOCK", "message": "Pastillas Cerámicas D1044 (Wagner) agotadas (0 unidades)."},
            {"level": "LOW_STOCK", "message": "Llanta Pilot Sport 4S (225/45 R17) tiene 5 unidades."},
            {"level": "LOW_STOCK", "message": "Filtro de Aire C24025 (Mann-Filter) tiene 9 unidades."}
        ]
        """;
        JSONArray alerts = new JSONArray(jsonText);
        for (int i = 0; i < alerts.length(); i++) {
            alertListModel.addElement(alerts.getJSONObject(i));
        }
    }
    
    private void generateReport() {
        int selectedIndex = reportTypeComboBox.getSelectedIndex();
        String reportName = (String) reportTypeComboBox.getSelectedItem();
        String details = "";

        switch (selectedIndex) {
            case 0: // Ventas
                details = String.format("Desde %tF hasta %tF", startDateChooser.getDate(), endDateChooser.getDate());
                break;
            case 2: // Top N
                details = String.format("Top %d productos", topNSpinner.getValue());
                break;
            default: // Inventario
                details = "Reporte de inventario completo";
        }
        
        JOptionPane.showMessageDialog(this,
            "Generando '" + reportName + "'...\nDetalles: " + details,
            "Generación de Reporte",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private ImageIcon createIcon(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(path);
        if (icon.getImage().getWidth(null) == -1) { // Verifica si la imagen se cargó
            System.err.println("Error al cargar el ícono: " + path);
            return new ImageIcon(); // Devuelve un ícono vacío para evitar errores
        }
        return new ImageIcon(icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
    }
    
    // --- Clase Interna para Renderizar las Celdas de Alerta ---
    
    private class AlertListCellRenderer extends JPanel implements ListCellRenderer<JSONObject> {
        private JLabel iconLabel;
        private JTextArea messageArea;
        private Color lowStockColor = new Color(255, 193, 7); // Amarillo
        private Color criticalStockColor = new Color(220, 53, 69); // Rojo

        public AlertListCellRenderer() {
            setLayout(new BorderLayout(15, 0));
            setOpaque(true);
            
            iconLabel = new JLabel();
            messageArea = new JTextArea();
            messageArea.setWrapStyleWord(true);
            messageArea.setLineWrap(true);
            messageArea.setOpaque(false);
            messageArea.setEditable(false);
            messageArea.setFont(GlobalView.TABLE_BODY_FONT.deriveFont(14f));

            add(iconLabel, BorderLayout.WEST);
            add(messageArea, BorderLayout.CENTER);
            setBorder(new EmptyBorder(10, 10, 10, 10));
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends JSONObject> list, JSONObject value, int index, boolean isSelected, boolean cellHasFocus) {
            String level = value.getString("level");
            String message = value.getString("message");
            
            messageArea.setText(message);

            Color borderColor;
            if ("CRITICAL_STOCK".equals(level)) {
                iconLabel.setIcon(createIcon(p.getProperties("error"), 32, 32));
                borderColor = criticalStockColor;
            } else { // LOW_STOCK
                iconLabel.setIcon(createIcon(p.getProperties("warning"), 32, 32));
                borderColor = lowStockColor;
            }

            setBorder(new CompoundBorder(
                new MatteBorder(0, 5, 0, 0, borderColor),
                new EmptyBorder(10, 10, 10, 10)
            ));

            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            return this;
        }
    }
}