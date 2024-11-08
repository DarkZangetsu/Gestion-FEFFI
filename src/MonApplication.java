import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MonApplication extends JFrame {
    private JPanel mainPanel;
    private JMenuBar menuBar;
    private JTextField champTexte;
    private JButton boutonValider;
    private JTextArea zoneTexte;

    public MonApplication() {
        // Configuration de base de la fenêtre
        setTitle("Mon Application");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Création du menu
        createMenu();

        // Création du panneau principal
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        setContentPane(mainPanel);

        // Création des composants
        createComponents();

        // Ajout des écouteurs d'événements
        addListeners();
    }

    private void createMenu() {
        menuBar = new JMenuBar();
        
        JMenu fichierMenu = new JMenu("Fichier");
        JMenuItem nouveauItem = new JMenuItem("Nouveau");
        JMenuItem quitterItem = new JMenuItem("Quitter");
        
        fichierMenu.add(nouveauItem);
        fichierMenu.addSeparator();
        fichierMenu.add(quitterItem);
        
        menuBar.add(fichierMenu);
        setJMenuBar(menuBar);
    }

    private void createComponents() {
        // Panneau supérieur avec champ texte et bouton
        JPanel topPanel = new JPanel(new FlowLayout());
        champTexte = new JTextField(20);
        boutonValider = new JButton("Valider");
        topPanel.add(champTexte);
        topPanel.add(boutonValider);
        
        // Zone de texte centrale avec scroll
        zoneTexte = new JTextArea();
        zoneTexte.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(zoneTexte);
        
        // Ajout des composants au panneau principal
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
    }

    private void addListeners() {
        boutonValider.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String texte = champTexte.getText();
                if (!texte.isEmpty()) {
                    zoneTexte.append(texte + "\n");
                    champTexte.setText("");
                }
            }
        });
    }

    public static void main(String[] args) {
        // Exécution de l'application dans l'EDT (Event Dispatch Thread)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MonApplication().setVisible(true);
            }
        });
    }
}