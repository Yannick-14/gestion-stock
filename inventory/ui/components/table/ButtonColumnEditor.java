package inventory.ui.components.table;

import inventory.ui.components.button.Button;
import javax.swing.*;
// import javax.swing.table.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Editor pour les boutons dans les tableaux.
 * Enveloppe un JButton dans un JPanel pour correspondre au design du renderer.
 */
public class ButtonColumnEditor extends DefaultCellEditor {
    private final JPanel panel;
    private final JButton button;
    private String label;
    private boolean isPushed;
    private final Consumer<Object> action;

    public ButtonColumnEditor(Consumer<Object> action) {
        super(new JCheckBox());
        this.action = action;
        
        panel = new JPanel(new GridBagLayout());
        panel.setOpaque(true);

        button = new JButton();
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 11));
        button.setMargin(new Insets(2, 8, 2, 8));
        
        button.addActionListener(e -> fireEditingStopped());
        panel.add(button);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        label = (value == null) ? "Voir" : value.toString();
        button.setText(label);

        // Match selection colors
        panel.setBackground(table.getSelectionBackground());

        isPushed = true;
        return panel;
    }

    @Override
    public Object getCellEditorValue() {
        if (isPushed) {
            action.accept(label);
        }
        isPushed = false;
        return label;
    }

    public void setStyle(Button.Style style) {
        if (style == null) style = Button.Style.PRIMARY;
        switch (style) {
            case PRIMARY:
                button.setBackground(new Color(52, 120, 246));
                button.setForeground(Color.WHITE);
                break;
            case SECONDARY:
                button.setBackground(new Color(85, 95, 110));
                button.setForeground(Color.WHITE);
                break;
            case DANGER:
                button.setBackground(new Color(220, 53, 69));
                button.setForeground(Color.WHITE);
                break;
        }
    }
}
