package planificationpec;

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
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import rapportpec.RapportPECPanel;

public class PlanificationPECPanel extends JPanel {
    // Color Palette (matching EtablissementPanel)
    private final Color primaryColor = new Color(23, 32, 42);
    private final Color hoverColor = new Color(33, 97, 140);
    private final Color activeColor = new Color(33, 97, 140);
    private final Color otherColor = new Color(41, 128, 185);
    private final Color backgroundColor = new Color(236, 240, 241);
    private final Color textColor = new Color(44, 62, 80);
    private final Color tableBackgroundColor = new Color(247, 249, 250);

    private final PlanificationPECController controller;
    private final JTable table;
    private final DefaultTableModel tableModel;
    private JTextField searchField;

    public PlanificationPECPanel() throws ClassNotFoundException {
        setLayout(new BorderLayout(0, 10)); 
        setBackground(backgroundColor);

        this.controller = new PlanificationPECController();
        
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
        String[] columns = {"Date Début", "Date Fin", "Activités", "Date de création", "Actions"};
        tableModel = createTableModel(columns);
        table = createStyledTable(columns);

        // Add table panel
        JScrollPane scrollPane = createStyledScrollPane(table);
        JPanel tablePanel = createTablePanel();
        tablePanel.add(scrollPane);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        // Add main panel to center of PlanificationPECPanel
        add(mainPanel, BorderLayout.CENTER);

        // Load initial data
        refreshTable();
    }
    
    private JPanel createHeaderBanner() {
        JPanel bannerPanel = new JPanel(new BorderLayout());
        bannerPanel.setBackground(primaryColor);
        bannerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Gérer les Planifications PEC");
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

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout(0, 10));
        tablePanel.setBorder(new EmptyBorder(10, 20, 20, 20));

        // Title above table
        JLabel tableTitle = new JLabel("Liste des Planifications PEC");
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
        int[] columnWidths = {150, 150, 200, 150, 80};
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
        while (tableModel.getRowCount() > 0) {
            tableModel.removeRow(0);
        }
        
