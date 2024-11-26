package caisseecole;

import javax.swing.*;
import java.awt.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import javaswingdev.swing.table.Table;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class CaisseEcolePanel extends JPanel {
    private final CaisseEcoleController controller;
    private final Table table;
    private final DefaultTableModel tableModel;
    private final String etablissementId;
    private JTextField searchField;
    private JLabel totalLabel;

    public CaisseEcolePanel(String etablissementId) {
        this.etablissementId = etablissementId;
        setBackground(Color.WHITE);
        setLayout(new BorderLayout(0, 10));

        this.controller = new CaisseEcoleController();
        
        // Bannière d'en-tête
        JPanel headerBanner = createHeaderBanner();
        add(headerBanner, BorderLayout.NORTH);

        // Panneau supérieur avec recherche et boutons
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Raison", "Note", "Montant", "Date", "Actions"};
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

        // Panel du total
        JPanel totalPanel = createTotalPanel();
        add(totalPanel, BorderLayout.SOUTH);

        // Chargement initial des données
        refreshTable();
    }
    
    private JPanel createHeaderBanner() {
        JPanel bannerPanel = new JPanel(new BorderLayout());
        bannerPanel.setBackground(new Color(76, 175, 80));
        bannerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Gérer Caisse École");
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

    private JPanel createTotalPanel() {
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.setBackground(Color.WHITE);
        totalPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        totalLabel = new JLabel("Total: 0.00 Ar");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalPanel.add(totalLabel);

        return totalPanel;
    }

    private Table createTable(String[] columns) {
        Table newTable = new Table();
        newTable.setModel(tableModel);
        
        newTable.getColumnModel().getColumn(columns.length - 1).setCellRenderer(new ButtonRenderer());
        newTable.getColumnModel().getColumn(columns.length - 1).setCellEditor(
            new ButtonEditor(new JCheckBox(), this, controller, newTable, etablissementId));

        return newTable;
    }

    public void refreshTable() {
        while (tableModel.getRowCount() > 0) {
            tableModel.removeRow(0);
        }
        
        List<CaisseEcole> caisses = controller.getCaissesByEtablissement(etablissementId);
        double total = 0;
        
        for (CaisseEcole caisse : caisses) {
            tableModel.addRow(new Object[]{
                caisse.getRaison(),
                caisse.getNote(),
                String.format("%.2f Ar", caisse.getMontant()),
                caisse.getCreatedAt(),
                "Actions"
            });
            total += caisse.getMontant();
        }
        
        totalLabel.setText(String.format("Total: %.2f Ar", total));
    }

    private void showAddDialog() {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog((Window) parentWindow, "Ajouter une caisse", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout());

        JPanel form = createForm(null, dialog);
        dialog.add(form, BorderLayout.CENTER);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    JPanel createForm(CaisseEcole caisse, JDialog dialog) {
        JPanel form = new JPanel(new GridLayout(4, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField raisonField = new JTextField(caisse != null ? caisse.getRaison() : "");
        JTextField montantField = new JTextField(caisse != null ? String.valueOf(caisse.getMontant()) : "");
        JTextArea noteArea = new JTextArea(caisse != null ? caisse.getNote() : "");
        noteArea.setLineWrap(true);
        noteArea.setWrapStyleWord(true);
        JScrollPane noteScroll = new JScrollPane(noteArea);

        form.add(new JLabel("Raison:"));
        form.add(raisonField);
        form.add(new JLabel("Montant (Ar):"));
        form.add(montantField);
        form.add(new JLabel("Note:"));
        form.add(noteScroll);

        JButton saveButton = new JButton(caisse != null ? "Mettre à jour" : "Enregistrer");
        saveButton.setBackground(new Color(76, 175, 80));
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(e -> {
            try {
                double montant = Double.parseDouble(montantField.getText().replace(",", "."));
                
                if (caisse != null) {
                    CaisseEcole updatedCaisse = new CaisseEcole(
                        caisse.getId(),
                        etablissementId,
                        montant,
                        caisse.getCreatedAt(),
                        raisonField.getText(),
                        noteArea.getText()
                    );
                    controller.updateCaisseEcole(updatedCaisse);
                } else {
                    CaisseEcole newCaisse = new CaisseEcole(
                        UUID.randomUUID().toString(),
                        etablissementId,
                        montant,
                        new Timestamp(System.currentTimeMillis()),
                        raisonField.getText(),
                        noteArea.getText()
                    );
                    controller.createCaisseEcole(newCaisse);
                }
                dialog.dispose();
                refreshTable();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Le montant doit être un nombre valide",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        form.add(new JPanel());
        form.add(saveButton);

        return form;
    }

    private void performSearch() {
        String keyword = searchField.getText();
        tableModel.setRowCount(0);
        
        List<CaisseEcole> results = controller.searchCaisseEcole(keyword, etablissementId);
        double total = 0;
        
        for (CaisseEcole caisse : results) {
            tableModel.addRow(new Object[]{
                caisse.getRaison(),
                caisse.getNote(),
                String.format("%.2f Ar", caisse.getMontant()),
                caisse.getCreatedAt(),
                "Actions"
            });
            total += caisse.getMontant();
        }
        
        totalLabel.setText(String.format("Total: %.2f Ar", total));
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
            controller.exportToPDF(filePath, etablissementId);
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
    private final CaisseEcolePanel panel;
    private final CaisseEcoleController controller;
    private final JTable table;
    private final String etablissementId;

    public ButtonEditor(JCheckBox checkBox, CaisseEcolePanel panel,
                       CaisseEcoleController controller, JTable table,
                       String etablissementId) {
        super(checkBox);
        this.panel = panel;
        this.controller = controller;
        this.table = table;
        this.etablissementId = etablissementId;

        button = new JButton();
        button.setOpaque(true);
        button.setBackground(new Color(33, 150, 243));
        button.setForeground(Color.WHITE);
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

        String raison = table.getValueAt(row, 0).toString();
        List<CaisseEcole> caisses = controller.searchCaisseEcole(raison, etablissementId);
        if (caisses.isEmpty()) return;
        
        CaisseEcole caisse = caisses.get(0);

        JPopupMenu popup = new JPopupMenu();

        JMenuItem editItem = new JMenuItem("Modifier");
        editItem.setBackground(new Color(33, 150, 243));
        editItem.setForeground(Color.WHITE);
        editItem.addActionListener(e -> showEditDialog(caisse));
        popup.add(editItem);

        JMenuItem deleteItem = new JMenuItem("Supprimer");
        deleteItem.setBackground(new Color(244, 67, 54));
        deleteItem.setForeground(Color.WHITE);
        deleteItem.addActionListener(e -> {
            int confirmation = JOptionPane.showConfirmDialog(
                button,
                "Êtes-vous sûr de vouloir supprimer cette caisse ?",
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION
            );

            if (confirmation == JOptionPane.YES_OPTION) {
                controller.deleteCaisseEcole(caisse.getId());
                panel.refreshTable();
            }
       });

        popup.show(button, 0, button.getHeight());
    }

    private void showEditDialog(CaisseEcole caisse) {
        Window parentWindow = SwingUtilities.getWindowAncestor(panel);
        JDialog dialog = new JDialog((Window) parentWindow, "Modifier la caisse", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout());

        JPanel form = panel.createForm(caisse, dialog);
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