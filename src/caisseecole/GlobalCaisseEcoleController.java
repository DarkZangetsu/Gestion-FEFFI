package caisseecole;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;
import java.util.stream.Stream;

public class GlobalCaisseEcoleController {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/feffi?zeroDateTimeBehavior=CONVERT_TO_NULL";
    private static final String USER = "root";
    private static final String PASS = "";

    public GlobalCaisseEcoleController() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public List<GlobalCaisseEntry> getAllCaisses() {
        List<GlobalCaisseEntry> entries = new ArrayList<>();
        String query = "SELECT c.*, e.nom as etablissement_nom FROM caisseecole c " +
                      "JOIN etablissement e ON c.etablissement_id = e.id " +
                      "ORDER BY c.created_at DESC";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                entries.add(new GlobalCaisseEntry(
                    rs.getString("etablissement_nom"),
                    rs.getDouble("montant"),
                    rs.getTimestamp("created_at"),
                    rs.getString("raison"),
                    rs.getString("note")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la récupération des caisses", e);
        }
        return entries;
    }

    public List<GlobalCaisseEntry> searchGlobalCaisse(String keyword) {
        List<GlobalCaisseEntry> results = new ArrayList<>();
        String query = "SELECT c.*, e.nom as etablissement_nom FROM caisseecole c " +
                      "JOIN etablissement e ON c.etablissement_id = e.id " +
                      "WHERE e.nom LIKE ? OR c.raison LIKE ? OR c.note LIKE ? " +
                      "ORDER BY c.created_at DESC";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");
            pstmt.setString(3, "%" + keyword + "%");
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                results.add(new GlobalCaisseEntry(
                    rs.getString("etablissement_nom"),
                    rs.getDouble("montant"),
                    rs.getTimestamp("created_at"),
                    rs.getString("raison"),
                    rs.getString("note")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la recherche des caisses", e);
        }
        return results;
    }

    public void exportToPDF(String filePath) {
        try {
            Document document = new Document(PageSize.A4.rotate()); // Format paysage
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // En-tête
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("Rapport Global des Caisses École", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20f);
            document.add(title);

            // Tableau
            PdfPTable table = new PdfPTable(5); // 5 colonnes
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            // En-têtes de colonnes
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Stream.of("Établissement", "Raison", "Note", "Montant", "Date")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle, headerFont));
                    table.addCell(header);
                });

            // Données
            List<GlobalCaisseEntry> entries = getAllCaisses();
            double total = 0;

            Font cellFont = new Font(Font.FontFamily.HELVETICA, 10);
            for (GlobalCaisseEntry entry : entries) {
                table.addCell(new Phrase(entry.getEtablissementNom(), cellFont));
                table.addCell(new Phrase(entry.getRaison(), cellFont));
                table.addCell(new Phrase(entry.getNote(), cellFont));
                table.addCell(new Phrase(String.format("%.2f Ar", entry.getMontant()), cellFont));
                table.addCell(new Phrase(entry.getCreatedAt().toString(), cellFont));
                total += entry.getMontant();
            }

            document.add(table);

            // Total
            Paragraph totalParagraph = new Paragraph(
                String.format("Total Global: %.2f Ar", total),
                new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD)
            );
            totalParagraph.setAlignment(Element.ALIGN_RIGHT);
            totalParagraph.setSpacingBefore(20f);
            document.add(totalParagraph);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'export PDF", e);
        }
    }
}

class GlobalCaisseEntry {
    private String etablissementNom;
    private double montant;
    private Timestamp createdAt;
    private String raison;
    private String note;

    public GlobalCaisseEntry(String etablissementNom, double montant, Timestamp createdAt, 
                           String raison, String note) {
        this.etablissementNom = etablissementNom;
        this.montant = montant;
        this.createdAt = createdAt;
        this.raison = raison;
        this.note = note;
    }

    // Getters
    public String getEtablissementNom() { return etablissementNom; }
    public double getMontant() { return montant; }
    public Timestamp getCreatedAt() { return createdAt; }
    public String getRaison() { return raison; }
    public String getNote() { return note; }
}