package inventory.ui.screen;

import inventory.feature.repository.dao.GenericMethodCRUD;
import inventory.models.Article;
import inventory.models.StockMovement;
import inventory.ui.components.table.PanelList;
import inventory.ui.components.table.TableFilter;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Écran d'accueil : affiche la liste des articles et la liste des mouvements de stock.
 * Utilise PanelList générique avec des filtres configurés.
 */
public class HomeScreen extends JPanel {

    private final GenericMethodCRUD crud = new GenericMethodCRUD();

    public HomeScreen() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 252));

        // Titre
        JLabel lblTitle = new JLabel("Tableau de bord");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(16, 20, 12, 20));
        add(lblTitle, BorderLayout.NORTH);

        // Panneau divisé verticalement
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        split.setResizeWeight(0.5);
        split.setBorder(BorderFactory.createEmptyBorder(8, 16, 16, 16));
        split.setOpaque(false);

        split.setTopComponent(buildArticlesPanel());
        split.setBottomComponent(buildMovementsPanel());

        add(split, BorderLayout.CENTER);
    }

    private JPanel buildArticlesPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);

        JLabel lbl = new JLabel("Articles");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbl.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        panel.add(lbl, BorderLayout.NORTH);

        // Filtres pour les articles : par nom et par méthode de gestion
        List<TableFilter> filters = Arrays.asList(
            new TableFilter("nameArticle", "Nom")
        );

        List<Article> articles = loadArticles();
        PanelList<Article> list = new PanelList<>(new Article(), articles, filters);
        panel.add(list, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildMovementsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);

        JLabel lbl = new JLabel("Mouvements de stock");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbl.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        panel.add(lbl, BorderLayout.NORTH);

        // Filtres pour les mouvements : par article et par type
        List<TableFilter> filters = Arrays.asList(
            new TableFilter("article",            "Article"),
            new TableFilter("typeStockMovement",  "Type")
        );

        List<StockMovement> movements = loadMovements();
        PanelList<StockMovement> list = new PanelList<>(new StockMovement(), movements, filters);
        panel.add(list, BorderLayout.CENTER);
        return panel;
    }

    // ── Chargement données ────────────────────────────────────────────────────

    private List<Article> loadArticles() {
        try {
            return crud.findAllData(new Article());
        } catch (Exception e) {
            System.err.println("[HomeScreen] Impossible de charger les articles : " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private List<StockMovement> loadMovements() {
        try {
            return crud.findAllData(new StockMovement());
        } catch (Exception e) {
            System.err.println("[HomeScreen] Impossible de charger les mouvements : " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
