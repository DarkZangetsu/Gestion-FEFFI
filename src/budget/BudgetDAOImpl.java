package budget;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BudgetDAOImpl implements BudgetDAO {
    private final Connection connection;

    public BudgetDAOImpl() throws ClassNotFoundException, SQLException {
        // Initialisation de la connexion à la base de données
        Class.forName("com.mysql.cj.jdbc.Driver");
        this.connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/votre_base_de_donnees", "utilisateur", "mot_de_passe");
    }

    @Override
    public void create(Budget budget) {
        String sql = "INSERT INTO budgets (id, montant_initial, montant_restant, type_budget, created_at) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, budget.getId());
            statement.setDouble(2, budget.getMontantInitial());
            statement.setDouble(3, budget.getMontantRestant());
            statement.setString(4, budget.getTypeBudget());
            statement.setTimestamp(5, budget.getCreatedAt());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création du budget : " + e.getMessage());
        }
    }

    @Override
    public Budget read(String id) {
        String sql = "SELECT * FROM budgets WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new Budget(
                    resultSet.getString("id"),
                    resultSet.getDouble("montant_initial"),
                    resultSet.getDouble("montant_restant"),
                    resultSet.getString("type_budget"),
                    resultSet.getTimestamp("created_at")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la lecture du budget : " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Budget> readAll() {
        String sql = "SELECT * FROM budgets";
        List<Budget> budgets = new ArrayList<>();
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                budgets.add(new Budget(
                    resultSet.getString("id"),
                    resultSet.getDouble("montant_initial"),
                    resultSet.getDouble("montant_restant"),
                    resultSet.getString("type_budget"),
                    resultSet.getTimestamp("created_at")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la lecture des budgets : " + e.getMessage());
        }
        return budgets;
    }

    @Override
    public void update(Budget budget) {
        String sql = "UPDATE budgets SET montant_initial = ?, montant_restant = ?, type_budget = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDouble(1, budget.getMontantInitial());
            statement.setDouble(2, budget.getMontantRestant());
            statement.setString(3, budget.getTypeBudget());
            statement.setString(4, budget.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du budget : " + e.getMessage());
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM budgets WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du budget : " + e.getMessage());
        }
    }

    @Override
    public List<Budget> search(String keyword) {
        String sql = "SELECT * FROM budgets WHERE type_budget LIKE ?";
        List<Budget> budgets = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "%" + keyword + "%");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                budgets.add(new Budget(
                    resultSet.getString("id"),
                    resultSet.getDouble("montant_initial"),
                    resultSet.getDouble("montant_restant"),
                    resultSet.getString("type_budget"),
                    resultSet.getTimestamp("created_at")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de budgets : " + e.getMessage());
        }
        return budgets;
    }

    @Override
    public void exportToPDF(List<Budget> budgets, String filePath) {
        // Implémentation simplifiée pour l'export PDF
        try {
            // Utilisez une bibliothèque comme iText ou Apache PDFBox
            System.out.println("Exportation des budgets au format PDF : " + filePath);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'export PDF : " + e.getMessage());
        }
    }
}
