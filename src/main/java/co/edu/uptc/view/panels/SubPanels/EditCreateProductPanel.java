package co.edu.uptc.view.panels.SubPanels;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.function.Consumer;
import org.json.JSONObject;

import co.edu.uptc.view.GlobalView;
import co.edu.uptc.view.dialogs.ConfirmDialog;
import co.edu.uptc.view.dialogs.SuccessPopUp;
import co.edu.uptc.view.utils.ViewController;

public class EditCreateProductPanel extends JPanel {

    private JTextField nombreField, marcaField, precioField, stockField, imagenField;
    private JLabel imagenPreview;
    private Consumer<JSONObject> onSaveCallback;
    private JPanel parentPanel;
    private ViewController controller;
    private boolean isCreateMode;

    public EditCreateProductPanel(String titleText, JSONObject tire, Consumer<JSONObject> onSaveCallback,
            JPanel parentPanel, ViewController controller) {
        this.onSaveCallback = onSaveCallback;
        this.parentPanel = parentPanel;
        this.controller = controller;
        this.isCreateMode = tire.optString("nombre").isEmpty();

        initComponents(titleText, tire);
    }

    private void initComponents(String titleText, JSONObject tire) {
        setLayout(new BorderLayout(20, 20));
        setBackground(GlobalView.GENERAL_BACKGROUND);
        setBorder(new EmptyBorder(30, 50, 30, 50));

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(Color.BLACK);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(new EmptyBorder(0, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout(30, 0));
        mainPanel.setBackground(GlobalView.GENERAL_BACKGROUND);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(GlobalView.GENERAL_BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        formPanel.add(createLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.9;
        nombreField = createStyledField(tire.optString("nombre", ""));
        formPanel.add(nombreField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.1;
        formPanel.add(createLabel("Marca:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.9;
        marcaField = createStyledField(tire.optString("marca", ""));
        formPanel.add(marcaField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.1;
        formPanel.add(createLabel("Precio:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.9;
        precioField = createStyledField(String.valueOf(tire.optInt("precio", 0)));
        formPanel.add(precioField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.1;
        formPanel.add(createLabel("Stock:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.9;
        stockField = createStyledField(String.valueOf(tire.optInt("stock", 0)));
        formPanel.add(stockField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.1;
        formPanel.add(createLabel("Ruta de imagen:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.9;
        formPanel.add(createImageSelectionPanel(tire.optString("imagen", "")), gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel imagePreviewPanel = new JPanel(new BorderLayout());
        imagePreviewPanel.setBackground(GlobalView.GENERAL_BACKGROUND);
        imagePreviewPanel.setBorder(new TitledBorder(new LineBorder(Color.LIGHT_GRAY, 1, true), " Previsualización ",
                TitledBorder.CENTER, TitledBorder.TOP, new Font("Segoe UI", Font.PLAIN, 14), Color.DARK_GRAY));
        imagenPreview = new JLabel("Sin imagen", SwingConstants.CENTER);
        imagenPreview.setPreferredSize(new Dimension(200, 200));
        updateImagePreview(tire.optString("imagen", ""));
        imagePreviewPanel.add(imagenPreview, BorderLayout.CENTER);
        mainPanel.add(imagePreviewPanel, BorderLayout.EAST);

        add(mainPanel, BorderLayout.CENTER);

        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lbl.setForeground(Color.BLACK);
        return lbl;
    }

    private JTextField createStyledField(String text) {
        JTextField field = new JTextField(text);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setBackground(new Color(245, 245, 245));
        field.setBorder(new CompoundBorder(
                new LineBorder(new Color(180, 180, 180), 1, true),
                new EmptyBorder(8, 12, 8, 12)));

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(new CompoundBorder(
                        new LineBorder(GlobalView.ASIDE_BACKGROUND, 2, true),
                        new EmptyBorder(7, 11, 7, 11))); // Ajustar borde para mantener tamaño
            }

            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(new CompoundBorder(
                        new LineBorder(new Color(180, 180, 180), 1, true),
                        new EmptyBorder(8, 12, 8, 12)));
            }
        });
        return field;
    }

    private JPanel createImageSelectionPanel(String initialPath) {
        imagenField = createStyledField(initialPath);
        JButton browseBtn = new JButton("Seleccionar...");
        browseBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        browseBtn.setFocusPainted(false);
        browseBtn.setBackground(GlobalView.ASIDE_BACKGROUND);
        browseBtn.setForeground(Color.WHITE);
        browseBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        browseBtn.addActionListener(e -> chooseImage());

        JPanel imgPanel = new JPanel(new BorderLayout(5, 0));
        imgPanel.setBackground(GlobalView.GENERAL_BACKGROUND);
        imgPanel.add(imagenField, BorderLayout.CENTER);
        imgPanel.add(browseBtn, BorderLayout.EAST);
        return imgPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 20));
        buttonPanel.setBackground(GlobalView.GENERAL_BACKGROUND);

        JButton cancelBtn = createStyledButton("Cancelar", GlobalView.CANCEL_BUTTON_BACKGROUND,
                GlobalView.CANCEL_BUTTON_BACKGROUND.darker());
        JButton saveBtn = createStyledButton("Aceptar", GlobalView.CONFIRM_BUTTON_BACKGROUND,
                GlobalView.CONFIRM_BUTTON_BACKGROUND.darker());

        cancelBtn.addActionListener(e -> closePanel());
        saveBtn.addActionListener(e -> saveChanges());

        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);
        return buttonPanel;
    }

    private JButton createStyledButton(String text, Color background, Color hoverBackground) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(160, 50));
        button.setFont(new Font("Segoe UI", Font.BOLD, 18));
        button.setBackground(background);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverBackground);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(background);
            }
        });
        return button;
    }

    private void chooseImage() {
        // 1. Guardar el Look and Feel actual para poder restaurarlo después.
        LookAndFeel originalLaf = UIManager.getLookAndFeel();

        try {
            // 2. Establecer el Look and Feel del sistema operativo (Windows en este caso).
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            // 3. Crear y configurar el JFileChooser DESPUÉS de cambiar el L&F.
            JFileChooser chooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            chooser.setDialogTitle("Seleccionar una imagen");
            chooser.setAcceptAllFileFilterUsed(false);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Imágenes (JPG, PNG, GIF)", "jpg", "jpeg",
                    "png", "gif");
            chooser.addChoosableFileFilter(filter);

            // Forzar la actualización de la UI del chooser para que tome el nuevo L&F.
            SwingUtilities.updateComponentTreeUI(chooser);

            // 4. Mostrar el diálogo.
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = chooser.getSelectedFile();
                String path = selectedFile.getAbsolutePath();
                imagenField.setText(path);
                updateImagePreview(path);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "No se pudo abrir el selector de archivos con el estilo de Windows.",
                    "Error de UI", JOptionPane.WARNING_MESSAGE);

        } finally {
            try {
                UIManager.setLookAndFeel(originalLaf);
            } catch (UnsupportedLookAndFeelException ex) {
                System.err.println("Error al restaurar el Look and Feel original: " + ex.getMessage());
            }
        }
    }

    private void updateImagePreview(String path) {
        if (path == null || path.trim().isEmpty()) {
            imagenPreview.setIcon(null);
            imagenPreview.setText("Sin imagen");
            return;
        }
        try {
            ImageIcon icon = new ImageIcon(path);
            Image scaled = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            imagenPreview.setIcon(new ImageIcon(scaled));
            imagenPreview.setText(null);
        } catch (Exception e) {
            imagenPreview.setIcon(null);
            imagenPreview.setText("Vista previa no disponible");
        }
    }

    /**
     * @return true si todos los campos son válidos, false en caso contrario.
     */
    private boolean validateInput() {
        StringBuilder errors = new StringBuilder();
        String nombre = nombreField.getText().trim();
        String marca = marcaField.getText().trim();
        String precioTxt = precioField.getText().trim();
        String stockTxt = stockField.getText().trim();

        if (nombre.isEmpty())
            errors.append("- El campo 'Nombre' es obligatorio.\n");
        if (marca.isEmpty())
            errors.append("- El campo 'Marca' es obligatorio.\n");
        if (precioTxt.isEmpty())
            errors.append("- El campo 'Precio' es obligatorio.\n");

        if (!precioTxt.isEmpty()) {
            try {
                if (Integer.parseInt(precioTxt) < 0)
                    errors.append("- El precio no puede ser negativo.\n");
            } catch (NumberFormatException ex) {
                errors.append("- El valor del 'Precio' debe ser un número entero.\n");
            }
        }

        if (!stockTxt.isEmpty()) {
            try {
                if (Integer.parseInt(stockTxt) < 0)
                    errors.append("- El stock no puede ser negativo.\n");
            } catch (NumberFormatException ex) {
                errors.append("- El valor del 'Stock' debe ser un número entero.\n");
            }
        }

        if (errors.length() > 0) {
            ConfirmDialog.showErrorDialog(SwingUtilities.getWindowAncestor(this), errors.toString(),
                    "Errores de validación");
            return false;
        }
        return true;
    }

    private void saveChanges() {
        if (!validateInput()) {
            return;
        }
        try {
            JSONObject updatedTire = new JSONObject();
            updatedTire.put("nombre", nombreField.getText().trim());
            updatedTire.put("marca", marcaField.getText().trim());
            updatedTire.put("precio", Integer.parseInt(precioField.getText().trim()));
            updatedTire.put("stock",
                    stockField.getText().trim().isEmpty() ? 0 : Integer.parseInt(stockField.getText().trim()));
            updatedTire.put("imagen", imagenField.getText().trim());

            String message = isCreateMode ? "¿Desea crear este nuevo producto?"
                    : "¿Desea guardar los cambios realizados?";
            boolean confirmed = ConfirmDialog.showConfirmDialog(SwingUtilities.getWindowAncestor(this), message,
                    "Confirmación");
            if (confirmed) {
                onSaveCallback.accept(updatedTire);
                closePanel();
                new SuccessPopUp(null, "Éxito:", "El producto se actualizó exitosamente.");
            }
        } catch (Exception ex) {
            ConfirmDialog.showErrorDialog(SwingUtilities.getWindowAncestor(this),
                    "Ocurrió un error al guardar los datos:\n" + ex.getMessage(), "Error inesperado");
        }
    }

    private void closePanel() {
        Window win = SwingUtilities.getWindowAncestor(this);
        if (win instanceof JDialog) {
            ((JDialog) win).dispose();
        } else {
            controller.showPanel(parentPanel);
        }
    }
}