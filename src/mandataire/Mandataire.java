package mandataire;

public class Mandataire {
    private String id;
    private String nom;
    private String fonction;
    private java.sql.Date dateMandat;
    private String codeEcole;
    private String cin;
    private String contact;
    private String email;
    private String nomEtablissement;
    private java.sql.Timestamp createdAt;

    // Constructeur
    public Mandataire(String id, String nom, String fonction, java.sql.Date dateMandat, 
                      String codeEcole, String cin, String contact, String email, 
                      String nomEtablissement, java.sql.Timestamp createdAt) {
        this.id = id;
        this.nom = nom;
        this.fonction = fonction;
        this.dateMandat = dateMandat;
        this.codeEcole = codeEcole;
        this.cin = cin;
        this.contact = contact;
        this.email = email;
        this.nomEtablissement = nomEtablissement;
        this.createdAt = createdAt;
    }

    // Getters et Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getFonction() { return fonction; }
    public void setFonction(String fonction) { this.fonction = fonction; }

    public java.sql.Date getDateMandat() { return dateMandat; }
    public void setDateMandat(java.sql.Date dateMandat) { this.dateMandat = dateMandat; }

    public String getCodeEcole() { return codeEcole; }
    public void setCodeEcole(String codeEcole) { this.codeEcole = codeEcole; }

    public String getCin() { return cin; }
    public void setCin(String cin) { this.cin = cin; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNomEtablissement() { return nomEtablissement; }
    public void setNomEtablissement(String nomEtablissement) { this.nomEtablissement = nomEtablissement; }

    public java.sql.Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.sql.Timestamp createdAt) { this.createdAt = createdAt; }
}
