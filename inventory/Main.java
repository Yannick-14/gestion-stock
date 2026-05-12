package inventory;

import inventory.feature.applicationService.GenerateTable;
import inventory.models.Article;
import inventory.models.StockManagementMethod;
import inventory.models.StockMovement;
import inventory.models.TypeStockMovement;

/**
 * Point d'entrée principal — Migration de la base de données.
 *
 * Lance la création/synchronisation des tables dans l'ordre des dépendances:
 *   1. stock_management_method  (aucune dépendance)
 *   2. type_stock_movement      (aucune dépendance)
 *   3. article                  (FK → stock_management_method)
 *   4. stock_movement           (FK → article, type_stock_movement)
 */
public class Main {

    public static void main(String[] args) {
        // Ordre de migration: dépendances d'abord
        GenerateTable migration = new GenerateTable(
            StockManagementMethod.class,
            TypeStockMovement.class,
            Article.class,
            StockMovement.class
        );

        migration.migrate();
    }
}
