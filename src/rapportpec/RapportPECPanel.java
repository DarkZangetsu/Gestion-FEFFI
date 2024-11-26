package rapportpec;

import javax.swing.*;
import java.awt.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import javaswingdev.swing.table.Table;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public final class RapportPECPanel extends JPanel {
    private final RapportPECController controller;
    private final JTable table;
    private final DefaultTableModel tableModel;
    private JTextField searchField;
    private final String planificationId;

    public RapportPECPanel(String planificationId) throws ClassNotFoundException {
        this.planificationId = planificationId;
        setBackground(Color.WHITE);
        setLayout(new BorderLayout(0, 10));

        this.controller = new RapportPECController();
        
        // Bannière d'en-tête
        JPanel headerBanner = createHeaderBanner();
        add(headerBanner, BorderLayout.NORTH);

        // Panneau supérieur avec recherche et boutons
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Période", "Indicateurs", "Activités", "Désignation", "Prix Unitaire", 
                          "Quantité", "Source Financement", "Date de création", "Observation", "Actions"};
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
        bannerPanel.setBackground(new Color(33, 150, 243)); // Bleu
        bannerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Gérer les Rapports PEC");
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
        addButton.addActionListener(e -> showAddDialog());

        JButton exportButton = new JButton("Exporter PDF");
        exportButton.setBackground(new Color(33, 150, 243));
        exportButton.setForeground(Color.WHITE);
        exportButton.setBorderPainted(false);
        exportButton.setPreferredSize(new Dimension(150, 40));
        exportButton.setFont(new Font("Arial", Font.BOLD, 14));
        exportButton.addActionListener(e -> exportToPDF());

        actionPanel.add(addButton);
        actionPanel.add(exportButton);

        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(actionPanel, BorderLayout.EAST);

        return topPanel;
    }

    private Table createTable(String[] columns) {
        Table newTable = new Table();
        newTable.setModel(tableModel);
        
        newTable.getColumnModel().getColumn(columns.length - 1).setCellRenderer(new ButtonRenderer());
        newTable.getColumnModel().getColumn(columns.length - 1).setCellEditor(
            new ButtonEditor(new JCheckBox(), this, controller, newTable, planificationId));

        // Définir les largeurs des colonnes
        int[] widths = {100, 100, 150, 150, 100, 80, 150, 150, 150, 100};
        for (int i = 0; i < widths.length; i++) {
            newTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
    
        return newTable;
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
    private final RapportPECPanel panel;
    private final RapportPECController controller;
    private final JTable table;
    private final String planificationId;

    public ButtonEditor(JCheckBox checkBox, RapportPECPanel panel,
                       RapportPECController controller, JTable table, String planificationId) {
        super(checkBox);
        this.panel = panel;
        this.controller = controller;
        this.table = table;
        this.planificationId = planificationId;

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

        String activities = table.getValueAt(row, 2).toString();

        JPopupMenu popup = new JPopupMenu();

        JMenuItem editItem = new JMenuItem("Modifier");
        editItem.setBackground(new Color(33, 150, 243));
        editItem.setForeground(Color.WHITE);
        editItem.setFont(new Font("Arial", Font.BOLD, 14));
        editItem.addActionListener(e -> {
            List<RapportPEC> rapports = controller.searchRapports(activities, planificationId);
            if (!rapports.isEmpty()) {
                RapportPEC rapport = rapports.get(0);
                showEditDialog(rapport);
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
                "Êtes-vous sûr de vouloir supprimer ce rapport ?",
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION
            );

            if (confirmation == JOptionPane.YES_OPTION) {
                List<RapportPEC> rapports = controller.searchRapports(activities, planificationId);
                if (!rapports.isEmpty()) {
                    controller.deleteRapport(rapports.get(0).getId());
                    panel.refreshTable();
                }
            }
        });
        popup.add(deleteItem);

        popup.show(button, 0, button.getHeight());
    }

    // Et aussi mettre à jour la méthode showEditDialog dans ButtonEditor :
        private void showEditDialog(RapportPEC rapport) {
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
        }


    @Override
    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
    }
}