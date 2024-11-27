package caisseecole;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import javaswingdev.swing.table.Table;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.border.EmptyBorder;

public class GlobalCaisseEcolePanel extends JPanel {
    // Color Palette
    private final Color primaryColor = new Color(23, 32, 42);
    private final Color hoverColor = new Color(33, 97, 140);
    private final Color activeColor = new Color(33, 97, 140);
    private final Color otherColor = new Color(41, 128, 185);
    private final Color backgroundColor = new Color(236, 240, 241);
    private final Color textColor = new Color(44, 62, 80);
    private final Color tableBackgroundColor = new Color(247, 249, 250);

    private final GlobalCaisseEcoleController controller;
    private final Table table;
    private final DefaultTableModel tableModel;
    private JTextField searchField;
    private JLabel totalLabel;

    private final String[] columns = {"Établissement", "Raison", "Note", "Montant", "Date"};

    public GlobalCaisseEcolePanel() {
        setLayout(new BorderLayout(0, 10));
        setBackground(backgroundColor);

        this.controller = new GlobalCaisseEcoleController();

        // Header Banner
        JPanel headerBanner = createHeaderBanner();
        add(headerBanner, BorderLayout.NORTH);

        // Main Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(0, 10));
        mainPanel.setBackground(backgroundColor);

        // Top Panel
        JPanel topPanel = createTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Table Model and Table
        tableModel = createTableModel(columns);
        table = createStyledTable(columns);

        // Table Panel
        JPanel tablePanel = createTablePanel();
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        // Charger les données initiales
        refreshTable();
    }

    private JPanel createHeaderBanner() {
        JPanel bannerPanel = new JPanel(new BorderLayout());
        bannerPanel.setBackground(primaryColor);
        bannerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Vue Globale des Caisses École");
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

        JButton exportButton = createStyledButton("Exporter PDF", otherColor);
        exportButton.addActionListener(e -> exportToPDF());

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
        return new DefaultTableModel(columns, 0);
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout(0, 10));
        tablePanel.setBorder(new EmptyBorder(10, 20, 20, 20));

        // Total Label
        totalLabel = new JLabel("Total Global: 0.00 Ar");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        totalLabel.setForeground(textColor);
        tablePanel.add(totalLabel, BorderLayout.SOUTH);

        // Titre au-dessus du tableau
        JLabel tableTitle = new JLabel("Liste des Caisses École");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tableTitle.setForeground(textColor);
        tablePanel.add(tableTitle, BorderLayout.NORTH);

        // Tableau
        Table styledTable = createStyledTable(columns);
        JScrollPane scrollPane = createStyledScrollPane(styledTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private Table createStyledTable(String[] columns) {
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
        int[] columnWidths = {150, 200, 100, 100, 100};
        for (int i = 0; i < columns.length; i++) {
            styledTable.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }

        return styledTable;
    }

    private JScrollPane createStyledScrollPane(Table table) {
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
        
        List<GlobalCaisseEntry> caisses = controller.getAllCaisses();
        double total = 0;
        
        for (GlobalCaisseEntry entry : caisses) {
            tableModel.addRow(new Object[]{
                entry.getEtablissementNom(),
                entry.getRaison(),
                entry.getNote(),
                String.format("%.2f Ar", entry.getMontant()),
                entry.getCreatedAt()
            });
            total += entry.getMontant();
        }
        
        totalLabel.setText(String.format("Total Global: %.2f Ar", total));
    }

    private void performSearch() {
        String keyword = searchField.getText();
        tableModel.setRowCount(0);
        
        List<GlobalCaisseEntry> results = controller.searchGlobalCaisse(keyword);
        double total = 0;
        
        for (GlobalCaisseEntry entry : results) {
            tableModel.addRow(new Object[]{
                entry.getEtablissementNom(),
                entry.getRaison(),
                entry.getNote(),
                String.format("%.2f Ar", entry.getMontant()),
                entry.getCreatedAt()
            });
            total += entry.getMontant();
        }
        
        totalLabel.setText(String.format("Total Global: %.2f Ar", total));
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