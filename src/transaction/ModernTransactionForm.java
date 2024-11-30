package transaction;

import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.UUID;

public class ModernTransactionForm extends JPanel {
    private final Color hoverColor = new Color(33, 97, 140);
    private final Color activeColor = new Color(33, 97, 140);
    private final Color backgroundColor = new Color(236, 240, 241);
    private final Color textColor = new Color(44, 62, 80);

    private JTextField montantField;
    private JComboBox<String> typeComboBox;
    private JTextArea descriptionArea;
    private JTextField creeParField;
    private JTextField valideParField;
    private JDateChooser datePicker;

    public ModernTransactionForm(Transaction transaction, JDialog parentDialog) {
        initializeComponents(transaction);
        createLayout();
        setupValidation();
    }

    private void initializeComponents(Transaction transaction) {
        Font inputFont = new Font("Segoe UI", Font.PLAIN, 14);

        // Champ Montant
        montantField = new JTextField();
        montantField.setFont(inputFont);
        montantField.setEnabled(true);

        // ComboBox pour le type de transaction
        typeComboBox = new JComboBox<>(new String[]{
            "Revenu", "Dépense", "Transfert", "Autre"
        });
        typeComboBox.setFont(inputFont);
        typeComboBox.setBackground(Color.WHITE);

        // Zone de description avec scrollpane
        descriptionArea = new JTextArea(3, 30);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(inputFont);
        descriptionArea.setEnabled(true);

        // Champs texte
        creeParField = new JTextField();
        valideParField = new JTextField();
        creeParField.setFont(inputFont);
        valideParField.setFont(inputFont);
        creeParField.setEnabled(true);
        valideParField.setEnabled(true);

        // Date Picker
        datePicker = new JDateChooser();
        datePicker.setDateFormatString("dd/MM/yyyy");
        datePicker.setPreferredSize(new Dimension(250, 35));

        // Pré-remplissage
        if (transaction != null) {
            montantField.setText(String.format("%.2f", transaction.getMontant()));
            typeComboBox.setSelectedItem(transaction.getTypeTransaction());
            descriptionArea.setText(transaction.getDescription());
            creeParField.setText(transaction.getCreePar());
            valideParField.setText(transaction.getValidePar());
            datePicker.setDate(transaction.getDate());
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

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(createLabel("Date de Transaction", labelFont, labelColor), gbc);
        gbc.gridx = 1;
        add(datePicker, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(createLabel("Montant", labelFont, labelColor), gbc);
        gbc.gridx = 1;
        add(montantField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(createLabel("Type de Transaction", labelFont, labelColor), gbc);
        gbc.gridx = 1;
        add(typeComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        add(createLabel("Description", labelFont, labelColor), gbc);
        gbc.gridx = 1;
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        scrollPane.setPreferredSize(new Dimension(250, 100));
        add(scrollPane, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        add(createLabel("Créé par", labelFont, labelColor), gbc);
        gbc.gridx = 1;
        add(creeParField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        add(createLabel("Validé par", labelFont, labelColor), gbc);
        gbc.gridx = 1;
        add(valideParField, gbc);
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
                String text = montantField.getText().trim();
                try {
                    double montant = Double.parseDouble(text);
                    return montant > 0;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
        });
    }

    public Transaction saveTransaction(Transaction existingTransaction) {
        try {
            double montant = Double.parseDouble(montantField.getText().trim());
            Date date = datePicker.getDate();
            String type = (String) typeComboBox.getSelectedItem();
            String description = descriptionArea.getText().trim();
            String creePar = creeParField.getText().trim();
            String validePar = valideParField.getText().trim();

            if (existingTransaction != null) {
                existingTransaction.setDate(date);
                existingTransaction.setMontant(montant);
                existingTransaction.setTypeTransaction(type);
                existingTransaction.setDescription(description);
                existingTransaction.setCreePar(creePar);
                existingTransaction.setValidePar(validePar);
                return existingTransaction;
            } else {
                return new Transaction(
                    UUID.randomUUID().toString(),
                    date,
                    montant,
                    type,
                    description,
                    creePar,
                    validePar
                );
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur de saisie. Veuillez vérifier vos données.", 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}
