package co.edu.uptc.view.panels;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import org.json.*;

import co.edu.uptc.view.GlobalView;
import co.edu.uptc.view.dialogs.ConfirmDialog;
import co.edu.uptc.view.dialogs.SuccessPopUp;
import co.edu.uptc.view.utils.PropertiesService;

public class ServicesPanel extends JPanel {

    private PropertiesService p;
    private JTable table;
    private DefaultTableModel model;
    private List<JSONObject> allData;
    private List<JSONObject> filteredData;
    private int currentPage = 1;
    private final int rowsPerPage = 8;
    private JLabel pageLabel;
    private JButton prevBtn, nextBtn;
    private JTextField searchField;

    public ServicesPanel() {
        p = new PropertiesService();
        initComponents();
        loadJSONData();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(GlobalView.GENERAL_BACKGROUND);

        JLabel title = new JLabel("Gestión de Servicios", SwingConstants.CENTER);
        title.setFont(GlobalView.TITLE_FONT);
        title.setForeground(GlobalView.TEXT_COLOR);
        title.setBorder(new EmptyBorder(15, 0, 15, 0));
        add(title, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(GlobalView.GENERAL_BACKGROUND_LIGHT);
        centerPanel.setBorder(new EmptyBorder(10, 30, 41, 30));
        add(centerPanel, BorderLayout.CENTER);

        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setBackground(GlobalView.GENERAL_BACKGROUND_LIGHT);

        searchPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        searchField = new JTextField(" Buscar Servicio por nombre");
        searchField.setFont(GlobalView.TEXT_FIELD_FONT);
        searchField.setForeground(GlobalView.PLACEHOLDER_COLOR);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GlobalView.BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        searchField.setPreferredSize(new Dimension(0, 45));

        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().trim().equals("Buscar Servicio por nombre")) {
                    searchField.setText("");
                    searchField.setForeground(GlobalView.TEXT_COLOR);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText(" Buscar Servicio por nombre");
                    searchField.setForeground(GlobalView.PLACEHOLDER_COLOR);
                }
            }
        });

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                performSearch();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                performSearch();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                performSearch();
            }
        });

        searchPanel.add(searchField, BorderLayout.CENTER);
        centerPanel.add(searchPanel, BorderLayout.NORTH);

        String[] columns = { "Código", "Nombre Servicio", "Productos Asociados", "Precio servicio", "Total",
                "Acciones" };
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 5;
            }
        };
        table = new JTable(model);
        table.setRowHeight(47);
        table.setFont(GlobalView.TABLE_BODY_FONT);
        table.getTableHeader().setFont(GlobalView.TABLE_HEADER_FONT);
        table.getTableHeader().setBackground(GlobalView.TABLE_HEADER_BACKGROUND);
        table.getTableHeader().setForeground(GlobalView.TABLE_HEADER_FOREGROUND);
        table.setSelectionBackground(GlobalView.TABLE_SELECTION_BACKGROUND);
        table.setSelectionForeground(GlobalView.TABLE_SELECTION_FOREGROUND);
        table.setGridColor(GlobalView.BORDER_COLOR);
        table.setShowVerticalLines(false);
        table.setBorder(new EmptyBorder(0, 0, 0, 0));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount() - 1; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        table.getColumnModel().getColumn(5).setCellRenderer(new ActionRenderer());
        table.getColumnModel().getColumn(5).setCellEditor(new ActionEditor());
        table.getColumnModel().getColumn(5).setMaxWidth(120);
        table.getColumnModel().getColumn(5).setMinWidth(120);
        table.getTableHeader().setResizingAllowed(false);
        table.getTableHeader().setReorderingAllowed(false);

        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(100);
        columnModel.getColumn(1).setPreferredWidth(270);
        columnModel.getColumn(2).setPreferredWidth(350);
        columnModel.getColumn(3).setPreferredWidth(120);
        columnModel.getColumn(4).setPreferredWidth(120);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        int headerHeight = table.getTableHeader().getPreferredSize().height;
        int tableHeight = rowsPerPage * table.getRowHeight();
        scrollPane.setPreferredSize(new Dimension(0, headerHeight + tableHeight));

        scrollPane.setBorder(new LineBorder(GlobalView.BORDER_COLOR, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(GlobalView.GENERAL_BACKGROUND_LIGHT);
        bottomPanel.setBorder(new EmptyBorder(15, 30, 15, 30));
        centerPanel.add(bottomPanel, BorderLayout.SOUTH);

        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        paginationPanel.setBackground(GlobalView.GENERAL_BACKGROUND_LIGHT);

        prevBtn = new JButton("<<");
        nextBtn = new JButton(">>");
        pageLabel = new JLabel();
        pageLabel.setFont(GlobalView.TEXT_FIELD_FONT);

        for (JButton b : new JButton[] { prevBtn, nextBtn }) {
            b.setFont(GlobalView.BUTTON_FONT);
            b.setFocusable(false);
            b.setBackground(GlobalView.BUTTON_BACKGROUND_COLOR);
            b.setForeground(GlobalView.BUTTON_FOREGROUND_COLOR);
            b.setBorder(new LineBorder(GlobalView.BORDER_COLOR));
            b.setPreferredSize(new Dimension(50, 38));
            b.setCursor(new Cursor(Cursor.HAND_CURSOR));
            b.addMouseListener(
                    new ButtonHoverEffect(b, GlobalView.BUTTON_BACKGROUND_COLOR, GlobalView.BUTTON_HOVER_COLOR));
        }

        prevBtn.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                refreshTable();
            }
        });

        nextBtn.addActionListener(e -> {
            int maxPage = (int) Math.ceil((double) filteredData.size() / rowsPerPage);
            if (currentPage < maxPage) {
                currentPage++;
                refreshTable();
            }
        });

        JLabel paginaLabel = new JLabel("Página");
        paginaLabel.setFont(GlobalView.TEXT_FIELD_FONT);
        paginationPanel.add(paginaLabel);
        paginationPanel.add(prevBtn);
        paginationPanel.add(pageLabel);
        paginationPanel.add(nextBtn);
        bottomPanel.add(paginationPanel, BorderLayout.WEST);

        // --- Botón Nuevo Servicio ---
        ImageIcon addIcon = createIcon(p.getProperties("add"), 22, 22);
        JButton newServiceBtn = new JButton("Nuevo Servicio", addIcon);
        newServiceBtn.setIconTextGap(15);

        Font newButtonFont = new Font(GlobalView.BUTTON_FONT.getFamily(), Font.BOLD, 20);
        newServiceBtn.setFont(newButtonFont);
        newServiceBtn.setBackground(GlobalView.CONFIRM_BUTTON_BACKGROUND);
        newServiceBtn.setForeground(Color.WHITE);
        newServiceBtn.setFocusPainted(false);
        newServiceBtn.setBorder(new EmptyBorder(10, 25, 10, 25));
        newServiceBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        newServiceBtn.addMouseListener(new ButtonHoverEffect(newServiceBtn, GlobalView.CONFIRM_BUTTON_BACKGROUND,
                GlobalView.CONFIRM_BUTTON_BACKGROUND.darker()));
        newServiceBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(ServicesPanel.this, "Abriendo formulario para Nuevo Servicio...");
        });
        bottomPanel.add(newServiceBtn, BorderLayout.EAST);
    }

    private void performSearch() {
        String query = searchField.getText().trim();
        if (!query.equals("Buscar Servicio por nombre")) {
            filterData(query.toLowerCase());
        } else {
            filterData("");
        }
    }

    private void filterData(String query) {
        if (query.isEmpty()) {
            filteredData = new ArrayList<>(allData);
        } else {
            filteredData = new ArrayList<>();
            for (JSONObject obj : allData) {
                if (obj.has("nombre") && obj.getString("nombre").toLowerCase().contains(query)) {
                    filteredData.add(obj);
                }
            }
        }
        currentPage = 1;
        refreshTable();
    }

    public void loadJSONData() {
        String jsonText = """
                [
                    {"codigo":"SVC-001","nombre":"Cambio de aceite (Aceite 10W40)","productos":"Aceite 10W40 Mobil 3.78 L, Filtro aceite DGP","precio":"$120,000","total":"$160,000"},
                    {"codigo":"SVC-002","nombre":"Balanceo","productos":"","precio":"$80,000","total":"$80,000"},
                    {"codigo":"SVC-003","nombre":"Cambio de aceite (Aceite 5W30)","productos":"Aceite 5W30, Filtro Aceite DGP","precio":"$120,000","total":"$160,000"},
                    {"codigo":"SVC-004","nombre":"Alineación","productos":"Ninguno","precio":"$90,000","total":"$90,000"},
                    {"codigo":"SVC-005","nombre":"Revisión de frenos","productos":"Liquido de frenos Dot4","precio":"$75,000","total":"$85,000"},
                    {"codigo":"SVC-006","nombre":"Rotación de llantas","productos":"Ninguno","precio":"$50,000","total":"$50,000"},
                    {"codigo":"SVC-007","nombre":"Mantenimiento de batería","productos":"Limpiador de bornes, Grasa dieléctrica","precio":"$40,000","total":"$45,000"},
                    {"codigo":"SVC-008","nombre":"Cambio de filtro de aire","productos":"Filtro de aire","precio":"$30,000","total":"$60,000"},
                    {"codigo":"SVC-009","nombre":"Limpieza de inyectores","productos":"Aditivo limpiador de inyectores","precio":"$110,000","total":"$130,000"},
                    {"codigo":"SVC-010","nombre":"Cambio de bujías","productos":"Juego de Bujías Iridium","precio":"$60,000","total":"$120,000"},
                    {"codigo":"SVC-011","nombre":"Diagnóstico electrónico","productos":"Ninguno","precio":"$150,000","total":"$150,000"},
                    {"codigo":"SVC-012","nombre":"Instalación de accesorios","productos":"Según accesorio","precio":"$var","total":"$var"},
                    {"codigo":"SVC-013","nombre":"Lavado de motor","productos":"Desengrasante de motor","precio":"$70,000","total":"$80,000"},
                    {"codigo":"SVC-014","nombre":"Inspección Pre-compra","productos":"Ninguno","precio":"$100,000","total":"$100,000"}
                ]
                """;

        allData = new ArrayList<>();
        JSONArray arr = new JSONArray(jsonText);
        for (int i = 0; i < arr.length(); i++) {
            allData.add(arr.getJSONObject(i));
        }
        filteredData = new ArrayList<>(allData);
        refreshTable();
    }

    private void refreshTable() {
        model.setRowCount(0);

        if (filteredData.isEmpty()) {
            pageLabel.setText(" 0 / 0 ");
            prevBtn.setEnabled(false);
            nextBtn.setEnabled(false);
            return;
        }

        int start = (currentPage - 1) * rowsPerPage;
        int end = Math.min(start + rowsPerPage, filteredData.size());

        for (int i = start; i < end; i++) {
            JSONObject obj = filteredData.get(i);
            model.addRow(new Object[] {
                    obj.getString("codigo"),
                    obj.getString("nombre"),
                    obj.getString("productos"),
                    obj.getString("precio"),
                    obj.getString("total"),
                    ""
            });
        }

        int maxPage = Math.max(1, (int) Math.ceil((double) filteredData.size() / rowsPerPage));
        pageLabel.setText(" " + currentPage + " / " + maxPage + " ");
        prevBtn.setEnabled(currentPage > 1);
        nextBtn.setEnabled(currentPage < maxPage);
    }

    private ImageIcon createIcon(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(path);
        if (icon.getImage() == null) {
            System.err.println("Error: No se pudo cargar la imagen desde la ruta: " + path);
            return new ImageIcon(); // Devolver un icono vacío para evitar NullPointerException
        }
        Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    class ActionRenderer extends DefaultTableCellRenderer {
        private final JPanel panel = new JPanel();
        private final JButton editBtn;
        private final JButton deleteBtn;

        public ActionRenderer() {
            panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
            panel.setOpaque(true);

            editBtn = new JButton(createIcon(p.getProperties("edit"), 18, 18));
            deleteBtn = new JButton(createIcon(p.getProperties("delete"), 18, 18));

            for (JButton b : new JButton[] { editBtn, deleteBtn }) {
                b.setFocusPainted(false);
                b.setBorderPainted(false);
                b.setContentAreaFilled(true);
                b.setBackground(new Color(0, 0, 0, 0));
                b.setOpaque(false);
                b.setPreferredSize(new Dimension(35, 35));
                b.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            panel.add(editBtn);
            panel.add(deleteBtn);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus,
                int row, int column) {
            panel.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            return panel;
        }
    }

    class ActionEditor extends AbstractCellEditor implements TableCellEditor {
        private final JPanel panel = new JPanel();
        private final JButton editBtn;
        private final JButton deleteBtn;
        private JSONObject currentServiceData;

        public ActionEditor() {
            panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
            panel.setOpaque(true);

            editBtn = new JButton(createIcon(p.getProperties("edit"), 18, 18));
            deleteBtn = new JButton(createIcon(p.getProperties("delete"), 18, 18));

            for (JButton b : new JButton[] { editBtn, deleteBtn }) {
                b.setFocusPainted(false);
                b.setBorderPainted(false);
                b.setContentAreaFilled(true);
                b.setBackground(new Color(0, 0, 0, 0));
                b.setOpaque(false);
                b.setPreferredSize(new Dimension(35, 35));
                b.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            panel.add(editBtn);
            panel.add(deleteBtn);

            editBtn.addActionListener(e -> {
                fireEditingStopped();
                if (currentServiceData != null) {
                    JOptionPane.showMessageDialog(ServicesPanel.this,
                            "Editar: " + currentServiceData.getString("nombre"));
                }
            });

            deleteBtn.addActionListener(e -> {
                fireEditingStopped();
                if (currentServiceData != null) {
                    Window window = SwingUtilities.getWindowAncestor(ServicesPanel.this);
                    Frame parentFrame = window instanceof Frame ? (Frame) window : null;

                    boolean confirm = ConfirmDialog.showConfirmDialog(
                            parentFrame,
                            "¿Desea eliminar el servicio \"" + currentServiceData.getString("nombre") + "\"?",
                            "Confirmar eliminación");

                    if (confirm) {
                        allData.remove(currentServiceData);
                        filteredData.remove(currentServiceData);
                        refreshTable();

                        SuccessPopUp success = new SuccessPopUp(parentFrame, "Éxito:",
                                "El servicio se eliminó correctamente");
                        success.setVisible(true);
                    }
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            panel.setBackground(table.getSelectionBackground());
            int dataRowIndex = (currentPage - 1) * rowsPerPage + row;
            if (dataRowIndex >= 0 && dataRowIndex < filteredData.size()) {
                currentServiceData = filteredData.get(dataRowIndex);
            } else {
                currentServiceData = null;
            }
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return null;
        }
    }

    class ButtonHoverEffect extends MouseAdapter {
        private final Color defaultBackground;
        private final Color hoverBackground;
        private final JButton button;

        public ButtonHoverEffect(JButton button, Color defaultBackground, Color hoverBackground) {
            this.button = button;
            this.defaultBackground = defaultBackground;
            this.hoverBackground = hoverBackground;
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            button.setBackground(hoverBackground);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            button.setBackground(defaultBackground);
        }
    }
}