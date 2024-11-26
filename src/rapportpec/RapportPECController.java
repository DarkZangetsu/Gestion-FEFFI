package rapportpec;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class RapportPECController {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/feffi";
    private static final String USER = "root";
    private static final String PASS = "";
    private static Connection connection = null;

    public RapportPECController() {
        initializeDatabase();
    }

    private void initializeDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Properties props = new Properties();
            props.setProperty("user", USER);
            props.setProperty("password", PASS);
            props.setProperty("useSSL", "false");
            props.setProperty("serverTimezone", "UTC");
            props.setProperty("zeroDateTimeBehavior", "CONVERT_TO_NULL");
            props.setProperty("autoReconnect", "true");
            
            connection = DriverManager.getConnection(DB_URL, props);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver MySQL non trouvé", e);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur de connexion à la base de données", e);
        }
    }

    private Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            initializeDatabase();
        }
        return connection;
    }

    public void createRapport(RapportPEC rapport) {
        if (rapport == null) {
            throw new IllegalArgumentException("Le rapport ne peut pas être null");
        }

        String sql = "INSERT INTO rapportpec (id, planification_pec_id, periode, double_indicateurs, " +
                    "activites, designation, prix_unitaire, quantite, source_financement, created_at, observation) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, rapport.getId());
            pstmt.setString(2, rapport.getPlanificationPecId());
            pstmt.setString(3, rapport.getPeriode());
            pstmt.setDouble(4, rapport.getDoubleIndicateurs());
            pstmt.setString(5, rapport.getActivites());
            pstmt.setString(6, rapport.getDesignation());
            pstmt.setDouble(7, rapport.getPrixUnitaire());
            pstmt.setInt(8, rapport.getQuantite());
            pstmt.setString(9, rapport.getSourceFinancement());
            pstmt.setTimestamp(10, rapport.getCreatedAt());
            pstmt.setString(11, rapport.getObservation());
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("La création du rapport a échoué");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création du rapport: " + e.getMessage(), e);
        }
    }

    public void updateRapport(RapportPEC rapport) {
        if (rapport == null || rapport.getId() == null) {
            throw new IllegalArgumentException("Le rapport ou son ID ne peut pas être null");
        }

        String sql = "UPDATE rapportpec SET periode = ?, double_indicateurs = ?, activites = ?, " +
                    "designation = ?, prix_unitaire = ?, quantite = ?, source_financement = ?, " +
                    "observation = ? WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, rapport.getPeriode());
            pstmt.setDouble(2, rapport.getDoubleIndicateurs());
            pstmt.setString(3, rapport.getActivites());
            pstmt.setString(4, rapport.getDesignation());
            pstmt.setDouble(5, rapport.getPrixUnitaire());
            pstmt.setInt(6, rapport.getQuantite());
            pstmt.setString(7, rapport.getSourceFinancement());
            pstmt.setString(8, rapport.getObservation());
            pstmt.setString(9, rapport.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Aucun rapport trouvé avec l'ID: " + rapport.getId());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du rapport: " + e.getMessage(), e);
        }
    }

    public void deleteRapport(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("L'ID ne peut pas être null ou vide");
        }

        String sql = "DELETE FROM rapportpec WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, id);
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected == 0) {
                throw new SQLException("Aucun rapport trouvé avec l'ID: " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du rapport: " + e.getMessage(), e);
        }
    }

    public List<RapportPEC> getAllRapports(String planificationId) {
        if (planificationId == null || planificationId.trim().isEmpty()) {
            throw new IllegalArgumentException("L'ID de planification ne peut pas être null ou vide");
        }

        List<RapportPEC> rapports = new ArrayList<>();
        String sql = "SELECT * FROM rapportpec WHERE planification_pec_id = ? ORDER BY created_at DESC";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, planificationId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    rapports.add(createRapportFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des rapports: " + e.getMessage(), e);
        }
        return rapports;
    }

    public List<RapportPEC> searchRapports(String keyword, String planificationId) {
        if (planificationId == null || planificationId.trim().isEmpty()) {
            throw new IllegalArgumentException("L'ID de planification ne peut pas être null ou vide");
        }

        List<RapportPEC> rapports = new ArrayList<>();
        String sql = "SELECT * FROM rapportpec WHERE planification_pec_id = ? AND " +
                    "(periode LIKE ? OR activites LIKE ? OR designation LIKE ? OR source_financement LIKE ?) " +
                    "ORDER BY created_at DESC";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchTerm = "%" + (keyword != null ? keyword : "") + "%";
            pstmt.setString(1, planificationId);
            pstmt.setString(2, searchTerm);
            pstmt.setString(3, searchTerm);
            pstmt.setString(4, searchTerm);
            pstmt.setString(5, searchTerm);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    rapports.add(createRapportFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des rapports: " + e.getMessage(), e);
        }
        return rapports;
    }

    private RapportPEC createRapportFromResultSet(ResultSet rs) throws SQLException {
        return new RapportPEC(
            rs.getString("id"),
            rs.getString("planification_pec_id"),
            rs.getString("periode"),
            rs.getDouble("double_indicateurs"),
            rs.getString("activites"),
            rs.getString("designation"),
            rs.getDouble("prix_unitaire"),
            rs.getInt("quantite"),
            rs.getString("source_financement"),
            rs.getTimestamp("created_at"),
            rs.getString("observation")
        );
    }
    
    public void exportToPDF(String filePath, String planificationId) {
        // Implémentation de l'export PDF
    }

    /**
     *
     * @throws Throwable
     */
    @Override
    protected void finalize() throws Throwable {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } finally {
            super.finalize();
        }
    }
}