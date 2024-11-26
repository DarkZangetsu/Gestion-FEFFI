package etablissement;

import javax.swing.JOptionPane;
import java.util.List;

public class EtablissementController {
    private final EtablissementDAO etablissementDAO;

    public EtablissementController() throws ClassNotFoundException {
        this.etablissementDAO = new EtablissementDAOImpl();
    }

    public void createEtablissement(Etablissement etablissement) {
        etablissementDAO.create(etablissement);
    }

    public Etablissement getEtablissement(String id) {
        return etablissementDAO.read(id);
    }

    public List<Etablissement> getAllEtablissements() {
        return etablissementDAO.readAll();
    }

    public void updateEtablissement(Etablissement etablissement) {
        etablissementDAO.update(etablissement);
    }

    public void deleteEtablissement(String id) {
        etablissementDAO.delete(id);
    }

    public List<Etablissement> searchEtablissements(String keyword) {
        return etablissementDAO.search(keyword);
    }

    public void exportToPDF(String filePath) {
    try {
        List<Etablissement> etablissements = getAllEtablissements();
        System.out.println("Nombre d'établissements à exporter : " + etablissements.size());
        if (!etablissements.isEmpty()) {
            etablissementDAO.exportToPDF(etablissements, filePath);
            System.out.println("Export PDF terminé avec succès : " + filePath);
        } else {
            throw new RuntimeException("Aucun établissement à exporter");
        }
    } catch (Exception e) {
        System.err.println("Erreur lors de l'export : " + e.getMessage());
        throw new RuntimeException("Erreur lors de l'export PDF: " + e.getMessage());
    }
}
}