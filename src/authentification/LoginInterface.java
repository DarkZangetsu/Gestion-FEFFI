package authentification;

import mainframe.MainFrame;
import db.DriverManager;
import com.sun.jdi.connect.spi.Connection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginInterface extends JFrame {
    private final JPanel mainPanel;
    private JTextField nomField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel titleLabel;
    private JLabel imageLabel;
    private JPanel rightPanel;
    
    private final Color PRIMARY_COLOR = new Color(47, 128, 237);    // Bleu normal
    private final Color PRIMARY_DARK = new Color(25, 95, 190);      // Bleu foncé au survol
    private final Color BACKGROUND_COLOR = new Color(250, 251, 252);
    private final Color TEXT_COLOR = new Color(27, 32, 43);         // Texte sombre
    private final Color BORDER_COLOR = new Color(226, 232, 240);
    private final Color FIELD_BACKGROUND = Color.WHITE;
    private final Color PLACEHOLDER_COLOR = new Color(160, 174, 192); // Couleur pour le placeholder
    
    private final UtilisateurController controller;
    
    public LoginInterface(UtilisateurController controller) {
        this.controller = controller;
        
        setTitle("Connexion");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setBackground(BACKGROUND_COLOR);

        mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        createLeftPanel();
        createRightPanel();
        
        mainPanel.add(imageLabel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);
        
        setContentPane(mainPanel);
        setResizable(false);
    }
    
    private void createLeftPanel() {
        imageLabel = new JLabel();
        try {
            File imageFile = new File("src/authentification/login.jpg");
            if (imageFile.exists()) {
                ImageIcon imageIcon = new ImageIcon(ImageIO.read(imageFile));
                Image image = imageIcon.getImage().getScaledInstance(400, 500, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(image));
            } else {
                java.net.URL imageURL = getClass().getResource("/login_illustration.png");
                if (imageURL != null) {
                    ImageIcon imageIcon = new ImageIcon(imageURL);
                    Image image = imageIcon.getImage().getScaledInstance(400, 500, Image.SCALE_SMOOTH);
                    imageLabel.setIcon(new ImageIcon(image));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        imageLabel.setPreferredSize(new Dimension(400, 500));
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
    }
    
    private void createRightPanel() {
        rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(BACKGROUND_COLOR);
        
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(60, 50, 50, 50));
        contentPanel.setMaximumSize(new Dimension(400, 600));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        
        titleLabel = new JLabel("Connexion");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        gbc.gridwidth = 2;
        contentPanel.add(titleLabel, gbc);
        
        gbc.gridy++;
        gbc.gridwidth = 1;
        contentPanel.add(Box.createVerticalStrut(35), gbc);

        nomField = new JTextField(20);
        nomField.setText("Nom d'utilisateur");
        nomField.setForeground(PLACEHOLDER_COLOR);
        styleTextField(nomField);
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        contentPanel.add(nomField, gbc);
        
        passwordField = new JPasswordField(20);
        passwordField.setEchoChar((char) 0);
        passwordField.setText("Mot de passe");
        passwordField.setForeground(PLACEHOLDER_COLOR);
        styleTextField(passwordField);
        
        gbc.gridy++;
        contentPanel.add(passwordField, gbc);
        
        loginButton = new JButton("Se connecter");
        styleButton(loginButton);
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.CENTER;
        contentPanel.add(loginButton, gbc);
        
        // Ajout du gestionnaire d'événements pour le bouton de connexion
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nom = nomField.getText();
                String motDePasse = new String(passwordField.getPassword());
                
                // Tentative de connexion
                Utilisateur utilisateur = controller.login(nom, motDePasse);
                
                if (utilisateur != null) {
                    // Connexion réussie, ouvrir le MainFrame
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            MainFrame mainFrame = null;
                            try {
                                mainFrame = new MainFrame(utilisateur, controller);
                            } catch (ClassNotFoundException ex) {
                                Logger.getLogger(LoginInterface.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            mainFrame.setVisible(true);
                            dispose(); // Fermer la fenêtre de login
                        }
                    });
                } else {
                    // Échec de la connexion
                    JOptionPane.showMessageDialog(LoginInterface.this, 
                        "Nom d'utilisateur ou mot de passe incorrect", 
                        "Erreur de connexion", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        JPanel signupPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        signupPanel.setBackground(BACKGROUND_COLOR);
        JLabel noAccountLabel = new JLabel("Vous n'avez pas de compte ? ");
        noAccountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JLabel signupLink = new JLabel("S'inscrire");
        styleLink(signupLink);
        signupPanel.add(noAccountLabel);
        signupPanel.add(signupLink);
        signupLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        RegisterInterface registerInterface = new RegisterInterface(controller);
                        registerInterface.setVisible(true);
                    }
                });
            }
        });

        gbc.gridy++;
        contentPanel.add(signupPanel, gbc);
        
        JPanel wrapperPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapperPanel.setBackground(BACKGROUND_COLOR);
        wrapperPanel.add(contentPanel);
        
        rightPanel.add(wrapperPanel);

        // Ajout des gestionnaires d'événements pour les placeholders
        addPlaceholderBehavior(nomField, "Nom d'utilisateur");
        addPlaceholderBehavior(passwordField, "Mot de passe");
    }

    private void addPlaceholderBehavior(JTextField field, String placeholder) {
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(TEXT_COLOR);
                    if (field instanceof JPasswordField) {
                        ((JPasswordField) field).setEchoChar('●');
                    }
                }
                field.setBorder(BorderFactory.createCompoundBorder(
                    new RoundedBorder(PRIMARY_COLOR, 8),
                    BorderFactory.createEmptyBorder(5, 15, 5, 15)
                ));
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(PLACEHOLDER_COLOR);
                    if (field instanceof JPasswordField) {
                        ((JPasswordField) field).setEchoChar((char) 0);
                    }
                }
                field.setBorder(BorderFactory.createCompoundBorder(
                    new RoundedBorder(BORDER_COLOR, 8),
                    BorderFactory.createEmptyBorder(5, 15, 5, 15)
                ));
            }
        });
    }
    
    private void styleTextField(JTextField field) {
        field.setMaximumSize(new Dimension(300, 45));
        field.setPreferredSize(new Dimension(300, 45));
        field.setBackground(FIELD_BACKGROUND);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(BORDER_COLOR, 8),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
    }
    
    private void styleButton(JButton button) {
        button.setMaximumSize(new Dimension(300, 45));
        button.setPreferredSize(new Dimension(300, 45));
        button.setBackground(Color.WHITE);  // Fond blanc par défaut
        button.setForeground(PRIMARY_COLOR);  // Texte bleu
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setBorder(new RoundedBorder(PRIMARY_COLOR, 8));  // Bordure bleue
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(PRIMARY_COLOR);  // Fond bleu au survol
                button.setForeground(Color.WHITE);    // Texte blanc au survol
                button.setBorder(new RoundedBorder(PRIMARY_COLOR, 8));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.WHITE);     // Retour au fond blanc
                button.setForeground(PRIMARY_COLOR);   // Retour au texte bleu
                button.setBorder(new RoundedBorder(PRIMARY_COLOR, 8));
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(PRIMARY_DARK);
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                button.setBackground(PRIMARY_COLOR);
            }
        });
    }
    
    private void styleLink(JLabel link) {
        link.setFont(new Font("Segoe UI", Font.BOLD, 14));
        link.setForeground(PRIMARY_COLOR);
        link.setCursor(new Cursor(Cursor.HAND_CURSOR));
        link.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        
        link.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                link.setForeground(PRIMARY_DARK);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                link.setForeground(PRIMARY_COLOR);
            }
        });
    }
    
    private class RoundedBorder extends AbstractBorder {
        private final Color color;
        private final int radius;
        
        RoundedBorder(Color color, int radius) {
            this.color = color;
            this.radius = radius;
        }
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(color);
            g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2d.dispose();
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(8, 8, 8, 8);
        }
    }

    // Méthode main pour tester l'interface (optionnel)
   public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                // Establish a database connection 
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/feffi?zeroDateTimeBehavior=CONVERT_TO_NULL", "root", "");
                
                // Create UtilisateurController with the connection
                UtilisateurController controller = new UtilisateurController((java.sql.Connection) connection);
                LoginInterface login = new LoginInterface(controller);
                login.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                // Handle connection error
                JOptionPane.showMessageDialog(null, "Erreur de connexion à la base de données");
            }
        }
    });
}
}