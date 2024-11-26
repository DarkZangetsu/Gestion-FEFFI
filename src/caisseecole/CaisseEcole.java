package caisseecole;

import java.sql.Timestamp;

public class CaisseEcole {
    private String id; // Correspond à 'id' dans la table
    private String etablissementId; // Correspond à 'etablissement_id'
    private double montant; // Correspond à 'montant'
    private Timestamp createdAt; // Correspond à 'created_at'
    private String raison; // Correspond à 'raison'
    private String note; // Correspond à 'note'

    // Constructeur
    public CaisseEcole(String id, String etablissementId, double montant, Timestamp createdAt, String raison, String note) {
        this.id = id;
        this.etablissementId = etablissementId;
        this.montant = montant;
        this.createdAt = createdAt;
        this.raison = raison;
        this.note = note;
    }

    // Getters et Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEtablissementId() {
        return etablissementId;
    }

    public void setEtablissementId(String etablissementId) {
        this.etablissementId = etablissementId;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getRaison() {
        return raison;
    }

    public void setRaison(String raison) {
        this.raison = raison;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
