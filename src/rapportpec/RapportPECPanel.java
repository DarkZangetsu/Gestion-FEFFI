package rapportpec;

import javax.swing.*;
import java.awt.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import javaswingdev.swing.table.Table;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.border.EmptyBorder;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public final class RapportPECPanel extends JPanel {
    // Color Palette - Matching CaisseEcolePanel
    private final Color primaryColor = new Color(23, 32, 42);
    private final Color hoverColor = new Color(33, 97, 140);
    private final Color activeColor = new Color(33, 97, 140);
    private final Color otherColor = new Color(41, 128, 185);
    private final Color backgroundColor = new Color(236, 240, 241);
    private final Color textColor = new Color(44, 62, 80);
    private final Color tableBackgroundColor = new Color(247, 249, 250);

    private final RapportPECController controller;
    private final JTable table;
    private final DefaultTableModel tableModel;
    private final String planificationId;
    private JTextField searchField;

    private final String[] columns = {"Période", "Indicateurs", "Activités", "Désignation", 
                                      "Prix Unitaire", "Quantité", "Source Financement", 
                                      "Date de création", "Observation", "Actions"};

    public RapportPECPanel(String planificationId) {
        this.planificationId = planificationId;
        setLayout(new BorderLayout(0, 10));
        setBackground(backgroundColor);

        this.controller = new RapportPECController();

        // Add header banner
        JPanel headerBanner = createHeaderBanner();
        add(headerBanner, BorderLayout.NORTH);

        // Create main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(0, 10));
        mainPanel.setBackground(backgroundColor);

        // Top panel with search and actions
        JPanel topPanel = createTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Table initialization
        tableModel = createTableModel(columns);
        table = createStyledTable(columns);

        // Table panel
        JPanel tablePanel = createTablePanel();
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
        
        SwingUtilities.invokeLater(() -> {
            Window parentWindow = SwingUtilities.getWindowAncestor(RapportPECPanel.this);
            if (parentWindow instanceof JFrame frame) {
                frame.setSize(1200, 600);
                frame.setLocationRelativeTo(null);
            }
        });

        // Initial data load
        refreshTable();
    }

    private JPanel createHeaderBanner() {
        JPanel bannerPanel = new JPanel(new BorderLayout());
        bannerPanel.setBackground(primaryColor);
        bannerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Gérer les Rapports PEC");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        bannerPanel.add(titleLabel, BorderLayout.CENTER);
        return bannerPanel;
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
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(backgroundColor.darker());
            }
            @Override
            public void mouseExited(MouseEvent evt) {
                button.setBackground(backgroundColor);
            }
        });
        return button;
    }

    private DefaultTableModel createTableModel(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == columns.length - 1;
            }
        };
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout(0, 10));
        tablePanel.setBorder(new EmptyBorder(10, 20, 20, 20));

        // Table title
        JLabel tableTitle = new JLabel("Liste des Rapports PEC");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tableTitle.setForeground(textColor);
        tablePanel.add(tableTitle, BorderLayout.NORTH);

        // Table with scrollpane
        JScrollPane scrollPane = createStyledScrollPane(table);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

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

        // Table visual configuration
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

        // Column widths
        int[] columnWidths = {100, 100, 150, 150, 100, 80, 150, 150, 150, 80};
        for (int i = 0; i < columns.length; i++) {
            styledTable.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }

        // Actions column - using CaisseEcole style icon renderer and editor
        styledTable.getColumnModel().getColumn(columns.length - 1).setCellRenderer(new IconRenderer());
        styledTable.getColumnModel().getColumn(columns.length - 1).setCellEditor(
            new IconEditor(new JCheckBox(), this, controller, styledTable, planificationId));

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
        
        List<RapportPEC> rapports = controller.getAllRapports(planificationId);
        for (RapportPEC r : rapports) {
            tableModel.addRow(new Object[]{
                r.getPeriode(),
                r.getDoubleIndicateurs(),
                r.getActivites(),
                r.getDesignation(),
                r.getPrixUnitaire(),
                r.getQuantite(),
                r.getSourceFinancement(),
                r.getCreatedAt(),
                r.getObservation(),
                "Actions"
            });
        }
    }

