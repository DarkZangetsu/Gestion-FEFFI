package authentification;

import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class UtilisateurController {
    private final Connection connection;
    
    public UtilisateurController(Connection connection) {
        this.connection = connection;
    }
    
    /**
     * Authentifie un utilisateur avec son email et mot de passe
     * @param nom
     * @param motDePasse
     * @return 
     */
    public Utilisateur login(String nom, String motDePasse) {
    String sql = "SELECT * FROM utilisateur WHERE nom = ? AND actif = true";
    
    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
        pstmt.setString(1, nom);
        
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                // Vérifier le mot de passe
                String hashedPassword = hashPassword(motDePasse);
                String storedPassword = rs.getString("motDePasse");
                
                if (hashedPassword.equals(storedPassword)) {
                    // Mettre à jour la dernière connexion
                    updateDerniereConnexion(rs.getString("id"));
                    
                    // Créer et retourner l'objet Utilisateur
                    return new Utilisateur(
                        rs.getString("id"),
                        rs.getString("etablissement_id"),
                        rs.getString("nom"),
                        rs.getString("email"),
                        rs.getString("motDePasse"),
                        rs.getDate("date_inscription"),
                        rs.getBoolean("actif"),
                        rs.getTimestamp("derniere_connexion"),
                        rs.getTimestamp("created_at")
                    );
                }
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
        throw new RuntimeException("Erreur lors de la connexion: " + e.getMessage());
    }
    
    return null; // Authentification échouée
}
    
    /**
     * Enregistre un nouvel utilisateur
     */
    public Utilisateur register(String etablissementId, String nom, String email, String motDePasse) {
        // Vérifier si l'email existe déjà
        if (emailExists(email)) {
            throw new RuntimeException("Cet email est déjà utilisé");
        }
        
        String sql = "INSERT INTO utilisateur (id, etablissement_id, nom, email, motDePasse, " +
                    "date_inscription, actif, derniere_connexion, created_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            String id = generateUniqueId(); // Générer un ID unique
            Timestamp now = new Timestamp(System.currentTimeMillis());
            
            pstmt.setString(1, id);
            pstmt.setString(2, etablissementId);
            pstmt.setString(3, nom);
            pstmt.setString(4, email);
            pstmt.setString(5, hashPassword(motDePasse));
            pstmt.setDate(6, new java.sql.Date(now.getTime()));
            pstmt.setBoolean(7, true);
            pstmt.setTimestamp(8, now);
            pstmt.setTimestamp(9, now);
            
            pstmt.executeUpdate();
            
            // Retourner le nouvel utilisateur créé
            return new Utilisateur(
                id, etablissementId, nom, email, hashPassword(motDePasse),
                new java.sql.Date(now.getTime()), true, now, now
            );
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'inscription: " + e.getMessage());
        }
    }
    
    /**
     * Met à jour la dernière connexion de l'utilisateur
     */
    private void updateDerniereConnexion(String userId) {
        String sql = "UPDATE utilisateur SET derniere_connexion = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            pstmt.setString(2, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Vérifie si un email existe déjà
     */
    private boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM utilisateur WHERE email = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Génère un hash du mot de passe
     */
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur lors du hashage du mot de passe", e);
        }
    }
    
    /**
     * Génère un ID unique
     */
    private String generateUniqueId() {
        return java.util.UUID.randomUUID().toString();
    }
}