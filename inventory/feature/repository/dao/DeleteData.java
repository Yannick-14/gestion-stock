package inventory.feature.repository.dao;

import inventory.feature.repository.connection.DbConnection;
import java.sql.Connection;
import java.sql.Statement;

/**
 * Service pour vider les tables de la base de données.
 */
public class DeleteData {

    /**
     * Supprime toutes les données des tables correspondant aux classes fournies.
     * Utilise TRUNCATE CASCADE pour gérer les dépendances (si supporté par la BDD).
     * Sinon, utilise DELETE FROM.
     */
    public void deleteAll(Class<?>... classes) {
        if (classes == null || classes.length == 0) return;

        try (Connection conn = DbConnection.getInstance().getConnexion();
             Statement stmt = conn.createStatement()) {
            
            System.out.println("\n═══════════════════════════════════════════");
            System.out.println("       SUPPRESSION DES DONNÉES             ");
            System.out.println("═══════════════════════════════════════════");

            for (Class<?> cls : classes) {
                String nomTable = toSnakeCase(cls.getSimpleName());
                // TRUNCATE est plus rapide et réinitialise souvent les compteurs SERIAL
                // CASCADE permet de supprimer même si d'autres tables pointent dessus (attention)
                String sql = "TRUNCATE TABLE " + nomTable + " RESTART IDENTITY CASCADE";
                
                try {
                    stmt.execute(sql);
                    System.out.println("  [OK] Table vidée: " + nomTable);
                } catch (Exception e) {
                    // Fallback vers DELETE si TRUNCATE échoue ou n'est pas supporté comme attendu
                    String sqlFallback = "DELETE FROM " + nomTable;
                    stmt.execute(sqlFallback);
                    System.out.println("  [OK] Table vidée (fallback DELETE): " + nomTable);
                }
            }
            
            System.out.println("═══════════════════════════════════════════\n");

        } catch (Exception e) {
            System.err.println("[DeleteData] Erreur lors de la suppression: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Convertit PascalCase en snake_case.
     */
    private String toSnakeCase(String name) {
        if (name == null || name.isEmpty()) return name;
        return name
            .replaceAll("([A-Z]+)([A-Z][a-z])", "$1_$2")
            .replaceAll("([a-z])([A-Z])", "$1_$2")
            .toLowerCase();
    }
}
