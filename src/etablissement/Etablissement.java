package etablissement;

public class Etablissement {
    private String id;
    private String nom;
    private String type;
    private String localisation;
    private java.sql.Timestamp createdAt;

    public Etablissement(String id, String nom, String type, String localisation, java.sql.Timestamp createdAt) {
        this.id = id;
        this.nom = nom;
        this.type = type;
        this.localisation = localisation;
        this.createdAt = createdAt;
    }

    // Getters et Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getLocalisation() { return localisation; }
    public void setLocalisation(String localisation) { this.localisation = localisation; }
    public java.sql.Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.sql.Timestamp createdAt) { this.createdAt = createdAt; }
}