private void showAddDialog() {
    Window parentWindow = SwingUtilities.getWindowAncestor(this);
    JDialog dialog;
        switch (parentWindow) {
            case JFrame jFrame -> dialog = new JDialog(jFrame, "Ajouter un rapport", true);
            case JDialog jDialog -> dialog = new JDialog(jDialog, "Ajouter un rapport", true);
            default -> dialog = new JDialog(new JFrame(), "Ajouter un rapport", true);
        }
    dialog.setLayout(new BorderLayout());

    JPanel form = createForm(null, dialog);
    dialog.add(form, BorderLayout.CENTER);
    dialog.pack();
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
}


  // Validateur pour les nombres décimaux (double et decimal)
class DecimalInputVerifier extends InputVerifier {
    @Override
    public boolean verify(JComponent input) {
        String text = ((JTextField) input).getText();
        if (text.isEmpty()) return true;
        try {
            Double.valueOf(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

// Validateur pour les nombres entiers
class IntegerInputVerifier extends InputVerifier {
    @Override
    public boolean verify(JComponent input) {
        String text = ((JTextField) input).getText();
        if (text.isEmpty()) return true;
        try {
            Integer.valueOf(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

// Filtre pour n'accepter que les nombres décimaux
class DecimalDocumentFilter extends DocumentFilter {
    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        StringBuilder builder = new StringBuilder(fb.getDocument().getText(0, fb.getDocument().getLength()));
        builder.insert(offset, string);
        if (isValidDecimal(builder.toString())) {
            super.insertString(fb, offset, string, attr);
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        StringBuilder builder = new StringBuilder(fb.getDocument().getText(0, fb.getDocument().getLength()));
        builder.replace(offset, offset + length, text);
        if (isValidDecimal(builder.toString())) {
            super.replace(fb, offset, length, text, attrs);
        }
    }

    private boolean isValidDecimal(String text) {
        if (text.isEmpty()) return true;
        try {
            Double.valueOf(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

// Filtre pour n'accepter que les nombres entiers
class IntegerDocumentFilter extends DocumentFilter {

        IntegerDocumentFilter() {
        }
    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        StringBuilder builder = new StringBuilder(fb.getDocument().getText(0, fb.getDocument().getLength()));
        builder.insert(offset, string);
        if (isValidInteger(builder.toString())) {
            super.insertString(fb, offset, string, attr);
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        StringBuilder builder = new StringBuilder(fb.getDocument().getText(0, fb.getDocument().getLength()));
        builder.replace(offset, offset + length, text);
        if (isValidInteger(builder.toString())) {
            super.replace(fb, offset, length, text, attrs);
        }
    }

    private boolean isValidInteger(String text) {
        if (text.isEmpty()) return true;
        try {
            Integer.valueOf(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    }

// Extension de JTextField pour les champs de longueur limitée
class LimitedTextField extends JTextField {
    public LimitedTextField(int limit) {
        ((AbstractDocument) getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) 
                    throws BadLocationException {
                if ((fb.getDocument().getLength() + string.length()) <= limit) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) 
                    throws BadLocationException {
                int newLength = fb.getDocument().getLength() - length + text.length();
                if (newLength <= limit) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
    }
}

// Méthode mise à jour pour créer le formulaire avec validation
public JPanel createForm(RapportPEC rapport, JDialog dialog) {
    JPanel form = new JPanel(new GridBagLayout());
    form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(5, 5, 5, 5);

    // Période (varchar(100))
    gbc.gridx = 0; gbc.gridy = 0;
    form.add(new JLabel("Période:"), gbc);
    LimitedTextField periodeField = new LimitedTextField(100);
    periodeField.setText(rapport != null ? rapport.getPeriode() : "");
    gbc.gridx = 1;
    form.add(periodeField, gbc);

    // Indicateurs (double)
    gbc.gridx = 0; gbc.gridy = 1;
    form.add(new JLabel("Indicateurs:"), gbc);
    JTextField indicateursField = new JTextField(20);
    indicateursField.setText(rapport != null ? String.valueOf(rapport.getDoubleIndicateurs()) : "");
    indicateursField.setInputVerifier(new DecimalInputVerifier());
    ((AbstractDocument) indicateursField.getDocument()).setDocumentFilter(new DecimalDocumentFilter());
    gbc.gridx = 1;
    form.add(indicateursField, gbc);

    // Activités (text)
    gbc.gridx = 0; gbc.gridy = 2;
    form.add(new JLabel("Activités:"), gbc);
    JTextArea activitiesArea = new JTextArea(rapport != null ? rapport.getActivites() : "", 3, 20);
    JScrollPane activitiesScroll = new JScrollPane(activitiesArea);
    gbc.gridx = 1;
    form.add(activitiesScroll, gbc);

    // Désignation (varchar(255))
    gbc.gridx = 0; gbc.gridy = 3;
    form.add(new JLabel("Désignation:"), gbc);
    LimitedTextField designationField = new LimitedTextField(255);
    designationField.setText(rapport != null ? rapport.getDesignation() : "");
    gbc.gridx = 1;
    form.add(designationField, gbc);

    // Prix Unitaire (decimal(10,2))
    gbc.gridx = 0; gbc.gridy = 4;
    form.add(new JLabel("Prix Unitaire:"), gbc);
    JTextField prixField = new JTextField(20);
    prixField.setText(rapport != null ? String.valueOf(rapport.getPrixUnitaire()) : "");
    prixField.setInputVerifier(new DecimalInputVerifier());
    ((AbstractDocument) prixField.getDocument()).setDocumentFilter(new DecimalDocumentFilter());
    gbc.gridx = 1;
    form.add(prixField, gbc);

    // Quantité (int)
    gbc.gridx = 0; gbc.gridy = 5;
    form.add(new JLabel("Quantité:"), gbc);
    JTextField quantiteField = new JTextField(20);
    quantiteField.setText(rapport != null ? String.valueOf(rapport.getQuantite()) : "");
    quantiteField.setInputVerifier(new IntegerInputVerifier());
    ((AbstractDocument) quantiteField.getDocument()).setDocumentFilter(new IntegerDocumentFilter());
    gbc.gridx = 1;
    form.add(quantiteField, gbc);

    // Source Financement (varchar(255))
    gbc.gridx = 0; gbc.gridy = 6;
    form.add(new JLabel("Source Financement:"), gbc);
    LimitedTextField sourceField = new LimitedTextField(255);
    sourceField.setText(rapport != null ? rapport.getSourceFinancement() : "");
    gbc.gridx = 1;
    form.add(sourceField, gbc);

    // Observation (varchar(200))
    gbc.gridx = 0; gbc.gridy = 7;
    form.add(new JLabel("Observation:"), gbc);
    LimitedTextField observationField = new LimitedTextField(200);
    observationField.setText(rapport != null ? rapport.getObservation() : "");
    gbc.gridx = 1;
    form.add(observationField, gbc);

    // Bouton de sauvegarde avec validation complète
    JButton saveButton = new JButton(rapport != null ? "Mettre à jour" : "Enregistrer");
    saveButton.setBackground(new Color(76, 175, 80));
    saveButton.setForeground(Color.WHITE);
    saveButton.setBorderPainted(false);
    saveButton.setFont(new Font("Arial", Font.BOLD, 14));
    gbc.gridx = 1;
    gbc.gridy = 8;
    gbc.anchor = GridBagConstraints.LINE_END;
    
    saveButton.addActionListener(e -> {
        // Validation finale avant sauvegarde
        if (!validateForm(indicateursField, prixField, quantiteField)) {
            JOptionPane.showMessageDialog(dialog,
                "Veuillez corriger les erreurs de saisie avant de sauvegarder.",
                "Erreur de validation",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double indicateurs = Double.parseDouble(indicateursField.getText());
            double prix = Double.parseDouble(prixField.getText());
            int quantite = Integer.parseInt(quantiteField.getText());

            RapportPEC newRapport = rapport != null ?
                new RapportPEC(
                    rapport.getId(),
                    planificationId,
                    periodeField.getText(),
                    indicateurs,
                    activitiesArea.getText(),
                    designationField.getText(),
                    prix,
                    quantite,
                    sourceField.getText(),
                    rapport.getCreatedAt(),
                    observationField.getText()
                ) :
                new RapportPEC(
                    UUID.randomUUID().toString(),
                    planificationId,
                    periodeField.getText(),
                    indicateurs,
                    activitiesArea.getText(),
                    designationField.getText(),
                    prix,
                    quantite,
                    sourceField.getText(),
                    new Timestamp(System.currentTimeMillis()),
                    observationField.getText()
                );

            if (rapport != null) {
                controller.updateRapport(newRapport);
            } else {
                controller.createRapport(newRapport);
            }
            dialog.dispose();
            refreshTable();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(dialog,
                "Veuillez vérifier les valeurs numériques saisies",
                "Erreur de saisie",
                JOptionPane.ERROR_MESSAGE);
        }
    });

    form.add(saveButton, gbc);
    return form;
}

private boolean validateForm(JTextField... fields) {
    for (JTextField field : fields) {
        if (!field.getInputVerifier().verify(field)) {
            return false;
        }
    }
    return true;
}

    private void performSearch() {
        String keyword = searchField.getText();
        tableModel.setRowCount(0);
        List<RapportPEC> results = controller.searchRapports(keyword, planificationId);
        for (RapportPEC r : results) {
            tableModel.addRow(new Object[]{
                r.getPeriode(),
                r.getDoubleIndicateurs(),
                r.getActivites(),
                r.getDesignation(),
                r.getPrixUnitaire(),
                r.getQuantite(),
                r.getSourceFinancement(),
                r.getCreatedAt(),
                r.getObservation(),
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
            controller.exportToPDF(filePath, planificationId);
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
    private final RapportPECPanel panel;
    private final RapportPECController controller;
    private final JTable table;
    private final String planificationId;
    private JDialog activeDialog;

    public IconEditor(JCheckBox checkBox, RapportPECPanel panel, 
                            RapportPECController controller, JTable table, String planificationId) {
        super(checkBox);
        this.panel = panel;
        this.controller = controller;
        this.table = table;
        this.planificationId = planificationId;

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
                        int row = table.getSelectedRow();
                        String activities = table.getValueAt(row, 2).toString();
                        
                        List<RapportPEC> rapports = controller.searchRapports(activities, planificationId);
                        if (!rapports.isEmpty()) {
                            RapportPEC rapport = rapports.get(0);
                            activeDialog = showEditDialog(rapport);
                            
                            // Ajouter un écouteur pour la fermeture du dialogue
                            activeDialog.addWindowListener(new java.awt.event.WindowAdapter() {
                                @Override
                                public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                                    // Restaurer l'état initial des icônes
                                    restoreMoreIcon();
                                }
                            });
                        }
                    }
                });

                deleteIcon.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                        // Centrer la boîte de dialogue
                        JOptionPane optionPane = new JOptionPane(
                            "Êtes-vous sûr de vouloir supprimer ce rapport ?",
                            JOptionPane.QUESTION_MESSAGE,
                            JOptionPane.YES_NO_OPTION
                        );
                        JDialog dialog = optionPane.createDialog(panel, "Confirmation de suppression");
                        dialog.setModal(true);
                        dialog.setLocationRelativeTo(panel); // Centrer par rapport au panel parent
                        dialog.setVisible(true);

                        Object selectedValue = optionPane.getValue();
                        if (selectedValue != null && 
                            (int) selectedValue == JOptionPane.YES_OPTION) {
                            int selectedRow = table.getSelectedRow();
                            String activities = table.getValueAt(selectedRow, 2).toString();
                            List<RapportPEC> rapports = controller.searchRapports(activities, planificationId);
                            if (!rapports.isEmpty()) {
                                controller.deleteRapport(rapports.get(0).getId());
                                panel.refreshTable();
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

    private void restoreMoreIcon() {
        actionPanel.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        actionPanel.add(moreIcon, gbc);
        actionPanel.revalidate();
        actionPanel.repaint();
    }

    private JDialog showEditDialog(RapportPEC rapport) {
        Window parentWindow = SwingUtilities.getWindowAncestor(panel);
        JDialog dialog;
        switch (parentWindow) {
            case JFrame jFrame -> dialog = new JDialog(jFrame, "Modifier le rapport", true);
            case JDialog jDialog -> dialog = new JDialog(jDialog, "Modifier le rapport", true);
            default -> dialog = new JDialog(new JFrame(), "Modifier le rapport", true);
        }
        dialog.setLayout(new BorderLayout());

        JPanel form = panel.createForm(rapport, dialog);
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