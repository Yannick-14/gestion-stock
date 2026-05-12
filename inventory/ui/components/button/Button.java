package inventory.ui.components.button;

import javax.swing.*;
import java.awt.*;

/**
 * Bouton stylisé générique.
 */
public class Button extends JButton {

    public enum Style { PRIMARY, SECONDARY, DANGER }

    public Button(String text) {
        this(text, Style.PRIMARY);
    }

    public Button(String text, Style style) {
        super(text);
        applyStyle(style);
        setFocusPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setFont(new Font("Segoe UI", Font.BOLD, 13));
        setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
    }

    private void applyStyle(Style style) {
        switch (style) {
            case PRIMARY:
                setBackground(new Color(52, 120, 246));
                setForeground(Color.WHITE);
                break;
            case SECONDARY:
                setBackground(new Color(80, 80, 80));
                setForeground(Color.WHITE);
                break;
            case DANGER:
                setBackground(new Color(220, 53, 69));
                setForeground(Color.WHITE);
                break;
        }
        setOpaque(true);
        setBorderPainted(false);
    }
}
