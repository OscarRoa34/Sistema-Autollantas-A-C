package co.edu.uptc.view.panels.SubPanels;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
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

public class BrakePadsProductPanel extends JPanel {

    private List<JSONObject> allProducts;
    private List<JSONObject> filteredProducts;
    private JPanel gridPanel;
    private JPanel paginationPanel;
    private JPanel filtersPanel;
    private JTextField searchField;
    private JRadioButton rbMayorPrecio, rbMenorPrecio;
    private PropertiesService p;
    private List<JCheckBox> brandCheckboxes;
    private JSONObject selectedProduct;
    private JButton editBtn, deleteBtn, addBtn;
    private int currentPage = 1;
    private final int ITEMS_PER_PAGE = 8;
    private final ViewController controller;
    private static final String JSON_FILE_PATH = "src/main/resources/JSON/brake_pads.json";

    // --- Iconos personalizados ---
    private static final Icon CHECKBOX_DEFAULT_ICON = createCheckboxIcon(false, false);
    private static final Icon CHECKBOX_SELECTED_ICON = createCheckboxIcon(true, false);
    private static final Icon CHECKBOX_HOVER_ICON = createCheckboxIcon(false, true);
    private static final Icon CHECKBOX_SELECTED_HOVER_ICON = createCheckboxIcon(true, true);

    private static final Icon RADIO_DEFAULT_ICON = createRadioButtonIcon(false, false);
    private static final Icon RADIO_SELECTED_ICON = createRadioButtonIcon(true, false);
    private static final Icon RADIO_HOVER_ICON = createRadioButtonIcon(false, true);
    private static final Icon RADIO_SELECTED_HOVER_ICON = createRadioButtonIcon(true, true);

