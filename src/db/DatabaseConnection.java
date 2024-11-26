package db;


import com.sun.jdi.connect.spi.Connection;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    
    private final String URL = "jdbc:mysql://localhost:3306/votre_base";
    private final String USERNAME = "votre_username";
    private final String PASSWORD = "votre_password";
    
    private DatabaseConnection() {
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur de connexion à la base de données");
        }
    }
    
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    
    public Connection getConnection() {
        return connection;
    }
}