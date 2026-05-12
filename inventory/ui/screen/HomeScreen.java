package inventory.ui.screen;

import inventory.feature.repository.dao.GenericMethodCRUD;
import inventory.models.Article;
import inventory.models.StockMovement;
import inventory.ui.components.button.Button;
import inventory.ui.components.table.CustomColumn;
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
    private PanelList<Article> articlesList;
    private PanelList<StockMovement> movementsList;
    private List<StockMovement> allMovements;

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

        List<TableFilter> filters = List.of(
            new TableFilter("nameArticle", "Nom")
        );

        List<CustomColumn<Article>> customColumns = List.of(
            createStockBalanceColumn(),
            createViewMovementsButtonColumn()
        );

        List<Article> articles = loadArticles();
        this.allMovements = loadMovements(); // Charger tous les mouvements au début

        this.articlesList = new PanelList<>(new Article(), articles, filters, customColumns);
        panel.add(articlesList, BorderLayout.CENTER);
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

        this.movementsList = new PanelList<>(new StockMovement(), allMovements, filters);
        panel.add(movementsList, BorderLayout.CENTER);
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

    private CustomColumn<Article> createStockBalanceColumn() {
        return new CustomColumn<Article>() {
            @Override
            public String getName() {
                return "Stock Actuel";
            }

            @Override
            public Object getValue(Article article) {
                if (article.getId() == 0) return "0";
                try {
                    int balance = calculateStockBalance(article.getId());
                    return balance;
                } catch (Exception e) {
                    return "?";
                }
            }
        };
    }

    private CustomColumn<Article> createViewMovementsButtonColumn() {
        return new CustomColumn<Article>() {
            @Override
            public String getName() {
                return "Mouvements";
            }

            @Override
            public Object getValue(Article article) {
                return "Voir";
            }

            @Override
            public boolean isButtonColumn() {
                return true;
            }

            @Override
            public Button.Style getButtonStyle() {
                return Button.Style.SECONDARY;
            }

            @Override
            public void onButtonClick(Article article) {
                showMovementsForArticle(article);
            }
        };
    }

    public void showMovementsForArticle(Article article) {
        if (article == null) return;
        
        List<StockMovement> filtered = new ArrayList<>();
        for (StockMovement m : allMovements) {
            if (m.getArticle() != null && m.getArticle().getId() == article.getId()) {
                filtered.add(m);
            }
        }
        
        // Créer une fenêtre (JDialog) pour afficher les mouvements
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentWindow instanceof Frame ? (Frame) parentWindow : null, 
            "Mouvements de stock : " + article.getNameArticle(), true);
        dialog.setSize(900, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        // Utiliser PanelList à l'intérieur du dialogue (sans filtres pour la simplicité, ou avec filtres si on veut)
        PanelList<StockMovement> list = new PanelList<>(new StockMovement(), filtered);
        dialog.add(list, BorderLayout.CENTER);
        
        dialog.setVisible(true);
    }

    private int calculateStockBalance(int articleId) {
        int balance = 0;
        for (StockMovement m : allMovements) {
            if (m.getArticle() != null && m.getArticle().getId() == articleId) {
                String type = m.getTypeStockMovement() != null ? m.getTypeStockMovement().getNameType().toLowerCase() : "";
                if (type.contains("entr")) {
                    balance += m.getQuantity();
                } else if (type.contains("sort")) {
                    balance -= m.getQuantity();
                }
            }
        }
        return balance;
    }
}
