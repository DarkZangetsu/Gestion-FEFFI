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
import rapportpec.RapportPECPanel;

public class PlanificationPECPanel extends JPanel {
    private final PlanificationPECController controller;
    private final JTable table;
    private final DefaultTableModel tableModel;
    private JTextField searchField;

    public PlanificationPECPanel() throws ClassNotFoundException {
        setBackground(Color.WHITE);
        setLayout(new BorderLayout(0, 10));

        this.controller = new PlanificationPECController();
        
        // Bannière d'en-tête
        JPanel headerBanner = createHeaderBanner();
        add(headerBanner, BorderLayout.NORTH);

        // Panneau supérieur avec recherche et boutons
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Date Début", "Date Fin", "Activités", "Date de création", "Actions"};
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

        JLabel titleLabel = new JLabel("Gérer les Planifications PEC");
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
            new ButtonEditor(new JCheckBox(), this, controller, newTable));

        newTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        newTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        newTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        newTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        newTable.getColumnModel().getColumn(4).setPreferredWidth(100);
    
        return newTable;
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
        if (parentWindow instanceof JFrame) {
            dialog = new JDialog((JFrame) parentWindow, "Ajouter une planification", true);
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
    private final PlanificationPECPanel panel;
    private final PlanificationPECController controller;
    private final JTable table;

    public ButtonEditor(JCheckBox checkBox, PlanificationPECPanel panel,
                       PlanificationPECController controller, JTable table) {
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

        Date dateDebut = (Date) table.getValueAt(row, 0);
        Date dateFin = (Date) table.getValueAt(row, 1);
        String activities = table.getValueAt(row, 2).toString();
        Timestamp createdAt = (Timestamp) table.getValueAt(row, 3);

        JPopupMenu popup = new JPopupMenu();

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

        JMenuItem deleteItem = new JMenuItem("Supprimer");
        deleteItem.setBackground(new Color(244, 67, 54));
        deleteItem.setForeground(Color.WHITE);
        deleteItem.setFont(new Font("Arial", Font.BOLD, 14));
        deleteItem.addActionListener(e -> {
            int confirmation = JOptionPane.showConfirmDialog(
                button,
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

        popup.show(button, 0, button.getHeight());
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

        try {
            RapportPECPanel rapportPanel = new RapportPECPanel(planification.getId());
            rapportsDialog.add(rapportPanel, BorderLayout.CENTER);
            rapportsDialog.setSize(1000, 600);
            rapportsDialog.setLocationRelativeTo(panel);
            rapportsDialog.setVisible(true);
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(panel,
                "Erreur lors de l'ouverture des rapports",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
    }
}