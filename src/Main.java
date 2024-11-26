import authentification.UtilisateurController;
import authentification.LoginInterface;
import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Charger le pilote JDBC MySQL
                Class.forName("com.mysql.cj.jdbc.Driver");

                // Établir la connexion à la base de données
                Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/feffi?zeroDateTimeBehavior=CONVERT_TO_NULL", 
                    "root", 
                    ""
                );

                // Créer le contrôleur utilisateur
                UtilisateurController controller = new UtilisateurController(connection);

                // Lancer l'interface de login
                LoginInterface login = new LoginInterface(controller);
                login.setVisible(true);

            } catch (ClassNotFoundException e) {
                JOptionPane.showMessageDialog(null, 
                    "Pilote JDBC MySQL non trouvé", 
                    "Erreur de pilote", 
                    JOptionPane.ERROR_MESSAGE
                );
                e.printStackTrace();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, 
                    "Erreur de connexion à la base de données : " + e.getMessage(), 
                    "Erreur de connexion", 
                    JOptionPane.ERROR_MESSAGE
                );
                e.printStackTrace();
            }
        });
    }
}
