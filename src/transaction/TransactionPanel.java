package transaction;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.UUID;
import java.util.Date;
import javaswingdev.swing.table.Table;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public final class TransactionPanel extends JPanel {
    private final TransactionController controller;
    private final JTable table;
    private final DefaultTableModel tableModel;
    private JTextField searchField;

    public TransactionPanel() throws ClassNotFoundException {
        setBackground(Color.WHITE);
        setLayout(new BorderLayout(0, 10));

        this.controller = new TransactionController();
        
        // Bannière d'en-tête
        JPanel headerBanner = createHeaderBanner();
        add(headerBanner, BorderLayout.NORTH);

        // Panneau supérieur avec recherche et boutons
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Date", "Montant", "Type", "Description", "Créé par", "Validé par", "Actions"};
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

        JLabel titleLabel = new JLabel("Gérer Transactions");
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

        JButton addButton = new JButton("Nouvelle Transaction");
        addButton.setBackground(new Color(76, 175, 80));
        addButton.setForeground(Color.WHITE);
        addButton.setBorderPainted(false);
        addButton.setPreferredSize(new Dimension(160, 40));
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
            new ButtonEditor(new JCheckBox(), this, controller, newTable));

        // Ajustement des colonnes
        newTable.getColumnModel().getColumn(0).setPreferredWidth(100); // Date
        newTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Montant
        newTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Type
        newTable.getColumnModel().getColumn(3).setPreferredWidth(200); // Description
        newTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Créé par
        newTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Validé par
        newTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Actions
    
        return newTable;
    }

    public void refreshTable() {
        while (tableModel.getRowCount() > 0) {
            tableModel.removeRow(0);
        }
        
        List<Transaction> transactions = controller.getAllTransactions();
        for (Transaction t : transactions) {
            tableModel.addRow(new Object[]{
                t.getDate(),
                t.getMontant(),
                t.getTypeTransaction(),
                t.getDescription(),
                t.getCreePar(),
                t.getValidePar(),
                "Actions"
            });
        }
    }

    private void showAddDialog() {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog dialog;
        if (parentWindow instanceof JFrame) {
            dialog = new JDialog((JFrame) parentWindow, "Nouvelle transaction", true);
        } else {
            dialog = new JDialog((Dialog) parentWindow, "Nouvelle transaction", true);
        }
        dialog.setLayout(new BorderLayout());

        JPanel form = createForm(null, dialog);
        dialog.add(form, BorderLayout.CENTER);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    JPanel createForm(Transaction transaction, JDialog dialog) {
        JPanel form = new JPanel(new GridLayout(7, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Création des champs du formulaire
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        if (transaction != null && transaction.getDate() != null) {
            dateSpinner.setValue(transaction.getDate());
        }
        
        JTextField montantField = new JTextField(transaction != null ? String.valueOf(transaction.getMontant()) : "");
        JTextField typeField = new JTextField(transaction != null ? transaction.getTypeTransaction() : "");
        JTextField descriptionField = new JTextField(transaction != null ? transaction.getDescription() : "");
        JTextField creeParField = new JTextField(transaction != null ? transaction.getCreePar() : "");
        JTextField valideParField = new JTextField(transaction != null ? transaction.getValidePar() : "");

        // Ajout des composants au formulaire
        form.add(new JLabel("Date:"));
        form.add(dateSpinner);
        form.add(new JLabel("Montant:"));
        form.add(montantField);
        form.add(new JLabel("Type:"));
        form.add(typeField);
        form.add(new JLabel("Description:"));
        form.add(descriptionField);
        form.add(new JLabel("Créé par:"));
        form.add(creeParField);
        form.add(new JLabel("Validé par:"));
        form.add(valideParField);

        JButton saveButton = new JButton(transaction != null ? "Mettre à jour" : "Enregistrer");
        saveButton.setBackground(new Color(76, 175, 80));
        saveButton.setForeground(Color.WHITE);
        saveButton.setBorderPainted(false);
        saveButton.setFont(new Font("Arial", Font.BOLD, 14));
        
        saveButton.addActionListener(e -> {
            try {
                double montant = Double.parseDouble(montantField.getText());
                Date date = (Date) dateSpinner.getValue();
                
                if (transaction != null) {
                    // Mode modification
                    Transaction updatedTransaction = new Transaction(
                        transaction.getId(),
                        date,
                        montant,
                        typeField.getText(),
                        descriptionField.getText(),
                        creeParField.getText(),
                        valideParField.getText()
                    );
                    controller.updateTransaction(updatedTransaction);
                } else {
                    // Mode création
                    Transaction newTransaction = new Transaction(
                        UUID.randomUUID().toString(),
                        date,
                        montant,
                        typeField.getText(),
                        descriptionField.getText(),
                        creeParField.getText(),
                        valideParField.getText()
                    );
                    controller.createTransaction(newTransaction);
                }
                dialog.dispose();
                refreshTable();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Le montant doit être un nombre valide",
                    "Erreur de saisie",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        form.add(new JPanel()); // Spacer
        form.add(saveButton);

        return form;
    }

    private void performSearch() {
        String keyword = searchField.getText();
        tableModel.setRowCount(0);
        List<Transaction> results = controller.searchTransactions(keyword);
        for (Transaction t : results) {
            tableModel.addRow(new Object[]{
                t.getDate(),
                t.getMontant(),
                t.getTypeTransaction(),
                t.getDescription(),
                t.getCreePar(),
                t.getValidePar(),
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
            controller.exportTransactionsToPDF(filePath);
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
    private final TransactionPanel panel;
    private final TransactionController controller;
    private final JTable table;

    public ButtonEditor(JCheckBox checkBox, TransactionPanel panel,
                       TransactionController controller, JTable table) {
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

        Date date = (Date) table.getValueAt(row, 0);
        double montant = (double) table.getValueAt(row, 1);
        String type = table.getValueAt(row, 2).toString();
        String description = table.getValueAt(row, 3).toString();
        String creePar = table.getValueAt(row, 4).toString();
        String validePar = table.getValueAt(row, 5).toString();

        JPopupMenu popup = new JPopupMenu();

        JMenuItem editItem = new JMenuItem("Modifier");
        editItem.setBackground(new Color(33, 150, 243));
        editItem.setForeground(Color.WHITE);
        editItem.setFont(new Font("Arial", Font.BOLD, 14));
        editItem.addActionListener(e -> {
            List<Transaction> transactions = controller.searchTransactions(description);
            if (!transactions.isEmpty()) {
                Transaction transaction = transactions.get(0);
                showEditDialog(transaction);
            }
        });
        popup.add(editItem);

        JMenuItem deleteItem = new JMenuItem("Supprimer");
       deleteItem.setBackground(new Color(244, 67, 54)); // Rouge
        deleteItem.setForeground(Color.WHITE);
        deleteItem.setFont(new Font("Arial", Font.BOLD, 14));
        deleteItem.addActionListener(e -> {
            int confirmation = JOptionPane.showConfirmDialog(
                button,
                "Êtes-vous sûr de vouloir supprimer cette transaction ?",
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION
            );

            if (confirmation == JOptionPane.YES_OPTION) {
                List<Transaction> transactions = controller.searchTransactions(description);
                if (!transactions.isEmpty()) {
                    String id = transactions.get(0).getId();
                    controller.deleteTransaction(id);
                    panel.refreshTable();
                }
            }
        });
        popup.add(deleteItem);

        popup.show(button, 0, button.getHeight());
    }

    private void showEditDialog(Transaction transaction) {
        Window parentWindow = SwingUtilities.getWindowAncestor(panel);
        JDialog dialog;
        if (parentWindow instanceof JFrame) {
            dialog = new JDialog((JFrame) parentWindow, "Modifier la transaction", true);
        } else {
            dialog = new JDialog((Dialog) parentWindow, "Modifier la transaction", true);
        }
        dialog.setLayout(new BorderLayout());

        JPanel form = panel.createForm(transaction, dialog);
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