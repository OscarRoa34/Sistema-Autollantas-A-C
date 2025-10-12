package co.edu.uptc.view.panels.SubPanels;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import org.json.*;

import co.edu.uptc.view.GlobalView;
import co.edu.uptc.view.dialogs.ConfirmDialog;
import co.edu.uptc.view.dialogs.SuccessPopUp;
import co.edu.uptc.view.utils.PropertiesService;
import co.edu.uptc.view.utils.TextPrompt;
import co.edu.uptc.view.utils.ViewController;

public class TiresProductPanel extends JPanel {

    private List<JSONObject> allTires;
    private List<JSONObject> filteredTires;
    private JPanel gridPanel;
    private JPanel paginationPanel;
    private JPanel filtersPanel;
    private JTextField searchField;
    private JCheckBox chkWanli, chkDoubleStar, chkHankook;
    private JRadioButton rbMayorPrecio, rbMenorPrecio;
    private PropertiesService p;
    private List<JCheckBox> brandCheckboxes;
    private JSONObject selectedTire;
    private JButton editBtn, deleteBtn, addBtn;
    private int currentPage = 1;
    private final int ITEMS_PER_PAGE = 8;
    private final ViewController controller;

    public TiresProductPanel(ViewController controller) {
        this.controller = controller;
        p = new PropertiesService();

        setLayout(new BorderLayout());
        setBackground(GlobalView.GENERAL_BACKGROUND);

        JSONArray data = loadTiresFromJson("src/main/resources/JSON/tires.json");

        allTires = new ArrayList<>();
        for (int i = 0; i < data.length(); i++) {
            allTires.add(data.getJSONObject(i));
        }
        filteredTires = new ArrayList<>(allTires);

        filtersPanel = createFilterPanel();
        filtersPanel.setBackground(GlobalView.GENERAL_BACKGROUND);
        add(filtersPanel, BorderLayout.EAST);

        gridPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        gridPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        gridPanel.setBackground(GlobalView.GENERAL_BACKGROUND);
        add(gridPanel, BorderLayout.CENTER);

        paginationPanel = new JPanel();
        paginationPanel.setBackground(GlobalView.GENERAL_BACKGROUND);
        paginationPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(paginationPanel, BorderLayout.SOUTH);

        updateGrid();
        updatePagination();
    }

