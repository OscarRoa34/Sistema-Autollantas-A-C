package co.edu.uptc.view.panels;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import org.json.*;

public class ServicesPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private java.util.List<JSONObject> allData;
    private java.util.List<JSONObject> filteredData;
    private int currentPage = 1;
    private int rowsPerPage = 10;
    private JLabel pageLabel;
    private JButton prevBtn, nextBtn;
    private JTextField searchField;

    public ServicesPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));

        JLabel title = new JLabel("Gestión de Servicios", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 20));
        title.setBorder(new EmptyBorder(10, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(240, 240, 240));
        centerPanel.setBorder(new EmptyBorder(10, 30, 10, 30));
        add(centerPanel, BorderLayout.CENTER);

        // ----- Buscador -----
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setBackground(new Color(240, 240, 240));

        searchField = new JTextField(" Buscar Servicio por nombre");
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setForeground(Color.GRAY);
        searchField.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));

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

        JButton searchButton = new JButton("\uD83D\uDD0D");
        searchButton.setBackground(Color.WHITE);
        searchButton.setFocusable(false);
        searchButton.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        searchButton.addActionListener(e -> filterData(searchField.getText().trim().toLowerCase()));

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        centerPanel.add(searchPanel, BorderLayout.NORTH);

        // ----- Tabla -----
        String[] columns = {"Código", "Nombre Servicio", "Productos Asociados", "Precio servicio", "Total"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
        table.getTableHeader().setBackground(new Color(245, 245, 245));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(0, 350)); // 🔽 controla la altura visible
        scrollPane.setBorder(new LineBorder(new Color(200, 200, 200)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // ----- Panel inferior -----
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(240, 240, 240));
        bottomPanel.setBorder(new EmptyBorder(15, 30, 15, 30)); // 🔼 más alto
        centerPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Paginación
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        paginationPanel.setBackground(new Color(240, 240, 240));

        prevBtn = new JButton("<<");
        nextBtn = new JButton(">>");
        pageLabel = new JLabel();

        JButton[] navButtons = {prevBtn, nextBtn};
        for (JButton b : navButtons) {
            b.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            b.setFocusable(false);
            b.setBackground(Color.WHITE);
            b.setBorder(new LineBorder(new Color(180, 180, 180)));
            b.setPreferredSize(new Dimension(50, 35)); // 🔼 botones más grandes
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

        // Botón “Nuevo Servicio”
        JButton newServiceBtn = new JButton("+  Nuevo Servicio");
        newServiceBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        newServiceBtn.setBackground(new Color(30, 30, 30));
        newServiceBtn.setForeground(Color.WHITE);
        newServiceBtn.setFocusPainted(false);
        newServiceBtn.setBorder(new EmptyBorder(10, 20, 10, 20)); // 🔼 más alto y ancho
        newServiceBtn.setPreferredSize(new Dimension(220, 45));
        bottomPanel.add(newServiceBtn, BorderLayout.EAST);

        // ----- Cargar JSON -----
        loadJSONData();
    }


    public void loadJSONData() {
        String jsonText = """
                      [
                        {"codigo":"SVC-001","nombre":"Cambio de aceite (Aceite 10W40)","productos":"Aceite 10W40 Mobil 3.78 L, Filtro aceite DGP","precio":"$120,000","total":"$160,000"},
                        {"codigo":"SVC-002","nombre":"Balanceo","productos":"","precio":"$80,000","total":"$80,000"},
                        {"codigo":"SVC-003","nombre":"Cambio de aceite (Aceite 5W30)","productos":"Aceite 5W30, Filtro Aceite DGP","precio":"$120,000","total":"$160,000"},
                        {"codigo":"SVC-004","nombre":"Alineación","productos":"","precio":"$100,000","total":"$100,000"},
                        {"codigo":"SVC-005","nombre":"Rectificación de discos","productos":"","precio":"$150,000","total":"$150,000"},
                        {"codigo":"SVC-006","nombre":"Lavado general","productos":"","precio":"$60,000","total":"$60,000"},
                        {"codigo":"SVC-007","nombre":"Cambio de frenos","productos":"","precio":"$90,000","total":"$90,000"},
                {"codigo":"SVC-008","nombre":"Cambio de llantas","productos":"Juego de llantas Michelin 185/65R14","precio":"$400,000","total":"$400,000"},
                {"codigo":"SVC-009","nombre":"Rotación de llantas","productos":"","precio":"$70,000","total":"$70,000"},
                {"codigo":"SVC-010","nombre":"Cambio de bujías","productos":"Juego de bujías NGK","precio":"$90,000","total":"$90,000"},
                {"codigo":"SVC-011","nombre":"Cambio de batería","productos":"Batería MAC Silver 12V","precio":"$250,000","total":"$250,000"},
                {"codigo":"SVC-012","nombre":"Cambio de pastillas de freno","productos":"Juego de pastillas TRW","precio":"$130,000","total":"$130,000"},
                {"codigo":"SVC-013","nombre":"Cambio de líquido de frenos","productos":"Líquido DOT4","precio":"$60,000","total":"$60,000"},
                {"codigo":"SVC-014","nombre":"Revisión de suspensión","productos":"","precio":"$100,000","total":"$100,000"},
                {"codigo":"SVC-015","nombre":"Revisión de luces","productos":"Bombillos H4 Philips","precio":"$50,000","total":"$50,000"},
                {"codigo":"SVC-016","nombre":"Cambio de filtro de aire","productos":"Filtro aire Bosch","precio":"$70,000","total":"$70,000"},
                {"codigo":"SVC-017","nombre":"Cambio de filtro de combustible","productos":"Filtro gasolina Fram","precio":"$80,000","total":"$80,000"},
                {"codigo":"SVC-018","nombre":"Sincronización de motor","productos":"Scanner y calibración electrónica","precio":"$220,000","total":"$220,000"},
                {"codigo":"SVC-019","nombre":"Revisión general de frenos","productos":"","precio":"$120,000","total":"$120,000"},
                {"codigo":"SVC-020","nombre":"Cambio de líquido refrigerante","productos":"Refrigerante Prestone 1 Galón","precio":"$90,000","total":"$90,000"},
                {"codigo":"SVC-021","nombre":"Cambio de correa de distribución","productos":"Kit correa Gates","precio":"$350,000","total":"$350,000"},
                {"codigo":"SVC-022","nombre":"Revisión técnico-mecánica preventiva","productos":"","precio":"$180,000","total":"$180,000"}
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

    private void refreshTable() {
        model.setRowCount(0);
        int start = (currentPage - 1) * rowsPerPage;
        int end = Math.min(start + rowsPerPage, filteredData.size());

        for (int i = start; i < end; i++) {
            JSONObject obj = filteredData.get(i);
            model.addRow(new Object[]{
                    obj.getString("codigo"),
                    obj.getString("nombre"),
                    obj.getString("productos"),
                    obj.getString("precio"),
                    obj.getString("total")
            });
        }

        int maxPage = Math.max(1, (int) Math.ceil((double) filteredData.size() / rowsPerPage));
        pageLabel.setText(" " + currentPage + " / " + maxPage + " ");
        prevBtn.setEnabled(currentPage > 1);
        nextBtn.setEnabled(currentPage < maxPage);
    }
}