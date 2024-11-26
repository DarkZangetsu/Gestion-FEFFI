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

public class EtablissementPanel extends JPanel {
    private final EtablissementController controller;
    private final JTable table;
    private final DefaultTableModel tableModel;
    private JTextField searchField;

    public EtablissementPanel() throws ClassNotFoundException {
        setBackground(Color.WHITE);
        setLayout(new BorderLayout(0, 10));

        this.controller = new EtablissementController();
        
         // Bannière d'en-tête
        JPanel headerBanner = createHeaderBanner();
        add(headerBanner, BorderLayout.NORTH);

        // Panneau supérieur avec recherche et boutons
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Nom", "Type", "Localisation", "Date de création", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == columns.length - 1;
            }
        };

        table = createTable(columns);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        add(scrollPane, BorderLayout.CENTER);

        // Chargement initial des données
        refreshTable();
    }
    
    private JPanel createHeaderBanner() {
    // Message de débogage
    System.out.println("Création de la bannière d'en-tête");

    JPanel bannerPanel = new JPanel(new BorderLayout());
    bannerPanel.setBackground(new Color(76, 175, 80)); // Vert
    bannerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

    JLabel titleLabel = new JLabel("Gérer Établissements");
    titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
    titleLabel.setForeground(Color.WHITE);
    titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

    bannerPanel.add(titleLabel, BorderLayout.CENTER);
    return bannerPanel;
}

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Panneau de recherche
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setBackground(Color.WHITE);

        searchField = new JTextField(30);
        searchField.setPreferredSize(new Dimension(300, 40));
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JButton searchButton = new JButton("Rechercher");
        searchButton.setBackground(new Color(33, 150, 243)); // Bleu
        searchButton.setForeground(Color.WHITE);
        searchButton.setBorderPainted(false);
        searchButton.setPreferredSize(new Dimension(120, 40));
        searchButton.setFont(new Font("Arial", Font.BOLD, 14));

        searchButton.addActionListener(e -> performSearch());
        searchField.addActionListener(e -> performSearch());

        searchPanel.add(new JLabel("Rechercher: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // Panneau des boutons d'action
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setBackground(Color.WHITE);

        JButton addButton = new JButton("Ajouter");
        addButton.setBackground(new Color(76, 175, 80)); // Vert
        addButton.setForeground(Color.WHITE);
        addButton.setBorderPainted(false);
        addButton.setPreferredSize(new Dimension(120, 40));
        addButton.setFont(new Font("Arial", Font.BOLD, 14));
        addButton.addActionListener(e -> showAddDialog());

        JButton exportButton = new JButton("Exporter PDF");
        exportButton.setBackground(new Color(33, 150, 243)); // Bleu
        exportButton.setForeground(Color.WHITE);
        exportButton.setBorderPainted(false);
        exportButton.setPreferredSize(new Dimension(150, 40));
        exportButton.setFont(new Font("Arial", Font.BOLD, 14));
        exportButton.addActionListener(e -> exportToPDF());

        actionPanel.add(addButton);
        actionPanel.add(exportButton);

        // Combinaison des panneaux de recherche et d'action
        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(actionPanel, BorderLayout.EAST);

        return topPanel;
    }

    private Table createTable(String[] columns) {
        Table newTable = new Table();
        newTable.setModel(tableModel);
        
        // Personnalisation du rendu des cellules
        newTable.getColumnModel().getColumn(columns.length - 1).setCellRenderer(new ButtonRenderer());
        newTable.getColumnModel().getColumn(columns.length - 1).setCellEditor(
            new ButtonEditor(new JCheckBox(), this, controller, newTable));

        // Ajustement de la largeur des colonnes
        newTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        newTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        newTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        newTable.getColumnModel().getColumn(3).setPreferredWidth(200);
        newTable.getColumnModel().getColumn(4).setPreferredWidth(200);
    
        return newTable;
    }

     public void refreshTable() {
        // Suppression des lignes existantes
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
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog dialog;
        if (parentWindow instanceof JFrame) {
            dialog = new JDialog((JFrame) parentWindow, "Ajouter un établissement", true);
        } else {
            dialog = new JDialog((Dialog) parentWindow, "Ajouter un établissement", true);
        }
        dialog.setLayout(new BorderLayout());

        JPanel form = createForm(null, dialog);
        dialog.add(form, BorderLayout.CENTER);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    JPanel createForm(Etablissement etablissement, JDialog dialog) {
        JPanel form = new JPanel(new GridLayout(4, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField nomField = new JTextField(etablissement != null ? etablissement.getNom() : "");
        nomField.setFont(new Font("Arial", Font.PLAIN, 14));
        JTextField typeField = new JTextField(etablissement != null ? etablissement.getType() : "");
        typeField.setFont(new Font("Arial", Font.PLAIN, 14));
        JTextField localisationField = new JTextField(etablissement != null ? etablissement.getLocalisation() : "");
        localisationField.setFont(new Font("Arial", Font.PLAIN, 14));

        form.add(new JLabel("Nom:"));
        form.add(nomField);
        form.add(new JLabel("Type:"));
        form.add(typeField);
        form.add(new JLabel("Localisation:"));
        form.add(localisationField);

        JButton saveButton = new JButton(etablissement != null ? "Mettre à jour" : "Enregistrer");
        saveButton.setBackground(new Color(76, 175, 80)); // Vert
        saveButton.setForeground(Color.WHITE);
        saveButton.setBorderPainted(false);
        saveButton.setFont(new Font("Arial", Font.BOLD, 14));
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
                e.getId(),
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

class ButtonRenderer extends JButton implements TableCellRenderer {
    public ButtonRenderer() {
        setOpaque(true);
        setBackground(new Color(33, 150, 243)); // Bleu
        setForeground(Color.WHITE);
        setBorderPainted(false);
        setFont(new Font("Arial", Font.BOLD, 14));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        setText("Actions");
        return this;
    }
}

class ButtonEditor extends DefaultCellEditor {
    private final JButton button;
    private String label;
    private boolean isPushed;
    private final EtablissementPanel panel;
    private final EtablissementController controller;
    private final JTable table;

    public ButtonEditor(JCheckBox checkBox, EtablissementPanel panel,
                       EtablissementController controller, JTable table) {
        super(checkBox);
        this.panel = panel;
        this.controller = controller;
        this.table = table;

        button = new JButton();
        button.setOpaque(true);
        button.setBackground(new Color(33, 150, 243)); // Bleu
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.addActionListener(e -> fireEditingStopped());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        label = (value == null) ? "" : value.toString();
        button.setText(label);
        isPushed = true;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        if (isPushed) {
            showActionsPopup();
        }
        isPushed = false;
        return label;
    }

    private void showActionsPopup() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        // Correctly retrieve the ID from the first column
        String nom = table.getValueAt(row, 0).toString();
        String type = table.getValueAt(row, 1).toString();
        String localisation = table.getValueAt(row, 2).toString();
        Timestamp createdAt = (Timestamp) table.getValueAt(row, 3);

        JPopupMenu popup = new JPopupMenu();

        JMenuItem editItem = new JMenuItem("Modifier");
        editItem.setBackground(new Color(33, 150, 243)); // Bleu
        editItem.setForeground(Color.WHITE);
        editItem.setFont(new Font("Arial", Font.BOLD, 14));
        editItem.addActionListener(e -> {
            Etablissement etablissement = new Etablissement(
                controller.searchEtablissements(nom).get(0).getId(), // Get the ID from the database
                nom,
                type,
                localisation,
                createdAt
            );
            showEditDialog(etablissement);
        });
        popup.add(editItem);

        JMenuItem deleteItem = new JMenuItem("Supprimer");
        deleteItem.setBackground(new Color(244, 67, 54)); // Rouge
        deleteItem.setForeground(Color.WHITE);
        deleteItem.setFont(new Font("Arial", Font.BOLD, 14));
        deleteItem.addActionListener(e -> {
            int confirmation = JOptionPane.showConfirmDialog(
                button,
                "Êtes-vous sûr de vouloir supprimer cet établissement ?",
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION
            );

            if (confirmation == JOptionPane.YES_OPTION) {
                // Get the ID from the database search
                String id = controller.searchEtablissements(nom).get(0).getId();
                controller.deleteEtablissement(id);
                panel.refreshTable();
            }
        });
        popup.add(deleteItem);// Dans la méthode showActionsPopup de la classe ButtonEditor dans EtablissementPanel
        JMenuItem caisseItem = new JMenuItem("Gérer Caisse");
        caisseItem.setBackground(new Color(33, 150, 243));
        caisseItem.setForeground(Color.WHITE);
        caisseItem.setFont(new Font("Arial", Font.BOLD, 14));
        caisseItem.addActionListener(e -> {
            String id = controller.searchEtablissements(nom).get(0).getId();
            showCaisseEcolePanel(id);
        });
        popup.add(caisseItem);

        popup.show(button, 0, button.getHeight());
    }
    
    // Ajouter cette méthode dans la classe ButtonEditor
private void showCaisseEcolePanel(String etablissementId) {
    JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(panel),
        "Gestion de la Caisse École", false);
    dialog.setLayout(new BorderLayout());
    dialog.add(new CaisseEcolePanel(etablissementId));
    dialog.setSize(800, 600);
    dialog.setLocationRelativeTo(panel);
    dialog.setVisible(true);
}

    private void showEditDialog(Etablissement etablissement) {
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
    }

    @Override
    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
    }
}