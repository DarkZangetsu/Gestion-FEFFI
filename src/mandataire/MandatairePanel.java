package mandataire;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import javaswingdev.swing.table.Table;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import com.toedter.calendar.JDateChooser;
import etablissement.Etablissement;
import etablissement.EtablissementController;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MandatairePanel extends JPanel {
    private final MandataireController controller;
    private final JTable table;
    private final DefaultTableModel tableModel;
    private JTextField searchField;

    public MandatairePanel() throws ClassNotFoundException {
        setBackground(Color.WHITE);
        setLayout(new BorderLayout(0, 10));

        this.controller = new MandataireController();
        
        // Bannière d'en-tête
        JPanel headerBanner = createHeaderBanner();
        add(headerBanner, BorderLayout.NORTH);

        // Panneau supérieur avec recherche et boutons
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Nom", "Fonction", "Date Mandat", "Code École", "CIN", 
                          "Contact", "Email", "Établissement", "Date de création", "Actions"};
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
        JPanel bannerPanel = new JPanel(new BorderLayout());
        bannerPanel.setBackground(new Color(76, 175, 80)); // Vert
        bannerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Gérer Mandataires");
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
        searchButton.setBackground(new Color(33, 150, 243));
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
        addButton.setBackground(new Color(76, 175, 80));
        addButton.setForeground(Color.WHITE);
        addButton.setBorderPainted(false);
        addButton.setPreferredSize(new Dimension(120, 40));
        addButton.setFont(new Font("Arial", Font.BOLD, 14));
        addButton.addActionListener(e -> {
            try {
                showAddDialog();
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(MandatairePanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        JButton exportButton = new JButton("Exporter PDF");
        exportButton.setBackground(new Color(33, 150, 243));
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
        newTable.getColumnModel().getColumn(0).setPreferredWidth(100); // Nom
        newTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Fonction
        newTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Date Mandat
        newTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Code École
        newTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // CIN
        newTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Contact
        newTable.getColumnModel().getColumn(6).setPreferredWidth(150); // Email
        newTable.getColumnModel().getColumn(7).setPreferredWidth(150); // Établissement
        newTable.getColumnModel().getColumn(8).setPreferredWidth(120); // Date création
        newTable.getColumnModel().getColumn(9).setPreferredWidth(100); // Actions
    
        return newTable;
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        List<Mandataire> mandataires = controller.getAllMandataires();
        for (Mandataire m : mandataires) {
            tableModel.addRow(new Object[]{
                m.getNom(),
                m.getFonction(),
                m.getDateMandat(),
                m.getCodeEcole(),
                m.getCin(),
                m.getContact(),
                m.getEmail(),
                m.getNomEtablissement(),
                m.getCreatedAt(),
                "Actions"
            });
        }
    }

    private void showAddDialog() throws ClassNotFoundException {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog dialog;
        if (parentWindow instanceof JFrame) {
            dialog = new JDialog((JFrame) parentWindow, "Ajouter un mandataire", true);
        } else {
            dialog = new JDialog((Dialog) parentWindow, "Ajouter un mandataire", true);
        }
        dialog.setLayout(new BorderLayout());

        JPanel form = createForm(null, dialog);
        dialog.add(form, BorderLayout.CENTER);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    JPanel createForm(Mandataire mandataire, JDialog dialog) throws ClassNotFoundException {
    JPanel form = new JPanel(new GridBagLayout());
    form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    form.setBackground(Color.WHITE);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.weightx = 1.0;

    // Création des composants
    JTextField nomField = new JTextField(20);
    JTextField fonctionField = new JTextField(20);
    JDateChooser dateMandatChooser = new JDateChooser();
    JTextField codeEcoleField = new JTextField(20);
    JTextField cinField = new JTextField(20);
    JTextField contactField = new JTextField(20);
    JTextField emailField = new JTextField(20);

    // Style des composants
    Font labelFont = new Font("Arial", Font.BOLD, 12);
    Font fieldFont = new Font("Arial", Font.PLAIN, 12);
    Dimension fieldDimension = new Dimension(200, 30);

    // Configuration des champs
    nomField.setPreferredSize(fieldDimension);
    fonctionField.setPreferredSize(fieldDimension);
    dateMandatChooser.setPreferredSize(fieldDimension);
    codeEcoleField.setPreferredSize(fieldDimension);
    cinField.setPreferredSize(fieldDimension);
    contactField.setPreferredSize(fieldDimension);
    emailField.setPreferredSize(fieldDimension);

    // Remplissage des champs en mode édition
    if (mandataire != null) {
        nomField.setText(mandataire.getNom());
        fonctionField.setText(mandataire.getFonction());
        if (mandataire.getDateMandat() != null) {
            dateMandatChooser.setDate(mandataire.getDateMandat());
        }
        codeEcoleField.setText(mandataire.getCodeEcole());
        cinField.setText(mandataire.getCin());
        contactField.setText(mandataire.getContact());
        emailField.setText(mandataire.getEmail());
    }

    // Initialisation des contrôleurs
    EtablissementController etablissementController = new EtablissementController();
    // Récupération de la liste des établissements
    List<Etablissement> etablissements = etablissementController.getAllEtablissements();
    List<String> etablissementNames = new ArrayList<>();
    Map<String, String> nomToIdMap = new HashMap<>();
    for (Etablissement etab : etablissements) {
        etablissementNames.add(etab.getNom());
    }
    // Création de la ComboBox avec autocomplétion
    AutoCompleteComboBox etablissementCombo = new AutoCompleteComboBox(etablissementNames);
    etablissementCombo.setPreferredSize(fieldDimension);
    etablissementCombo.setFont(fieldFont);
    // Présélection de l'établissement en mode édition
    if (mandataire != null) {
        etablissementCombo.setSelectedItem(mandataire.getNomEtablissement());
    }
    // Ajout des composants avec GridBagLayout
    gbc.gridx = 0;
    gbc.gridy = 0;
    JLabel nomLabel = new JLabel("Nom:");
    nomLabel.setFont(labelFont);
    form.add(nomLabel, gbc);
    gbc.gridx = 1;
    form.add(nomField, gbc);
    gbc.gridx = 0;
    gbc.gridy = 1;
    JLabel fonctionLabel = new JLabel("Fonction:");
    fonctionLabel.setFont(labelFont);
    form.add(fonctionLabel, gbc);
    gbc.gridx = 1;
    form.add(fonctionField, gbc);
    gbc.gridx = 0;
    gbc.gridy = 2;
    JLabel dateMandatLabel = new JLabel("Date Mandat:");
    dateMandatLabel.setFont(labelFont);
    form.add(dateMandatLabel, gbc);
    gbc.gridx = 1;
    form.add(dateMandatChooser, gbc);
    gbc.gridx = 0;
    gbc.gridy = 3;
    JLabel codeEcoleLabel = new JLabel("Code École:");
    codeEcoleLabel.setFont(labelFont);
    form.add(codeEcoleLabel, gbc);
    gbc.gridx = 1;
    form.add(codeEcoleField, gbc);
    gbc.gridx = 0;
    gbc.gridy = 4;
    JLabel cinLabel = new JLabel("CIN:");
    cinLabel.setFont(labelFont);
    form.add(cinLabel, gbc);
    gbc.gridx = 1;
    form.add(cinField, gbc);
    gbc.gridx = 0;
    gbc.gridy = 5;
    JLabel contactLabel = new JLabel("Contact:");
    contactLabel.setFont(labelFont);
    form.add(contactLabel, gbc);
    gbc.gridx = 1;
    form.add(contactField, gbc);
    gbc.gridx = 0;
    gbc.gridy = 6;
    JLabel emailLabel = new JLabel("Email:");
    emailLabel.setFont(labelFont);
    form.add(emailLabel, gbc);
    gbc.gridx = 1;
    form.add(emailField, gbc);
    gbc.gridx = 0;
    gbc.gridy = 7;
    JLabel etablissementLabel = new JLabel("Établissement:");
    etablissementLabel.setFont(labelFont);
    form.add(etablissementLabel, gbc);
    gbc.gridx = 1;
    form.add(etablissementCombo, gbc);
    // Boutons
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonPanel.setBackground(Color.WHITE);
    JButton cancelButton = new JButton("Annuler");
    cancelButton.setFont(new Font("Arial", Font.BOLD, 12));
    cancelButton.setBackground(new Color(244, 67, 54));
    cancelButton.setForeground(Color.WHITE);
    cancelButton.setBorderPainted(false);
    cancelButton.setPreferredSize(new Dimension(100, 30));
    cancelButton.addActionListener(e -> dialog.dispose());
    JButton saveButton = new JButton(mandataire != null ? "Modifier" : "Enregistrer");
    saveButton.setFont(new Font("Arial", Font.BOLD, 12));
    saveButton.setBackground(new Color(76, 175, 80));
    saveButton.setForeground(Color.WHITE);
    saveButton.setBorderPainted(false);
    saveButton.setPreferredSize(new Dimension(100, 30));
    // Action du bouton de sauvegarde
    saveButton.addActionListener(e -> {
        // Validation des champs requis
        if (nomField.getText().trim().isEmpty() ||
                fonctionField.getText().trim().isEmpty() ||
                dateMandatChooser.getDate() == null || 
                cinField.getText().trim().isEmpty() ||
                etablissementCombo.getSelectedEstablishment() == null) {
            
            JOptionPane.showMessageDialog(dialog,
                    "Veuillez remplir tous les champs obligatoires",
                    "Erreur de validation",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String selectedEstablishment = etablissementCombo.getSelectedEstablishment();
        String etablissementId = nomToIdMap.get(selectedEstablishment);
        
        try {
            if (mandataire != null) {
                // Mode modification
                Mandataire updatedMandataire = new Mandataire(
                        mandataire.getId(),
                        nomField.getText().trim(),
                        fonctionField.getText().trim(),
                        new Date(dateMandatChooser.getDate().getTime()),
                        codeEcoleField.getText().trim(),
                        cinField.getText().trim(),
                        contactField.getText().trim(),
                        emailField.getText().trim(),
                        selectedEstablishment,
                        mandataire.getCreatedAt()
                );
                controller.updateMandataire(updatedMandataire);
                JOptionPane.showMessageDialog(dialog,
                        "Mandataire modifié avec succès!",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Mode création
                Mandataire newMandataire = new Mandataire(
                        UUID.randomUUID().toString(),
                        nomField.getText().trim(),
                        fonctionField.getText().trim(),
                        new Date(dateMandatChooser.getDate().getTime()),
                        codeEcoleField.getText().trim(),
                        cinField.getText().trim(),
                        contactField.getText().trim(),
                        emailField.getText().trim(),
                        selectedEstablishment,
                        new Timestamp(System.currentTimeMillis())
                );
                controller.createMandataire(newMandataire);
                JOptionPane.showMessageDialog(dialog,
                        "Mandataire créé avec succès!",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
            }
            dialog.dispose();
            refreshTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialog,
                    "Erreur lors de l'enregistrement: " + ex.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    });
    buttonPanel.add(cancelButton);
    buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
    buttonPanel.add(saveButton);
    gbc.gridx = 0;
    gbc.gridy = 8;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.LINE_END;
    form.add(buttonPanel, gbc);

    return form;
}

    private void performSearch() {
        String keyword = searchField.getText();
        tableModel.setRowCount(0);
        List<Mandataire> results = controller.searchMandataires(keyword);
        for (Mandataire m : results) {
            tableModel.addRow(new Object[]{
                m.getNom(),
                m.getFonction(),
                m.getDateMandat(),
                m.getCodeEcole(),
                m.getCin(),
                m.getContact(),
                m.getEmail(),
                m.getNomEtablissement(),
                m.getCreatedAt(),
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
        setBackground(new Color(33, 150, 243));
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
    private final MandatairePanel panel;
    private final MandataireController controller;
    private final JTable table;

    public ButtonEditor(JCheckBox checkBox, MandatairePanel panel,
                       MandataireController controller, JTable table) {
        super(checkBox);
        this.panel = panel;
        this.controller = controller;
        this.table = table;

        button = new JButton();
        button.setOpaque(true);
        button.setBackground(new Color(33, 150, 243));
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

        // Récupération des données de la ligne sélectionnée
        String nom = table.getValueAt(row, 0).toString();
        String fonction = table.getValueAt(row, 1).toString();
        Date dateMandat = (Date) table.getValueAt(row, 2);
        String codeEcole = table.getValueAt(row, 3).toString();
        String cin = table.getValueAt(row, 4).toString();
        String contact = table.getValueAt(row, 5).toString();
        String email = table.getValueAt(row, 6).toString();
        String nomEtablissement = table.getValueAt(row, 7).toString();
        Timestamp createdAt = (Timestamp) table.getValueAt(row, 8);

        JPopupMenu popup = new JPopupMenu();

        JMenuItem editItem = new JMenuItem("Modifier");
        editItem.setBackground(new Color(33, 150, 243));
        editItem.setForeground(Color.WHITE);
        editItem.setFont(new Font("Arial", Font.BOLD, 14));
        editItem.addActionListener(e -> {
            // Rechercher le mandataire complet dans la base de données
            List<Mandataire> matchingMandataires = controller.searchMandataires(nom);
            if (!matchingMandataires.isEmpty()) {
                Mandataire mandataire = matchingMandataires.get(0);
                try {
                    showEditDialog(mandataire);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(ButtonEditor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        popup.add(editItem);

        JMenuItem deleteItem = new JMenuItem("Supprimer");
        deleteItem.setBackground(new Color(244, 67, 54));
        deleteItem.setForeground(Color.WHITE);
        deleteItem.setFont(new Font("Arial", Font.BOLD, 14));
        deleteItem.addActionListener(e -> {
            int confirmation = JOptionPane.showConfirmDialog(
                button,
                "Êtes-vous sûr de vouloir supprimer ce mandataire ?",
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION
            );

            if (confirmation == JOptionPane.YES_OPTION) {
                // Rechercher l'ID du mandataire
                List<Mandataire> matchingMandataires = controller.searchMandataires(nom);
                if (!matchingMandataires.isEmpty()) {
                    String id = matchingMandataires.get(0).getId();
                    controller.deleteMandataire(id);
                    panel.refreshTable();
                }
            }
        });
        popup.add(deleteItem);

        popup.show(button, 0, button.getHeight());
    }

    private void showEditDialog(Mandataire mandataire) throws ClassNotFoundException {
        Window parentWindow = SwingUtilities.getWindowAncestor(panel);
        JDialog dialog;
        if (parentWindow instanceof JFrame jFrame) {
            dialog = new JDialog(jFrame, "Modifier le mandataire", true);
        } else {
            dialog = new JDialog((Dialog) parentWindow, "Modifier le mandataire", true);
        }
        dialog.setLayout(new BorderLayout());

        JPanel form = panel.createForm(mandataire, dialog);
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