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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.border.EmptyBorder;
import com.toedter.calendar.JDateChooser;
import etablissement.Etablissement;
import etablissement.EtablissementController;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import mandataire.Mandataire;

public class MandatairePanel extends JPanel {
    // Color Palette (matching EtablissementPanel)
    private final Color primaryColor = new Color(23, 32, 42);
    private final Color hoverColor = new Color(33, 97, 140);
    private final Color activeColor = new Color(33, 97, 140);
    private final Color otherColor = new Color(41, 128, 185);
    private final Color backgroundColor = new Color(236, 240, 241);
    private final Color textColor = new Color(44, 62, 80);
    private final Color tableBackgroundColor = new Color(247, 249, 250);

    private final MandataireController controller;
    private final JTable table;
    private final DefaultTableModel tableModel;
    private JTextField searchField;

    private final String[] columns = {"Nom", "Fonction", "Date Mandat", "Code École", "CIN", 
                                      "Contact", "Email", "Établissement", "Date de création", "Actions"};

    public MandatairePanel() throws ClassNotFoundException {
        setLayout(new BorderLayout(0, 10)); 
        setBackground(backgroundColor);

        this.controller = new MandataireController();

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

        // Add main panel to center of MandatairePanel
        add(mainPanel, BorderLayout.CENTER);

        // Load initial data
        refreshTable();
    }
    
    private JPanel createHeaderBanner() {
        JPanel bannerPanel = new JPanel(new BorderLayout());
        bannerPanel.setBackground(primaryColor);
        bannerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Gérer Mandataires");
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
        addButton.addActionListener(e -> {
            try {
                showAddDialog();
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(MandatairePanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

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
        JLabel tableTitle = new JLabel("Liste des Mandataires");
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
        int[] columnWidths = {100, 100, 100, 80, 80, 100, 150, 150, 120, 80};
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
    saveButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
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
            
            try {
                if (mandataire != null) {
                    // Mode modification
                    Mandataire updatedMandataire = new Mandataire(
                            mandataire.getId(), // Utilisez directement l'ID existant du mandataire
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

class IconRenderer extends JPanel implements TableCellRenderer {
    private final JLabel moreIcon;

    public IconRenderer() {
        setLayout(new GridBagLayout());
        setOpaque(true);
        
        // Charger l'icône à partir du fichier
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
    private final MandatairePanel panel;
    private final MandataireController controller;
    private final JTable table;
    private JDialog activeDialog;
    private Mandataire currentMandataire;

    public IconEditor(JCheckBox checkBox, MandatairePanel panel,
                     MandataireController controller, JTable table) {
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

        // Récupérer directement les données complètes du mandataire
        currentMandataire = extractMandataireFromRow(row);

        actionPanel.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        actionPanel.add(moreIcon, gbc);

        moreIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                actionPanel.removeAll();
                
                // Disposition centrée pour les icônes edit et delete
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.insets = new Insets(0, 5, 0, 5); // Espacement horizontal
                gbc.anchor = GridBagConstraints.CENTER;
                
                actionPanel.add(editIcon, gbc);
                gbc.gridx = 1;
                actionPanel.add(deleteIcon, gbc);
                
                actionPanel.revalidate();
                actionPanel.repaint();

                // Gestion des actions pour les icônes
                editIcon.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                        if (currentMandataire != null) {
                            try {
                                activeDialog = showEditDialog(currentMandataire);
                                
                                // Ajouter un écouteur pour la fermeture du dialogue
                                activeDialog.addWindowListener(new java.awt.event.WindowAdapter() {
                                    @Override
                                    public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                                        // Restaurer l'état initial des icônes
                                        restoreMoreIcon();
                                    }
                                });
                            } catch (ClassNotFoundException e) {
                                JOptionPane.showMessageDialog(panel, 
                                    "Erreur lors de l'ouverture du formulaire de modification : " + e.getMessage(), 
                                    "Erreur", 
                                    JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                });

                deleteIcon.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                        // Centrer la boîte de dialogue
                        int confirmation = JOptionPane.showConfirmDialog(
                            actionPanel,
                            "Êtes-vous sûr de vouloir supprimer ce mandataire ?",
                            "Confirmation de suppression",
                            JOptionPane.YES_NO_OPTION
                        );

                        if (confirmation == JOptionPane.YES_OPTION && currentMandataire != null) {
                            try {
                                // Utiliser directement l'ID du mandataire récupéré
                                controller.deleteMandataire(currentMandataire.getId());
                                panel.refreshTable();
                            } catch (Exception e) {
                                JOptionPane.showMessageDialog(panel, 
                                    "Erreur lors de la suppression : " + e.getMessage(), 
                                    "Erreur", 
                                    JOptionPane.ERROR_MESSAGE);
                            }
                        }

                        // Restaurer l'état initial des icônes
                        restoreMoreIcon();
                    }
                });
            }
        });

        return actionPanel;
    }

    // Méthode pour extraire directement le Mandataire de la ligne
    private Mandataire extractMandataireFromRow(int row) {
    if (row < 0 || row >= table.getRowCount()) {
        return null;
    }

    try {
        
        String nom = table.getValueAt(row, 0).toString();
        String fonction = table.getValueAt(row, 1).toString();
        Date dateMandat = (Date) table.getValueAt(row, 2);
        String codeEcole = table.getValueAt(row, 3).toString();
        String cin = table.getValueAt(row, 4).toString();
        String contact = table.getValueAt(row, 5).toString();
        String email = table.getValueAt(row, 6).toString();
        String etablissement = table.getValueAt(row, 7).toString();
        Timestamp createdAt = (Timestamp) table.getValueAt(row, 8);

        // Récupérer l'ID depuis le contrôleur en utilisant les autres attributs
        String mandataireId = controller.findMandataireIdByDetails(nom, cin, etablissement);

        if (mandataireId == null) {
            throw new Exception("Impossible de trouver l'ID du mandataire");
        }

        // Créer un mandataire avec toutes les informations, y compris l'ID
        Mandataire mandataire = new Mandataire(
            mandataireId, 
            nom,
            fonction,
            dateMandat,
            codeEcole,
            cin,
            contact,
            email,
            etablissement,
            createdAt
        );

        return mandataire;
    } catch (Exception e) {
        System.err.println("Erreur lors de l'extraction du mandataire : " + e.getMessage());
        return null;
    }
}


    private void restoreMoreIcon() {
        actionPanel.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        actionPanel.add(moreIcon, gbc);
        actionPanel.revalidate();
        actionPanel.repaint();
    }

    private JDialog showEditDialog(Mandataire mandataire) throws ClassNotFoundException {
        Window parentWindow = SwingUtilities.getWindowAncestor(panel);
        JDialog dialog;
        if (parentWindow instanceof JFrame) {
            dialog = new JDialog((JFrame) parentWindow, "Modifier le mandataire", true);
        } else if (parentWindow instanceof Dialog) {
            dialog = new JDialog((Dialog) parentWindow, "Modifier le mandataire", true);
        } else {
            dialog = new JDialog(); // Fallback
            dialog.setModal(true);
            dialog.setTitle("Modifier le mandataire");
        }
        dialog.setLayout(new BorderLayout());

        JPanel form = panel.createForm(mandataire, dialog);
        dialog.add(form, BorderLayout.CENTER);
        dialog.pack();
        dialog.setLocationRelativeTo(panel);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
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