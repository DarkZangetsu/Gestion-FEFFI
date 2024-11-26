package role;


import java.security.Timestamp;

public class Role {
    private String id;
    private String nom;
    private String description;
    private String listPermission;
    private Timestamp createdAt;

    public Role(String id, String nom, String description, String listPermission) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.listPermission = listPermission;
    }

    // Getters et Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getListPermission() { return listPermission; }
    public void setListPermission(String listPermission) { this.listPermission = listPermission; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}