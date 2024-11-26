package budget;

import java.util.List;

public interface BudgetDAO {
    void create(Budget budget);
    Budget read(String id);
    List<Budget> readAll();
    void update(Budget budget);
    void delete(String id);
    List<Budget> search(String keyword);
    void exportToPDF(List<Budget> budgets, String filePath);
}
