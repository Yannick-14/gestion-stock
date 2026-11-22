package inventory.ui.components.fields;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;

/**
 * Champ de saisie textuelle avec label.
 * Supporte le mode mot de passe (texte masqué) via le constructeur booléen.
 */
public class FieldInput extends JPanel {

    private final JLabel label;
    private final JTextField textField;
    private final boolean passwordMode;

    public FieldInput(String labelText) {
        this(labelText, "", false);
    }

    public FieldInput(String labelText, String defaultValue) {
        this(labelText, defaultValue, false);
    }

    /** @param passwordMode true pour masquer la saisie (mot de passe) */
    public FieldInput(String labelText, boolean passwordMode) {
        this(labelText, "", passwordMode);
    }

    private FieldInput(String labelText, String defaultValue, boolean passwordMode) {
        this.passwordMode = passwordMode;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);

        // Label
        label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(label);
        add(Box.createVerticalStrut(5));

        // TextField ou PasswordField selon le mode
        textField = passwordMode ? new JPasswordField(defaultValue) : new JTextField(defaultValue);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        textField.setMaximumSize(new Dimension(400, 32));
        textField.setPreferredSize(new Dimension(350, 32));
        textField.setMinimumSize(new Dimension(250, 32));
        textField.setAlignmentX(Component.LEFT_ALIGNMENT);

        textField.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

        add(textField);

        // Marge sous le champ
        setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
    }

    /**
     * Retourne le texte saisi.
     * En mode password, utilise getPassword() pour éviter le warning de sécurité.
     */
    public String getText() {
        if (passwordMode && textField instanceof JPasswordField) {
            return new String(((JPasswordField) textField).getPassword());
        }
        return textField.getText();
    }

    public void setText(String text) {
        textField.setText(text);
    }

    public JTextField getTextField() {
        return textField;
    }
}
