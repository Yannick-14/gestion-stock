package inventory.ui.screen;

// import inventory.feature.repository.dao.GenericMethodCRUD;
import inventory.models.Article;
// import inventory.ui.components.button.Button;

// import javax.swing.*;

/**
 * Écran d'insertion d'un article.
 * 
 * Hérite de AbstractFormScreen pour bénéficier du design standard.
 * Les boutons "Réinitialiser" et "Enregistrer" sont inclus par défaut.
 * Le formulaire est entièrement automatique grâce à la classe Form.
 */
public class ArticleScreen extends AbstractFormScreen<Article> {

    // private final GenericMethodCRUD crud = new GenericMethodCRUD();

    public ArticleScreen() {
        super("Insertion d'article", new Article());
        
        // Ajouter des actions supplémentaires si nécessaire
        // Exemple : un bouton "Retour" avant les boutons par défaut
        // setActions(java.util.List.of(
        //     new FormAction("Retour", Button.Style.SECONDARY, e -> goBack())
        // ));
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
