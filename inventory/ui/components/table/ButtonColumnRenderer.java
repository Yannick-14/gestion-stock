package inventory.ui.components.table;

import inventory.ui.components.button.Button;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

/**
 * Renderer pour les boutons dans les tableaux.
 * Enveloppe un JButton dans un JPanel pour ajouter du padding.
 */
public class ButtonColumnRenderer extends JPanel implements TableCellRenderer {
    private final JButton button;

    public ButtonColumnRenderer() {
        setLayout(new GridBagLayout()); // Permet de centrer et de respecter les dimensions
        setOpaque(true);
        
        button = new JButton();
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 11));
        button.setMargin(new Insets(2, 8, 2, 8)); // Plus petit
        
        add(button);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        button.setText(value == null ? "Action" : value.toString());
        
        // Gérer la couleur de fond de la cellule (sélection ou non)
        if (isSelected) {
            setBackground(table.getSelectionBackground());
        } else {
            setBackground(table.getBackground());
        }
        
        return this;
    }

    public void setStyle(Button.Style style) {
        if (style == null) style = Button.Style.PRIMARY;
        switch (style) {
            case PRIMARY:
                button.setBackground(new Color(52, 120, 246));
                button.setForeground(Color.WHITE);
                break;
            case SECONDARY:
                button.setBackground(new Color(85, 95, 110)); // Gris plus doux
                button.setForeground(Color.WHITE);
                break;
            case DANGER:
                button.setBackground(new Color(220, 53, 69));
                button.setForeground(Color.WHITE);
                break;
        }
    }
}