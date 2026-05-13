package inventory.feature.applicationService;

import inventory.feature.repository.connection.DbConnection;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Génère et synchronise les tables SQL depuis les classes Java (migration).
 *
 * Convention de nommage:
 *   - Classe Java (camelCase/PascalCase) → table SQL (snake_case)
 *   - Champ Java (camelCase)             → colonne SQL (snake_case)
 *   - Champ de type objet métier          → FK: "id_" + snake_case du champ
 *   - Le premier champ nommé "id"         → SERIAL PRIMARY KEY
 */
public class GenerateTable {

    // Tableau des classes à migrer (dans l'ordre des dépendances)
    private final Class<?>[] classes;

    public GenerateTable(Class<?>... classes) {
        this.classes = classes;
    }

    // ── Point d'entrée de la migration ────────────────────────────────────────

    /**
     * Pour chaque classe: vérifie si la table existe, la crée ou la met à jour.
     */
    public void migrate() {
        try {
            Connection conn = DbConnection.getInstance().getConnexion();
            System.out.println("═══════════════════════════════════════════");
            System.out.println("       MIGRATION DE BASE DE DONNÉES        ");
            System.out.println("═══════════════════════════════════════════");

            for (Class<?> cls : classes) {
                String nomTable = toSnakeCase(cls.getSimpleName());
                System.out.println("\n[Table] " + nomTable + " ← " + cls.getSimpleName());

                if (checkStateTable(conn, nomTable)) {
                    System.out.println("  → Table existante, vérification des mises à jour...");
                    updateStateTable(conn, cls);
                } else {
                    System.out.println("  → Table absente, création en cours...");
                    createTable(conn, cls);
                }
            }

            System.out.println("\n═══════════════════════════════════════════");
            System.out.println("       MIGRATION TERMINÉE AVEC SUCCÈS      ");
            System.out.println("═══════════════════════════════════════════");

        } catch (Exception e) {
            System.err.println("[GenerateTable] Erreur de migration: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ── checkStateTable ───────────────────────────────────────────────────────

    /**
     * Vérifie si une table existe dans la base de données.
     * @return true si la table existe, false sinon
     */
    public boolean checkStateTable(Connection conn, String nomTable) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        try (ResultSet rs = meta.getTables(null, null, nomTable, new String[]{"TABLE"})) {
            return rs.next();
        }
    }

    // ── updateStateTable ──────────────────────────────────────────────────────

    /**
     * Compare les champs Java avec les colonnes existantes en BDD.
     * Ajoute les colonnes manquantes via ALTER TABLE ADD COLUMN.
     */
    public void updateStateTable(Connection conn, Class<?> cls) throws SQLException {
        String nomTable = toSnakeCase(cls.getSimpleName());
        List<String> colonnesExistantes = getColonnesExistantes(conn, nomTable);

        for (Field champ : cls.getDeclaredFields()) {
            String nomColonne = getNomColonne(champ);
            if (!colonnesExistantes.contains(nomColonne)) {
                String typeSql = getSqlType(champ);
                String sql = "ALTER TABLE " + nomTable + " ADD COLUMN " + nomColonne + " " + typeSql;
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(sql);
                    System.out.println("  [ALTER] Colonne ajoutée: " + nomColonne + " (" + typeSql + ")");
                }
            }
        }
    }

    // ── createTable ───────────────────────────────────────────────────────────

    /**
     * Crée la table SQL correspondant à la classe Java.
     * - id → SERIAL PRIMARY KEY
     * - champ objet métier → id_<champ> INT REFERENCES <table_cible>(id)
     * - String → VARCHAR(255)
     * - int/Integer → INT
     * - double/Double → DOUBLE PRECISION
     * - Timestamp/Date → TIMESTAMP
     */
    public void createTable(Connection conn, Class<?> cls) throws SQLException {
        String nomTable = toSnakeCase(cls.getSimpleName());
        Field[] champs = cls.getDeclaredFields();

        StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        sql.append(nomTable).append(" (\n");

        List<String> fkLines = new ArrayList<>();

        for (int i = 0; i < champs.length; i++) {
            Field champ = champs[i];
            String nomColonne = getNomColonne(champ);
            String typeSql   = getSqlType(champ);

            sql.append("  ").append(nomColonne).append(" ").append(typeSql);

            // FK: si champ de type objet métier
            if (isMetierType(champ.getType())) {
                String tableRef = toSnakeCase(champ.getType().getSimpleName());
                fkLines.add("  FOREIGN KEY (" + nomColonne + ") REFERENCES " + tableRef + "(id)");
            }

            sql.append(",\n");
        }

        // Ajouter les FK à la fin
        if (fkLines.isEmpty()) {
            // Supprimer la dernière virgule
            int idx = sql.lastIndexOf(",\n");
            if (idx >= 0) sql.delete(idx, idx + 2).append("\n");
        } else {
            for (int i = 0; i < fkLines.size(); i++) {
                sql.append(fkLines.get(i));
                if (i < fkLines.size() - 1) sql.append(",\n");
                else sql.append("\n");
            }
        }

        sql.append(")");

        System.out.println("  [SQL] " + sql.toString().replace("\n", " "));
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql.toString());
            System.out.println("  [OK] Table créée: " + nomTable);
        }
    }

    // ── Helpers privés ────────────────────────────────────────────────────────

    private List<String> getColonnesExistantes(Connection conn, String nomTable) throws SQLException {
        List<String> colonnes = new ArrayList<>();
        DatabaseMetaData meta = conn.getMetaData();
        try (ResultSet rs = meta.getColumns(null, null, nomTable, null)) {
            while (rs.next()) {
                colonnes.add(rs.getString("COLUMN_NAME").toLowerCase());
            }
        }
        return colonnes;
    }

    private String getNomColonne(Field champ) {
        if (champ.getName().equals("id")) return "id";
        if (isMetierType(champ.getType())) {
            return "id_" + toSnakeCase(champ.getName());
        }
        return toSnakeCase(champ.getName());
    }

    private String getSqlType(Field champ) {
        if (champ.getName().equals("id")) return "SERIAL PRIMARY KEY";

        Class<?> type = champ.getType();

        if (isMetierType(type)) return "INT";
        if (type == String.class) return "VARCHAR(255)";
        if (type == int.class || type == Integer.class) return "INT";
        if (type == long.class || type == Long.class) return "BIGINT";
        if (type == double.class || type == Double.class || type == float.class  || type == Float.class) return "DOUBLE PRECISION";
        if (type == boolean.class || type == Boolean.class) return "BOOLEAN";
        if (type == java.sql.Timestamp.class || type == java.time.LocalDateTime.class) return "TIMESTAMP";
        if (type == java.sql.Date.class || type == java.util.Date.class || type == java.time.LocalDate.class) return "DATE";
        if (type == byte[].class) return "BYTEA";

        return "TEXT"; // fallback
    }

    private boolean isMetierType(Class<?> type) {
        if (type.isPrimitive()) return false;
        if (type == String.class) return false;
        if (Number.class.isAssignableFrom(type)) return false;
        if (type == Boolean.class) return false;
        if (java.util.Date.class.isAssignableFrom(type)) return false;
        if (java.sql.Date.class.isAssignableFrom(type)) return false;
        if (java.sql.Timestamp.class.isAssignableFrom(type)) return false;
        if (type == java.time.LocalDate.class || type == java.time.LocalDateTime.class) return false;
        if (type.getName().startsWith("java.")) return false;
        return true; // c'est un objet métier → FK
    }

    public static String toSnakeCase(String name) {
        if (name == null || name.isEmpty()) return name;
        return name
            .replaceAll("([A-Z]+)([A-Z][a-z])", "$1_$2")
            .replaceAll("([a-z])([A-Z])", "$1_$2")
            .toLowerCase();
    }
}
