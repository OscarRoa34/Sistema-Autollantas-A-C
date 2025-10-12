package co.edu.uptc.view.panels.SubPanels;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileSystemView;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.function.Consumer;
import org.json.JSONObject;

import co.edu.uptc.view.GlobalView;
import co.edu.uptc.view.dialogs.ConfirmDialog;
import co.edu.uptc.view.utils.ViewController;

public class EditTirePanel extends JPanel {

    private JTextField nombreField, marcaField, precioField, stockField, imagenField;
    private JLabel imagenPreview;
    private JSONObject tireData;
    private Consumer<JSONObject> onSaveCallback;
    private JPanel parentPanel;
    private ViewController controller;

    public EditTirePanel(JSONObject tire, Consumer<JSONObject> onSaveCallback, JPanel parentPanel,
            ViewController controller) {
        this.tireData = tire;
        this.onSaveCallback = onSaveCallback;
        this.parentPanel = parentPanel;
        this.controller = controller;

        setLayout(new BorderLayout());
        setBackground(GlobalView.GENERAL_BACKGROUND);
        setBorder(new EmptyBorder(30, 100, 30, 100));

        JLabel title = new JLabel("Editar Llanta");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.BLACK);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 15, 15));
        formPanel.setBackground(GlobalView.GENERAL_BACKGROUND);

        formPanel.add(createLabel("Nombre:"));
        nombreField = createStyledField(tire.optString("nombre", ""));
        formPanel.add(nombreField);

        formPanel.add(createLabel("Marca:"));
        marcaField = createStyledField(tire.optString("marca", ""));
        formPanel.add(marcaField);

        formPanel.add(createLabel("Precio:"));
        precioField = createStyledField(String.valueOf(tire.optInt("precio", 0)));
        formPanel.add(precioField);

        formPanel.add(createLabel("Stock:"));
        stockField = createStyledField(String.valueOf(tire.optInt("stock", 0)));
        formPanel.add(stockField);

        formPanel.add(createLabel("Ruta de imagen:"));
        imagenField = createStyledField(tire.optString("imagen", ""));
        JButton browseBtn = new JButton("Seleccionar...");
        browseBtn.setFocusPainted(false);
        browseBtn.setBackground(GlobalView.ASIDE_BACKGROUND);
        browseBtn.setForeground(Color.WHITE);
        browseBtn.addActionListener(e -> chooseImage());
        JPanel imgPanel = new JPanel(new BorderLayout());
        imgPanel.setBackground(GlobalView.GENERAL_BACKGROUND);
        imgPanel.add(imagenField, BorderLayout.CENTER);
        imgPanel.add(browseBtn, BorderLayout.EAST);
        formPanel.add(imgPanel);

        imagenPreview = new JLabel();
        imagenPreview.setHorizontalAlignment(SwingConstants.CENTER);
        updateImagePreview(tire.optString("imagen", ""));
        formPanel.add(new JLabel(""));
        formPanel.add(imagenPreview);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.setBackground(GlobalView.GENERAL_BACKGROUND);

        JButton cancelBtn = new JButton("Cancelar");
        JButton saveBtn = new JButton("Aceptar");

        cancelBtn.setBackground(Color.GRAY);
        cancelBtn.setForeground(Color.WHITE);
        saveBtn.setBackground(GlobalView.ASIDE_BACKGROUND);
        saveBtn.setForeground(Color.WHITE);

        cancelBtn.addActionListener(e -> {
            Window win = SwingUtilities.getWindowAncestor(EditTirePanel.this);
            if (win instanceof JDialog) {
                ((JDialog) win).dispose();
            } else {
                controller.showPanel(parentPanel);
            }
        });

        saveBtn.addActionListener(e -> saveChanges());

        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);
        add(buttonPanel, BorderLayout.SOUTH);
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
                new EmptyBorder(6, 10, 6, 10)));

        // Efecto al enfocar
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(new CompoundBorder(
                        new LineBorder(GlobalView.ASIDE_BACKGROUND, 2, true),
                        new EmptyBorder(6, 10, 6, 10)));
            }

            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(new CompoundBorder(
                        new LineBorder(new Color(180, 180, 180), 1, true),
                        new EmptyBorder(6, 10, 6, 10)));
            }
        });

        return field;
    }

    private void chooseImage() {
        try {
            JFileChooser chooser = new JFileChooser(FileSystemView.getFileSystemView());
            chooser.setDialogTitle("Seleccionar imagen");
            chooser.setMultiSelectionEnabled(false);
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                String path = chooser.getSelectedFile().getAbsolutePath();
                imagenField.setText(path);
                updateImagePreview(path);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateImagePreview(String path) {
        try {
            ImageIcon icon = new ImageIcon(path);
            Image scaled = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
            imagenPreview.setIcon(new ImageIcon(scaled));
        } catch (Exception e) {
            imagenPreview.setText("Sin imagen");
        }
    }

    private void saveChanges() {
        try {
            JSONObject updated = new JSONObject(tireData.toString());

            if (!nombreField.getText().trim().isEmpty())
                updated.put("nombre", nombreField.getText().trim());

            if (!marcaField.getText().trim().isEmpty())
                updated.put("marca", marcaField.getText().trim());

            if (!precioField.getText().trim().isEmpty())
                updated.put("precio", Integer.parseInt(precioField.getText().trim()));

            if (!stockField.getText().trim().isEmpty())
                updated.put("stock", Integer.parseInt(stockField.getText().trim()));

            if (!imagenField.getText().trim().isEmpty())
                updated.put("imagen", imagenField.getText().trim());

            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            boolean confirmed = ConfirmDialog.showConfirmDialog(
                    parentWindow,
                    "¿Desea guardar los cambios?",
                    "Confirmación");

            if (confirmed) {
                onSaveCallback.accept(updated);
                if (parentWindow instanceof JDialog) {
                    ((JDialog) parentWindow).dispose();
                } else {
                    controller.showPanel(parentPanel);
                }
            }

        } catch (Exception ex) {
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            ConfirmDialog.showErrorDialog(
                    parentWindow,
                    "Error al guardar los cambios:\n" + ex.getMessage(),
                    "Error");
        }
    }

}
