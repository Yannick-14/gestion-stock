package inventory.ui.screen;

import inventory.feature.repository.dao.GenericMethodCRUD;
import inventory.models.StockMovement;

import javax.swing.*;

/**
 * Écran d'entrée en stock.
 * 
 * Hérite de AbstractFormScreen.
 * Force le type de mouvement à "Entrée".
 */
public class StockMovementScreen extends AbstractFormScreen<StockMovement> {

    private final GenericMethodCRUD crud = new GenericMethodCRUD();

    public StockMovementScreen() {
        super("Entrée en stock", new StockMovement());
    }
    /**
     * Implémentation de la méthode abstraite saveArticle.
     * Enregistre l'article en base de données.
     */
    @Override
    protected void saveData() {
        try {
            // Créer une nouvelle instance et la remplir via le formulaire
            StockMovement movement = new StockMovement();
            movement = form.getData(movement);

            // Date de création automatique
            movement.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
            
            // Insertion via CRUD
            int id = crud.insertData(movement);
            
            JOptionPane.showMessageDialog(this,
                "Mouvement de stock enregistré avec succès ! (id=" + id + ")",
                "Succès", JOptionPane.INFORMATION_MESSAGE);
                
            // Réinitialiser le formulaire après l'enregistrement réussi
            resetForm();

        } catch (Exception ex) {
            System.err.println("Erreur lors de l'enregistrement mouvement: " + ex);
            JOptionPane.showMessageDialog(this,
                "Erreur lors de l'enregistrement : " + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
