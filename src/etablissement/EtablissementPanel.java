package etablissement;

import caisseecole.CaisseEcolePanel;
import javax.swing.*;
import java.awt.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import javaswingdev.swing.table.Table;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;

public final class EtablissementPanel extends JPanel {
    // Color Palette (matching TransactionPanel)
    private final Color primaryColor = new Color(23, 32, 42);
    private final Color hoverColor = new Color(33, 97, 140);
    private final Color activeColor = new Color(33, 97, 140);
    private final Color otherColor = new Color(41, 128, 185);
    private final Color backgroundColor = new Color(236, 240, 241);
    private final Color textColor = new Color(44, 62, 80);
    private final Color tableBackgroundColor = new Color(247, 249, 250);

    private final EtablissementController controller;
    private final JTable table;
    private final DefaultTableModel tableModel;
    private JTextField searchField;

    private final String[] columns = {"Nom", "Type", "Localisation", "Date de création", "Actions"};

    public EtablissementPanel() throws ClassNotFoundException {
        setLayout(new BorderLayout(0, 10)); 
        setBackground(backgroundColor);

        this.controller = new EtablissementController();

        // Add header banner
        JPanel headerBanner = createHeaderBanner();
        add(headerBanner, BorderLayout.NORTH);

        // Create main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(0, 10));
        mainPanel.setBackground(backgroundColor);

        // Add top panel (search and actions)
        JPanel topPanel = createTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Initialize table model and table
        tableModel = createTableModel(columns);
        table = createStyledTable(columns);

        // Add table panel
        JScrollPane scrollPane = createStyledScrollPane(table);
        JPanel tablePanel = createTablePanel();
        tablePanel.add(scrollPane);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        // Add main panel to center of EtablissementPanel
        add(mainPanel, BorderLayout.CENTER);

        // Load initial data
        refreshTable();
    }

    private JPanel createHeaderBanner() {
        JPanel bannerPanel = new JPanel(new BorderLayout());
        bannerPanel.setBackground(primaryColor);
        bannerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Gérer Établissements");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        bannerPanel.add(titleLabel, BorderLayout.CENTER);
        return bannerPanel;
    }

