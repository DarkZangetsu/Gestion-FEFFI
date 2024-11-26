package budget;

import java.util.List;

public class BudgetController {
    private final BudgetDAO budgetDAO;

    public BudgetController() throws ClassNotFoundException, Exception {
        this.budgetDAO = new BudgetDAOImpl();
    }

    public void createBudget(Budget budget) {
        budgetDAO.create(budget);
    }

    public Budget getBudget(String id) {
        return budgetDAO.read(id);
    }

    public List<Budget> getAllBudgets() {
        return budgetDAO.readAll();
    }

    public void updateBudget(Budget budget) {
        budgetDAO.update(budget);
    }

    public void deleteBudget(String id) {
        budgetDAO.delete(id);
    }

    public List<Budget> searchBudgets(String keyword) {
        return budgetDAO.search(keyword);
    }

    public void exportBudgetsToPDF(String filePath) {
        try {
            List<Budget> budgets = getAllBudgets();
            if (!budgets.isEmpty()) {
                budgetDAO.exportToPDF(budgets, filePath);
                System.out.println("Export PDF réussi : " + filePath);
            } else {
                System.err.println("Aucun budget à exporter.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'export PDF : " + e.getMessage());
        }
    }
}
