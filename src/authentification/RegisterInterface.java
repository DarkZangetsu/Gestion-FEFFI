package authentification;

import authentification.LoginInterface;
import db.DriverManager;
import com.sun.jdi.connect.spi.Connection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import javax.imageio.ImageIO;
import java.io.File;

public class RegisterInterface extends JFrame {
    private JPanel mainPanel;
    private JTextField nomField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton registerButton;
    private JLabel titleLabel;
    private JPanel formPanel;
    private JLabel imageLabel;
    private JPanel rightPanel;
    
    private final Color PRIMARY_COLOR = new Color(47, 128, 237);    // Bleu normal
    private final Color PRIMARY_DARK = new Color(25, 95, 190);      // Bleu foncé au survol
    private final Color BACKGROUND_COLOR = new Color(250, 251, 252);
    private final Color TEXT_COLOR = new Color(27, 32, 43);         // Texte sombre
    private final Color BORDER_COLOR = new Color(226, 232, 240);
    private final Color FIELD_BACKGROUND = Color.WHITE;
    private final Color PLACEHOLDER_COLOR = new Color(160, 174, 192); // Couleur pour le placeholder
    
    private UtilisateurController controller;
    
    public RegisterInterface(UtilisateurController controller) {
        this.controller = controller;
        
        setTitle("Inscription Administrateur");
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
            File imageFile = new File("src/authentification/register.jpg");
            if (imageFile.exists()) {
                ImageIcon imageIcon = new ImageIcon(ImageIO.read(imageFile));
                Image image = imageIcon.getImage().getScaledInstance(400, 500, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(image));
            } else {
                java.net.URL imageURL = getClass().getResource("/register_illustration.png");
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
        contentPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 50, 50));
        contentPanel.setMaximumSize(new Dimension(400, 600));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        
        titleLabel = new JLabel("Inscription Administrateur");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        gbc.gridwidth = 2;
        contentPanel.add(titleLabel, gbc);
        
        gbc.gridy++;
        gbc.gridwidth = 1;
        contentPanel.add(Box.createVerticalStrut(25), gbc);

        nomField = new JTextField(20);
        nomField.setText("Nom d'utilisateur");
        nomField.setForeground(PLACEHOLDER_COLOR);
        styleTextField(nomField);
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        contentPanel.add(nomField, gbc);
        
        emailField = new JTextField(20);
        emailField.setText("Email");
        emailField.setForeground(PLACEHOLDER_COLOR);
        styleTextField(emailField);
        
        gbc.gridy++;
        contentPanel.add(emailField, gbc);
        
        passwordField = new JPasswordField(20);
        passwordField.setEchoChar((char) 0);
        passwordField.setText("Mot de passe");
        passwordField.setForeground(PLACEHOLDER_COLOR);
        styleTextField(passwordField);
        
        gbc.gridy++;
        contentPanel.add(passwordField, gbc);
        
        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setEchoChar((char) 0);
        confirmPasswordField.setText("Confirmer le mot de passe");
        confirmPasswordField.setForeground(PLACEHOLDER_COLOR);
        styleTextField(confirmPasswordField);
        
        gbc.gridy++;
        contentPanel.add(confirmPasswordField, gbc);
        
        registerButton = new JButton("S'inscrire");
        styleButton(registerButton);
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.CENTER;
        contentPanel.add(registerButton, gbc);
        
        // Ajout du gestionnaire d'événements pour le bouton d'inscription
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nom = nomField.getText();
                String email = emailField.getText();
                String motDePasse = new String(passwordField.getPassword());
                String confirmMotDePasse = new String(confirmPasswordField.getPassword());
                
                // Validation des champs
                if (nom.isEmpty() || nom.equals("Nom d'utilisateur") ||
                    email.isEmpty() || email.equals("Email") ||
                    motDePasse.isEmpty() || motDePasse.equals("Mot de passe") ||
                    confirmMotDePasse.isEmpty() || confirmMotDePasse.equals("Confirmer le mot de passe")) {
                    JOptionPane.showMessageDialog(RegisterInterface.this, 
                        "Veuillez remplir tous les champs", 
                        "Erreur", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Vérification de la correspondance des mots de passe
                if (!motDePasse.equals(confirmMotDePasse)) {
                    JOptionPane.showMessageDialog(RegisterInterface.this, 
                        "Les mots de passe ne correspondent pas", 
                        "Erreur", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                try {
                    // Utiliser un ID d'établissement par défaut pour l'admin
                    String etablissementId = "admin_etablissement"; 
                    
                    // Tentative d'inscription
                    Utilisateur utilisateur = controller.register(etablissementId, nom, email, motDePasse);
                    
                    JOptionPane.showMessageDialog(RegisterInterface.this, 
                        "Inscription réussie!", 
                        "Succès", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Optionnel : Retourner à l'écran de login ou ouvrir directement le MainFrame
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            LoginInterface loginInterface = new LoginInterface(controller);
                            loginInterface.setVisible(true);
                            dispose(); // Fermer la fenêtre d'inscription
                        }
                    });
                    
                } catch (RuntimeException ex) {
                    JOptionPane.showMessageDialog(RegisterInterface.this, 
                        ex.getMessage(), 
                        "Erreur d'inscription", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        JPanel loginPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        loginPanel.setBackground(BACKGROUND_COLOR);
        JLabel hasAccountLabel = new JLabel("Vous avez déjà un compte ? ");
        hasAccountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JLabel loginLink = new JLabel("Se connecter");
        styleLink(loginLink);
        
        loginLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Retourner à l'écran de login
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        LoginInterface loginInterface = new LoginInterface(controller);
                        loginInterface.setVisible(true);
                        dispose(); // Fermer la fenêtre d'inscription
                    }
                });
            }
        });
        
        loginPanel.add(hasAccountLabel);
        loginPanel.add(loginLink);

        gbc.gridy++;
        contentPanel.add(loginPanel, gbc);
        
        JPanel wrapperPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapperPanel.setBackground(BACKGROUND_COLOR);
        wrapperPanel.add(contentPanel);
        
        rightPanel.add(wrapperPanel);

        // Ajout des gestionnaires d'événements pour les placeholders
        addPlaceholderBehavior(nomField, "Nom d'utilisateur");
        addPlaceholderBehavior(emailField, "Email");
        addPlaceholderBehavior(passwordField, "Mot de passe");
        addPlaceholderBehavior(confirmPasswordField, "Confirmer le mot de passe");
    }

    // Les méthodes suivantes sont identiques à celles de LoginInterface
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
                    RegisterInterface register = new RegisterInterface(controller);
                    register.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    // Handle connection error
                    JOptionPane.showMessageDialog(null, "Erreur de connexion à la base de données");
                }
            }
        });
    }
}
                   
