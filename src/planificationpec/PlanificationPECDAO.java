package planificationpec;

import java.util.List;

public interface PlanificationPECDAO {
    void create(PlanificationPEC planificationPEC);
    PlanificationPEC read(String id);
    List<PlanificationPEC> readAll();
    void update(PlanificationPEC planificationPEC);
    void delete(String id);
    List<PlanificationPEC> search(String keyword);
    void exportToPDF(List<PlanificationPEC> planifications, String filePath);
}
