package etablissement;

import java.io.FileOutputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

// Imports iText 7
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
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

public class EtablissementDAOImpl implements EtablissementDAO {
    private final DataSource dataSource;
    
    public EtablissementDAOImpl() throws ClassNotFoundException {
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
    public void create(Etablissement etablissement) {
        String sql = "INSERT INTO etablissement (id, nom, type, localisation, created_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, etablissement.getId());
            pstmt.setString(2, etablissement.getNom());
            pstmt.setString(3, etablissement.getType());
            pstmt.setString(4, etablissement.getLocalisation());
            pstmt.setTimestamp(5, etablissement.getCreatedAt());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création de l'établissement", e);
        }
    }

    @Override
    public Etablissement read(String id) {
        String sql = "SELECT * FROM etablissement WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEtablissement(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la lecture de l'établissement", e);
        }
        return null;
    }

    @Override
    public List<Etablissement> readAll() {
        List<Etablissement> etablissements = new ArrayList<>();
        String sql = "SELECT * FROM etablissement";
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                etablissements.add(mapResultSetToEtablissement(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la lecture de tous les établissements", e);
        }
        return etablissements;
    }

    @Override
    public void update(Etablissement etablissement) {
        String sql = "UPDATE etablissement SET nom = ?, type = ?, localisation = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, etablissement.getNom());
            pstmt.setString(2, etablissement.getType());
            pstmt.setString(3, etablissement.getLocalisation());
            pstmt.setString(4, etablissement.getId());
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Établissement non trouvé avec l'ID: " + etablissement.getId());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de l'établissement", e);
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM etablissement WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Établissement non trouvé avec l'ID: " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de l'établissement", e);
        }
    }

    @Override
    public List<Etablissement> search(String keyword) {
        List<Etablissement> etablissements = new ArrayList<>();
        String sql = "SELECT * FROM etablissement WHERE nom LIKE ? OR type LIKE ? OR localisation LIKE ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    etablissements.add(mapResultSetToEtablissement(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche d'établissements", e);
        }
        return etablissements;
    }

    public void exportToPDF(String filePath) {
        List<Etablissement> etablissements = readAll();
        try {
            // Create document
            PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filePath));
            try (Document document = new Document(pdfDoc, PageSize.A4)) {
                document.setMargins(50, 50, 50, 50);
                
                // Create fonts
                PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
                PdfFont regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
                
                // Style for title
                Style titleStyle = new Style()
                        .setFont(boldFont)
                        .setFontSize(18);
                
                // Add title
                Paragraph title = new Paragraph("Liste des Établissements")
                        .addStyle(titleStyle)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginBottom(20);
                document.add(title);
                
                // Create table
                Table table = new Table(UnitValue.createPercentArray(new float[]{3, 2, 3, 2}))
                        .setWidth(UnitValue.createPercentValue(100));
                
                // Style for headers
                Style headerStyle = new Style()
                        .setFont(boldFont)
                        .setFontSize(12)
                        .setFontColor(new DeviceRgb(255, 255, 255));
                
                DeviceRgb headerBackground = new DeviceRgb(44, 62, 80);
                
                // Add headers
                String[] headers = {"Nom", "Type", "Localisation", "Date de création"};
                for (String headerText : headers) {
                    Cell header = new Cell()
                            .setBackgroundColor(headerBackground)
                            .setPadding(8)
                            .add(new Paragraph(headerText).addStyle(headerStyle));
                    table.addHeaderCell(header);
                }
                
                // Style for data
                Style dataStyle = new Style()
                        .setFont(regularFont)
                        .setFontSize(11);
                
                DeviceRgb alternateBackground = new DeviceRgb(241, 241, 241);
                DeviceRgb whiteBackground = new DeviceRgb(255, 255, 255);
                
                // Add data
                for (int i = 0; i < etablissements.size(); i++) {
                    Etablissement etablissement = etablissements.get(i);
                    DeviceRgb background = (i % 2 == 0) ? whiteBackground : alternateBackground;
                    
                    addCellToTable(table, etablissement.getNom(), background, dataStyle);
                    addCellToTable(table, etablissement.getType(), background, dataStyle);
                    addCellToTable(table, etablissement.getLocalisation(), background, dataStyle);
                    addCellToTable(table, etablissement.getCreatedAt().toString(), background, dataStyle);
                }
                
                document.add(table);
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'export PDF", e);
        }
    }

    private void addCellToTable(Table table, String content, DeviceRgb background, Style style) {
        Cell cell = new Cell()
            .setBackgroundColor(background)
            .setPadding(5)
            .add(new Paragraph(content).addStyle(style));
        table.addCell(cell);
    }
    
    private Etablissement mapResultSetToEtablissement(ResultSet rs) throws SQLException {
        return new Etablissement(
            rs.getString("id"),
            rs.getString("nom"),
            rs.getString("type"),
            rs.getString("localisation"),
            rs.getTimestamp("created_at")
        );
    }

    @Override
    public void exportToPDF(List<Etablissement> etablissements, String filePath) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}