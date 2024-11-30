package mandataire;

import java.util.List;

public class MandataireController {
    private final MandataireDAO mandataireDAO;

    public MandataireController() throws ClassNotFoundException {
        this.mandataireDAO = new MandataireDAOImpl();
    }

    public void createMandataire(Mandataire mandataire) {
        mandataireDAO.create(mandataire);
    }

    public Mandataire getMandataire(String id) {
        return mandataireDAO.read(id);
    }

    public List<Mandataire> getAllMandataires() {
        return mandataireDAO.readAll();
    }

    public void updateMandataire(Mandataire mandataire) {
        mandataireDAO.update(mandataire);
    }

    public void deleteMandataire(String id) {
        mandataireDAO.delete(id);
    }

    public List<Mandataire> searchMandataires(String keyword) {
        return mandataireDAO.search(keyword);
    }

    public void exportToPDF(String filePath) {
        try {
            List<Mandataire> mandataires = getAllMandataires();
            System.out.println("Nombre de mandataires à exporter : " + mandataires.size());
            if (!mandataires.isEmpty()) {
                mandataireDAO.exportToPDF(mandataires, filePath);
                System.out.println("Export PDF terminé avec succès : " + filePath);
            } else {
                throw new RuntimeException("Aucun mandataire à exporter");
            }
        } catch (RuntimeException e) {
            System.err.println("Erreur lors de l'export : " + e.getMessage());
            throw new RuntimeException("Erreur lors de l'export PDF : " + e.getMessage());
        }
    }
    
    public String findMandataireIdByDetails(String nom, String cin, String etablissement) {
    try {
        List<Mandataire> mandataires = mandataireDAO.readAll();
        for (var mandataire : mandataires) {
            if (mandataire.getNom().equals(nom) && 
                mandataire.getCin().equals(cin) && 
                mandataire.getNomEtablissement().equals(etablissement)) {
                return mandataire.getId();
            }
        }
        return null;
    } catch (Exception e) {
        System.err.println("Erreur lors de la recherche de l'ID du mandataire : " + e.getMessage());
        return null;
    }
}
}