    private JSONArray loadTiresFromJson(String path) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(path)));
            return new JSONArray(content);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al leer el archivo JSON.", "Error", JOptionPane.ERROR_MESSAGE);
            return new JSONArray();
        }
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(220, 0));
        panel.setBackground(GlobalView.GENERAL_BACKGROUND);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new CompoundBorder(
                new MatteBorder(0, 1, 0, 0, Color.DARK_GRAY),
                new EmptyBorder(15, 15, 15, 15)));

        JLabel lblTitle = new JLabel("FILTROS");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblTitle.setForeground(Color.BLACK);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblBusqueda = new JLabel("Búsqueda");
        lblBusqueda.setFont(new Font("Segoe UI", Font.BOLD, 25));
        lblBusqueda.setForeground(Color.BLACK);
        lblBusqueda.setAlignmentX(Component.LEFT_ALIGNMENT);

        searchField = new JTextField();
        searchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        searchField.setBackground(Color.white);
        searchField.setForeground(Color.black);
        new TextPrompt("Nombre de la llanta", searchField);
        searchField.setBorder(new CompoundBorder(
                new LineBorder(Color.GRAY, 1, true),
                new EmptyBorder(5, 8, 5, 8)));
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                applyFilters();
            }
        });

        JLabel lblMarca = new JLabel("Marca");
        lblMarca.setFont(new Font("Segoe UI", Font.BOLD, 25));
        lblMarca.setForeground(Color.BLACK);
        lblMarca.setAlignmentX(Component.LEFT_ALIGNMENT);

        Map<String, Long> conteoMarcas = allTires.stream()
                .collect(Collectors.groupingBy(t -> t.getString("marca"), LinkedHashMap::new, Collectors.counting()));

        List<String> marcasList = conteoMarcas.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder()))
                .limit(8)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        List<JCheckBox> marcaCheckboxes = new ArrayList<>();

        for (String marca : marcasList) {
            JCheckBox chk = createStyledCheckBox(marca);
            chk.addActionListener(e -> applyFilters());
            marcaCheckboxes.add(chk);
        }

        JLabel lblPrecio = new JLabel("Precio");
        lblPrecio.setFont(new Font("Segoe UI", Font.BOLD, 25));
        lblPrecio.setForeground(Color.BLACK);
        lblPrecio.setAlignmentX(Component.LEFT_ALIGNMENT);

        rbMayorPrecio = createStyledRadio("Mayor precio");
        rbMenorPrecio = createStyledRadio("Menor precio");

        ButtonGroup precioGroup = new ButtonGroup();
        precioGroup.add(rbMayorPrecio);
        precioGroup.add(rbMenorPrecio);

        ActionListener precioListener = e -> applyFilters();
        rbMayorPrecio.addActionListener(precioListener);
        rbMenorPrecio.addActionListener(precioListener);

        panel.add(lblTitle);
        panel.add(Box.createVerticalStrut(15));
        panel.add(lblBusqueda);
        panel.add(searchField);
        panel.add(Box.createVerticalStrut(15));
        panel.add(lblMarca);
        for (JCheckBox chk : marcaCheckboxes)
            panel.add(chk);
        panel.add(Box.createVerticalStrut(15));
        panel.add(lblPrecio);
        panel.add(rbMayorPrecio);
        panel.add(rbMenorPrecio);
        panel.add(Box.createVerticalGlue());
        brandCheckboxes = marcaCheckboxes;

        return panel;
    }

    private JCheckBox createStyledCheckBox(String text) {
        JCheckBox chk = new JCheckBox(text);
        chk.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        chk.setForeground(Color.BLACK);
        chk.setBackground(GlobalView.GENERAL_BACKGROUND);
        chk.setFocusPainted(false);
        chk.setAlignmentX(Component.LEFT_ALIGNMENT);
        chk.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return chk;
    }

    private JRadioButton createStyledRadio(String text) {
        JRadioButton rb = new JRadioButton(text);
        rb.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        rb.setForeground(Color.BLACK);
        rb.setBackground(GlobalView.GENERAL_BACKGROUND);
        rb.setFocusPainted(false);
        rb.setAlignmentX(Component.LEFT_ALIGNMENT);
        rb.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return rb;
    }

    private void applyFilters() {
        String search = searchField.getText().trim().toLowerCase();

        List<String> marcas = brandCheckboxes.stream()
                .filter(AbstractButton::isSelected)
                .map(AbstractButton::getText)
                .collect(Collectors.toList());

        filteredTires = allTires.stream()
                .filter(t -> t.getString("nombre").toLowerCase().contains(search))
                .filter(t -> marcas.isEmpty() || marcas.contains(t.getString("marca")))
                .collect(Collectors.toList());

        if (rbMayorPrecio.isSelected()) {
            filteredTires.sort((a, b) -> Integer.compare(b.getInt("precio"), a.getInt("precio")));
        } else if (rbMenorPrecio.isSelected()) {
            filteredTires.sort(Comparator.comparingInt(a -> a.getInt("precio")));
        }

        currentPage = 1;
        updateGrid();
        updatePagination();
    }

    private void updateGrid() {
        gridPanel.removeAll();

        int start = (currentPage - 1) * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, filteredTires.size());

        for (int i = start; i < end; i++) {
            JSONObject tire = filteredTires.get(i);
            JPanel card = createCard(tire);
            gridPanel.add(card);
        }

        for (int i = end; i < start + ITEMS_PER_PAGE; i++) {
            gridPanel.add(new JLabel());
        }

        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private JPanel createCard(JSONObject tire) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(GlobalView.CARDS_BACKGROUND);
        card.setBorder(new CompoundBorder(
                new LineBorder(Color.LIGHT_GRAY, 1, true),
                new EmptyBorder(10, 10, 10, 10)));

        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        try {
            ImageIcon icon = new ImageIcon(tire.getString("imagen"));
            Image scaled = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaled));
        } catch (Exception e) {
            imageLabel.setText("Sin imagen");
        }

        JLabel nameLabel = new JLabel(tire.getString("nombre"));
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel brandLabel = new JLabel("Marca: " + tire.getString("marca"));
        brandLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        brandLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel priceLabel = new JLabel("Precio por unidad: $ " + tire.getInt("precio"));
        priceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        priceLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel stockLabel = new JLabel("Stock: " + tire.getInt("stock") + " unidades");
        stockLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        stockLabel.setForeground(Color.GRAY);
        stockLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel infoPanel = new JPanel(new GridLayout(4, 1));
        infoPanel.setBackground(GlobalView.CARDS_BACKGROUND);
        infoPanel.add(nameLabel);
        infoPanel.add(brandLabel);
        infoPanel.add(priceLabel);
        infoPanel.add(stockLabel);

        card.add(imageLabel, BorderLayout.NORTH);
        card.add(infoPanel, BorderLayout.CENTER);

        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedTire = tire;
                editBtn.setEnabled(true);
                deleteBtn.setEnabled(true);
                updateGrid();
            }
        });

        if (selectedTire != null && selectedTire.equals(tire)) {
            card.setBorder(new CompoundBorder(
                    new LineBorder(Color.RED, 3, true),
                    new EmptyBorder(8, 8, 8, 8)));
        }

        return card;
    }

    private void updatePagination() {
        paginationPanel.removeAll();

        int total = (int) Math.ceil((double) filteredTires.size() / ITEMS_PER_PAGE);
        if (total == 0)
            total = 1;
        final int totalPages = total;

        JPanel container = new JPanel(new BorderLayout(120, 0));
        container.setBackground(GlobalView.GENERAL_BACKGROUND);
        container.setBorder(new EmptyBorder(10, 0, 10, 60));

        JPanel pagesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 5));
        pagesPanel.setBackground(GlobalView.GENERAL_BACKGROUND);

        JButton firstBtn = createStyledButton("<<");
        JButton prevBtn = createStyledButton("<");
        JButton nextBtn = createStyledButton(">");
        JButton lastBtn = createStyledButton(">>");

        for (JButton btn : new JButton[] { firstBtn, prevBtn, nextBtn, lastBtn }) {
            btn.setPreferredSize(new Dimension(40, 40));
            btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        }

        firstBtn.addActionListener(e -> {
            currentPage = 1;
            updateGrid();
            updatePagination();
        });
        prevBtn.addActionListener(e -> {
            if (currentPage > 1)
                currentPage--;
            updateGrid();
            updatePagination();
        });
        nextBtn.addActionListener(e -> {
            if (currentPage < totalPages)
                currentPage++;
            updateGrid();
            updatePagination();
        });
        lastBtn.addActionListener(e -> {
            currentPage = totalPages;
            updateGrid();
            updatePagination();
        });

        JLabel pagLabel = new JLabel("Página ");
        pagLabel.setFont(new Font("Segoe UI", Font.BOLD, 25));
        pagesPanel.add(pagLabel);
        pagesPanel.add(firstBtn);
        pagesPanel.add(prevBtn);

        int maxVisible = 4;
        int startPage = Math.max(1, currentPage - 1);
        int endPage = Math.min(totalPages, startPage + maxVisible - 1);
        if (endPage - startPage < maxVisible - 1)
            startPage = Math.max(1, endPage - maxVisible + 1);

        if (startPage > 1)
            pagesPanel.add(new JLabel("..."));

        for (int i = startPage; i <= endPage; i++) {
            JButton pageBtn = createStyledButton(String.valueOf(i));
            final int page = i;
            pageBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
            pageBtn.setPreferredSize(new Dimension(45, 45));
            pageBtn.setBackground(i == currentPage ? GlobalView.ASIDE_BACKGROUND
                    : GlobalView.ASIDE_BUTTONS_BACKGROUND_ACTIVE);
            pageBtn.addActionListener(e -> {
                currentPage = page;
                updateGrid();
                updatePagination();
            });
            pagesPanel.add(pageBtn);
        }

        if (endPage < totalPages)
            pagesPanel.add(new JLabel("..."));

        pagesPanel.add(nextBtn);
        pagesPanel.add(lastBtn);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 5));
        actionPanel.setBackground(GlobalView.GENERAL_BACKGROUND);

        editBtn = new JButton(new ImageIcon(p.getProperties("edit2")));
        deleteBtn = new JButton(new ImageIcon(p.getProperties("delete2")));
        addBtn = new JButton(new ImageIcon(p.getProperties("add")));

        for (JButton btn : new JButton[] { editBtn, deleteBtn, addBtn }) {
            btn.setBackground(GlobalView.ASIDE_BUTTONS_BACKGROUND_ACTIVE);
            btn.setFocusPainted(false);
            btn.setBorder(new LineBorder(Color.GRAY, 1, true));
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setPreferredSize(new Dimension(45, 45));
        }
        editBtn.setEnabled(selectedTire != null);
        deleteBtn.setEnabled(selectedTire != null);

        editBtn.addActionListener(e -> {
            if (selectedTire == null)
                return;

            EditTirePanel editPanel = new EditTirePanel(
                    selectedTire,
                    updatedTire -> {
                        selectedTire.put("nombre", updatedTire.getString("nombre"));
                        selectedTire.put("marca", updatedTire.getString("marca"));
                        selectedTire.put("precio", updatedTire.getInt("precio"));
                        selectedTire.put("stock", updatedTire.getInt("stock"));
                        selectedTire.put("imagen", updatedTire.getString("imagen"));
                        applyFilters();
                    },
                    this,
                    controller);

            JDialog editDialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Editar producto",
                    Dialog.ModalityType.APPLICATION_MODAL);
            editDialog.setUndecorated(true);
            editDialog.setContentPane(editPanel);
            Point location = this.getLocationOnScreen();
            Dimension size = this.getSize();
            editDialog.setBounds(location.x, location.y, size.width, size.height);
            editDialog.setVisible(true);
            new SuccessPopUp(null, "Éxito:", "El producto se actualizó exitosamente.");
        });

        deleteBtn.addActionListener(e -> {
            if (selectedTire == null)
                return;

            Frame frame = (Frame) SwingUtilities.getWindowAncestor(this);
            boolean confirmed = ConfirmDialog.showConfirmDialog(
                    frame,
                    "¿Deseas eliminar esta llanta?",
                    "Confirmar eliminación");

            if (confirmed) {
                allTires.remove(selectedTire);
                applyFilters();
                selectedTire = null;
                editBtn.setEnabled(false);
                deleteBtn.setEnabled(false);
                new SuccessPopUp(frame, "Éxito:", "El producto se eliminó exitosamente.");
            }
        });

        addBtn.addActionListener(e -> System.out.println("Agregar producto"));

        actionPanel.add(editBtn);
        actionPanel.add(deleteBtn);
        actionPanel.add(addBtn);

        container.add(pagesPanel, BorderLayout.WEST);
        container.add(actionPanel, BorderLayout.EAST);

        paginationPanel.add(container);
        paginationPanel.revalidate();
        paginationPanel.repaint();
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(GlobalView.ASIDE_BUTTONS_BACKGROUND_ACTIVE);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorder(new LineBorder(Color.DARK_GRAY, 1, true));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(6, 12, 6, 12));
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(GlobalView.ASIDE_BACKGROUND);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!(btn.getText().equals(String.valueOf(currentPage))))
                    btn.setBackground(GlobalView.ASIDE_BUTTONS_BACKGROUND_ACTIVE);
            }
        });

        return btn;
    }

}
