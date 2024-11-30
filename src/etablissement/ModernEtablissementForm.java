package etablissement;

import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class ModernEtablissementForm extends JPanel {
    // Palette de couleurs
    private final Color hoverColor = new Color(33, 97, 140);
    private final Color activeColor = new Color(33, 97, 140);
    private final Color backgroundColor = new Color(236, 240, 241);
    private final Color textColor = new Color(44, 62, 80);

    private JTextField nomField;
    private JComboBox<String> typeComboBox;
    private JTextField localisationField; // Champ pour la localisation
    private JTextArea descriptionArea;
    private JDateChooser datePicker;

    public ModernEtablissementForm(Etablissement etablissement, JDialog parentDialog) {
        initializeComponents(etablissement);
        createLayout();
        setupValidation();
    }

    private void initializeComponents(Etablissement etablissement) {
        // Configuration des polices
        Font inputFont = new Font("Segoe UI", Font.PLAIN, 14);

        // Champ Nom
        nomField = new JTextField();
        nomField.setFont(inputFont);

        // ComboBox pour le type d'établissement
        typeComboBox = new JComboBox<>(new String[]{
            "École", "Collège", "Lycée"
        });
        typeComboBox.setFont(inputFont);
        typeComboBox.setBackground(Color.WHITE);

        // Champ Localisation
        localisationField = new JTextField();
        localisationField.setFont(inputFont);

        // Zone de description avec scrollpane
        descriptionArea = new JTextArea(3, 30);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(inputFont);

        // Date Picker moderne avec JDateChooser
        datePicker = new JDateChooser();
        datePicker.setDateFormatString("dd/MM/yyyy");
        datePicker.setPreferredSize(new Dimension(250, 35));

        // Pré-remplissage si modification
        if (etablissement != null) {
            nomField.setText(etablissement.getNom());
            typeComboBox.setSelectedItem(etablissement.getType());
            localisationField.setText(etablissement.getLocalisation());
            descriptionArea.setText(""); // Vous pouvez ajouter un champ de description si nécessaire
            datePicker.setDate(etablissement.getCreatedAt() != null ? new Date(etablissement.getCreatedAt().getTime()) : null);
        }
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(activeColor);
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(180, 45));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor.darker());
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(activeColor);
            }
        });
        return button;
    }

    private void createLayout() {
        setLayout(new GridBagLayout());
        setBackground(backgroundColor);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Style des labels
        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Color labelColor = textColor;

        // Nom
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel nomLabel = createLabel("Nom de l'Établissement", labelFont, labelColor);
        add(nomLabel, gbc);

        gbc.gridx = 1;
        add(nomField, gbc);

        // Type
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel typeLabel = createLabel("Type d'Établissement", labelFont, labelColor);
        add(typeLabel, gbc);

        gbc.gridx = 1;
        add(typeComboBox, gbc);

        // Localisation
        gbc.gridx = 0;
        gbc.gridy = 2;
                JLabel localisationLabel = createLabel("Localisation", labelFont, labelColor);
        add(localisationLabel, gbc);

        gbc.gridx = 1;
        add(localisationField, gbc);

        // Description (facultatif, selon vos besoins)
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel descriptionLabel = createLabel("Description", labelFont, labelColor);
        add(descriptionLabel, gbc);

        gbc.gridx = 1;
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
        descriptionScrollPane.setPreferredSize(new Dimension(250, 100));
        add(descriptionScrollPane, gbc);

        // Date de création
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel dateLabel = createLabel("Date de Création", labelFont, labelColor);
        add(dateLabel, gbc);

        gbc.gridx = 1;
        add(datePicker, gbc);
    }

    private JLabel createLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    private void setupValidation() {
        // Validation des champs
        nomField.setInputVerifier(new InputVerifier() {
            @Override
            public boolean verify(JComponent input) {
                JTextField textField = (JTextField) input;
                return !textField.getText().trim().isEmpty(); // Vérifie que le champ n'est pas vide
            }
        });

        localisationField.setInputVerifier(new InputVerifier() {
            @Override
            public boolean verify(JComponent input) {
                JTextField textField = (JTextField) input;
                return !textField.getText().trim().isEmpty(); // Vérifie que le champ localisation n'est pas vide
            }
        });
    }

    public Etablissement saveEtablissement(Etablissement existingEtablissement) {
        try {
            String nom = nomField.getText().trim();
            String type = (String) typeComboBox.getSelectedItem();
            String localisation = localisationField.getText().trim();
            Timestamp createdAt = new Timestamp(datePicker.getDate().getTime());

            if (existingEtablissement != null) {
                // Mode modification
                existingEtablissement.setNom(nom);
                existingEtablissement.setType(type);
                existingEtablissement.setLocalisation(localisation);
                existingEtablissement.setCreatedAt(createdAt);
                return existingEtablissement;
            } else {
                // Mode création
                return new Etablissement(
                    UUID.randomUUID().toString(),
                    nom,
                    type,
                    localisation,
                    createdAt
                );
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Erreur de saisie. Veuillez vérifier vos données.",
                "Erreur",
                JOptionPane.ERROR_MESSAGE
            );
            return null;
        }
    }

    // Méthode pour créer un bouton stylisé (à utiliser dans la classe EtablissementPanel)
    public JButton createSaveButton(String text) {
        return createStyledButton(text);
    }
}