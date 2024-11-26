package mandataire;


import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.util.stream.Collectors;
import javax.swing.Timer;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class AutoCompleteComboBox extends JComboBox<String> {
    private final List<String> originalItems;
    private boolean isAdjusting = false;
    private Timer filterTimer;
    
    public AutoCompleteComboBox(List<String> items) {
        super();
        this.originalItems = new ArrayList<>(items);
        Collections.sort(this.originalItems);
        
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        originalItems.forEach(model::addElement);
        setModel(model);
        setEditable(true);
        
        // Création d'un timer pour éviter les filtrage trop fréquents
        filterTimer = new Timer(300, e -> filterItems());
        filterTimer.setRepeats(false);
        
        JTextField textField = (JTextField) getEditor().getEditorComponent();
        
        // Nettoyage des listeners existants
        for (ActionListener al : textField.getActionListeners()) {
            textField.removeActionListener(al);
        }
        
        // Nouveau DocumentFilter optimisé
        ((AbstractDocument) textField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) 
                    throws BadLocationException {
                super.replace(fb, offset, length, text, attrs);
                if (!isAdjusting) {
                    filterTimer.restart();
                }
            }
            
            @Override
            public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
                super.remove(fb, offset, length);
                if (!isAdjusting) {
                    filterTimer.restart();
                }
            }
        });
        
        // Gestion améliorée de la sélection
        addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED && !isAdjusting) {
                SwingUtilities.invokeLater(() -> {
                    if (e.getItem() != null) {
                        isAdjusting = true;
                        textField.setText(e.getItem().toString());
                        isAdjusting = false;
                    }
                });
            }
        });
        
        // Gestion du focus améliorée
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                filterTimer.stop();
                if (!isPopupVisible()) {
                    validateSelection();
                }
            }
        });
        
        // Ajout d'un KeyListener pour gérer la touche Entrée
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    filterTimer.stop();
                    validateSelection();
                    hidePopup();
                }
            }
        });
    }
    
    private void filterItems() {
        if (isAdjusting) return;
        
        String input = ((JTextField) getEditor().getEditorComponent()).getText().toLowerCase();
        if (input.isEmpty()) {
            updateCompleteList();
            return;
        }
        
        SwingUtilities.invokeLater(() -> {
            isAdjusting = true;
            try {
                DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) getModel();
                List<String> filteredItems = originalItems.stream()
                    .filter(item -> item.toLowerCase().contains(input))
                    .collect(Collectors.toList());
                
                model.removeAllElements();
                filteredItems.forEach(model::addElement);
                
                if (!filteredItems.isEmpty() && !isPopupVisible()) {
                    showPopup();
                }
                
                getEditor().setItem(input);
                setSelectedItem(null);
            } finally {
                isAdjusting = false;
            }
        });
    }
    
    private void validateSelection() {
        if (isAdjusting) return;
        
        SwingUtilities.invokeLater(() -> {
            isAdjusting = true;
            try {
                String currentText = ((JTextField) getEditor().getEditorComponent()).getText();
                Optional<String> match = originalItems.stream()
                    .filter(item -> item.equalsIgnoreCase(currentText))
                    .findFirst();
                
                if (match.isPresent()) {
                    setSelectedItem(match.get());
                    ((JTextField) getEditor().getEditorComponent()).setText(match.get());
                } else {
                    updateCompleteList();
                    setSelectedItem(null);
                }
            } finally {
                isAdjusting = false;
            }
        });
    }
    
    private void updateCompleteList() {
        SwingUtilities.invokeLater(() -> {
            isAdjusting = true;
            try {
                DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) getModel();
                model.removeAllElements();
                originalItems.forEach(model::addElement);
            } finally {
                isAdjusting = false;
            }
        });
    }
    
    public String getSelectedEstablishment() {
        Object selected = getSelectedItem();
        return selected != null ? selected.toString() : null;
    }
    
    @Override
    public void setSelectedItem(Object item) {
        if (!isAdjusting) {
            super.setSelectedItem(item);
        }
    }
}