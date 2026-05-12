package inventory.ui.screen;

import inventory.feature.repository.dao.GenericMethodCRUD;
import inventory.models.StockMovement;
import inventory.models.TypeStockMovement;
import inventory.ui.components.button.Button;
import inventory.ui.components.fields.FieldSelect;

import javax.swing.*;
import java.util.List;

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
        
        // Forcer le type "Entrée" dans le sélecteur
        forceEntreeType();
        
        addAction("Enregistrer le mouvement", Button.Style.PRIMARY, e -> saveMovement());
    }

    /**
     * Recherche le type "Entrée" et l'isole dans le combo pour forcer le choix.
     */
    private void forceEntreeType() {
        try {
            TypeStockMovement entree = null;
            List<TypeStockMovement> types = crud.findAllData(new TypeStockMovement());
            for (TypeStockMovement t : types) {
                if (t.getNameType() != null && t.getNameType().toLowerCase().contains("entr")) {
                    entree = t;
                    break;
                }
            }

            if (entree != null) {
                // Récupérer le composant FieldSelect pour typeStockMovement
                JPanel panel = form.getFieldComponent("typeStockMovement");
                if (panel instanceof FieldSelect) {
                    FieldSelect<TypeStockMovement> select = (FieldSelect<TypeStockMovement>) panel;
                    select.setSelectedValue(entree);
                    // On peut même désactiver le champ pour forcer l'utilisateur
                    select.getComboBox().setEnabled(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveMovement() {
        try {
            StockMovement movement = new StockMovement();
            movement = form.getData(movement);
            movement.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
            
            int id = crud.insertData(movement);
            JOptionPane.showMessageDialog(this,
                "Mouvement de stock enregistré ! (id=" + id + ")",
                "Succès", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de l'enregistrement : " + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
