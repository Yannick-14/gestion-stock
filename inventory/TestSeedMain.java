package inventory;

import inventory.feature.repository.dao.GenericMethodCRUD;
import inventory.models.Article;
import inventory.models.StockManagementMethod;
import inventory.models.StockMovement;
import inventory.models.TypeStockMovement;
import inventory.models.Role;

import java.util.List;

/**
 * TestSeedMain — Point d'entrée pour insérer des données de test (Articles et Mouvements).
 */
public class TestSeedMain {

    public static void main(String[] args) {
        try {
            GenericMethodCRUD crud = new GenericMethodCRUD();
            System.out.println("[TestSeedMain] Insertion de données de test...");

            // 1. Récupérer ou créer les données de référence
            StockManagementMethod method = getFirst(crud, new StockManagementMethod(), "CUMP");
            TypeStockMovement typeEntree = getFirst(crud, new TypeStockMovement(), "Entrée");
            TypeStockMovement typeSortie = getFirst(crud, new TypeStockMovement(), "Sortie");

            // 2. Créer des Articles de test
            Article art1 = new Article("Ordinateur portable", method);
            Article art2 = new Article("Souris sans fil", method);
            Article art3 = new Article("Clavier mécanique", method);

            art1 = crud.insertData(art1);
            art2 = crud.insertData(art2);
            art3 = crud.insertData(art3);

            System.out.println("  → Articles créés: " + art1 + ", " + art2 + ", " + art3);

            // 3. Créer des Mouvements de stock
            System.out.println("  → Insertion de mouvements...");
            
            // Entrées
            crud.insertData(new StockMovement(art1, typeEntree, 10, 1500.0));
            crud.insertData(new StockMovement(art2, typeEntree, 50, 25.0));
            crud.insertData(new StockMovement(art3, typeEntree, 20, 80.0));

            // Sorties
            crud.insertData(new StockMovement(art1, typeSortie, 2, 1500.0));
            crud.insertData(new StockMovement(art2, typeSortie, 10, 25.0));

            Role role = new Role("Super Admin");
            role = crud.insertData(role);
            System.out.println("  → Roles créés "+ role);

            System.out.println("[TestSeedMain] Données de test insérées avec succès.");

        } catch (Exception e) {
            System.err.println("[TestSeedMain] Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Utilitaire pour récupérer le premier élément ou en créer un si absent.
     */
    private static <T> T getFirst(GenericMethodCRUD crud, T template, String nameToSearch) throws Exception {
        List<T> list = crud.findAllData(template);
        if (list.isEmpty()) {
            return crud.insertData(template); // Simplifié : on insère le template vide/par défaut
        }
        
        // Essayer de trouver par nom si possible
        for (T item : list) {
            String str = item.toString();
            if (str != null && str.equalsIgnoreCase(nameToSearch)) {
                return item;
            }
        }
        return list.get(0);
    }
}
