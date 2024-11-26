package budget;

import java.sql.Timestamp;

public class Budget {
    private String id;
    private double montantInitial;
    private double montantRestant;
    private String typeBudget;
    private java.sql.Timestamp createdAt;

    // Constructeur
    public Budget(String id, double montantInitial, double montantRestant, String caisseEcoleId, Timestamp createdAt) {
        this.id = id;
        this.montantInitial = montantInitial;
        this.montantRestant = montantRestant;
        this.typeBudget = typeBudget;
        this.createdAt = createdAt;
    }

    // Getters et Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public double getMontantInitial() { return montantInitial; }
    public void setMontantInitial(double montantInitial) { this.montantInitial = montantInitial; }

    public double getMontantRestant() { return montantRestant; }
    public void setMontantRestant(double montantRestant) { this.montantRestant = montantRestant; }

    public String getTypeBudget() { return typeBudget; }
    public void setTypeBudget(String typeBudget) { this.typeBudget = typeBudget; }

    public java.sql.Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.sql.Timestamp createdAt) { this.createdAt = createdAt; }
}
