package transaction;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAOImpl implements TransactionDAO {
    private final DataSource dataSource;

    public TransactionDAOImpl() throws ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/feffi?zeroDateTimeBehavior=CONVERT_TO_NULL");
        config.setUsername("root");
        config.setPassword("");
        config.setMaximumPoolSize(10);
        this.dataSource = new HikariDataSource(config);
    }

    @Override
    public void create(Transaction transaction) {
        String sql = "INSERT INTO transaction (id, date, montant, type_transaction, description, cree_par, valide_par, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, transaction.getId());
            pstmt.setDate(2, new java.sql.Date(transaction.getDate().getTime()));
            pstmt.setDouble(3, transaction.getMontant());
            pstmt.setString(4, transaction.getTypeTransaction());
            pstmt.setString(5, transaction.getDescription());
            pstmt.setString(6, transaction.getCreePar());
            pstmt.setString(7, transaction.getValidePar());
            pstmt.setTimestamp(8, transaction.getCreatedAt());  // Make sure Transaction class uses java.sql.Timestamp
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création de la transaction", e);
        }
    }

    @Override
    public Transaction read(String id) {
        String sql = "SELECT * FROM transaction WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTransaction(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la lecture de la transaction", e);
        }
        return null;
    }

    @Override
    public List<Transaction> readAll() {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transaction";
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la lecture de toutes les transactions", e);
        }
        return transactions;
    }

    @Override
    public void update(Transaction transaction) {
        String sql = "UPDATE transaction SET date = ?, montant = ?, type_transaction = ?, description = ?, cree_par = ?, valide_par = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, new java.sql.Date(transaction.getDate().getTime()));
            pstmt.setDouble(2, transaction.getMontant());
            pstmt.setString(3, transaction.getTypeTransaction());
            pstmt.setString(4, transaction.getDescription());
            pstmt.setString(5, transaction.getCreePar());
            pstmt.setString(6, transaction.getValidePar());
            pstmt.setString(7, transaction.getId());
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Transaction non trouvée avec l'ID: " + transaction.getId());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de la transaction", e);
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM transaction WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Transaction non trouvée avec l'ID: " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de la transaction", e);
        }
    }

    @Override
    public List<Transaction> search(String keyword) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transaction WHERE type_transaction LIKE ? OR description LIKE ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de transactions", e);
        }
        return transactions;
    }

    @Override
    public void exportToPDF(List<Transaction> transactions, String filePath) {
        // Export logic similar to EtablissementDAOImpl
        // Omitted for brevity
    }

    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        return new Transaction(
            rs.getString("id"),
            rs.getDate("date"),
            rs.getDouble("montant"),
            rs.getString("type_transaction"),
            rs.getString("description"),
            rs.getString("cree_par"),
            rs.getString("valide_par")
        );
    }
}
