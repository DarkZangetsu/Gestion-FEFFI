package caisseecole;

import javax.swing.*;
import java.awt.*;
import javaswingdev.swing.table.Table;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class GlobalCaisseEcolePanel extends JPanel {
    private final GlobalCaisseEcoleController controller;
    private final Table table;
    private final DefaultTableModel tableModel;
    private JTextField searchField;
    private JLabel totalLabel;

    public GlobalCaisseEcolePanel() {
        setBackground(Color.WHITE);
        setLayout(new BorderLayout(0, 10));

        this.controller = new GlobalCaisseEcoleController();
        
        // Bannière d'en-tête
        JPanel headerBanner = createHeaderBanner();
        add(headerBanner, BorderLayout.NORTH);

        // Panneau supérieur avec recherche et export
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Établissement", "Raison", "Note", "Montant", "Date"};
        tableModel = new DefaultTableModel(columns, 0);

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

        JLabel titleLabel = new JLabel("Vue Globale des Caisses École");
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

        // Panneau d'export
        JPanel exportPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        exportPanel.setBackground(Color.WHITE);

        JButton exportButton = new JButton("Exporter PDF");
        exportButton.setBackground(new Color(33, 150, 243));
        exportButton.setForeground(Color.WHITE);
        exportButton.setBorderPainted(false);
        exportButton.setPreferredSize(new Dimension(150, 40));
        exportButton.setFont(new Font("Arial", Font.BOLD, 14));
        exportButton.addActionListener(e -> exportToPDF());

        exportPanel.add(exportButton);

        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(exportPanel, BorderLayout.EAST);

        return topPanel;
    }

    private JPanel createTotalPanel() {
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.setBackground(Color.WHITE);
        totalPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        totalLabel = new JLabel("Total Global: 0.00 Ar");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalPanel.add(totalLabel);

        return totalPanel;
    }

    private Table createTable(String[] columns) {
        Table newTable = new Table();
        newTable.setModel(tableModel);
        return newTable;
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