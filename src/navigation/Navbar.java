package navigation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

public class Navbar extends JPanel {
    private Color primaryColor = new Color(23, 32, 42); // Darker blue-gray for professional look
    private Color iconColor = new Color(41, 128, 185); // Light blue for icons
    private Color dropdownColor = new Color(248, 249, 250); // Light gray for dropdown menu

    public Navbar() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(0, 60));
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        setLayout(new BorderLayout());

        // Bouton menu (hamburger)
        JPanel menuButtonPanel = new JPanel();
        menuButtonPanel.setLayout(new BoxLayout(menuButtonPanel, BoxLayout.X_AXIS));
        menuButtonPanel.setBackground(Color.WHITE);
        menuButtonPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

        JButton menuButton = new JButton("☰");
        menuButton.setFont(new Font("Arial", Font.BOLD, 18));
        menuButton.setForeground(iconColor);
        menuButton.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 20));
        menuButton.setBackground(Color.WHITE);

        menuButtonPanel.add(menuButton);
        menuButtonPanel.add(Box.createHorizontalGlue());
        add(menuButtonPanel, BorderLayout.WEST);

        // Profil utilisateur
        JPanel userProfile = new JPanel();
        userProfile.setLayout(new BoxLayout(userProfile, BoxLayout.X_AXIS));
        userProfile.setBackground(Color.WHITE);
        userProfile.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));

        JLabel userIcon = new JLabel("👤");
        userIcon.setForeground(iconColor);
        userIcon.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel userName = new JLabel("Utilisateur");
        userName.setForeground(primaryColor);
        userName.setFont(new Font("Arial", Font.PLAIN, 14));
        userName.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

        // Menu déroulant pour les options de profil
        JPopupMenu profileMenu = new JPopupMenu();
        JMenuItem profileOption = new JMenuItem("Profil");
        JMenuItem logoutOption = new JMenuItem("Déconnexion");

        profileMenu.add(profileOption);
        profileMenu.add(logoutOption);

        userProfile.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                profileMenu.show(userProfile, e.getX(), e.getY());
            }
        });

        userProfile.add(userIcon);
        userProfile.add(userName);
        add(userProfile, BorderLayout.EAST);
    }
}