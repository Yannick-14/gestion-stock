package inventory;

import inventory.feature.applicationService.GenerateTable;
import inventory.feature.repository.dao.GenericMethodCRUD;
import inventory.models.Article;
import inventory.models.StockManagementMethod;
import inventory.models.StockMovement;
import inventory.models.TypeStockMovement;

import java.util.List;

/**
 * Point d'entrée principal — Migration de la base de données.
 *
 * Lance la création/synchronisation des tables, puis insère des données initiales.
 */
public class Main {

    public static void main(String[] args) {
        try {
            // 1. Migration de schéma (création des tables)
            GenerateTable migration = new GenerateTable(
                StockManagementMethod.class,
                TypeStockMovement.class,
                Article.class,
                StockMovement.class
            );
            migration.migrate();

            // 2. Migration de données (insertions initiales)
            GenericMethodCRUD crud = new GenericMethodCRUD();

            System.out.println("\n[Main] Vérification et insertion des données initiales...");

            // Méthodes de gestion de stock
            if (crud.findAllData(new StockManagementMethod()).isEmpty()) {
                System.out.println("  → Insertion des méthodes de gestion de stock...");
                crud.insertData(new StockManagementMethod("CUMP"));
                crud.insertData(new StockManagementMethod("FIFO"));
                crud.insertData(new StockManagementMethod("LIFO"));
            } else {
                System.out.println("  → Méthodes de gestion de stock déjà présentes.");
            }

            // Types de mouvement
            if (crud.findAllData(new TypeStockMovement()).isEmpty()) {
                System.out.println("  → Insertion des types de mouvement...");
                crud.insertData(new TypeStockMovement("Entrée"));
                crud.insertData(new TypeStockMovement("Sortie"));
            } else {
                System.out.println("  → Types de mouvement déjà présents.");
            }

            System.out.println("\n[Main] Migration d'insertion terminée.");

        } catch (Exception e) {
            System.err.println("[Main] Erreur lors de la migration: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