    private DefaultTableModel createTableModel(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == columns.length - 1;
            }
        };
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout(10, 10)); 
        topPanel.setBackground(backgroundColor);
        topPanel.setBorder(new EmptyBorder(5, 20, 5, 20)); 

        // Search Panel
        JPanel searchPanel = createSearchPanel();
        topPanel.add(searchPanel, BorderLayout.WEST);

        // Action Panel
        JPanel actionPanel = createActionPanel();
        topPanel.add(actionPanel, BorderLayout.EAST);

        return topPanel;
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setBackground(backgroundColor);

        searchField = new JTextField(30);
        searchField.setPreferredSize(new Dimension(350, 45));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(hoverColor, 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JButton searchButton = createStyledButton("Rechercher", hoverColor);
        searchButton.addActionListener(e -> performSearch());
        searchField.addActionListener(e -> performSearch());

        searchPanel.add(new JLabel("Rechercher: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        return searchPanel;
    }

    private JPanel createActionPanel() {
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setBackground(backgroundColor);

        JButton addButton = createStyledButton("Ajouter", activeColor);
        addButton.addActionListener(e -> showAddDialog());

        JButton exportButton = createStyledButton("Exporter PDF", otherColor);
        exportButton.addActionListener(e -> exportToPDF());

        actionPanel.add(addButton);
        actionPanel.add(exportButton);

        return actionPanel;
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(180, 45));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor.darker());
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
            }
        });
        return button;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout(0, 10));
        tablePanel.setBorder(new EmptyBorder(10, 20, 20, 20));

        // Title above table
        JLabel tableTitle = new JLabel("Liste des Établissements");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tableTitle.setForeground(textColor);
        tablePanel.add(tableTitle, BorderLayout.NORTH);

        return tablePanel;
    }

    private JTable createStyledTable(String[] columns) {
        Table styledTable = new Table();
        styledTable.setModel(tableModel);
        
        // Center text in all cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < styledTable.getColumnCount(); i++) {
            styledTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Visual table configuration
        styledTable.setBackground(Color.WHITE);
        styledTable.setOpaque(true);
        styledTable.setSelectionBackground(hoverColor);
        styledTable.setSelectionForeground(Color.WHITE);
        styledTable.setRowHeight(40);
        styledTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Table header
        styledTable.getTableHeader().setBackground(primaryColor);
        styledTable.getTableHeader().setForeground(Color.WHITE);
        styledTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));

        // Column width configuration
        int[] columnWidths = {150, 100, 150, 150, 80};
        for (int i = 0; i < columns.length; i++) {
            styledTable.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }

        // Actions column
        styledTable.getColumnModel().getColumn(columns.length - 1).setCellRenderer(new IconRenderer());
        styledTable.getColumnModel().getColumn(columns.length - 1).setCellEditor(
            new IconEditor(new JCheckBox(), this, controller, styledTable));

        return styledTable;
    }

    private JScrollPane createStyledScrollPane(JTable table) {
        JScrollPane scrollPane = new JScrollPane(table);
        
        // Custom background for scroll pane
        scrollPane.getViewport().setBackground(tableBackgroundColor);
        scrollPane.setBackground(tableBackgroundColor);
        
        // Border with subtle shadow
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 20, 20, 20),
            BorderFactory.createLineBorder(new Color(220, 230, 240), 1)
        ));

        // Add shadow effect
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            scrollPane.getBorder(),
            BorderFactory.createMatteBorder(1, 1, 2, 2, new Color(200, 210, 220))
        ));

        return scrollPane;
    }


    public void refreshTable() {
        while (tableModel.getRowCount() > 0) {
            tableModel.removeRow(0);
        }
        
        List<Etablissement> etablissements = controller.getAllEtablissements();
        for (Etablissement e : etablissements) {
            tableModel.addRow(new Object[]{
                e.getNom(),
                e.getType(),
                e.getLocalisation(),
                e.getCreatedAt(),
                "Actions"
            });
        }
    }

    private void showAddDialog() {
        // Créer un nouveau JDialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Ajouter Établissement", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(this);

        // Créer une instance de ModernEtablissementForm
        ModernEtablissementForm form = new ModernEtablissementForm(null, dialog);

        // Ajouter le formulaire au dialogue
        dialog.add(form, BorderLayout.CENTER);

        // Créer un bouton pour sauvegarder
        JButton saveButton = form.createSaveButton("Sauvegarder");
        saveButton.addActionListener(e -> {
            Etablissement newEtablissement = form.saveEtablissement(null); // null car on crée un nouvel établissement
            if (newEtablissement != null) {
                // Ajouter l'établissement à la base de données ou à la liste
                controller.createEtablissement(newEtablissement);
                refreshTable(); // Rafraîchir la table pour afficher le nouvel établissement
                dialog.dispose(); // Fermer le dialogue
            }
        });

        // Ajouter le bouton de sauvegarde en bas du dialogue
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Afficher le dialogue
        dialog.setVisible(true);
    }

    JPanel createForm(Etablissement etablissement, JDialog dialog) {
        JPanel form = new JPanel(new GridLayout(4, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField nomField = new JTextField(etablissement != null ? etablissement.getNom() : "");
        nomField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        JTextField typeField = new JTextField(etablissement != null ? etablissement.getType() : "");
        typeField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        JTextField localisationField = new JTextField(etablissement != null ? etablissement.getLocalisation() : "");
        localisationField.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        form.add(new JLabel("Nom:"));
        form.add(nomField);
        form.add(new JLabel("Type:"));
        form.add(typeField);
        form.add(new JLabel("Localisation:"));
        form.add(localisationField);

        JButton saveButton = new JButton(etablissement != null ? "Mettre à jour" : "Enregistrer");
        saveButton.setBackground(activeColor);
        saveButton.setForeground(Color.WHITE);
        saveButton.setBorderPainted(false);
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveButton.addActionListener(e -> {
            if (etablissement != null) {
                // Mode modification
                Etablissement updatedEtablissement = new Etablissement(
                    etablissement.getId(),
                    nomField.getText(),
                    typeField.getText(),
                    localisationField.getText(),
                    etablissement.getCreatedAt()
                );
                controller.updateEtablissement(updatedEtablissement);
            } else {
                // Mode création
                Etablissement newEtablissement = new Etablissement(
                                     UUID.randomUUID().toString(),
                    nomField.getText(),
                    typeField.getText(),
                    localisationField.getText(),
                    new Timestamp(System.currentTimeMillis())
                );
                controller.createEtablissement(newEtablissement);
            }
            dialog.dispose();
            refreshTable();
        });

        form.add(new JPanel()); // Spacer
        form.add(saveButton);

        return form;
    }

    private void performSearch() {
        String keyword = searchField.getText();
        tableModel.setRowCount(0);
        List<Etablissement> results = controller.searchEtablissements(keyword);
        for (Etablissement e : results) {
            tableModel.addRow(new Object[]{
                e.getNom(),
                e.getType(),
                e.getLocalisation(),
                e.getCreatedAt(),
                "Actions"
            });
        }
    }

    private void exportToPDF() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Enregistrer le PDF");
        fileChooser.setFileFilter(new FileNameExtensionFilter("PDF files", "pdf"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".pdf")) {
                filePath += ".pdf";
            }
            controller.exportToPDF(filePath);
            JOptionPane.showMessageDialog(this,
                "PDF exporté avec succès!",
                "Succès",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
 
}

// Remplacez les classes ButtonRenderer et ButtonEditor par :

class IconRenderer extends JPanel implements TableCellRenderer {
    private final JLabel moreIcon;

    public IconRenderer() {
        setLayout(new GridBagLayout());
        setOpaque(true);
        
        ImageIcon icon = loadScaledIcon("src/icons/more.png", 24, 24);
        moreIcon = new JLabel(icon);
        moreIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        add(moreIcon);
    }

    private ImageIcon loadScaledIcon(String path, int width, int height) {
        try {
            ImageIcon originalIcon = new ImageIcon(path);
            Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (Exception e) {
            System.err.println("Erreur de chargement de l'icône : " + path);
            return null;
        }
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            setBackground(new Color(220, 220, 220));
        } else {
            setBackground(Color.WHITE);
        }
        return this;
    }
}

class IconEditor extends DefaultCellEditor {
    private final JPanel actionPanel;
    private final JLabel moreIcon;
    private final JLabel editIcon;
    private final JLabel deleteIcon;
    private final EtablissementPanel panel;
    private final EtablissementController controller;
    private JDialog activeDialog;
    private final JTable table;

    public IconEditor(JCheckBox checkBox, EtablissementPanel panel,
                     EtablissementController controller, JTable table) {
        super(checkBox);
        this.panel = panel;
        this.controller = controller;
        this.table = table;

        actionPanel = new JPanel(new GridBagLayout());
        actionPanel.setOpaque(true);
        actionPanel.setBackground(Color.WHITE);

        // Charger les icônes à partir des fichiers
        moreIcon = new JLabel(loadScaledIcon("src/icons/more.png", 24, 24));
        editIcon = new JLabel(loadScaledIcon("src/icons/edit.png", 24, 24));
        deleteIcon = new JLabel(loadScaledIcon("src/icons/delete.png", 24, 24));

        // Configurer les curseurs
        moreIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        editIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        deleteIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private ImageIcon loadScaledIcon(String path, int width, int height) {
        try {
            ImageIcon originalIcon = new ImageIcon(path);
            Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (Exception e) {
            System.err.println("Erreur de chargement de l'icône : " + path);
            return null;
        }
    }

        @Override
public Component getTableCellEditorComponent(JTable table, Object value,
        boolean isSelected, int row, int column) {
    // Fermer tout dialogue actif
    if (activeDialog != null && activeDialog.isVisible()) {
        activeDialog.dispose();
        activeDialog = null;
    }

    actionPanel.removeAll();
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.CENTER;
    actionPanel.add(moreIcon, gbc);

    moreIcon.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            SwingUtilities.invokeLater(() -> {
                JPopupMenu popup = new JPopupMenu();
                popup.setBackground(Color.WHITE);

                // Edit menu item
                JMenuItem editItem = createEditMenuItem(table);
                popup.add(editItem);

                // Delete menu item
                JMenuItem deleteItem = createDeleteMenuItem(table);
                popup.add(deleteItem);

                // Caisse menu item
                JMenuItem caisseItem = createCaisseMenuItem(table);
                popup.add(caisseItem);

                // Show popup relative to the invoking component
                popup.show(moreIcon, 0, moreIcon.getHeight());
            });
        }
    });

    return actionPanel;
}

private JMenuItem createEditMenuItem(JTable table) {
    JMenuItem editItem = new JMenuItem("Modifier");
    editItem.setBackground(new Color(33, 150, 243));
    editItem.setForeground(Color.WHITE);
    editItem.setFont(new Font("Arial", Font.BOLD, 14));
    editItem.addActionListener(e -> {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            String nom = table.getValueAt(selectedRow, 0).toString();
            String type = table.getValueAt(selectedRow, 1).toString();
            String localisation = table.getValueAt(selectedRow, 2).toString();
            Timestamp createdAt = (Timestamp) table.getValueAt(selectedRow, 3);

            List<Etablissement> etablissements = controller.searchEtablissements(nom);
            if (!etablissements.isEmpty()) {
                Etablissement etablissement = new Etablissement(
                    etablissements.get(0).getId(),
                    nom, type, localisation, createdAt
                );
                activeDialog = showEditDialog(etablissement);
            }
        }
    });
    return editItem;
}

private JMenuItem createDeleteMenuItem(JTable table) {
    JMenuItem deleteItem = new JMenuItem("Supprimer");
    deleteItem.setBackground(new Color(244, 67, 54));
    deleteItem.setForeground(Color.WHITE);
    deleteItem.setFont(new Font("Arial", Font.BOLD, 14));
    deleteItem.addActionListener(e -> {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            String nom = table.getValueAt(selectedRow, 0).toString();
            int confirmation = JOptionPane.showConfirmDialog(
                actionPanel,
                "Êtes-vous sûr de vouloir supprimer cet établissement ?",
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION
            );

            if (confirmation == JOptionPane.YES_OPTION) {
                List<Etablissement> etablissements = controller.searchEtablissements(nom);
                if (!etablissements.isEmpty()) {
                    String id = etablissements.get(0).getId();
                    controller.deleteEtablissement(id);
                    panel.refreshTable();
                }
            }
        }
    });
    return deleteItem;
}

