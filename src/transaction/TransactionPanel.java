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
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;

public final class TransactionPanel extends JPanel {
    // Color Palette (unchanged)
    private final Color primaryColor = new Color(23, 32, 42);
    private final Color hoverColor = new Color(33, 97, 140);
    private final Color activeColor = new Color(33, 97, 140);
    private final Color otherColor = new Color(41, 128, 185);
    private final Color backgroundColor = new Color(236, 240, 241);
    private final Color textColor = new Color(44, 62, 80);
    private final Color tableBackgroundColor = new Color(247, 249, 250);

    private final TransactionController controller;
    private final JTable table;
    private final DefaultTableModel tableModel;
    private JTextField searchField;

    private final String[] columns = {"Date", "Montant", "Type", "Description", "Créé par", "Validé par", "Actions"};

    public TransactionPanel() throws ClassNotFoundException {
    setLayout(new BorderLayout(0, 10)); // Définit le layout principal
    setBackground(backgroundColor); // Définit la couleur de fond

    this.controller = new TransactionController(); // Initialise le contrôleur

    // Ajouter la bannière d'en-tête
    JPanel headerBanner = createHeaderBanner();
    add(headerBanner, BorderLayout.NORTH);

    // Crée un panneau principal pour contenir les parties supérieures et le tableau
    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BorderLayout(0, 10)); // Layout vertical avec espacement
    mainPanel.setBackground(backgroundColor); // Appliquer la couleur de fond

    // Ajouter le panneau supérieur (recherche et actions)
    JPanel topPanel = createTopPanel();
    mainPanel.add(topPanel, BorderLayout.NORTH);

    // Initialisation du modèle et du tableau
    tableModel = createTableModel(columns);
    table = createStyledTable(columns);

    // Ajouter le panneau contenant le tableau
    JScrollPane scrollPane = createStyledScrollPane(table);
    JPanel tablePanel = createTablePanel();
    tablePanel.add(scrollPane); // Ajouter le JScrollPane contenant le tableau
    mainPanel.add(tablePanel, BorderLayout.CENTER);

    // Ajouter le panneau principal au centre du `TransactionPanel`
    add(mainPanel, BorderLayout.CENTER);

