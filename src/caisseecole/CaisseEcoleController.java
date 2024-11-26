package caisseecole;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;
import java.util.stream.Stream;

public class CaisseEcoleController {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/feffi?zeroDateTimeBehavior=CONVERT_TO_NULL";
    private static final String USER = "root";
    private static final String PASS = "";

    public CaisseEcoleController() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public List<CaisseEcole> getCaissesByEtablissement(String etablissementId) {
        List<CaisseEcole> caisses = new ArrayList<>();
        String query = "SELECT * FROM caisseecole WHERE etablissement_id = ? ORDER BY created_at DESC";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, etablissementId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                caisses.add(new CaisseEcole(
                    rs.getString("id"),
                    rs.getString("etablissement_id"),
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
        return caisses;
    }

    public void createCaisseEcole(CaisseEcole caisse) {
        String query = "INSERT INTO caisseecole (id, etablissement_id, montant, created_at, raison, note) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, caisse.getId());
            pstmt.setString(2, caisse.getEtablissementId());
            pstmt.setDouble(3, caisse.getMontant());
            pstmt.setTimestamp(4, caisse.getCreatedAt());
            pstmt.setString(5, caisse.getRaison());
            pstmt.setString(6, caisse.getNote());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la création de la caisse", e);
        }
    }

    public void updateCaisseEcole(CaisseEcole caisse) {
        String query = "UPDATE caisseecole SET montant = ?, raison = ?, note = ? WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setDouble(1, caisse.getMontant());
            pstmt.setString(2, caisse.getRaison());
            pstmt.setString(3, caisse.getNote());
            pstmt.setString(4, caisse.getId());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la mise à jour de la caisse", e);
        }
    }

    public void deleteCaisseEcole(String id) {
        String query = "DELETE FROM caisseecole WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la suppression de la caisse", e);
        }
    }

    public List<CaisseEcole> searchCaisseEcole(String keyword, String etablissementId) {
        List<CaisseEcole> results = new ArrayList<>();
        String query = "SELECT * FROM caisseecole WHERE etablissement_id = ? AND (raison LIKE ? OR note LIKE ?) ORDER BY created_at DESC";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, etablissementId);
            pstmt.setString(2, "%" + keyword + "%");
            pstmt.setString(3, "%" + keyword + "%");
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                results.add(new CaisseEcole(
                    rs.getString("id"),
                    rs.getString("etablissement_id"),
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

    public void exportToPDF(String filePath, String etablissementId) {
        try {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // En-tête
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("Rapport de Caisse École", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20f);
            document.add(title);

            // Tableau
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            // En-têtes de colonnes
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Stream.of("Raison", "Note", "Montant", "Date")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle, headerFont));
                    table.addCell(header);
                });

            // Données
            List<CaisseEcole> caisses = getCaissesByEtablissement(etablissementId);
            double total = 0;

            Font cellFont = new Font(Font.FontFamily.HELVETICA, 10);
            for (CaisseEcole caisse : caisses) {
                table.addCell(new Phrase(caisse.getRaison(), cellFont));
                table.addCell(new Phrase(caisse.getNote(), cellFont));
                table.addCell(new Phrase(String.format("%.2f Ar", caisse.getMontant()), cellFont));
                table.addCell(new Phrase(caisse.getCreatedAt().toString(), cellFont));
                total += caisse.getMontant();
            }

            document.add(table);

            // Total
            Paragraph totalParagraph = new Paragraph(
                String.format("Total: %.2f Ar", total),
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