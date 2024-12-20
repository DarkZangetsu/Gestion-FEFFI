package navigation;

import mainframe.MainFrame;
import java.awt.*;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

public class Sidebar extends JPanel {
    private final Color primaryColor = new Color(23, 32, 42); // Darker blue-gray for professional look
    private final Color hoverColor = new Color(33, 97, 140);  // Light blue for hover effect
    private final Color activeColor = new Color(33, 97, 140); // Same as hover for consistency
    private final Color sectionColor = new Color(41, 128, 185); // Section headers color
    private final MainFrame mainFrame;
    private final Map<String, JPanel> menuItems = new HashMap<>();
    private String activePage = "";

    public Sidebar(MainFrame frame) {
        this.mainFrame = frame;
        setPreferredSize(new Dimension(250, 0));
        setBackground(primaryColor);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Logo
        JPanel logoPanel = new JPanel();
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.X_AXIS));
        logoPanel.setBackground(primaryColor);
        logoPanel.setMaximumSize(new Dimension(250, 40));
        logoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel logo = new JLabel("FEFFI Gestion");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("Arial", Font.BOLD, 16));

        logoPanel.add(logo);
        logoPanel.add(Box.createHorizontalGlue());
        add(logoPanel);

        // Sections
        addSection("Administration");
        addMenuItem("Tableau de bord", "dashboard", "🏠");
        addMenuItem("Établissement", "etablissement", "🏫");
        addMenuItem("Mandataires", "mandataires", "📋");
        
        // Nouvelle section Transactions
        addSection("Transactions et caisse");
        addMenuItem("Transactions", "liste-transactions", "📝");
        addMenuItem("Caisse École", "caisse", "💰");
        
        addSection("Planification");
        addMenuItem("Planification PEC", "pec", "📅");

        addSection("Paramètres");
        addMenuItem("Utilisateurs", "utilisateurs", "👤");
        addMenuItem("Configuration", "configuration", "⚙️");

        // Fill remaining vertical space
        add(Box.createVerticalGlue());
    }

    private void addSection(String title) {
        JPanel sectionPanel = new JPanel();
        sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.X_AXIS));
        sectionPanel.setBackground(primaryColor);
        sectionPanel.setMaximumSize(new Dimension(250, 40));
        sectionPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));

        JLabel sectionLabel = new JLabel(title);
        sectionLabel.setForeground(sectionColor);
        sectionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        sectionPanel.add(sectionLabel);
        sectionPanel.add(Box.createHorizontalGlue());
        
        add(sectionPanel);
    }

    private void addMenuItem(String text, String page, String icon) {
        JPanel menuItem = new JPanel();
        menuItem.setLayout(new BoxLayout(menuItem, BoxLayout.X_AXIS));
        menuItem.setBackground(primaryColor);
        menuItem.setMaximumSize(new Dimension(250, 40));
        menuItem.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setForeground(Color.WHITE);
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        JLabel textLabel = new JLabel(text);
        textLabel.setForeground(Color.WHITE);
        textLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        menuItem.add(iconLabel);
        menuItem.add(textLabel);
        menuItem.add(Box.createHorizontalGlue());

        // Store menuItem in map for later access
        menuItems.put(page, menuItem);

        // Add mouse effects
        menuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setActivePage(page);
                mainFrame.navigate(page);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (!page.equals(activePage)) {
                    menuItem.setBackground(hoverColor);
                }
                menuItem.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!page.equals(activePage)) {
                    menuItem.setBackground(primaryColor);
                }
            }
        });

        add(menuItem);
    }

    // Method to set active page and update UI
    public void setActivePage(String page) {
        // Reset old active page
        if (!activePage.isEmpty() && menuItems.containsKey(activePage)) {
            menuItems.get(activePage).setBackground(primaryColor);
        }
        
        // Set new active page
        activePage = page;
        if (menuItems.containsKey(page)) {
            menuItems.get(page).setBackground(activeColor);
        }
    }

    // Method to get current active page
    public String getActivePage() {
        return activePage;
    }
}