package inventory.ui.screen;

import inventory.feature.repository.dao.GenericMethodCRUD;
import inventory.feature.stock.StockManager;
import inventory.models.Article;
import inventory.models.StockMovement;
import inventory.models.StockState;
import inventory.ui.components.button.Button;
import inventory.ui.components.fields.FieldDate;
import inventory.ui.components.table.CustomColumn;
import inventory.ui.components.table.PanelList;
import inventory.ui.components.table.TableFilter;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
// import java.time.format.DateTimeFormatter;

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
    private final StockManager stockManager = new StockManager();
    private PanelList<Article> articlesList;
    private PanelList<StockMovement> movementsList;
    private List<StockMovement> allMovements;
    private FieldDate globalDateFilter;

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

        // Initialiser le filtre avant de charger les panels pour qu'il soit pris en compte
        buildHeader();

        split.setTopComponent(buildArticlesPanel());
        split.setBottomComponent(buildMovementsPanel());

        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setOpaque(false);
        mainContent.add(headerContainer, BorderLayout.NORTH);
        mainContent.add(split, BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);
    }

    private JPanel headerContainer;
    private void buildHeader() {
        headerContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        headerContainer.setOpaque(false);
        headerContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 20));

        globalDateFilter = new FieldDate("Filtrer par date (jusqu'au)");
        globalDateFilter.getDatePicker().setPreferredSize(new Dimension(200, 32));

        // Listener pour rechargement automatique
        globalDateFilter.getDatePicker().getTextField().getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { refreshAllData(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { refreshAllData(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { refreshAllData(); }
        });

        headerContainer.add(globalDateFilter);
    }

    private void refreshAllData() {
        this.allMovements = loadMovements();
        List<Article> articles = loadArticles();

        if (articlesList != null) articlesList.setData(articles);
        if (movementsList != null) movementsList.setData(allMovements);
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

        this.allMovements = loadMovements();

        List<CustomColumn<Article>> customColumns = List.of(
            createStockBalanceColumn(),
            createStockValueColumn(),
            createViewMovementsButtonColumn()
        );

        List<Article> articles = loadArticles();

        this.articlesList = new PanelList<>(new Article(), articles, filters, customColumns);
        panel.add(articlesList, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildMovementsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);

        JLabel lbl = new JLabel("Mouvements de stock récents");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbl.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        panel.add(lbl, BorderLayout.NORTH);

        List<TableFilter> filters = Arrays.asList(
            new TableFilter("article", "Article"),
            new TableFilter("typeStockMovement", "Type")
        );

        this.movementsList = new PanelList<>(new StockMovement(), allMovements, filters);
        panel.add(movementsList, BorderLayout.CENTER);
        return panel;
    }

    // ── Chargement données ────────────────────────────────────────────────────

    private List<Article> loadArticles() {
        try {
            List<Article> list = crud.findAllData(new Article());

            // Filtre par date
            if (globalDateFilter != null) {
                String dateStr = globalDateFilter.getText();
                if (dateStr != null && !dateStr.isEmpty()) {
                    LocalDate filterDate = LocalDate.parse(dateStr);
                    Timestamp limit = Timestamp.valueOf(LocalDateTime.of(filterDate, LocalTime.MAX));
                    return list.stream()
                        .filter(a -> a.getCreatedAt() == null || a.getCreatedAt().before(limit))
                        .toList();
                }
            }
            return list;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private List<StockMovement> loadMovements() {
        try {
            List<StockMovement> list = crud.findAllData(new StockMovement());

            // Filtre par date
            if (globalDateFilter != null) {
                String dateStr = globalDateFilter.getText();
                if (dateStr != null && !dateStr.isEmpty()) {
                    LocalDate filterDate = LocalDate.parse(dateStr);
                    // On prend tout ce qui est avant ou égal à la fin de cette date (23:59:59)
                    Timestamp limit = Timestamp.valueOf(LocalDateTime.of(filterDate, LocalTime.MAX));
                    return list.stream()
                        .filter(m -> m.getCreatedAt() != null && m.getCreatedAt().before(limit))
                        .toList();
                }
            }
            return list;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private CustomColumn<Article> createStockBalanceColumn() {
        return new CustomColumn<Article>() {
            @Override public String getName() { return "Qte Stock"; }
            @Override public Object getValue(Article article) {
                if (article.getId() == 0) return 0;
                StockState state = stockManager.calculateStockState(article, allMovements);
                return state.quantity;
            }
        };
    }

    private CustomColumn<Article> createStockValueColumn() {
        return new CustomColumn<Article>() {
            @Override public String getName() { return "Valeur Totale"; }
            @Override public Object getValue(Article article) {
                if (article.getId() == 0) return "0.00 Ar";
                StockState state = stockManager.calculateStockState(article, allMovements);
                return String.format("%.2f Ar", state.totalValue);
            }
        };
    }

    private CustomColumn<Article> createViewMovementsButtonColumn() {
        return new CustomColumn<Article>() {
            @Override public String getName() { return "Mouvements"; }
            @Override public Object getValue(Article article) { return "Voir"; }
            @Override public boolean isButtonColumn() { return true; }
            @Override public Button.Style getButtonStyle() { return Button.Style.SECONDARY; }
            @Override public void onButtonClick(Article article) { showMovementsForArticle(article); }
        };
    }

    public void showMovementsForArticle(Article article) {
        if (article == null) return;
        List<StockMovement> filtered = allMovements.stream()
            .filter(m -> m.getArticle() != null && m.getArticle().getId() == article.getId())
            .toList();

        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentWindow instanceof Frame ? (Frame) parentWindow : null, 
            "Mouvements : " + article.getNameArticle(), true);
        dialog.setSize(900, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        PanelList<StockMovement> list = new PanelList<>(new StockMovement(), filtered);
        dialog.add(list, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
}