    // Charger les données initiales dans le tableau
    refreshTable();
}


    
    private JPanel createHeaderBanner() {
        JPanel bannerPanel = new JPanel(new BorderLayout());
        bannerPanel.setBackground(primaryColor);
        bannerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Gestion des Transactions");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        bannerPanel.add(titleLabel, BorderLayout.CENTER);
        return bannerPanel;
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout(10, 10)); // Moins d'espace
        topPanel.setBackground(backgroundColor);
        topPanel.setBorder(new EmptyBorder(5, 20, 5, 20)); // Réduire la bordure

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

        JButton addButton = createStyledButton("Nouvelle Transaction", activeColor);
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

        // Titre au-dessus du tableau
        JLabel tableTitle = new JLabel("Liste des Transactions");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tableTitle.setForeground(textColor);
        tablePanel.add(tableTitle, BorderLayout.NORTH);

        // Tableau de transactions
        JTable styledTable = createStyledTable(columns);
        JScrollPane scrollPane = createStyledScrollPane(styledTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }
    
      private JTable createStyledTable(String[] columns) {
        Table styledTable = new Table();
        styledTable.setModel(tableModel);
        
        // Centrer le texte dans toutes les cellules
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < styledTable.getColumnCount(); i++) {
            styledTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Configuration visuelle du tableau
        styledTable.setBackground(Color.WHITE);
        styledTable.setOpaque(true);
        styledTable.setSelectionBackground(hoverColor);
        styledTable.setSelectionForeground(Color.WHITE);
        styledTable.setRowHeight(40);
        styledTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // En-tête du tableau
        styledTable.getTableHeader().setBackground(primaryColor);
        styledTable.getTableHeader().setForeground(Color.WHITE);
        styledTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));

        // Configuration des largeurs de colonnes
        int[] columnWidths = {100, 100, 100, 250, 120, 120, 80};
        for (int i = 0; i < columns.length; i++) {
            styledTable.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }

        // Colonne d'actions
        styledTable.getColumnModel().getColumn(columns.length - 1).setCellRenderer(new IconRenderer());
        styledTable.getColumnModel().getColumn(columns.length - 1).setCellEditor(
            new IconEditor(new JCheckBox(), this, controller, styledTable));

        return styledTable;
    }

    private JScrollPane createStyledScrollPane(JTable table) {
        JScrollPane scrollPane = new JScrollPane(table);
        
        // Arrière-plan personnalisé pour le scroll pane
        scrollPane.getViewport().setBackground(tableBackgroundColor);
        scrollPane.setBackground(tableBackgroundColor);
        
        // Bordure avec ombre subtile
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 20, 20, 20),
            BorderFactory.createLineBorder(new Color(220, 230, 240), 1)
        ));

        // Ajouter un effet d'ombre
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
        if (parentWindow instanceof JFrame jFrame) {
            dialog = new JDialog(jFrame, "Nouvelle transaction", true);
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
    JPanel formContainer = new JPanel(new BorderLayout());
    
    // Créer le nouveau formulaire moderne
    ModernTransactionForm modernForm = new ModernTransactionForm(transaction, dialog);
    formContainer.add(modernForm, BorderLayout.CENTER);

    // Bouton de sauvegarde personnalisé
    JButton saveButton = new JButton(transaction != null ? "Mettre à jour" : "Enregistrer");
    saveButton.setBackground(activeColor);
    saveButton.setForeground(Color.WHITE);
    saveButton.setBorderPainted(false);
    saveButton.setFont(new Font("Arial", Font.BOLD, 14));
    
    saveButton.addActionListener(e -> {
        try {
            Transaction updatedTransaction = modernForm.saveTransaction(transaction);
            if (updatedTransaction != null) {
                if (transaction != null) {
                    // Mode modification
                    controller.updateTransaction(updatedTransaction);
                } else {
                    // Mode création
                    controller.createTransaction(updatedTransaction);
                }
                dialog.dispose();
                refreshTable();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialog,
                "Erreur lors de l'enregistrement de la transaction",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    });

    // Ajouter le bouton de sauvegarde
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    buttonPanel.add(saveButton);
    formContainer.add(buttonPanel, BorderLayout.SOUTH);

    return formContainer;
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

class IconRenderer extends JPanel implements TableCellRenderer {
    private final JLabel moreIcon;

    public IconRenderer() {
        setLayout(new GridBagLayout()); // Utiliser GridBagLayout pour un centrage précis
        setOpaque(true);
        
        // Charger l'icône à partir du fichier
        ImageIcon icon = loadScaledIcon("src/icons/more.png", 24, 24);
        moreIcon = new JLabel(icon);
        moreIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        add(moreIcon); // L'icône sera automatiquement centrée
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
    private final TransactionPanel panel;
    private final TransactionController controller;
    private JDialog activeDialog;

    public IconEditor(JCheckBox checkBox, TransactionPanel panel,
                     TransactionController controller, JTable table) {
        super(checkBox);
        this.panel = panel;
        this.controller = controller;

        actionPanel = new JPanel(new GridBagLayout()); // Utiliser GridBagLayout pour un centrage précis
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
                        int selectedRow = table.getSelectedRow();
                        String description = table.getValueAt(selectedRow, 3).toString();
                        List<Transaction> transactions = controller.searchTransactions(description);
                        if (!transactions.isEmpty()) {
                            activeDialog = showEditDialog(transactions.get(0));
                            
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
                            "Êtes-vous sûr de vouloir supprimer cette transaction ?",
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
                            String description = table.getValueAt(selectedRow, 3).toString();
                            List<Transaction> transactions = controller.searchTransactions(description);
                            if (!transactions.isEmpty()) {
                                String id = transactions.get(0).getId();
                                controller.deleteTransaction(id);
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

    private JDialog showEditDialog(Transaction transaction) {
        Window parentWindow = SwingUtilities.getWindowAncestor(panel);
        JDialog dialog;
        if (parentWindow instanceof JFrame jFrame) {
            dialog = new JDialog(jFrame, "Modifier la transaction", true);
        } else {
            dialog = new JDialog((Dialog) parentWindow, "Modifier la transaction", true);
        }
        dialog.setLayout(new BorderLayout());

        JPanel form = panel.createForm(transaction, dialog);
        dialog.add(form, BorderLayout.CENTER);
        dialog.pack();
        dialog.setLocationRelativeTo(panel); // Centrer par rapport au panel parent
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