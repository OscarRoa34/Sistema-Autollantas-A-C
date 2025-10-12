package co.edu.uptc.view.panels;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import org.json.*;

import co.edu.uptc.view.GlobalView;
import co.edu.uptc.view.dialogs.ConfirmDialog;
import co.edu.uptc.view.dialogs.SuccessPopUp;
import co.edu.uptc.view.utils.PropertiesService;

public class ServicesPanel extends JPanel {

    private PropertiesService p;
    private JTable table;
    private DefaultTableModel model;
    private java.util.List<JSONObject> allData;
    private java.util.List<JSONObject> filteredData;
    private int currentPage = 1;
    private int rowsPerPage = 9;
    private JLabel pageLabel;
    private JButton prevBtn, nextBtn;
    private JTextField searchField;

    public ServicesPanel() {
        p = new PropertiesService();
        setLayout(new BorderLayout());
        setBackground(GlobalView.GENERAL_BACKGROUND);

        JLabel title = new JLabel("Gestión de Servicios", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 28));
        title.setBorder(new EmptyBorder(15, 0, 15, 0));
        add(title, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(240, 240, 240));
        centerPanel.setBorder(new EmptyBorder(10, 30, 41, 30));
        add(centerPanel, BorderLayout.CENTER);

        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchField = new JTextField(" Buscar Servicio por nombre");
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        searchField.setForeground(Color.GRAY);
        searchField.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        searchField.setPreferredSize(new Dimension(0, 40)); 

        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().trim().equals("Buscar Servicio por nombre") ||
                        searchField.getText().trim().equals(" Buscar Servicio por nombre")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText(" Buscar Servicio por nombre");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });

        ImageIcon searchIcon = createIcon(p.getProperties("search"), 25, 25);
        JButton searchButton = new JButton(searchIcon);
        searchButton.setBackground(Color.WHITE);
        searchButton.setFocusable(false);
        searchButton.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        searchButton.setPreferredSize(new Dimension(50, 40));
        searchButton.addActionListener(e -> filterData(searchField.getText().trim().toLowerCase()));

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
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
        table.setRowHeight(43);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
        table.getTableHeader().setBackground(new Color(245, 245, 245));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount() - 1; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        table.getColumnModel().getColumn(5).setCellRenderer(new ActionRenderer());
        table.getColumnModel().getColumn(5).setCellEditor(new ActionEditor());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(0, 350));
        scrollPane.setBorder(new LineBorder(new Color(200, 200, 200)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(240, 240, 240));
        bottomPanel.setBorder(new EmptyBorder(15, 30, 15, 30));
        centerPanel.add(bottomPanel, BorderLayout.SOUTH);

        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        paginationPanel.setBackground(new Color(240, 240, 240));

        prevBtn = new JButton("<<");
        nextBtn = new JButton(">>");
        pageLabel = new JLabel();

        for (JButton b : new JButton[] { prevBtn, nextBtn }) {
            b.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            b.setFocusable(false);
            b.setBackground(Color.WHITE);
            b.setBorder(new LineBorder(new Color(180, 180, 180)));
            b.setPreferredSize(new Dimension(50, 35));
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

        paginationPanel.add(new JLabel("Página"));
        paginationPanel.add(prevBtn);
        paginationPanel.add(pageLabel);
        paginationPanel.add(nextBtn);
        bottomPanel.add(paginationPanel, BorderLayout.WEST);

        JButton newServiceBtn = new JButton("+  Nuevo Servicio");
        newServiceBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        newServiceBtn.setBackground(new Color(30, 30, 30));
        newServiceBtn.setForeground(Color.WHITE);
        newServiceBtn.setFocusPainted(false);
        newServiceBtn.setBorder(new EmptyBorder(10, 20, 10, 20));
        newServiceBtn.setPreferredSize(new Dimension(220, 45));
        bottomPanel.add(newServiceBtn, BorderLayout.EAST);

        loadJSONData();
    }

    private void filterData(String query) {
        if (query.isEmpty() || query.equals("Buscar Servicio por nombre")) {
            filteredData = new ArrayList<>(allData);
        } else {
            filteredData = new ArrayList<>();
            for (JSONObject obj : allData) {
                if (obj.getString("nombre").toLowerCase().contains(query)) {
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
                {"codigo":"SVC-003","nombre":"Cambio de aceite (Aceite 5W30)","productos":"Aceite 5W30, Filtro Aceite DGP","precio":"$120,000","total":"$160,000"},
                {"codigo":"SVC-003","nombre":"Cambio de aceite (Aceite 5W30)","productos":"Aceite 5W30, Filtro Aceite DGP","precio":"$120,000","total":"$160,000"},
                {"codigo":"SVC-003","nombre":"Cambio de aceite (Aceite 5W30)","productos":"Aceite 5W30, Filtro Aceite DGP","precio":"$120,000","total":"$160,000"},
                {"codigo":"SVC-003","nombre":"Cambio de aceite (Aceite 5W30)","productos":"Aceite 5W30, Filtro Aceite DGP","precio":"$120,000","total":"$160,000"},
                {"codigo":"SVC-003","nombre":"Cambio de aceite (Aceite 5W30)","productos":"Aceite 5W30, Filtro Aceite DGP","precio":"$120,000","total":"$160,000"},
                {"codigo":"SVC-003","nombre":"Cambio de aceite (Aceite 5W30)","productos":"Aceite 5W30, Filtro Aceite DGP","precio":"$120,000","total":"$160,000"},
                {"codigo":"SVC-003","nombre":"Cambio de aceite (Aceite 5W30)","productos":"Aceite 5W30, Filtro Aceite DGP","precio":"$120,000","total":"$160,000"},
                {"codigo":"SVC-003","nombre":"Cambio de aceite (Aceite 5W30)","productos":"Aceite 5W30, Filtro Aceite DGP","precio":"$120,000","total":"$160,000"},
                {"codigo":"SVC-003","nombre":"Cambio de aceite (Aceite 5W30)","productos":"Aceite 5W30, Filtro Aceite DGP","precio":"$120,000","total":"$160,000"},
                {"codigo":"SVC-003","nombre":"Cambio de aceite (Aceite 5W30)","productos":"Aceite 5W30, Filtro Aceite DGP","precio":"$120,000","total":"$160,000"}
                        
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
                b.setBackground(GlobalView.ASIDE_BUTTONS_BACKGROUND_ACTIVE);
                b.setOpaque(true);
                b.setPreferredSize(new Dimension(35, 35));
                b.setAlignmentY(Component.CENTER_ALIGNMENT);
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

        public ActionEditor() {
            panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
            panel.setOpaque(true);

            editBtn = new JButton(createIcon(p.getProperties("edit"), 18, 18));
            deleteBtn = new JButton(createIcon(p.getProperties("delete"), 18, 18));

            for (JButton b : new JButton[] { editBtn, deleteBtn }) {
                b.setFocusPainted(false);
                b.setBorderPainted(false);
                b.setContentAreaFilled(true);
                b.setBackground(GlobalView.ASIDE_BUTTONS_BACKGROUND_ACTIVE);
                b.setOpaque(true);
                b.setPreferredSize(new Dimension(35, 35));
                b.setAlignmentY(Component.CENTER_ALIGNMENT);
            }

            panel.add(editBtn);
            panel.add(deleteBtn);

            editBtn.addActionListener(e -> {
                int row = table.getEditingRow();
                JSONObject obj = filteredData.get((currentPage - 1) * rowsPerPage + row);
                JOptionPane.showMessageDialog(ServicesPanel.this, "Editar: " + obj.getString("nombre"));
                fireEditingStopped();
            });

            deleteBtn.addActionListener(e -> {
                int row = table.getEditingRow();
                JSONObject obj = filteredData.get((currentPage - 1) * rowsPerPage + row);

                Window window = SwingUtilities.getWindowAncestor(ServicesPanel.this);
                Frame parentFrame = window instanceof Frame ? (Frame) window : null;

                boolean confirm = ConfirmDialog.showConfirmDialog(
                        parentFrame,
                        "¿Desea eliminar el servicio \"" + obj.getString("nombre") + "\"?",
                        "Confirmar eliminación");

                if (confirm) {
                    allData.remove(obj);
                    filteredData.remove(obj);
                    refreshTable();

                    SuccessPopUp success = new SuccessPopUp(parentFrame, "Exito:",
                            "El servicio se eliminó correctamente");
                    success.setVisible(true);
                }

                fireEditingStopped();
            });

        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            panel.setBackground(table.getSelectionBackground());
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return null;
        }
    }
}