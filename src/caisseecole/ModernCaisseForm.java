package caisseecole;

import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.text.*;
import java.sql.Timestamp;
import java.util.UUID;

public class ModernCaisseForm extends JPanel {
    private final Color hoverColor = new Color(33, 97, 140);
    private final Color activeColor = new Color(33, 97, 140);
    private final Color backgroundColor = new Color(236, 240, 241);
    private final Color textColor = new Color(44, 62, 80);

    private JTextField montantField;
    private JTextField raisonField;
    private JTextArea noteArea;
    private JDateChooser datePicker;
    private final String etablissementId;

    public ModernCaisseForm(CaisseEcole caisse, String etablissementId, JDialog parentDialog) {
        this.etablissementId = etablissementId;
        initializeComponents(caisse);
        createLayout();
        setupValidation();
    }

    private void initializeComponents(CaisseEcole caisse) {
        Font inputFont = new Font("Segoe UI", Font.PLAIN, 14);

        // Champ Montant
        montantField = new JTextField(10);
        montantField.setHorizontalAlignment(JTextField.RIGHT);
        montantField.setFont(inputFont);

        // Champ Raison
        raisonField = new JTextField();
        raisonField.setFont(inputFont);

        // Zone de texte pour Note
        noteArea = new JTextArea(3, 30);
        noteArea.setLineWrap(true);
        noteArea.setWrapStyleWord(true);
        noteArea.setFont(inputFont);

        // Sélecteur de date
        datePicker = new JDateChooser();
        datePicker.setDateFormatString("dd/MM/yyyy");
        datePicker.setPreferredSize(new Dimension(250, 35));

        // Pré-remplir les champs en cas de modification
        if (caisse != null) {
            montantField.setText(String.format("%.2f", caisse.getMontant()));
            raisonField.setText(caisse.getRaison());
            noteArea.setText(caisse.getNote());
            if (caisse.getCreatedAt() != null) {
                datePicker.setDate(new java.util.Date(caisse.getCreatedAt().getTime()));
            }
        }
    }

    private void createLayout() {
        setLayout(new GridBagLayout());
        setBackground(backgroundColor);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Color labelColor = textColor;

        // Champ Date
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel dateLabel = createLabel("Date", labelFont, labelColor);
        add(dateLabel, gbc);

        gbc.gridx = 1;
        add(datePicker, gbc);

        // Champ Montant
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel montantLabel = createLabel("Montant", labelFont, labelColor);
        add(montantLabel, gbc);

        gbc.gridx = 1;
        add(montantField, gbc);

        // Champ Raison
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel raisonLabel = createLabel("Raison", labelFont, labelColor);
        add(raisonLabel, gbc);

        gbc.gridx = 1;
        add(raisonField, gbc);

        // Champ Note
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel noteLabel = createLabel("Note", labelFont, labelColor);
        add(noteLabel, gbc);

        gbc.gridx = 1;
        JScrollPane noteScrollPane = new JScrollPane(noteArea);
        noteScrollPane.setPreferredSize(new Dimension(250, 100));
        add(noteScrollPane, gbc);
    }

    private JLabel createLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    private void setupValidation() {
        montantField.setInputVerifier(new InputVerifier() {
            @Override
            public boolean verify(JComponent input) {
                JTextField textField = (JTextField) input;
                try {
                    double montant = Double.parseDouble(textField.getText().replace(",", "."));
                    return montant > 0;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
        });
    }

    public CaisseEcole saveCaisse(CaisseEcole existingCaisse) {
        try {
            double montant = Double.parseDouble(montantField.getText().replace(",", "."));
            Timestamp createdAt = new Timestamp(datePicker.getDate().getTime());
            String raison = raisonField.getText();
            String note = noteArea.getText();

            if (existingCaisse != null) {
                // Mode modification
                existingCaisse.setCreatedAt(createdAt);
                existingCaisse.setMontant(montant);
                existingCaisse.setRaison(raison);
                existingCaisse.setNote(note);
                return existingCaisse;
            } else {
                // Mode création
                return new CaisseEcole(
                    UUID.randomUUID().toString(),
                    etablissementId,
                    montant,
                    createdAt,
                    raison,
                    note
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
}
