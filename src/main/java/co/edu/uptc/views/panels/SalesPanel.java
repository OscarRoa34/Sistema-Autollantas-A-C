package co.edu.uptc.views.panels;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import org.json.*;

import co.edu.uptc.view.GlobalView;
import co.edu.uptc.view.dialogs.ConfirmDialog;
import co.edu.uptc.view.dialogs.SuccessPopUp;
import co.edu.uptc.view.utils.PropertiesService;

public class SalesPanel extends JPanel {

    private PropertiesService p;
    private JTable table;
    private DefaultTableModel model;
    private List<JSONObject> allSales;
    private List<JSONObject> filteredSales;
    private int currentPage = 1;
    private final int rowsPerPage = 8;
    private JLabel pageLabel;
    private JButton prevBtn, nextBtn;
    private JTextField searchField;

    public SalesPanel() {
        p = new PropertiesService();
        initComponents();
        loadJSONData();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(GlobalView.GENERAL_BACKGROUND);

        JLabel title = new JLabel("Gestión de Facturas de Venta", SwingConstants.CENTER);
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

        searchField = new JTextField(" Buscar por nombre del cliente");
        searchField.setFont(GlobalView.TEXT_FIELD_FONT);
        searchField.setForeground(GlobalView.PLACEHOLDER_COLOR);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GlobalView.BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        searchField.setPreferredSize(new Dimension(0, 45));

        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().trim().equals("Buscar por nombre del cliente")) {
                    searchField.setText("");
                    searchField.setForeground(GlobalView.TEXT_COLOR);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().trim().isEmpty()) {
                    searchField.setText(" Buscar por nombre del cliente");
                    searchField.setForeground(GlobalView.PLACEHOLDER_COLOR);
                }
            }
        });

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { performSearch(); }
            public void removeUpdate(DocumentEvent e) { performSearch(); }
            public void changedUpdate(DocumentEvent e) { performSearch(); }
        });

        searchPanel.add(searchField, BorderLayout.CENTER);
        centerPanel.add(searchPanel, BorderLayout.NORTH);

        String[] columns = { "Factura Nº", "Cliente", "Fecha", "Total", "Estado", "Acciones" };
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 5; 
            }
        };

        table = new JTable(model);
        setupTableStyle();

        JScrollPane scrollPane = new JScrollPane(table);
        setupScrollPane(scrollPane);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        centerPanel.add(createBottomPanel(), BorderLayout.SOUTH);
    }

    private void setupTableStyle() {
        table.setRowHeight(48);
        table.setFont(GlobalView.TABLE_BODY_FONT);
        table.setSelectionBackground(GlobalView.TABLE_SELECTION_BACKGROUND);
        table.setSelectionForeground(GlobalView.TABLE_SELECTION_FOREGROUND);
        table.setGridColor(GlobalView.BORDER_COLOR);
        table.setShowVerticalLines(false);
        table.setBorder(new EmptyBorder(0, 0, 0, 0));

        JTableHeader header = table.getTableHeader();
        header.setFont(GlobalView.TABLE_HEADER_FONT);
        header.setBackground(GlobalView.TABLE_HEADER_BACKGROUND);
        header.setForeground(GlobalView.TABLE_HEADER_FOREGROUND);
        header.setResizingAllowed(false);
        header.setReorderingAllowed(false);
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount() - 1; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(5).setCellRenderer(new ActionRenderer());
        columnModel.getColumn(5).setCellEditor(new ActionEditor(this));
        columnModel.getColumn(5).setMaxWidth(120);
        columnModel.getColumn(5).setMinWidth(120);
    }
    
    private void setupScrollPane(JScrollPane scrollPane) {
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        int headerHeight = table.getTableHeader().getPreferredSize().height;
        int tableHeight = rowsPerPage * table.getRowHeight();
        scrollPane.setPreferredSize(new Dimension(0, headerHeight + tableHeight));
        scrollPane.setBorder(new LineBorder(GlobalView.BORDER_COLOR, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
    }
    
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(GlobalView.GENERAL_BACKGROUND_LIGHT);
        bottomPanel.setBorder(new EmptyBorder(15, 30, 15, 30));
        
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
            b.addMouseListener(new ButtonHoverEffect(b, GlobalView.BUTTON_BACKGROUND_COLOR, GlobalView.BUTTON_HOVER_COLOR));
        }

        prevBtn.addActionListener(e -> { if (currentPage > 1) { currentPage--; refreshTable(); } });
        nextBtn.addActionListener(e -> {
            int maxPage = (int) Math.ceil((double) filteredSales.size() / rowsPerPage);
            if (currentPage < maxPage) { currentPage++; refreshTable(); }
        });

        JLabel paginaLabel = new JLabel("Página");
        paginaLabel.setFont(GlobalView.TEXT_FIELD_FONT);
        paginationPanel.add(paginaLabel);
        paginationPanel.add(prevBtn);
        paginationPanel.add(pageLabel);
        paginationPanel.add(nextBtn);
        
        // --- Botón Nueva Venta ---
        ImageIcon addIcon = createIcon(p.getProperties("add"), 22, 22);
        JButton newSaleBtn = new JButton("Nueva Venta", addIcon);
        newSaleBtn.setFont(new Font(GlobalView.BUTTON_FONT.getFamily(), Font.BOLD, 16));
        newSaleBtn.setIconTextGap(15);
        newSaleBtn.setBackground(GlobalView.CONFIRM_BUTTON_BACKGROUND);
        newSaleBtn.setForeground(Color.WHITE);
        newSaleBtn.setFocusPainted(false);
        newSaleBtn.setBorder(new EmptyBorder(10, 25, 10, 25));
        newSaleBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        newSaleBtn.addMouseListener(new ButtonHoverEffect(newSaleBtn, GlobalView.CONFIRM_BUTTON_BACKGROUND, GlobalView.CONFIRM_BUTTON_BACKGROUND.darker()));
        newSaleBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Abriendo formulario para Nueva Venta..."));

        bottomPanel.add(paginationPanel, BorderLayout.WEST);
        bottomPanel.add(newSaleBtn, BorderLayout.EAST);
        return bottomPanel;
    }

    private void performSearch() {
        String query = searchField.getText().trim().toLowerCase();
        if (!query.equals("buscar por nombre del cliente")) {
            filterData(query);
        } else {
            filterData("");
        }
    }

    private void filterData(String query) {
        if (query.isEmpty()) {
            filteredSales = new ArrayList<>(allSales);
        } else {
            filteredSales = new ArrayList<>();
            for (JSONObject sale : allSales) {
                if (sale.getString("cliente").toLowerCase().contains(query)) {
                    filteredSales.add(sale);
                }
            }
        }
        currentPage = 1;
        refreshTable();
    }

    public void loadJSONData() {
        String jsonText = """
        [
            {"factura": "FV-2025-001", "cliente": "Carlos Ramírez", "fecha": "2025-10-15", "total": "$ 1,250,000", "estado": "Pagada"},
            {"factura": "FV-2025-002", "cliente": "Sofía Torres", "fecha": "2025-10-15", "total": "$ 880,000", "estado": "Pagada"},
            {"factura": "FV-2025-003", "cliente": "Andrés Gómez", "fecha": "2025-10-14", "total": "$ 450,000", "estado": "Pendiente"},
            {"factura": "FV-2025-004", "cliente": "Luisa Fernanda", "fecha": "2025-10-14", "total": "$ 2,100,000", "estado": "Pagada"},
            {"factura": "FV-2025-005", "cliente": "Javier Mendoza", "fecha": "2025-10-13", "total": "$ 320,000", "estado": "Anulada"},
            {"factura": "FV-2025-006", "cliente": "Valeria Castillo", "fecha": "2025-10-13", "total": "$ 760,000", "estado": "Pagada"},
            {"factura": "FV-2025-007", "cliente": "Ricardo Peña", "fecha": "2025-10-12", "total": "$ 1,500,000", "estado": "Pagada"},
            {"factura": "FV-2025-008", "cliente": "Mariana Ríos", "fecha": "2025-10-12", "total": "$ 510,000", "estado": "Pagada"},
            {"factura": "FV-2025-009", "cliente": "Esteban Díaz", "fecha": "2025-10-11", "total": "$ 930,000", "estado": "Pendiente"},
            {"factura": "FV-2025-010", "cliente": "Camila Vargas", "fecha": "2025-10-11", "total": "$ 1,800,000", "estado": "Pagada"},
            {"factura": "FV-2025-011", "cliente": "Daniela Mora", "fecha": "2025-10-10", "total": "$ 620,000", "estado": "Pagada"}
        ]
        """;
        allSales = new ArrayList<>();
        JSONArray arr = new JSONArray(jsonText);
        for (int i = 0; i < arr.length(); i++) {
            allSales.add(arr.getJSONObject(i));
        }
        filteredSales = new ArrayList<>(allSales);
        refreshTable();
    }

    private void refreshTable() {
        model.setRowCount(0);

        if (filteredSales.isEmpty()) {
            pageLabel.setText(" 0 / 0 ");
            prevBtn.setEnabled(false);
            nextBtn.setEnabled(false);
            return;
        }

        int start = (currentPage - 1) * rowsPerPage;
        int end = Math.min(start + rowsPerPage, filteredSales.size());

        for (int i = start; i < end; i++) {
            JSONObject sale = filteredSales.get(i);
            model.addRow(new Object[]{
                sale.getString("factura"),
                sale.getString("cliente"),
                sale.getString("fecha"),
                sale.getString("total"),
                sale.getString("estado"),
                ""
            });
        }

        int maxPage = Math.max(1, (int) Math.ceil((double) filteredSales.size() / rowsPerPage));
        pageLabel.setText(" " + currentPage + " / " + maxPage + " ");
        prevBtn.setEnabled(currentPage > 1);
        nextBtn.setEnabled(currentPage < maxPage);
    }
    
    
    private ImageIcon createIcon(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(path);
        if (icon.getImage() == null) {
            System.err.println("Error: No se pudo cargar la imagen desde la ruta: " + path);
            return new ImageIcon();
        }
        Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    class ActionRenderer extends DefaultTableCellRenderer {
        private final JPanel panel;
        private final JButton editBtn;
        private final JButton deleteBtn;

        public ActionRenderer() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            panel.setOpaque(true);
            
            editBtn = new JButton(createIcon(p.getProperties("edit"), 18, 18));
            deleteBtn = new JButton(createIcon(p.getProperties("delete"), 18, 18));
            
            for (JButton b : new JButton[]{editBtn, deleteBtn}) {
                b.setFocusPainted(false);
                b.setBorderPainted(false);
                b.setContentAreaFilled(false);
                b.setOpaque(false);
                b.setPreferredSize(new Dimension(35, 35));
                b.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            panel.add(editBtn);
            panel.add(deleteBtn);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            panel.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            return panel;
        }
    }

    class ActionEditor extends AbstractCellEditor implements TableCellEditor {
        private final JPanel panel;
        private JSONObject currentSaleData;
        private final SalesPanel salesPanel;

        public ActionEditor(SalesPanel salesPanel) {
            this.salesPanel = salesPanel;
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            panel.setOpaque(true);
            
            JButton editBtn = new JButton(createIcon(p.getProperties("edit"), 18, 18));
            JButton deleteBtn = new JButton(createIcon(p.getProperties("delete"), 18, 18));
            
            for (JButton b : new JButton[]{editBtn, deleteBtn}) {
                b.setFocusPainted(false);
                b.setBorderPainted(false);
                b.setContentAreaFilled(false);
                b.setPreferredSize(new Dimension(35, 35));
                b.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            editBtn.addActionListener(e -> {
                fireEditingStopped();
                if (currentSaleData != null) {
                    JOptionPane.showMessageDialog(salesPanel, "Editar Factura: " + currentSaleData.getString("factura"));
                }
            });

            deleteBtn.addActionListener(e -> {
                fireEditingStopped();
                if (currentSaleData != null) {
                    Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(salesPanel);
                    boolean confirm = ConfirmDialog.showConfirmDialog(
                        parentFrame,
                        "¿Desea eliminar la factura \"" + currentSaleData.getString("factura") + "\"?",
                        "Confirmar Eliminación");
                    if (confirm) {
                        allSales.remove(currentSaleData);
                        filteredSales.remove(currentSaleData);
                        refreshTable();
                        SuccessPopUp.showSuccessPopup(parentFrame, "Éxito:", "La factura se eliminó correctamente.");
                    }
                }
            });

            panel.add(editBtn);
            panel.add(deleteBtn);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            panel.setBackground(table.getSelectionBackground());
            int dataRowIndex = (currentPage - 1) * rowsPerPage + row;
            if (dataRowIndex >= 0 && dataRowIndex < filteredSales.size()) {
                currentSaleData = filteredSales.get(dataRowIndex);
            } else {
                currentSaleData = null;
            }
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return null;
        }
    }
    
    class ButtonHoverEffect extends MouseAdapter {
        private final JButton button;
        private final Color defaultBackground;
        private final Color hoverBackground;

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