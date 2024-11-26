package etablissement;

import java.util.List;

public interface EtablissementDAO {
    void create(Etablissement etablissement);
    Etablissement read(String id);
    List<Etablissement> readAll();
    void update(Etablissement etablissement);
    void delete(String id);
    List<Etablissement> search(String keyword);
    void exportToPDF(List<Etablissement> etablissements, String filePath);
}
