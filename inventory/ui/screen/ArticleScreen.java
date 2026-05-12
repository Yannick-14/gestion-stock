package inventory.ui.screen;

import inventory.feature.repository.dao.GenericMethodCRUD;
import inventory.models.Article;
import inventory.ui.components.button.Button;

import javax.swing.*;

/**
 * Écran d'insertion d'un article.
 * 
 * Hérite de AbstractFormScreen pour bénéficier du design standard.
 * Le formulaire est entièrement automatique grâce à la classe Form.
 */
public class ArticleScreen extends AbstractFormScreen<Article> {

    private final GenericMethodCRUD crud = new GenericMethodCRUD();

    public ArticleScreen() {
        super("Insertion d'article", new Article());
        
        setActions(java.util.List.of(
            new FormAction("Enregistrer l'article", Button.Style.PRIMARY, e -> saveArticle()),
            new FormAction("Annuler", Button.Style.DANGER, e -> saveArticle())
        ));
    }

    /**
     * Enregistre l'article en base de données.
     */
    private void saveArticle() {
        try {
            // Créer une nouvelle instance et la remplir via le formulaire
            Article article = new Article();
            article = form.getData(article);
            
            // Date de création automatique
            article.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
            
            // Insertion via CRUD
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
}
