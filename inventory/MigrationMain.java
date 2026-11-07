package inventory;

import inventory.feature.applicationService.GenerateTable;
import inventory.feature.repository.dao.GenericMethodCRUD;
import inventory.models.Article;
import inventory.models.StockManagementMethod;
import inventory.models.StockMovement;
import inventory.models.TypeStockMovement;

import java.util.ArrayList;
import java.util.List;

/**
 * MigrationMain — Gère la création et la synchronisation du schéma de la base de données.
 */
public class MigrationMain {

    public static void main(String[] args) {
        try {
            System.out.println("[MigrationMain] Lancement de la migration du schéma...");

            // Liste des classes à migrer pour assurer la structure de la base
            List<Class<?>> classes = new ArrayList<>();
            classes.add(StockManagementMethod.class);
            classes.add(TypeStockMovement.class);
            classes.add(Article.class);
            classes.add(StockMovement.class);

            GenerateTable migration = new GenerateTable(classes.toArray(new Class<?>[0]));
            migration.migrate();

            // Initialisation des données de base si nécessaire (référentiels)
            seedReferenceData();

            System.out.println("\n[MigrationMain] Migration du schéma terminée avec succès.");

        } catch (Exception e) {
            System.err.println("[MigrationMain] Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void seedReferenceData() throws Exception {
        GenericMethodCRUD crud = new GenericMethodCRUD();
        
        // Types de mouvement de base
        if (crud.findAllData(new TypeStockMovement()).isEmpty()) {
            System.out.println("  → Insertion des types de mouvement par défaut...");
            crud.insertData(new TypeStockMovement("Entrée"));
            crud.insertData(new TypeStockMovement("Sortie"));
        }

        // Méthodes de gestion de stock par défaut
        if (crud.findAllData(new StockManagementMethod()).isEmpty()) {
            System.out.println("  → Insertion des méthodes de gestion de stock par défaut...");
            crud.insertData(new StockManagementMethod("CUMP"));
            crud.insertData(new StockManagementMethod("FIFO"));
            crud.insertData(new StockManagementMethod("LIFO"));
        }
    }
}
