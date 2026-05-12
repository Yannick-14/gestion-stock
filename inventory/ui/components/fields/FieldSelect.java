package inventory.ui.components.fields;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.util.List;

/**
 * Champ de sélection (ComboBox) avec label.
 * Affiche uniquement le nom de l'objet, retourne l'objet complet.
 * Dimensions optimisées pour un meilleur design.
 */
public class FieldSelect<T> extends JPanel {

    private final JLabel label;
    private final JComboBox<T> comboBox;
    private List<T> items;

    public FieldSelect(String labelText, List<T> items) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        
        this.items = items;

        // Label
        label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(label);
        add(Box.createVerticalStrut(5)); // Espace entre label et champ

        // ComboBox
        comboBox = new JComboBox<>();
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboBox.setBackground(Color.WHITE);
        
        // Dimensions optimisées (pas trop grand !)
        comboBox.setMaximumSize(new Dimension(400, 32));
        comboBox.setPreferredSize(new Dimension(350, 32));
        comboBox.setMinimumSize(new Dimension(250, 32));
        comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Ajouter un renderer personnalisé pour afficher uniquement le nom
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                
                if (value != null) {
                    setText(getDisplayName(value));
                }
                
                return this;
            }
        });
        
        // Ajouter les items
        if (items != null) {
            for (T item : items) {
                comboBox.addItem(item);
            }
        }

        // Bordure (optionnel pour une meilleure apparence)
        comboBox.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        add(comboBox);
        
        // Marge sous le champ
        setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
    }

    /**
     * Extrait le nom à afficher de l'objet via réflexion.
     */
    private String getDisplayName(Object obj) {
        // Cas 1 : Si l'objet a une méthode getNameMethod()
        try {
            var method = obj.getClass().getMethod("getNameMethod");
            Object result = method.invoke(obj);
            return result != null ? result.toString() : obj.toString();
        } catch (Exception ignored) {}
        
        // Cas 2 : Sinon essayer getName()
        try {
            var method = obj.getClass().getMethod("getName");
            Object result = method.invoke(obj);
            return result != null ? result.toString() : obj.toString();
        } catch (Exception ignored) {}
        
        // Cas 3 : Essayer getLabel()
        try {
            var method = obj.getClass().getMethod("getLabel");
            Object result = method.invoke(obj);
            return result != null ? result.toString() : obj.toString();
        } catch (Exception ignored) {}
        
        // Cas 4 : Retourner le toString() par défaut
        return obj.toString();
    }

    /**
     * Retourne l'objet sélectionné (complet avec toutes ses propriétés).
     */
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
