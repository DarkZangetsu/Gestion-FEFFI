package rapportpec;

import java.sql.Timestamp;
public class RapportPEC {
    private String id;
    private String planification_pec_id;
    private String periode;
    private double double_indicateurs;
    private String activites; // Changé de 'activities' à 'activites'
    private String designation;
    private double prix_unitaire;
    private int quantite;
    private String source_financement;
    private Timestamp created_at;
    private String observation;

    // Constructeur
    public RapportPEC(String id, String planification_pec_id, String periode, 
                      double double_indicateurs, String activites, String designation,
                      double prix_unitaire, int quantite, String source_financement,
                      Timestamp created_at, String observation) {
        this.id = id;
        this.planification_pec_id = planification_pec_id;
        this.periode = periode;
        this.double_indicateurs = double_indicateurs;
        this.activites = activites;
        this.designation = designation;
        this.prix_unitaire = prix_unitaire;
        this.quantite = quantite;
        this.source_financement = source_financement;
        this.created_at = created_at;
        this.observation = observation;
    }

    // Getters et Setters mis à jour
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPlanificationPecId() { return planification_pec_id; }
    public void setPlanificationPecId(String planification_pec_id) { this.planification_pec_id = planification_pec_id; }

    public String getPeriode() { return periode; }
    public void setPeriode(String periode) { this.periode = periode; }

    public double getDoubleIndicateurs() { return double_indicateurs; }
    public void setDoubleIndicateurs(double double_indicateurs) { this.double_indicateurs = double_indicateurs; }

    public String getActivites() { return activites; } // Changé de getActivities
    public void setActivites(String activites) { this.activites = activites; } // Changé de setActivities

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public double getPrixUnitaire() { return prix_unitaire; }
    public void setPrixUnitaire(double prix_unitaire) { this.prix_unitaire = prix_unitaire; }

    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { this.quantite = quantite; }

    public String getSourceFinancement() { return source_financement; }
    public void setSourceFinancement(String source_financement) { this.source_financement = source_financement; }

    public Timestamp getCreatedAt() { return created_at; }
    public void setCreatedAt(Timestamp created_at) { this.created_at = created_at; }

    public String getObservation() { return observation; }
    public void setObservation(String observation) { this.observation = observation; }
}