package mandataire;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Imports iText 7
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

// Imports pour la base de données
import javax.sql.DataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.IOException;

public class MandataireDAOImpl implements MandataireDAO {
    private final DataSource dataSource;

    public MandataireDAOImpl() throws ClassNotFoundException {
        // Configuration du pool de connexions HikariCP
        Class.forName("com.mysql.cj.jdbc.Driver");
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/feffi?zeroDateTimeBehavior=CONVERT_TO_NULL");
        config.setUsername("root");
        config.setPassword("");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(5);
        config.setIdleTimeout(300000);
        config.setConnectionTimeout(20000);

        this.dataSource = new HikariDataSource(config);
    }

    @Override
    public void create(Mandataire mandataire) {
        String sql = "INSERT INTO mandataire (id, nom, fonction, date_mandat, code_ecole, cin, contact, email, nom_etablissement, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, mandataire.getId());
            pstmt.setString(2, mandataire.getNom());
            pstmt.setString(3, mandataire.getFonction());
            pstmt.setDate(4, mandataire.getDateMandat());
            pstmt.setString(5, mandataire.getCodeEcole());
            pstmt.setString(6, mandataire.getCin());
            pstmt.setString(7, mandataire.getContact());
            pstmt.setString(8, mandataire.getEmail());
            pstmt.setString(9, mandataire.getNomEtablissement());
            pstmt.setTimestamp(10, mandataire.getCreatedAt());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création du mandataire", e);
        }
    }

    @Override
    public Mandataire read(String id) {
        String sql = "SELECT * FROM mandataire WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMandataire(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la lecture du mandataire", e);
        }
        return null;
    }

    @Override
    public List<Mandataire> readAll() {
        List<Mandataire> mandataires = new ArrayList<>();
        String sql = "SELECT * FROM mandataire";
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                mandataires.add(mapResultSetToMandataire(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la lecture de tous les mandataires", e);
        }
        return mandataires;
    }

    @Override
    public void update(Mandataire mandataire) {
        String sql = "UPDATE mandataire SET nom = ?, fonction = ?, date_mandat = ?, code_ecole = ?, " +
                     "CIN = ?, contact = ?, email = ?, nom_etablissement = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, mandataire.getId());
            pstmt.setString(2, mandataire.getNom());
            pstmt.setString(3, mandataire.getFonction());
            pstmt.setDate(4, mandataire.getDateMandat());
            pstmt.setString(5, mandataire.getCodeEcole());
            pstmt.setString(6, mandataire.getCin());
            pstmt.setString(7, mandataire.getContact());
            pstmt.setString(8, mandataire.getEmail());
            pstmt.setString(9, mandataire.getNomEtablissement());
            pstmt.setTimestamp(10, mandataire.getCreatedAt());
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Mandataire non trouvé avec l'ID: " + mandataire.getId());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du mandataire", e);
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM mandataire WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Mandataire non trouvé avec l'ID: " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du mandataire", e);
        }
    }

    public void exportToPDF(String filePath) {
        List<Mandataire> mandataires = readAll();
        try {
            // Create document
            PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filePath));
            try (Document document = new Document(pdfDoc, PageSize.A4)) {
                document.setMargins(50, 50, 50, 50);

                // Create fonts
                PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
                PdfFont regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

                // Title
                Style titleStyle = new Style()
                        .setFont(boldFont)
                        .setFontSize(18);

                Paragraph title = new Paragraph("Liste des Mandataires")
                        .addStyle(titleStyle)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginBottom(20);
                document.add(title);

                // Table
                Table table = new Table(UnitValue.createPercentArray(new float[]{3, 2, 2, 2, 3}))
                        .setWidth(UnitValue.createPercentValue(100));

                // Headers
                String[] headers = {"Nom", "Fonction", "CIN", "Contact", "Email"};
                for (String header : headers) {
                    Cell headerCell = new Cell()
                            .add(new Paragraph(header).setFont(boldFont))
                            .setBackgroundColor(ColorConstants.GRAY);
                    table.addHeaderCell(headerCell);
                }

                // Data
                for (Mandataire mandataire : mandataires) {
                    table.addCell(new Cell().add(new Paragraph(mandataire.getNom()).setFont(regularFont)));
                    table.addCell(new Cell().add(new Paragraph(mandataire.getFonction()).setFont(regularFont)));
                    table.addCell(new Cell().add(new Paragraph(mandataire.getCin()).setFont(regularFont)));
                    table.addCell(new Cell().add(new Paragraph(mandataire.getContact()).setFont(regularFont)));
                    table.addCell(new Cell().add(new Paragraph(mandataire.getEmail()).setFont(regularFont)));
                }

                document.add(table);
            }
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'export PDF", e);
        }
    }

    private Mandataire mapResultSetToMandataire(ResultSet rs) throws SQLException {
        return new Mandataire(
                rs.getString("id"),
                rs.getString("nom"),
                rs.getString("fonction"),
                rs.getDate("date_mandat"),
                rs.getString("code_ecole"),
                rs.getString("cin"),
                rs.getString("contact"),
                rs.getString("email"),
                rs.getString("nom_etablissement"),
                rs.getTimestamp("created_at")
        );
    }

    @Override
    public List<Mandataire> search(String keyword) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void exportToPDF(List<Mandataire> mandataires, String filePath) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
