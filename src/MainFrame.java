import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainFrame extends JFrame {
    private JPanel contentPanel;
    private CardLayout cardLayout;

    public MainFrame() {
        setTitle("FEFFI Gestion");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Configuration principale
        setLayout(new BorderLayout());
        
        // Ajout de la Navbar
        add(new Navbar(), BorderLayout.NORTH);
        
        // Ajout de la Sidebar et du contenu
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.add(new Sidebar(this), BorderLayout.WEST);
        
        // Panel pour le contenu changeant
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Color.WHITE);
        
        // Ajout des différentes pages
        contentPanel.add(new DashboardPanel(), "dashboard");
        
        mainContent.add(contentPanel, BorderLayout.CENTER);
        add(mainContent, BorderLayout.CENTER);
    }

    public void navigate(String page) {
        cardLayout.show(contentPanel, page);
    }
}