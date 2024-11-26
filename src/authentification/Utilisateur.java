package authentification;

public class Utilisateur {
    private String id;
    private String etablissementId;
    private String nom;
    private String email;
    private String motDePasse;
    private java.sql.Date dateInscription;
    private boolean actif;
    private java.sql.Timestamp derniereConnexion;
    private java.sql.Timestamp createdAt;

    // Constructeur
    public Utilisateur(String id, String etablissementId, String nom, String email, String motDePasse, 
                       java.sql.Date dateInscription, boolean actif, 
                       java.sql.Timestamp derniereConnexion, java.sql.Timestamp createdAt) {
        this.id = id;
        this.etablissementId = etablissementId;
        this.nom = nom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.dateInscription = dateInscription;
        this.actif = actif;
        this.derniereConnexion = derniereConnexion;
        this.createdAt = createdAt;
    }

    // Getters et Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEtablissementId() { return etablissementId; }
    public void setEtablissementId(String etablissementId) { this.etablissementId = etablissementId; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }

    public java.sql.Date getDateInscription() { return dateInscription; }
    public void setDateInscription(java.sql.Date dateInscription) { this.dateInscription = dateInscription; }

    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }

    public java.sql.Timestamp getDerniereConnexion() { return derniereConnexion; }
    public void setDerniereConnexion(java.sql.Timestamp derniereConnexion) { this.derniereConnexion = derniereConnexion; }

    public java.sql.Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.sql.Timestamp createdAt) { this.createdAt = createdAt; }
}
