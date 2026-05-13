package inventory.ui.screen;

import inventory.feature.repository.dao.GenericMethodCRUD;
import inventory.feature.stock.StockManager;
import inventory.models.StockMovement;
import inventory.models.TypeStockMovement;

import javax.swing.*;
import java.util.List;

/**
 * Écran de mouvement de stock (Entrée/Sortie).
 */
public class StockMovementScreen extends AbstractFormScreen<StockMovement> {

    private final GenericMethodCRUD crud = new GenericMethodCRUD();
    private final StockManager stockManager = new StockManager();

    public StockMovementScreen() {
        super("Mouvement de stock", new StockMovement());
        setupFormListeners();
    }

    private void setupFormListeners() {
        // Écouter les changements de type de mouvement
        form.addFieldListener("typeStockMovement", value -> {
            if (value instanceof TypeStockMovement) {
                String typeName = ((TypeStockMovement) value).getNameType().toLowerCase();
                boolean isExit = typeName.contains("sort");
                
                // Masquer le prix unitaire si c'est une sortie
                form.setFieldVisible("unitPrice", !isExit);
            }
        });
    }

    @Override
    protected void saveData() {
        try {
            // Récupérer les données de base du formulaire
            StockMovement request = new StockMovement();
            request = form.getData(request);
            request.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));

            // Générer la référence de transaction UNIQUE pour cette opération
            String ref = "REF-" + System.currentTimeMillis();
            request.setTransactionRef(ref);

            String typeName = request.getTypeStockMovement() != null ? 
                              request.getTypeStockMovement().getNameType().toLowerCase() : "";

            if (typeName.contains("sort")) {
                // LOGIQUE DE SORTIE : Utilisation du StockManager pour le découpage FIFO/LIFO/CUMP
                List<StockMovement> toInsert = stockManager.prepareExit(request);
                
                for (StockMovement m : toInsert) {
                    crud.insertData(m);
                }
                
                JOptionPane.showMessageDialog(this,
                    "Sortie enregistrée avec succès en " + toInsert.size() + " ligne(s) de mouvement.",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // LOGIQUE D'ENTRÉE : Insertion simple
                request = crud.insertData(request);
                int id = request.getId();
                JOptionPane.showMessageDialog(this, 
                    "Entrée enregistrée avec succès ! (id=" + id + ")", 
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
            }

            // Réinitialiser le formulaire
            resetForm();

        } catch (Exception ex) {
            System.err.println("Erreur lors de l'enregistrement : " + ex.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Erreur : " + ex.getMessage(), 
                "Erreur", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
