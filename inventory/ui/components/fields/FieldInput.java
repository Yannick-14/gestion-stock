package inventory.ui.components.fields;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;

/**
 * Champ de saisie textuelle avec label.
 * Dimensions optimisées pour un meilleur design.
 */
public class FieldInput extends JPanel {

    private final JLabel label;
    private final JTextField textField;

    public FieldInput(String labelText) {
        this(labelText, "");
    }

    public FieldInput(String labelText, String defaultValue) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);

        // Label
        label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(label);
        add(Box.createVerticalStrut(5)); // Espace entre label et champ

        // TextField
        textField = new JTextField(defaultValue);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        // Dimensions optimisées (pas trop grand !)
        textField.setMaximumSize(new Dimension(400, 32));
        textField.setPreferredSize(new Dimension(350, 32));
        textField.setMinimumSize(new Dimension(250, 32));
        textField.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Bordure propre
        textField.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

        add(textField);

        // Marge sous le champ
        setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
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
