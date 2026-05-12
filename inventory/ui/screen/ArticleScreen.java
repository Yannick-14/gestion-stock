package inventory.ui.screen;

import inventory.feature.repository.dao.GenericMethodCRUD;
import inventory.models.Article;
import inventory.models.StockManagementMethod;
import inventory.ui.components.form.Form;
import inventory.ui.components.button.Button;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Écran d'insertion d'un article.
 * Le formulaire est généré automatiquement depuis la classe Article.
 * - nameArticle    → FieldInput  (texte)
 * - stockManagementMethod → FieldSelect (liste des méthodes chargées en BDD)
 */
public class ArticleScreen extends JPanel {

    private final GenericMethodCRUD crud = new GenericMethodCRUD();
    private Form<Article>           form;

    public ArticleScreen() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 252));

        // Titre
        JLabel lblTitle = new JLabel("Insertion d'article");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(16, 20, 12, 20));
        add(lblTitle, BorderLayout.NORTH);

        // Chargement des données référentielles
        List<StockManagementMethod> methods = loadMethods();

        // Options du formulaire : champ métier → liste
        // Cette liste sera utilisée pour remplir le FieldSelect
        Map<String, List<?>> options = new HashMap<>();
        options.put("stockManagementMethod", methods);

        // Formulaire générique
        form = new Form<>(Article.class, options);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(16, 30, 16, 30));
        content.setOpaque(false);
        content.add(form);
        content.add(Box.createVerticalStrut(16));

        // Bouton d'enregistrement
        Button btnSave = new Button("Enregistrer l'article");
        btnSave.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnSave.addActionListener(e -> saveArticle());
        content.add(btnSave);

        add(new JScrollPane(content), BorderLayout.CENTER);
    }

    /**
     * Enregistre l'article avec sa méthode de gestion de stock
     */
    private void saveArticle() {
        try {
            // Créer une nouvelle instance d'Article
            Article article = new Article();
            
            // Remplir l'article avec les données du formulaire
            // ⚠️ Important : form.getData() retourne maintenant l'objet StockManagementMethod complet,
            // pas juste son nom en string !
            article = form.getData(article);
            
            // Ajouter la date de création
            article.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
            
            // Debug : vérifier que l'objet est bien rempli
            System.out.println("[ArticleScreen] Article à enregistrer :");
            System.out.println("  - Nom : " + article.getNameArticle());
            System.out.println("  - Méthode : " + (article.getStockManagementMethod() != null 
                ? article.getStockManagementMethod().getNameMethod() 
                : "NULL"));
            
            // Enregistrer en base de données
            int id = crud.insertData(article);
            
            JOptionPane.showMessageDialog(this,
                "Article enregistré avec succès ! (id=" + id + ")",
                "Succès", JOptionPane.INFORMATION_MESSAGE);
                
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de l'enregistrement : " + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /**
     * Charge les méthodes de gestion de stock depuis la base de données
     */
    private List<StockManagementMethod> loadMethods() {
        try {
            return crud.findAllData(new StockManagementMethod());
        } catch (Exception e) {
            System.err.println("[ArticleScreen] Impossible de charger les méthodes : " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
