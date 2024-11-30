package dashboard;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.sql.*;
import java.text.NumberFormat;
import java.util.Locale;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

public class DashboardPanel extends JPanel {
    private Connection connection;
    private final Color primaryColor = new Color(33, 150, 243); // Bleu
    private final Color accentColor = new Color(255, 193, 7); // Jaune
    private final Color alertColor = new Color(244, 67, 54); // Rouge
    private final Color backgroundColor = new Color(250, 250, 250);
    private final Font titleFont = new Font("Segoe UI", Font.BOLD, 24);
    private final Font subtitleFont = new Font("Segoe UI", Font.PLAIN, 14);

    public DashboardPanel() {
        setBackground(backgroundColor);
        setLayout(new BorderLayout(15, 15));

        // En-tête moderne
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Panneau de statistiques avec grille responsive
        JPanel statsGrid = createStatsGrid();
        add(new JScrollPane(statsGrid), BorderLayout.CENTER);

        // Rafraîchir les données à chaque affichage
        addHierarchyListener(e -> {
            if (e.getChangeFlags() == HierarchyEvent.SHOWING_CHANGED && isShowing()) {
                refreshData();
            }
        });
    }

    private void refreshData() {
        removeAll(); // Efface les composants existants
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        JPanel statsGrid = createStatsGrid();
        add(new JScrollPane(statsGrid), BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Tableau de Bord");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(primaryColor);

        JLabel subtitleLabel = new JLabel("Vue d'ensemble de votre système");
        subtitleLabel.setFont(subtitleFont);
        subtitleLabel.setForeground(Color.GRAY);

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.CENTER);

        return headerPanel;
    }

    private JPanel createStatsGrid() {
        JPanel statsGrid = new JPanel(new GridLayout(2, 2, 15, 15));
        statsGrid.setBackground(backgroundColor);
        statsGrid.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        try {
            connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/feffi?zeroDateTimeBehavior=CONVERT_TO_NULL", 
                "root", 
                ""
            );

            statsGrid.add(createFinancialCard());
            statsGrid.add(createEtablissementCard());
            statsGrid.add(createTransactionChart());
            statsGrid.add(createUserChart());

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur de connexion à la base de données");
        }

        return statsGrid;
    }

    private JPanel createStyledCard(String title, JComponent content) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel cardTitle = new JLabel(title);
        cardTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        cardTitle.setForeground(primaryColor);

        card.add(cardTitle, BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);

        return card;
    }

    private JPanel createFinancialCard() throws SQLException {
        JPanel contentPanel = new JPanel(new GridLayout(3, 1));
        contentPanel.setOpaque(false);

        String query = "SELECT " +
            "SUM(montant) AS total_transactions, " +
            "AVG(montant) AS moyenne_transactions, " +
            "COUNT(*) AS nombre_transactions " +
            "FROM transaction";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.FRANCE);

                JLabel totalLabel = new JLabel("Total: " + 
                    currencyFormat.format(rs.getDouble("total_transactions")));
                JLabel moyenneLabel = new JLabel("Moyenne: " + 
                    currencyFormat.format(rs.getDouble("moyenne_transactions")));
                JLabel nombreLabel = new JLabel("Nombre de transactions: " + 
                    rs.getInt("nombre_transactions"));

                totalLabel.setFont(subtitleFont);
                moyenneLabel.setFont(subtitleFont);
                nombreLabel.setFont(subtitleFont);

                contentPanel.add(totalLabel);
                contentPanel.add(moyenneLabel);
                contentPanel.add(nombreLabel);
            }
        }

        return createStyledCard("Statistiques Financières", contentPanel);
    }

    private JPanel createEtablissementCard() throws SQLException {
        JPanel contentPanel = new JPanel(new GridLayout(3, 1));
        contentPanel.setOpaque(false);

        String query = "SELECT " +
            "SUM(CASE WHEN type = 'École' THEN 1 ELSE 0 END) AS ecoles, " +
            "SUM(CASE WHEN type = 'Collège' THEN 1 ELSE 0 END) AS colleges, " +
            "SUM(CASE WHEN type = 'Lycée' THEN 1 ELSE 0 END) AS lycees " +
            "FROM etablissement";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                JLabel ecolesLabel = new JLabel("Écoles: " + rs.getInt("ecoles"));
                JLabel collegesLabel = new JLabel("Collèges: " + rs.getInt("colleges"));
                JLabel lyceesLabel = new JLabel("Lycées: " + rs.getInt("lycees"));

                ecolesLabel.setFont(subtitleFont);
                collegesLabel.setFont(subtitleFont);
                lyceesLabel.setFont(subtitleFont);

                contentPanel.add(ecolesLabel);
                contentPanel.add(collegesLabel);
                contentPanel.add(lyceesLabel);
            }
        }

        return createStyledCard("Établissements", contentPanel);
    }

    private JPanel createTransactionChart() throws SQLException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        String query = "SELECT type_transaction, SUM(montant) AS total_montant " +
                       "FROM transaction GROUP BY type_transaction";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                dataset.addValue(
                    rs.getDouble("total_montant"), 
                    "Montant", 
                    rs.getString("type_transaction")
                );
            }
        }

        JFreeChart barChart = ChartFactory.createBarChart(
            "Transactions par Type",
            "Type de Transaction",
            "Montant Total",
            dataset, 
            PlotOrientation.VERTICAL, 
            false, true, false
        );

        barChart.setBackgroundPaint(Color.WHITE);
        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(300, 200));
        chartPanel.setMouseWheelEnabled(true);

        return createStyledCard("Analyse des Transactions", chartPanel);
    }

    private JPanel createUserChart() throws SQLException {
        DefaultPieDataset dataset = new DefaultPieDataset();

        String query = "SELECT " +
                       "SUM(CASE WHEN actif = 1 THEN 1 ELSE 0 END) AS utilisateurs_actifs, " +
                       "SUM(CASE WHEN actif = 0 THEN 1 ELSE 0 END) AS utilisateurs_inactifs " +
                       "FROM utilisateur";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                dataset.setValue("Actifs", rs.getInt("utilisateurs_actifs"));
                dataset.setValue("Inactifs", rs.getInt("utilisateurs_inactifs"));
            }
        }

        JFreeChart pieChart = ChartFactory.createPieChart(
            "Statut des Utilisateurs",
            dataset, 
            true, true, false
        );

        PiePlot plot = (PiePlot) pieChart.getPlot();
        plot.setSectionPaint("Actifs", new Color(33, 150, 243));
        plot.setSectionPaint("Inactifs", Color.LIGHT_GRAY);
        plot.setBackgroundPaint(Color.WHITE);

        ChartPanel chartPanel = new ChartPanel(pieChart);
        chartPanel.setPreferredSize(new Dimension(300, 200));
        chartPanel.setMouseWheelEnabled(true);

        return createStyledCard("Statistiques des Utilisateurs", chartPanel);
    }
}