    public BrakePadsProductPanel(ViewController controller) {
        this.controller = controller;
        this.p = new PropertiesService();

        setLayout(new BorderLayout());
        setBackground(GlobalView.GENERAL_BACKGROUND);

        JSONArray data = loadProductsFromJson(JSON_FILE_PATH);

        allProducts = new ArrayList<>();
        for (int i = 0; i < data.length(); i++) {
            allProducts.add(data.getJSONObject(i));
        }
        filteredProducts = new ArrayList<>(allProducts);

        filtersPanel = createFilterPanel();
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

    private JSONArray loadProductsFromJson(String path) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(path)));
            return new JSONArray(content);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al leer el archivo JSON: " + path, "Error", JOptionPane.ERROR_MESSAGE);
            return new JSONArray();
        }
    }

    private void saveProductsToJson(String path) {
        try {
            JSONArray array = new JSONArray(allProducts);
            Files.write(Paths.get(path), array.toString(4).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al guardar el archivo JSON.", "Error", JOptionPane.ERROR_MESSAGE);
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
        new TextPrompt("Nombre de la pastilla", searchField);
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

        Map<String, Long> brandCount = allProducts.stream()
                .collect(Collectors.groupingBy(p -> p.getString("marca"), LinkedHashMap::new, Collectors.counting()));

        List<String> brandList = brandCount.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder()))
                .limit(8)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        brandCheckboxes = new ArrayList<>();
        for (String brand : brandList) {
            JCheckBox chk = createStyledCheckBox(brand);
            chk.addActionListener(e -> applyFilters());
            brandCheckboxes.add(chk);
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
        for (JCheckBox chk : brandCheckboxes)
            panel.add(chk);
        panel.add(Box.createVerticalStrut(15));
        panel.add(lblPrecio);
        panel.add(rbMayorPrecio);
        panel.add(rbMenorPrecio);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private void applyFilters() {
        String search = searchField.getText().trim().toLowerCase();
        List<String> selectedBrands = brandCheckboxes.stream()
                .filter(AbstractButton::isSelected)
                .map(AbstractButton::getText)
                .collect(Collectors.toList());

        filteredProducts = allProducts.stream()
                .filter(p -> p.getString("nombre").toLowerCase().contains(search))
                .filter(p -> selectedBrands.isEmpty() || selectedBrands.contains(p.getString("marca")))
                .collect(Collectors.toList());

        if (rbMayorPrecio.isSelected()) {
            filteredProducts.sort((a, b) -> Integer.compare(b.getInt("precio"), a.getInt("precio")));
        } else if (rbMenorPrecio.isSelected()) {
            filteredProducts.sort(Comparator.comparingInt(a -> a.getInt("precio")));
        }

        int totalPages = (int) Math.ceil((double) filteredProducts.size() / ITEMS_PER_PAGE);
        if (currentPage > totalPages && totalPages > 0) {
            currentPage = totalPages;
        } else if (totalPages == 0) {
            currentPage = 1;
        }

        updateGrid();
        updatePagination();
    }

    private void updateGrid() {
        gridPanel.removeAll();
        int start = (currentPage - 1) * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, filteredProducts.size());

        for (int i = start; i < end; i++) {
            gridPanel.add(createCard(filteredProducts.get(i)));
        }

        for (int i = end; i < start + ITEMS_PER_PAGE; i++) {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setBackground(GlobalView.GENERAL_BACKGROUND);
            gridPanel.add(emptyPanel);
        }

        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private JPanel createCard(JSONObject product) {
        JPanel card = new JPanel(new BorderLayout(0, 5));
        card.setBackground(GlobalView.CARDS_BACKGROUND);
        card.setBorder(new CompoundBorder(
                new LineBorder(Color.LIGHT_GRAY, 1, true),
                new EmptyBorder(10, 10, 10, 10)));

        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        try {
            ImageIcon icon = new ImageIcon(product.getString("imagen"));
            Image scaled = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaled));
        } catch (Exception e) {
            imageLabel.setText("Sin imagen");
        }

        JPanel infoPanel = new JPanel(new GridLayout(4, 1));
        infoPanel.setBackground(GlobalView.CARDS_BACKGROUND);
        
        JLabel nameLabel = new JLabel(product.getString("nombre"), SwingConstants.CENTER);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        infoPanel.add(nameLabel);
        
        JLabel brandLabel = new JLabel("Marca: " + product.getString("marca"), SwingConstants.CENTER);
        brandLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoPanel.add(brandLabel);
        
        JLabel priceLabel = new JLabel("Precio: $" + product.getInt("precio"), SwingConstants.CENTER);
        priceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoPanel.add(priceLabel);
        
        JLabel stockLabel = new JLabel("Stock: " + product.getInt("stock") + " uds", SwingConstants.CENTER);
        stockLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        stockLabel.setForeground(Color.GRAY);
        infoPanel.add(stockLabel);

        card.add(imageLabel, BorderLayout.NORTH);
        card.add(infoPanel, BorderLayout.CENTER);

        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedProduct = product;
                editBtn.setEnabled(true);
                deleteBtn.setEnabled(true);
                updateGrid();
            }
        });

        if (product.equals(selectedProduct)) {
            card.setBorder(new CompoundBorder(
                    new LineBorder(Color.RED, 3, true),
                    new EmptyBorder(8, 8, 8, 8)));
        }

        return card;
    }

    private void updatePagination() {
        paginationPanel.removeAll();
        int total = (int) Math.ceil((double) filteredProducts.size() / ITEMS_PER_PAGE);
        if (total == 0) total = 1;
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
            if (currentPage > 1) currentPage--;
            updateGrid();
            updatePagination();
        });
        nextBtn.addActionListener(e -> {
            if (currentPage < totalPages) currentPage++;
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

        if (startPage > 1) pagesPanel.add(new JLabel("..."));

        for (int i = startPage; i <= endPage; i++) {
            JButton pageBtn = createStyledButton(String.valueOf(i));
            final int page = i;
            pageBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
            pageBtn.setPreferredSize(new Dimension(45, 45));
            pageBtn.setBackground(i == currentPage ? GlobalView.ASIDE_BACKGROUND : GlobalView.ASIDE_BUTTONS_ACTIVE_BACKGROUND);
            pageBtn.addActionListener(e -> {
                currentPage = page;
                updateGrid();
                updatePagination();
            });
            pagesPanel.add(pageBtn);
        }

        if (endPage < totalPages) pagesPanel.add(new JLabel("..."));

        pagesPanel.add(nextBtn);
        pagesPanel.add(lastBtn);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 5));
        actionPanel.setBackground(GlobalView.GENERAL_BACKGROUND);

        editBtn = new JButton(new ImageIcon(p.getProperties("edit2")));
        deleteBtn = new JButton(new ImageIcon(p.getProperties("delete2")));
        addBtn = new JButton(new ImageIcon(p.getProperties("add")));

        for (JButton btn : new JButton[] { editBtn, deleteBtn, addBtn }) {
            btn.setBackground(GlobalView.ASIDE_BUTTONS_ACTIVE_BACKGROUND);
            btn.setFocusPainted(false);
            btn.setBorder(new LineBorder(Color.GRAY, 1, true));
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setPreferredSize(new Dimension(45, 45));
        }
        editBtn.setEnabled(selectedProduct != null);
        deleteBtn.setEnabled(selectedProduct != null);

        editBtn.addActionListener(e -> {
            if (selectedProduct == null) return;
            EditCreateProductPanel editPanel = new EditCreateProductPanel("Editar Producto", selectedProduct,
                updatedProduct -> {
                    selectedProduct.put("nombre", updatedProduct.getString("nombre"));
                    selectedProduct.put("marca", updatedProduct.getString("marca"));
                    selectedProduct.put("precio", updatedProduct.getInt("precio"));
                    selectedProduct.put("stock", updatedProduct.getInt("stock"));
                    selectedProduct.put("imagen", updatedProduct.getString("imagen"));
                    applyFilters();
                    saveProductsToJson(JSON_FILE_PATH);
                }, this, controller);
            showDialog(editPanel, "Editar Producto");
        });

        deleteBtn.addActionListener(e -> {
            if (selectedProduct == null) return;
            Frame frame = (Frame) SwingUtilities.getWindowAncestor(this);
            boolean confirmed = ConfirmDialog.showConfirmDialog(frame, "¿Deseas eliminar este producto?", "Confirmar eliminación");
            if (confirmed) {
                allProducts.remove(selectedProduct);
                applyFilters();
                saveProductsToJson(JSON_FILE_PATH);
                selectedProduct = null;
                editBtn.setEnabled(false);
                deleteBtn.setEnabled(false);
                new SuccessPopUp(frame, "Éxito:", "El producto se eliminó exitosamente.").setVisible(true);
            }
        });

        addBtn.addActionListener(e -> {
            JSONObject newProduct = new JSONObject();
            newProduct.put("nombre", "").put("marca", "").put("precio", 0).put("stock", 0).put("imagen", "");
            EditCreateProductPanel addPanel = new EditCreateProductPanel("Crear Producto", newProduct,
                addedProduct -> {
                    allProducts.add(addedProduct);
                    applyFilters();
                    saveProductsToJson(JSON_FILE_PATH);
                    Frame frame = (Frame) SwingUtilities.getWindowAncestor(this);
                    new SuccessPopUp(frame, "Éxito:", "El producto se añadió exitosamente.").setVisible(true);
                }, this, controller);
            showDialog(addPanel, "Crear Producto");
        });

        actionPanel.add(editBtn);
        actionPanel.add(deleteBtn);
        actionPanel.add(addBtn);

        container.add(pagesPanel, BorderLayout.WEST);
        container.add(actionPanel, BorderLayout.EAST);
        paginationPanel.add(container);
        paginationPanel.revalidate();
        paginationPanel.repaint();
    }
    
    private void showDialog(JPanel panel, String title) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), title, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setUndecorated(true);
        dialog.setContentPane(panel);
        dialog.pack(); // Ajusta el tamaño al contenido
        dialog.setLocationRelativeTo(this); // Centra el diálogo
        dialog.setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(GlobalView.ASIDE_BUTTONS_ACTIVE_BACKGROUND);
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
                    btn.setBackground(GlobalView.ASIDE_BUTTONS_ACTIVE_BACKGROUND);
            }
        });
        return btn;
    }

    private JCheckBox createStyledCheckBox(String text) {
        JCheckBox chk = new JCheckBox(text);
        chk.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        chk.setForeground(Color.BLACK);
        chk.setBackground(GlobalView.GENERAL_BACKGROUND);
        chk.setFocusPainted(false);
        chk.setCursor(new Cursor(Cursor.HAND_CURSOR));
        chk.setAlignmentX(Component.LEFT_ALIGNMENT);
        chk.setBorder(new EmptyBorder(4, 0, 4, 0));
        chk.setIconTextGap(10);
        chk.setIcon(CHECKBOX_DEFAULT_ICON);
        chk.setSelectedIcon(CHECKBOX_SELECTED_ICON);
        chk.setRolloverIcon(CHECKBOX_HOVER_ICON);
        chk.setRolloverSelectedIcon(CHECKBOX_SELECTED_HOVER_ICON);
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
        rb.setBorder(new EmptyBorder(4, 0, 4, 0));
        rb.setIconTextGap(10);
        rb.setIcon(RADIO_DEFAULT_ICON);
        rb.setSelectedIcon(RADIO_SELECTED_ICON);
        rb.setRolloverIcon(RADIO_HOVER_ICON);
        rb.setRolloverSelectedIcon(RADIO_SELECTED_HOVER_ICON);
        return rb;
    }

    private static Icon createCheckboxIcon(boolean isSelected, boolean isHover) {
        int width = 18, height = 18;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (isSelected) {
            g2.setColor(isHover ? GlobalView.ASIDE_BACKGROUND.darker() : GlobalView.ASIDE_BACKGROUND);
            g2.fill(new RoundRectangle2D.Float(0, 0, width, height, 5, 5));
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(5, 9, 8, 12);
            g2.drawLine(8, 12, 14, 6);
        } else {
            g2.setColor(isHover ? Color.BLACK : Color.GRAY);
            g2.setStroke(new BasicStroke(1.8f));
            g2.draw(new RoundRectangle2D.Float(1, 1, width - 2, height - 2, 5, 5));
        }
        g2.dispose();
        return new ImageIcon(image);
    }

    private static Icon createRadioButtonIcon(boolean isSelected, boolean isHover) {
        int size = 18;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(isHover ? Color.BLACK : Color.GRAY);
        g2.setStroke(new BasicStroke(1.8f));
        g2.drawOval(1, 1, size - 3, size - 3);
        if (isSelected) {
            g2.setColor(isHover ? GlobalView.ASIDE_BACKGROUND.darker() : GlobalView.ASIDE_BACKGROUND);
            g2.fillOval(5, 5, size - 10, size - 10);
        }
        g2.dispose();
        return new ImageIcon(image);
    }
}