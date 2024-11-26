package fefi;

public class FEFI {
    private String id;
    private String etablissementId;
    private java.sql.Date dateDebut;
    private java.sql.Date dateFin;
    private String activities;
    private java.sql.Timestamp createdAt;

    public FEFI(String id, String etablissementId, java.sql.Date dateDebut, java.sql.Date dateFin, String activities, java.sql.Timestamp createdAt) {
        this.id = id;
        this.etablissementId = etablissementId;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.activities = activities;
        this.createdAt = createdAt;
    }

    // Getters et Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getEtablissementId() { return etablissementId; }
    public void setEtablissementId(String etablissementId) { this.etablissementId = etablissementId; }
    public java.sql.Date getDateDebut() { return dateDebut; }
    public void setDateDebut(java.sql.Date dateDebut) { this.dateDebut = dateDebut; }
    public java.sql.Date getDateFin() { return dateFin; }
    public void setDateFin(java.sql.Date dateFin) { this.dateFin = dateFin; }
    public String getActivities() { return activities; }
    public void setActivities(String activities) { this.activities = activities; }
    public java.sql.Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.sql.Timestamp createdAt) { this.createdAt = createdAt; }
}
