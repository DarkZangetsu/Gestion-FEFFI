package mandataire;

import java.util.List;

public interface MandataireDAO {
    void create(Mandataire mandataire);
    Mandataire read(String id);
    List<Mandataire> readAll();
    void update(Mandataire mandataire);
    void delete(String id);
    List<Mandataire> search(String keyword);
    void exportToPDF(List<Mandataire> mandataires, String filePath);
}
