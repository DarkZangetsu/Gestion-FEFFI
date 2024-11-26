package transaction;

import java.util.List;

public interface TransactionDAO {
    void create(Transaction transaction);
    Transaction read(String id);
    List<Transaction> readAll();
    void update(Transaction transaction);
    void delete(String id);
    List<Transaction> search(String keyword);
    void exportToPDF(List<Transaction> transactions, String filePath);
}
