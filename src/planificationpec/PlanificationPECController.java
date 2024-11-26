package planificationpec;

import java.util.List;

public class PlanificationPECController {
    private final PlanificationPECDAO planificationPECDAO;

    public PlanificationPECController() throws ClassNotFoundException {
        this.planificationPECDAO = new PlanificationPECDAOImpl();
    }

    public void createPlanification(PlanificationPEC planificationPEC) {
        planificationPECDAO.create(planificationPEC);
    }

    public PlanificationPEC getPlanification(String id) {
        return planificationPECDAO.read(id);
    }

    public List<PlanificationPEC> getAllPlanifications() {
        return planificationPECDAO.readAll();
    }

    public void updatePlanification(PlanificationPEC planificationPEC) {
        planificationPECDAO.update(planificationPEC);
    }

    public void deletePlanification(String id) {
        planificationPECDAO.delete(id);
    }

    public List<PlanificationPEC> searchPlanifications(String keyword) {
        return planificationPECDAO.search(keyword);
    }

    public void exportToPDF(String filePath) {
        try {
            List<PlanificationPEC> planifications = getAllPlanifications();
            System.out.println("Nombre de planifications à exporter : " + planifications.size());
            if (!planifications.isEmpty()) {
                planificationPECDAO.exportToPDF(planifications, filePath);
                System.out.println("Export PDF terminé avec succès : " + filePath);
            } else {
                throw new RuntimeException("Aucune planification à exporter");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'export : " + e.getMessage());
            throw new RuntimeException("Erreur lors de l'export PDF: " + e.getMessage());
        }
    }
}
