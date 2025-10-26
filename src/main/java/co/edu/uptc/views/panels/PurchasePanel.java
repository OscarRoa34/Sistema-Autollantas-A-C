package co.edu.uptc.views.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;

import org.json.JSONArray;
import org.json.JSONObject;

import co.edu.uptc.view.GlobalView;
import co.edu.uptc.view.dialogs.ConfirmDialog;
import co.edu.uptc.view.dialogs.SuccessPopUp;
import co.edu.uptc.view.utils.PropertiesService;

public class PurchasePanel extends JPanel {

    private PropertiesService p;
    private JTable table;
    private DefaultTableModel model;
    private List<JSONObject> allPurchases;
    private List<JSONObject> filteredPurchases;
    private int currentPage = 1;
    private final int rowsPerPage = 8;
    private JLabel pageLabel;
    private JButton prevBtn, nextBtn;
    private JTextField searchField;

    public PurchasePanel() {
        p = new PropertiesService();
        initComponents();
        loadJSONData();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(GlobalView.GENERAL_BACKGROUND);

        JLabel title = new JLabel("Gestión de Facturas de Compra", SwingConstants.CENTER);
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

        searchField = new JTextField(" Buscar por nombre del proveedor");
        searchField.setFont(GlobalView.TEXT_FIELD_FONT);
        searchField.setForeground(GlobalView.PLACEHOLDER_COLOR);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GlobalView.BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        searchField.setPreferredSize(new Dimension(0, 45));

        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().trim().equals("Buscar por nombre del proveedor")) {
                    searchField.setText("");
                    searchField.setForeground(GlobalView.TEXT_COLOR);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().trim().isEmpty()) {
                    searchField.setText(" Buscar por nombre del proveedor");
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

        String[] columns = { "Factura Nº", "Proveedor", "Fecha", "Total", "Estado", "Acciones" };
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
            int maxPage = (int) Math.ceil((double) filteredPurchases.size() / rowsPerPage);
            if (currentPage < maxPage) { currentPage++; refreshTable(); }
        });

        paginationPanel.add(new JLabel("Página") {{ setFont(GlobalView.TEXT_FIELD_FONT); }});
        paginationPanel.add(prevBtn);
        paginationPanel.add(pageLabel);
        paginationPanel.add(nextBtn);
        
        ImageIcon addIcon = createIcon(p.getProperties("add"), 22, 22);
        JButton newPurchaseBtn = new JButton("Nueva Compra", addIcon);
        newPurchaseBtn.setFont(new Font(GlobalView.BUTTON_FONT.getFamily(), Font.BOLD, 16));
        newPurchaseBtn.setIconTextGap(15);
        newPurchaseBtn.setBackground(GlobalView.CONFIRM_BUTTON_BACKGROUND);
        newPurchaseBtn.setForeground(Color.WHITE);
        newPurchaseBtn.setFocusPainted(false);
        newPurchaseBtn.setBorder(new EmptyBorder(10, 25, 10, 25));
        newPurchaseBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        newPurchaseBtn.addMouseListener(new ButtonHoverEffect(newPurchaseBtn, GlobalView.CONFIRM_BUTTON_BACKGROUND, GlobalView.CONFIRM_BUTTON_BACKGROUND.darker()));
        newPurchaseBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Abriendo formulario para Nueva Compra..."));

        bottomPanel.add(paginationPanel, BorderLayout.WEST);
        bottomPanel.add(newPurchaseBtn, BorderLayout.EAST);
        return bottomPanel;
    }

    private void performSearch() {
        String query = searchField.getText().trim().toLowerCase();
        if (!query.equals("buscar por nombre del proveedor")) {
            filterData(query);
        } else {
            filterData("");
        }
    }

    private void filterData(String query) {
        if (query.isEmpty()) {
            filteredPurchases = new ArrayList<>(allPurchases);
        } else {
            filteredPurchases = new ArrayList<>();
            for (JSONObject purchase : allPurchases) {
                if (purchase.getString("proveedor").toLowerCase().contains(query)) {
                    filteredPurchases.add(purchase);
                }
            }
        }
        currentPage = 1;
        refreshTable();
    }

    public void loadJSONData() {
        String jsonText = """
        [
            {"factura": "FC-2025-A01", "proveedor": "Distribuidora SAS", "fecha": "2025-10-15", "total": "$ 5,800,000", "estado": "Recibida"},
            {"factura": "FC-2025-B02", "proveedor": "Importaciones Auto", "fecha": "2025-10-15", "total": "$ 12,300,000", "estado": "Recibida"},
            {"factura": "FC-2025-C03", "proveedor": "Repuestos Global", "fecha": "2025-10-14", "total": "$ 2,150,000", "estado": "En Tránsito"},
            {"factura": "FC-2025-D04", "proveedor": "Llantas del Caribe", "fecha": "2025-10-14", "total": "$ 8,500,000", "estado": "Recibida"},
            {"factura": "FC-2025-E05", "proveedor": "Filtros y Partes", "fecha": "2025-10-13", "total": "$ 1,200,000", "estado": "Cancelada"},
            {"factura": "FC-2025-F06", "proveedor": "Distribuidora SAS", "fecha": "2025-10-13", "total": "$ 3,450,000", "estado": "Recibida"},
            {"factura": "FC-2025-G07", "proveedor": "Importaciones Auto", "fecha": "2025-10-12", "total": "$ 7,900,000", "estado": "Recibida"},
            {"factura": "FC-2025-H08", "proveedor": "Repuestos Global", "fecha": "2025-10-12", "total": "$ 4,000,000", "estado": "Pagada"},
            {"factura": "FC-2025-I09", "proveedor": "Llantas del Caribe", "fecha": "2025-10-11", "total": "$ 11,200,000", "estado": "Recibida"},
            {"factura": "FC-2025-J10", "proveedor": "Filtros y Partes", "fecha": "2025-10-11", "total": "$ 950,000", "estado": "Pagada"},
            {"factura": "FC-2025-K11", "proveedor": "Distribuidora SAS", "fecha": "2025-10-10", "total": "$ 6,100,000", "estado": "Recibida"}
        ]
        """;
        allPurchases = new ArrayList<>();
        JSONArray arr = new JSONArray(jsonText);
        for (int i = 0; i < arr.length(); i++) {
            allPurchases.add(arr.getJSONObject(i));
        }
        filteredPurchases = new ArrayList<>(allPurchases);
        refreshTable();
    }

    private void refreshTable() {
        model.setRowCount(0);

        if (filteredPurchases.isEmpty()) {
            pageLabel.setText(" 0 / 0 ");
            prevBtn.setEnabled(false);
            nextBtn.setEnabled(false);
            return;
        }

        int start = (currentPage - 1) * rowsPerPage;
        int end = Math.min(start + rowsPerPage, filteredPurchases.size());

        for (int i = start; i < end; i++) {
            JSONObject purchase = filteredPurchases.get(i);
            model.addRow(new Object[]{
                purchase.getString("factura"),
                purchase.getString("proveedor"),
                purchase.getString("fecha"),
                purchase.getString("total"),
                purchase.getString("estado"),
                ""
            });
        }

        int maxPage = Math.max(1, (int) Math.ceil((double) filteredPurchases.size() / rowsPerPage));
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
        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        
        public ActionRenderer() {
            setOpaque(true);
            JButton editBtn = new JButton(createIcon(p.getProperties("edit"), 18, 18));
            JButton deleteBtn = new JButton(createIcon(p.getProperties("delete"), 18, 18));
            for (JButton b : new JButton[]{editBtn, deleteBtn}) {
                b.setFocusPainted(false);
                b.setBorderPainted(false);
                b.setContentAreaFilled(false);
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
        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        private JSONObject currentPurchaseData;
        private final PurchasePanel purchasePanel;

        public ActionEditor(PurchasePanel purchasePanel) {
            this.purchasePanel = purchasePanel;
            setOpaque(true);
            
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
                if (currentPurchaseData != null) {
                    JOptionPane.showMessageDialog(purchasePanel, "Editar Factura de Compra: " + currentPurchaseData.getString("factura"));
                }
            });

            deleteBtn.addActionListener(e -> {
                fireEditingStopped();
                if (currentPurchaseData != null) {
                    Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(purchasePanel);
                    boolean confirm = ConfirmDialog.showConfirmDialog(
                        parentFrame,
                        "¿Desea eliminar la factura de compra \"" + currentPurchaseData.getString("factura") + "\"?",
                        "Confirmar Eliminación");
                    if (confirm) {
                        allPurchases.remove(currentPurchaseData);
                        filteredPurchases.remove(currentPurchaseData);
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
            currentPurchaseData = (dataRowIndex >= 0 && dataRowIndex < filteredPurchases.size()) ? filteredPurchases.get(dataRowIndex) : null;
            return panel;
        }

        @Override
        public Object getCellEditorValue() { return null; }
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
        public void mouseEntered(MouseEvent e) { button.setBackground(hoverBackground); }
        @Override
        public void mouseExited(MouseEvent e) { button.setBackground(defaultBackground); }
    }
}