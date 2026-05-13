package inventory.ui;

import inventory.ui.screen.ArticleScreen;
import inventory.ui.screen.HomeScreen;
import inventory.ui.screen.StockMovementScreen;
import inventory.ui.components.button.Button;

import javax.swing.*;
import java.awt.*;

/**
 * Navigation principale de l'application.
 *
 * Contient une barre latérale avec 3 entrées :
 *  - Accueil            → HomeScreen
 *  - Insertion Article  → ArticleScreen
 *  - Mouvement de Stock → StockMovementScreen
 *
 * Usage :
 *   Navigation nav = new Navigation();
 *   window.setContentPane(nav);
 */
public class Navigation extends JPanel {

    private final JPanel contentArea;

    // Boutons de navigation courants
    private Button activeButton;

    public Navigation() {
        setLayout(new BorderLayout());

        // ── Barre latérale ──────────────────────────────────────────────────
        JPanel sidebar = buildSidebar();
        add(sidebar, BorderLayout.WEST);

        // ── Zone de contenu principale ──────────────────────────────────────
        contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(new Color(245, 247, 252));
        add(contentArea, BorderLayout.CENTER);

        // Écran par défaut : Accueil
        showScreen(new HomeScreen());
    }

    // ── Construction de la sidebar ────────────────────────────────────────────

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(30, 40, 65));
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(16, 0, 16, 0));

        // Logo / titre appli
        JLabel logo = new JLabel("Gestion Stock");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        logo.setBorder(BorderFactory.createEmptyBorder(8, 20, 20, 20));
        sidebar.add(logo);

        sidebar.add(new JSeparator());
        sidebar.add(Box.createVerticalStrut(10));

        // Entrées de navigation
        Button btnHome = createNavButton("Accueil");
        Button btnArticle = createNavButton("Insertion Article");
        Button btnMouvement = createNavButton("Mouvement de Stock");

        btnHome.addActionListener(e -> {
            setActive(btnHome);
            showScreen(new HomeScreen());
        });
        btnArticle.addActionListener(e -> {
            setActive(btnArticle);
            showScreen(new ArticleScreen());
        });
        btnMouvement.addActionListener(e -> {
            setActive(btnMouvement);
            showScreen(new StockMovementScreen());
        });

        sidebar.add(btnHome);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(btnArticle);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(btnMouvement);
        sidebar.add(Box.createVerticalGlue());

        // Activer le premier bouton par défaut
        setActive(btnHome);

        return sidebar;
    }

    private Button createNavButton(String text) {
        Button btn = new Button(text, Button.Style.SECONDARY);
        btn.setMaximumSize(new Dimension(220, 44));
        btn.setPreferredSize(new Dimension(220, 44));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));
        btn.setBackground(new Color(30, 40, 65));
        return btn;
    }

    private void setActive(Button btn) {
        if (activeButton != null) {
            activeButton.setBackground(new Color(30, 40, 65));
        }
        activeButton = btn;
        btn.setBackground(new Color(52, 120, 246));
    }

    // ── Changement d'écran ────────────────────────────────────────────────────

    private void showScreen(JPanel screen) {
        contentArea.removeAll();
        contentArea.add(screen, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
    }
}
