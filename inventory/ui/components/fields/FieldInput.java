package inventory.ui.components.fields;

import javax.swing.*;
import java.awt.*;

/**
 * Champ de saisie textuelle avec label.
 */
public class FieldInput extends JPanel {

    private final JLabel label;
    private final JTextField textField;

    public FieldInput(String labelText) {
        this(labelText, "");
    }

    public FieldInput(String labelText, String defaultValue) {
        setLayout(new BorderLayout(5, 5));
        setOpaque(false);

        label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        textField = new JTextField(defaultValue);
        textField.setPreferredSize(new Dimension(200, 30));
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        add(label, BorderLayout.NORTH);
        add(textField, BorderLayout.CENTER);
        
        // Marge en bas
        setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
    }

    public String getText() {
        return textField.getText();
    }

    public void setText(String text) {
        textField.setText(text);
    }

    public JTextField getTextField() {
        return textField;
    }
}
