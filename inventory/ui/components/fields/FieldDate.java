package inventory.ui.components.fields;

import javax.swing.*;
import java.awt.*;

/**
 * Champ de saisie de date avec label.
 */
public class FieldDate extends JPanel {

    private final JLabel label;
    private final DatePicker datePicker;

    public FieldDate(String labelText) {
        this(labelText, null);
    }

    public FieldDate(String labelText, String defaultValue) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);

        // Label
        label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(label);
        add(Box.createVerticalStrut(5)); // Espace entre label et champ

        // DatePicker
        datePicker = new DatePicker();
        datePicker.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        if (defaultValue != null && !defaultValue.isEmpty()) {
            try {
                java.time.LocalDate date = java.time.LocalDate.parse(defaultValue);
                datePicker.setDate(date);
            } catch (Exception e) {
                // Keep default (today)
            }
        }

        add(datePicker);

        // Marge sous le champ
        setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
    }

    public String getText() {
        return datePicker.getDate();
    }

    public void setText(String text) {
        if (text == null || text.isEmpty()) return;
        try {
            java.time.LocalDate date = java.time.LocalDate.parse(text);
            datePicker.setDate(date);
        } catch (Exception e) {
            // Ignorer
        }
    }

    public DatePicker getDatePicker() {
        return datePicker;
    }
}
