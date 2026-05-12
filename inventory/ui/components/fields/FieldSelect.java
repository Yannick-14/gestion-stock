package inventory.ui.components.fields;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Champ de sélection (ComboBox) avec label.
 */
public class FieldSelect<T> extends JPanel {

    private final JLabel label;
    private final JComboBox<T> comboBox;

    public FieldSelect(String labelText, List<T> items) {
        setLayout(new BorderLayout(5, 5));
        setOpaque(false);

        label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        comboBox = new JComboBox<>();
        comboBox.setPreferredSize(new Dimension(200, 35));
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setBackground(Color.WHITE);
        
        if (items != null) {
            for (T item : items) {
                comboBox.addItem(item);
            }
        }

        add(label, BorderLayout.NORTH);
        add(comboBox, BorderLayout.CENTER);

        // Marge en bas
        setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
    }

    @SuppressWarnings("unchecked")
    public T getSelectedValue() {
        return (T) comboBox.getSelectedItem();
    }

    public void setSelectedValue(T value) {
        comboBox.setSelectedItem(value);
    }

    public JComboBox<T> getComboBox() {
        return comboBox;
    }
}
