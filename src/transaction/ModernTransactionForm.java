package transaction;

import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.text.*;
import java.util.Date;
import java.util.UUID;

public class ModernTransactionForm extends JPanel {
// Palette de couleurs de TransactionPanel
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
// Configuration des polices
        Font inputFont = new Font("Segoe UI", Font.PLAIN, 14);

        // Champ Montant avec formatage et validation
        montantField = createCurrencyField();
        montantField.setFont(inputFont);

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
        
        // Champs texte
        creeParField = new JTextField();
        valideParField = new JTextField();
        creeParField.setFont(inputFont);
        valideParField.setFont(inputFont);

        // Date Picker moderne avec JDateChooser
        datePicker = new JDateChooser();
        datePicker.setDateFormatString("dd/MM/yyyy");
        datePicker.setPreferredSize(new Dimension(250, 35));

        // Pré-remplissage si modification
        if (transaction != null) {
            montantField.setText(String.format("%.2f", transaction.getMontant()));
            typeComboBox.setSelectedItem(transaction.getTypeTransaction());
            descriptionArea.setText(transaction.getDescription());
            creeParField.setText(transaction.getCreePar());
            valideParField.setText(transaction.getValidePar());
            datePicker.setDate(transaction.getDate());
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

        // Date
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel dateLabel = createLabel("Date de Transaction", labelFont, labelColor);
        add(dateLabel, gbc);

        gbc.gridx = 1;
        add(datePicker, gbc);

        // Montant
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel montantLabel = createLabel("Montant", labelFont, labelColor);
        add(montantLabel, gbc);

        gbc.gridx = 1;
        add(montantField, gbc);

        // Type
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel typeLabel = createLabel("Type de Transaction", labelFont, labelColor);
        add(typeLabel, gbc);

        gbc.gridx = 1;
        add(typeComboBox, gbc);

        // Description
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel descriptionLabel = createLabel("Description", labelFont, labelColor);
        add(descriptionLabel, gbc);

        gbc.gridx = 1;
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
        descriptionScrollPane.setPreferredSize(new Dimension(250, 100));
        add(descriptionScrollPane, gbc);

        // Créé par
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel creeParLabel = createLabel("Créé par", labelFont, labelColor);
        add(creeParLabel, gbc);

        gbc.gridx = 1;
        add(creeParField, gbc);

        // Validé par
        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel valideParLabel = createLabel("Validé par", labelFont, labelColor);
        add(valideParLabel, gbc);

        gbc.gridx = 1;
        add(valideParField, gbc);
    }

    private JLabel createLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    private JTextField createCurrencyField() {
        JTextField field = new JTextField(10);
        field.setHorizontalAlignment(JTextField.RIGHT);

        // Formateur de devise
        DecimalFormat currencyFormat = new DecimalFormat("#,##0.00");
        NumberFormatter formatter = new NumberFormatter(currencyFormat);
        formatter.setValueClass(Double.class);
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true);

        JFormattedTextField formattedField = new JFormattedTextField(formatter);
        formattedField.setColumns(10);
        formattedField.setHorizontalAlignment(JTextField.RIGHT);

        return formattedField;
    }

    private void setupValidation() {
        // Validation des champs
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

    public Transaction saveTransaction(Transaction existingTransaction) {
        try {
            double montant = Double.parseDouble(
                montantField.getText().replace(",", ".")
            );
            Date date = datePicker.getDate();
            String type = (String) typeComboBox.getSelectedItem();
            String description = descriptionArea.getText();
            String creePar = creeParField.getText();
            String validePar = valideParField.getText();

            if (existingTransaction != null) {
                // Mode modification
                existingTransaction.setDate(date);
                existingTransaction.setMontant(montant);
                existingTransaction.setTypeTransaction(type);
                existingTransaction.setDescription(description);
                existingTransaction.setCreePar(creePar);
                existingTransaction.setValidePar(validePar);
                return existingTransaction;
            } else {
                // Mode création
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
            JOptionPane.showMessageDialog(
                this, 
                "Erreur de saisie. Veuillez vérifier vos données.",
                "Erreur", 
                JOptionPane.ERROR_MESSAGE
            );
            return null;
        }
    }

    // Méthode pour créer un bouton stylisé (à utiliser dans la classe TransactionPanel)
    public JButton createSaveButton(String text) {
        return createStyledButton(text);
    }
}