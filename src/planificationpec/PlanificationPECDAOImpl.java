package planificationpec;

import com.itextpdf.io.font.constants.StandardFonts;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
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

import javax.sql.DataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class PlanificationPECDAOImpl implements PlanificationPECDAO  {
    private final DataSource dataSource;

    public PlanificationPECDAOImpl() throws ClassNotFoundException {
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
    public void create(PlanificationPEC planification) {
        String sql = "INSERT INTO planificationpec (id, dateDebut, dateFin, activities, created_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, planification.getId());
            pstmt.setDate(2, planification.getDateDebut());
            pstmt.setDate(3, planification.getDateFin());
            pstmt.setString(4, planification.getActivities());
            pstmt.setTimestamp(5, planification.getCreatedAt());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création de la planification", e);
        }
    }

    @Override
    public PlanificationPEC read(String id) {
        String sql = "SELECT * FROM planificationpec WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPlanification(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la lecture de la planification", e);
        }
        return null;
    }

    @Override
    public List<PlanificationPEC> readAll() {
        List<PlanificationPEC> planifications = new ArrayList<>();
        String sql = "SELECT * FROM planificationpec";
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                planifications.add(mapResultSetToPlanification(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la lecture des planifications", e);
        }
        return planifications;
    }

    @Override
    public void update(PlanificationPEC planification) {
        String sql = "UPDATE planificationpec SET dateDebut = ?, dateFin = ?, activities = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, planification.getDateDebut());
            pstmt.setDate(2, planification.getDateFin());
            pstmt.setString(3, planification.getActivities());
            pstmt.setString(4, planification.getId());
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Planification non trouvée avec l'ID: " + planification.getId());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de la planification", e);
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM planificationpec WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Planification non trouvée avec l'ID: " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de la planification", e);
        }
    }

    @Override
    public List<PlanificationPEC> search(String keyword) {
        List<PlanificationPEC> planifications = new ArrayList<>();
        String sql = "SELECT * FROM planificationpec WHERE activities LIKE ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    planifications.add(mapResultSetToPlanification(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de planifications", e);
        }
        return planifications;
    }

    public void exportToPDF(String filePath) {
        List<PlanificationPEC> planifications = readAll();
        try (PdfWriter writer = new PdfWriter(filePath)) {
            PdfDocument pdfDoc = new PdfDocument(writer);
            try (Document document = new Document(pdfDoc, PageSize.A4)) {
                document.setMargins(50, 50, 50, 50);
                
                PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
                PdfFont regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
                
                Style headerStyle = new Style().setFont(boldFont).setFontSize(12);
                Style dataStyle = new Style().setFont(regularFont).setFontSize(10);
                
                document.add(new Paragraph("Planification PEC").setFont(boldFont).setFontSize(18).setTextAlignment(TextAlignment.CENTER));
                
                Table table = new Table(UnitValue.createPercentArray(new float[]{2, 2, 2, 3})).setWidth(UnitValue.createPercentValue(100));
                
                table.addHeaderCell(new Cell().add(new Paragraph("ID").addStyle(headerStyle)));
                table.addHeaderCell(new Cell().add(new Paragraph("Date Début").addStyle(headerStyle)));
                table.addHeaderCell(new Cell().add(new Paragraph("Date Fin").addStyle(headerStyle)));
                table.addHeaderCell(new Cell().add(new Paragraph("Activités").addStyle(headerStyle)));
                
                for (PlanificationPEC planification : planifications) {
                    table.addCell(new Cell().add(new Paragraph(planification.getId()).addStyle(dataStyle)));
                    table.addCell(new Cell().add(new Paragraph(planification.getDateDebut().toString()).addStyle(dataStyle)));
                    table.addCell(new Cell().add(new Paragraph(planification.getDateFin().toString()).addStyle(dataStyle)));
                    table.addCell(new Cell().add(new Paragraph(planification.getActivities()).addStyle(dataStyle)));
                }
                
                document.add(table);
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'exportation PDF", e);
        }
    }

    private PlanificationPEC mapResultSetToPlanification(ResultSet rs) throws SQLException {
        return new PlanificationPEC(
            rs.getString("id"),
            rs.getDate("dateDebut"),
            rs.getDate("dateFin"),
            rs.getString("activities"),
            rs.getTimestamp("created_at")
        );
    }

    @Override
    public void exportToPDF(List<PlanificationPEC> planifications, String filePath) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
