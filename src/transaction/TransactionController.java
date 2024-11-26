package transaction;

import java.util.List;

public class TransactionController {
    private final TransactionDAO transactionDAO;

    public TransactionController() throws ClassNotFoundException {
        this.transactionDAO = new TransactionDAOImpl();
    }

    // Créer une nouvelle transaction
    public void createTransaction(Transaction transaction) {
        transactionDAO.create(transaction);
    }

    // Lire une transaction par ID
    public Transaction getTransaction(String id) {
        return transactionDAO.read(id);
    }

    // Lire toutes les transactions
    public List<Transaction> getAllTransactions() {
        return transactionDAO.readAll();
    }

    // Mettre à jour une transaction existante
    public void updateTransaction(Transaction transaction) {
        transactionDAO.update(transaction);
    }

    // Supprimer une transaction par ID
    public void deleteTransaction(String id) {
        transactionDAO.delete(id);
    }

    // Rechercher des transactions par mot-clé
    public List<Transaction> searchTransactions(String keyword) {
        return transactionDAO.search(keyword);
    }

    // Exporter toutes les transactions au format PDF
    public void exportTransactionsToPDF(String filePath) {
        try {
            List<Transaction> transactions = getAllTransactions();
            System.out.println("Nombre de transactions à exporter : " + transactions.size());
            if (!transactions.isEmpty()) {
                transactionDAO.exportToPDF(transactions, filePath);
                System.out.println("Export PDF terminé avec succès : " + filePath);
            } else {
                throw new RuntimeException("Aucune transaction à exporter");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'export : " + e.getMessage());
            throw new RuntimeException("Erreur lors de l'export PDF : " + e.getMessage());
        }
    }
}
