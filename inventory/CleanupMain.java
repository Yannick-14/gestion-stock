package inventory;

import inventory.feature.repository.dao.DeleteData;
import inventory.models.*;

/**
 * CleanupMain — Point d'entrée pour vider les tables de la base de données.
 */
public class CleanupMain {

    public static void main(String[] args) {
        DeleteData cleaner = new DeleteData();

        System.out.println("[CleanupMain] Début du nettoyage des données...");

        // Ordre recommandé: de la table la plus dépendante à la moins dépendante
        // Toutefois, DeleteData utilise CASCADE donc l'ordre importe peu.
        
        // Vider les mouvements de stock (transactions)
        cleaner.deleteAll(TypeStockMovement.class);
        cleaner.deleteAll(StockManagementMethod.class);
        cleaner.deleteAll(StockMovement.class);
        cleaner.deleteAll(Article.class);
        
        // Vider les articles
        // cleaner.deleteAll(Article.class);
        
        // Vider les types et méthodes (Attention: données de référence)
        // cleaner.deleteAll(TypeStockMovement.class, StockManagementMethod.class);

        System.out.println("[CleanupMain] Fin du nettoyage.");
    }
}
