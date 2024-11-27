package mainframe;

import dashboard.DashboardPanel;
import mandataire.MandatairePanel;
import authentification.*;
import navigation.Sidebar;
import navigation.Navbar;
import caisseecole.GlobalCaisseEcolePanel;
import etablissement.EtablissementPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JPanel;
import planificationpec.PlanificationPECPanel;
import transaction.TransactionPanel;

public class MainFrame extends JFrame {
    private final JPanel contentPanel;
    private final CardLayout cardLayout;
    private final Utilisateur utilisateur;
    private final UtilisateurController controller;

    public MainFrame(Utilisateur utilisateur, UtilisateurController controller) throws ClassNotFoundException {
    this.utilisateur = utilisateur;
    this.controller = controller;
    
    // Configuration de base de la fenêtre
    setTitle("FEFFI Gestion");
    setSize(1200, 800);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
    setLayout(new BorderLayout());
    
    // Maximiser la fenêtre
    setExtendedState(JFrame.MAXIMIZED_BOTH);
    
    // Création du panneau de navigation
    Navbar navbar = new Navbar();
    add(navbar, BorderLayout.NORTH);
    
    // Création du panneau principal
    JPanel mainContent = new JPanel(new BorderLayout());
    
    // Création et ajout de la sidebar
    Sidebar sidebar = new Sidebar(this);
    mainContent.add(sidebar, BorderLayout.WEST);
    
    // Initialisation du CardLayout et du panneau de contenu
    cardLayout = new CardLayout();
    contentPanel = new JPanel(cardLayout);
    contentPanel.setBackground(Color.WHITE);
    
    try {
        // Création des panneaux avec gestion des exceptions
        JPanel dashboardPanel = new DashboardPanel();
        contentPanel.add(dashboardPanel, "dashboard");
        
        JPanel etablissementPanel = new EtablissementPanel();
        contentPanel.add(etablissementPanel, "etablissement");
        
        JPanel mandatairePanel = new MandatairePanel();
        contentPanel.add(mandatairePanel, "mandataires");
        
        JPanel transactionPanel = new TransactionPanel();
        contentPanel.add(transactionPanel, "liste-transactions");
        
        JPanel caissePanel = new GlobalCaisseEcolePanel();
        contentPanel.add(caissePanel, "caisse");
        
        JPanel pecPanel = new PlanificationPECPanel();
        contentPanel.add(pecPanel, "pec");
        
    } catch (ClassNotFoundException e) {
        System.err.println("Erreur lors de l'initialisastion des panneaux: " + e.getMessage());
    }
    
    // Ajout du panneau de contenu au panneau principal
    mainContent.add(contentPanel, BorderLayout.CENTER);
    
    // Ajout du panneau principal à la fenêtre
    add(mainContent, BorderLayout.CENTER);
}


    public void navigate(String page) {
        cardLayout.show(contentPanel, page);
    }
}