private JMenuItem createCaisseMenuItem(JTable table) {
    JMenuItem caisseItem = new JMenuItem("Gérer Caisse");
    caisseItem.setBackground(new Color(33, 150, 243));
    caisseItem.setForeground(Color.WHITE);
    caisseItem.setFont(new Font("Arial", Font.BOLD, 14));
    caisseItem.addActionListener(e -> {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            String nom = table.getValueAt(selectedRow, 0).toString();
            String id = controller.searchEtablissements(nom).get(0).getId();
            showCaisseEcolePanel(id);
        }
    }); 
    return caisseItem;
}

        private void showCaisseEcolePanel(String etablissementId) {
            Window parentWindow = SwingUtilities.getWindowAncestor(panel);
            JDialog dialog;

        switch (parentWindow) {
            case JFrame jFrame -> dialog = new JDialog(jFrame, "Gestion de la Caisse École", false);
            case Dialog dialog1 -> dialog = new JDialog(dialog1, "Gestion de la Caisse École", false);
            default -> {
                // Fallback to a default JDialog if parent window type is unexpected
                dialog = new JDialog();
                dialog.setTitle("Gestion de la Caisse École");
            }
        }

            dialog.setLayout(new BorderLayout());
            dialog.add(new CaisseEcolePanel(etablissementId));
            dialog.setSize(800, 600);
            dialog.setLocationRelativeTo(panel);
            dialog.setVisible(true);
        }

    private JDialog showEditDialog(Etablissement etablissement) {
        Window parentWindow = SwingUtilities.getWindowAncestor(panel);
        JDialog dialog;
        if (parentWindow instanceof JFrame jFrame) {
            dialog = new JDialog(jFrame, "Modifier l'établissement", true);
        } else {
            dialog = new JDialog((Dialog) parentWindow, "Modifier l'établissement", true);
        }
        dialog.setLayout(new BorderLayout());

        JPanel form = panel.createForm(etablissement, dialog);
        dialog.add(form, BorderLayout.CENTER);
                dialog.pack();
        dialog.setLocationRelativeTo(panel);
        dialog.setVisible(true);
        return dialog;
    }

    @Override
    public Object getCellEditorValue() {
        return "";
    }

    @Override
    public boolean stopCellEditing() {
        return super.stopCellEditing();
    }
}