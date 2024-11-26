package transaction;

import java.sql.Timestamp;  // Changed from java.security.Timestamp
import java.util.Date;

public class Transaction {
    private String id;
    private Date date;
    private double montant;
    private String typeTransaction;
    private String description;
    private String creePar;
    private String validePar;
    private Timestamp createdAt;  // Now using java.sql.Timestamp

    public Transaction(String id, Date date, double montant, 
                      String typeTransaction, String description, String creePar, 
                      String validePar) {
        this.id = id;
        this.date = date;
        this.montant = montant;
        this.typeTransaction = typeTransaction;
        this.description = description;
        this.creePar = creePar;
        this.validePar = validePar;
    }

    // Getters et Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public double getMontant() { return montant; }
    public void setMontant(double montant) { this.montant = montant; }

    public String getTypeTransaction() { return typeTransaction; }
    public void setTypeTransaction(String typeTransaction) { this.typeTransaction = typeTransaction; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCreePar() { return creePar; }
    public void setCreePar(String creePar) { this.creePar = creePar; }

    public String getValidePar() { return validePar; }
    public void setValidePar(String validePar) { this.validePar = validePar; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}