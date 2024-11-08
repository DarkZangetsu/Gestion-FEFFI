import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends JPanel {
    public DashboardPanel() {
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());
        
        // Titre
        JLabel title = new JLabel("Tableau de Bord");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(title, BorderLayout.NORTH);
    }
}