        List<PlanificationPEC> planifications = controller.getAllPlanifications();
        for (PlanificationPEC p : planifications) {
            tableModel.addRow(new Object[]{
                p.getDateDebut(),
                p.getDateFin(),
                p.getActivities(),
                p.getCreatedAt(),
                "Actions"
            });
        }
    }

    private void showAddDialog() {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog dialog;
        if (parentWindow instanceof JFrame jFrame) {
            dialog = new JDialog(jFrame, "Ajouter une planification", true);
        } else {
            dialog = new JDialog((Dialog) parentWindow, "Ajouter une planification", true);
        }
        dialog.setLayout(new BorderLayout());

        JPanel form = createForm(null, dialog);
        dialog.add(form, BorderLayout.CENTER);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    JPanel createForm(PlanificationPEC planification, JDialog dialog) {
        JPanel form = new JPanel(new GridLayout(4, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JDateChooser dateDebutChooser = new JDateChooser();
        JDateChooser dateFinChooser = new JDateChooser();
        if (planification != null) {
            dateDebutChooser.setDate(planification.getDateDebut());
            dateFinChooser.setDate(planification.getDateFin());
        }

        JTextArea activitiesArea = new JTextArea(planification != null ? planification.getActivities() : "");
        activitiesArea.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane activitiesScroll = new JScrollPane(activitiesArea);

        form.add(new JLabel("Date de début:"));
        form.add(dateDebutChooser);
        form.add(new JLabel("Date de fin:"));
        form.add(dateFinChooser);
        form.add(new JLabel("Activités:"));
        form.add(activitiesScroll);

        JButton saveButton = new JButton(planification != null ? "Mettre à jour" : "Enregistrer");
        saveButton.setBackground(new Color(76, 175, 80));
        saveButton.setForeground(Color.WHITE);
        saveButton.setBorderPainted(false);
        saveButton.setFont(new Font("Arial", Font.BOLD, 14));
        saveButton.addActionListener(e -> {
            Date dateDebut = new Date(dateDebutChooser.getDate().getTime());
            Date dateFin = new Date(dateFinChooser.getDate().getTime());
            
            if (planification != null) {
                PlanificationPEC updatedPlanification = new PlanificationPEC(
                    planification.getId(),
                    dateDebut,
                    dateFin,
                    activitiesArea.getText(),
                    planification.getCreatedAt()
                );
                controller.updatePlanification(updatedPlanification);
            } else {
                PlanificationPEC newPlanification = new PlanificationPEC(
                    UUID.randomUUID().toString(),
                    dateDebut,
                    dateFin,
                    activitiesArea.getText(),
                    new Timestamp(System.currentTimeMillis())
                );
                controller.createPlanification(newPlanification);
            }
            dialog.dispose();
            refreshTable();
        });

        form.add(new JPanel());
        form.add(saveButton);

        return form;
    }

    private void performSearch() {
        String keyword = searchField.getText();
        tableModel.setRowCount(0);
        List<PlanificationPEC> results = controller.searchPlanifications(keyword);
        for (PlanificationPEC p : results) {
            tableModel.addRow(new Object[]{
                p.getDateDebut(),
                p.getDateFin(),
                p.getActivities(),
                p.getCreatedAt(),
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
    private final PlanificationPECPanel panel;
    private final PlanificationPECController controller;
    private JDialog activeDialog;

    public IconEditor(JCheckBox checkBox, PlanificationPECPanel panel,
                       PlanificationPECController controller, JTable table) {
        super(checkBox);
        this.panel = panel;
        this.controller = controller;

        actionPanel = new JPanel(new GridBagLayout());
        actionPanel.setOpaque(true);
        actionPanel.setBackground(Color.WHITE);

        // Charger l'icône à partir du fichier
        moreIcon = new JLabel(loadScaledIcon("src/icons/more.png", 24, 24));
        moreIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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
                SwingUtilities.invokeLater(() -> {
                    JPopupMenu popup = new JPopupMenu();
                    popup.setBackground(Color.WHITE);

                    // Récupérer les informations de la ligne sélectionnée
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow < 0) return;

                    Date dateDebut = (Date) table.getValueAt(selectedRow, 0);
                    Date dateFin = (Date) table.getValueAt(selectedRow, 1);
                    String activities = table.getValueAt(selectedRow, 2).toString();
                    Timestamp createdAt = (Timestamp) table.getValueAt(selectedRow, 3);

                    // Edit menu item
                    JMenuItem editItem = new JMenuItem("Modifier");
                    editItem.setBackground(new Color(33, 150, 243));
                    editItem.setForeground(Color.WHITE);
                    editItem.setFont(new Font("Arial", Font.BOLD, 14));
                    editItem.addActionListener(e -> {
                        List<PlanificationPEC> planifications = controller.searchPlanifications(activities);
                        if (!planifications.isEmpty()) {
                            PlanificationPEC planification = planifications.get(0);
                            showEditDialog(planification);
                        }
                    });
                    popup.add(editItem);

                    // Delete menu item
                    JMenuItem deleteItem = new JMenuItem("Supprimer");
                    deleteItem.setBackground(new Color(244, 67, 54));
                    deleteItem.setForeground(Color.WHITE);
                    deleteItem.setFont(new Font("Arial", Font.BOLD, 14));
                    deleteItem.addActionListener(e -> {
                        int confirmation = JOptionPane.showConfirmDialog(
                            actionPanel,
                            "Êtes-vous sûr de vouloir supprimer cette planification ?",
                            "Confirmation de suppression",
                            JOptionPane.YES_NO_OPTION
                        );

                        if (confirmation == JOptionPane.YES_OPTION) {
                            List<PlanificationPEC> planifications = controller.searchPlanifications(activities);
                            if (!planifications.isEmpty()) {
                                controller.deletePlanification(planifications.get(0).getId());
                                panel.refreshTable();
                            }
                        }
                    });
                    popup.add(deleteItem);

                    // Rapports menu item
                    JMenuItem rapportsItem = new JMenuItem("Voir les Rapports");
                    rapportsItem.setBackground(new Color(33, 150, 243));
                    rapportsItem.setForeground(Color.WHITE);
                    rapportsItem.setFont(new Font("Arial", Font.BOLD, 14));
                    rapportsItem.addActionListener(e -> {
                        List<PlanificationPEC> planifications = controller.searchPlanifications(activities);
                        if (!planifications.isEmpty()) {
                            showRapportsDialog(planifications.get(0));
                        }
                    });
                    popup.add(rapportsItem);

                    // Show popup relative to the invoking component
                    popup.show(moreIcon, 0, moreIcon.getHeight());
                });
            }
        });

        return actionPanel;
    }

    private void showEditDialog(PlanificationPEC planification) {
        Window parentWindow = SwingUtilities.getWindowAncestor(panel);
        JDialog dialog;
        if (parentWindow instanceof JFrame jFrame) {
            dialog = new JDialog(jFrame, "Modifier la planification", true);
        } else {
            dialog = new JDialog((Dialog) parentWindow, "Modifier la planification", true);
        }
        dialog.setLayout(new BorderLayout());

        JPanel form = panel.createForm(planification, dialog);
        dialog.add(form, BorderLayout.CENTER);
        dialog.pack();
        dialog.setLocationRelativeTo(panel);
        dialog.setVisible(true);
    }
    
    private void showRapportsDialog(PlanificationPEC planification) {
        JDialog rapportsDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(panel),
                                           "Rapports PEC - " + planification.getActivities(),
                                           true);
        rapportsDialog.setLayout(new BorderLayout());

        var rapportPanel = new RapportPECPanel(planification.getId());
        rapportsDialog.add(rapportPanel, BorderLayout.CENTER);
        rapportsDialog.setSize(1000, 600);
        rapportsDialog.setLocationRelativeTo(panel);
        rapportsDialog.setVisible(true);
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