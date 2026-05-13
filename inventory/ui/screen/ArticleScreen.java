package inventory.ui.screen;

import inventory.feature.repository.dao.GenericMethodCRUD;
import inventory.models.Article;
// import inventory.ui.components.button.Button;

import javax.swing.*;

/**
 * Écran d'insertion d'un article.
 * 
 * Hérite de AbstractFormScreen pour bénéficier du design standard.
 * Les boutons "Réinitialiser" et "Enregistrer" sont inclus par défaut.
 * Le formulaire est entièrement automatique grâce à la classe Form.
 */
public class ArticleScreen extends AbstractFormScreen<Article> {

    private final GenericMethodCRUD crud = new GenericMethodCRUD();

    public ArticleScreen() {
        super("Insertion d'article", new Article());
        
        // Ajouter des actions supplémentaires si nécessaire
        // Exemple : un bouton "Retour" avant les boutons par défaut
        // setActions(java.util.List.of(
        //     new FormAction("Retour", Button.Style.SECONDARY, e -> goBack())
        // ));
    }

    /**
     * Implémentation de la méthode abstraite saveArticle.
     * Enregistre l'article en base de données.
     */
    @Override
    protected void saveData() {
        try {
            // Créer une nouvelle instance et la remplir via le formulaire
            Article article = new Article();
            article = form.getData(article);
            
            // Date de création automatique
            article.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));

            // Insertion via CRUD
            article = crud.insertData(article);
            int id = article.getId();

            JOptionPane.showMessageDialog(this,
                "Article enregistré avec succès ! (id=" + id + ")",
                "Succès", JOptionPane.INFORMATION_MESSAGE);

            // Réinitialiser le formulaire après l'enregistrement réussi
            resetForm();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de l'enregistrement : " + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /**
     * Exemple d'action supplémentaire : retour à l'écran précédent.
     */
    // private void goBack() {
    //     // Logique pour naviguer vers l'écran précédent
    //     JOptionPane.showMessageDialog(this,
    //         "Retour à l'écran précédent",
    //         "Navigation", JOptionPane.INFORMATION_MESSAGE);
    // }
}
