package inventory.feature.repository.dao;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import inventory.feature.repository.connection.DbConnection;

/**
 * Classe générique de CRUD.
 * - Le nom de table est déduit automatiquement depuis la classe (camelCase → snake_case).
 * - Les noms de colonnes suivent la même convention.
 * - Une seule connexion par opération, fermée proprement.
 */
public class GenericMethodCRUD {

    // ── Utilitaire: camelCase → snake_case ────────────────────────────────────
    /**
     * Convertit un nom camelCase ou PascalCase en snake_case.
     * Ex: "stockManagementMethod" → "stock_management_method"
     *     "Article"               → "article"
     */
    protected static String toSnakeCase(String name) {
        if (name == null || name.isEmpty()) return name;
        return name
            .replaceAll("([A-Z]+)([A-Z][a-z])", "$1_$2")
            .replaceAll("([a-z])([A-Z])", "$1_$2")
            .toLowerCase();
    }

    /**
     * Déduit le nom de table SQL depuis un objet Java.
     * Ex: new Article() → "article", new StockMovement() → "stock_movement"
     */
    protected static String tableNomDepuisClasse(Object obj) {
        return toSnakeCase(obj.getClass().getSimpleName());
    }

    // ── findAllData ───────────────────────────────────────────────────────────
    /**
     * Récupère toutes les lignes d'une table correspondant à la classe de l'objet.
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> findAllData(T obj) throws Exception {
        String table = tableNomDepuisClasse(obj);
        Class<T> cls = (Class<T>) obj.getClass();
        String sql = "SELECT * FROM " + table;
        List<T> resultats = new ArrayList<>();

        try (Connection conn = DbConnection.getInstance().getConnexion();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {

            Field[] champs = cls.getDeclaredFields();
            while (rs.next()) {
                T instance = cls.getDeclaredConstructor().newInstance();
                for (Field champ : champs) {
                    champ.setAccessible(true);
                    String col = toSnakeCase(champ.getName());
                    
                    try {
                        // verified foreign key
                        String fkCol = "id_" + col;
                        Object fkValue = null;
                        
                        try {
                            fkValue = rs.getObject(fkCol);
                        } catch (Exception e) {
                            // Pas de colonne FK correspondante
                        }
                        
                        if (fkValue != null && !isPrimitiveOrWrapper(champ.getType()) 
                            && !champ.getType().equals(String.class)
                            && !java.util.Date.class.isAssignableFrom(champ.getType())
                            && !java.sql.Timestamp.class.isAssignableFrom(champ.getType())) {
                            // C'est un objet lié, charger la FK
                            Object relatedObject = loadRelatedObject(champ.getType(), (Integer) fkValue, conn);
                            champ.set(instance, relatedObject);
                        } else {
                            // Champ normal
                            try {
                                champ.set(instance, rs.getObject(col));
                            } catch (Exception e) {
                                // Essayer avec le nom exact de la colonne
                                try {
                                    champ.set(instance, rs.getObject(champ.getName()));
                                } catch (Exception e2) {
                                    // Ignorer si la colonne n'existe pas
                                }
                            }
                        }
                    } catch (Exception ignored) { 
                        // colonne absente ou type incompatible
                    }
                }
                resultats.add(instance);
            }
        }
        System.out.println("[CRUD] findAllData → " + resultats.size() + " ligne(s) dans " + table);
        return resultats;
    }

    /**
     * Charge un objet lié par sa clé étrangère
     */
    private Object loadRelatedObject(Class<?> relatedClass, int id, Connection conn) throws Exception {
        String tableName = toSnakeCase(relatedClass.getSimpleName());
        String sql = "SELECT * FROM " + tableName + " WHERE id = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Object relatedInstance = relatedClass.getDeclaredConstructor().newInstance();
                    Field[] fields = relatedClass.getDeclaredFields();
                    
                    for (Field field : fields) {
                        field.setAccessible(true);
                        String colName = toSnakeCase(field.getName());
                        
                        try {
                            Object value = rs.getObject(colName);
                            if (value != null) {
                                field.set(relatedInstance, value);
                            }
                        } catch (Exception e) {
                            // Essayer avec le nom exact du champ
                            try {
                                Object value = rs.getObject(field.getName());
                                if (value != null) {
                                    field.set(relatedInstance, value);
                                }
                            } catch (Exception e2) {
                                // Ignorer les champs qui n'ont pas de colonne correspondante
                            }
                        }
                    }
                    
                    System.out.println("[CRUD] FK chargée: " + relatedClass.getSimpleName() + "(id=" + id + ") -> " + relatedInstance);
                    return relatedInstance;
                }
            }
        }
        
        System.out.println("[CRUD] FK non trouvée: " + relatedClass.getSimpleName() + "(id=" + id + ")");
        return null;
    }

    // ── read ──────────────────────────────────────────────────────────────────
    /**
     * Récupère une ligne par son id.
     * Convention: la PK s'appelle "id" en BDD.
     */
    @SuppressWarnings("unchecked")
    public <T> T read(T obj, int id) throws Exception {
        String table = tableNomDepuisClasse(obj);
        Class<T> cls = (Class<T>) obj.getClass();
        String sql = "SELECT * FROM " + table + " WHERE id = ?";

        try (Connection conn = DbConnection.getInstance().getConnexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    T instance = cls.getDeclaredConstructor().newInstance();
                    for (Field champ : cls.getDeclaredFields()) {
                        champ.setAccessible(true);
                        String col = toSnakeCase(champ.getName());
                        try {
                            champ.set(instance, rs.getObject(col));
                        } catch (Exception ignored) {}
                    }
                    System.out.println("[CRUD] read → id=" + id + " trouvé dans " + table);
                    return instance;
                }
            }
        }
        System.out.println("[CRUD] read → id=" + id + " non trouvé dans " + table);
        return null;
    }

    // ── findDataWithRequest ───────────────────────────────────────────────────
    /**
     * Récupère des lignes avec une clause WHERE libre.
     * @param obj         instance de la classe cible (pour déduire la table et le type)
     * @param whereClause ex: "WHERE name_method = 'FIFO' ORDER BY id"
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> findDataWithRequest(T obj, String whereClause) throws Exception {
        String table = tableNomDepuisClasse(obj);
        Class<T> cls = (Class<T>) obj.getClass();
        String sql = "SELECT * FROM " + table
                   + (whereClause != null && !whereClause.trim().isEmpty() ? " " + whereClause : "");
        List<T> resultats = new ArrayList<>();

        try (Connection conn = DbConnection.getInstance().getConnexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            Field[] champs = cls.getDeclaredFields();
            while (rs.next()) {
                T instance = cls.getDeclaredConstructor().newInstance();
                for (Field champ : champs) {
                    champ.setAccessible(true);
                    String col = toSnakeCase(champ.getName());
                    try {
                        champ.set(instance, rs.getObject(col));
                    } catch (Exception ignored) {}
                }
                resultats.add(instance);
            }
        }
        System.out.println("[CRUD] findDataWithRequest → " + resultats.size() + " ligne(s) dans " + table);
        return resultats;
    }

    // ── insertData ────────────────────────────────────────────────────────────
    /**
     * Insère un objet dans la table correspondante.
     * Ignore le premier champ (id auto-généré).
     * @return l'objet inséré avec son ID mis à jour
     */
    @SuppressWarnings("unchecked")
    public <T> T insertData(T obj) throws Exception {
        String table = tableNomDepuisClasse(obj);
        Field[] champs = obj.getClass().getDeclaredFields();

        StringBuilder cols = new StringBuilder();
        StringBuilder vals = new StringBuilder();
        List<Field> champsInserer = new ArrayList<>();

        for (Field champ : champs) {
            champ.setAccessible(true);
            // Ignorer le champ id (premier champ, auto-généré)
            if (champ.getName().equals("id")) continue;
            // Si le type du champ est un objet métier → on stocke son id
            Object valeur = champ.get(obj);
            if (valeur != null && !isPrimitiveOrWrapper(valeur.getClass()) && !(valeur instanceof String)) {
                // FK: on stocke id_ + snake_case du champ
                if (cols.length() > 0) { cols.append(", "); vals.append(", "); }
                cols.append("id_").append(toSnakeCase(champ.getName()));
                vals.append("?");
                champsInserer.add(champ); // on extrait l'id plus bas
            } else {
                if (cols.length() > 0) { cols.append(", "); vals.append(", "); }
                cols.append(toSnakeCase(champ.getName()));
                vals.append("?");
                champsInserer.add(champ);
            }
        }

        String sql = "INSERT INTO " + table + " (" + cols + ") VALUES (" + vals + ")";

        try (Connection conn = DbConnection.getInstance().getConnexion();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            int idx = 1;
            for (Field champ : champsInserer) {
                champ.setAccessible(true);
                Object valeur = champ.get(obj);
                if (valeur != null && !isPrimitiveOrWrapper(valeur.getClass()) && !(valeur instanceof String)) {
                    // Extraire l'id de l'objet FK
                    Field idField = valeur.getClass().getDeclaredField("id");
                    idField.setAccessible(true);
                    ps.setObject(idx++, idField.get(valeur));
                } else {
                    ps.setObject(idx++, valeur);
                }
            }
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int genId = rs.getInt(1);
                    // Mettre à jour l'ID de l'objet via réflexion
                    try {
                        Field idField = obj.getClass().getDeclaredField("id");
                        idField.setAccessible(true);
                        idField.set(obj, genId);
                    } catch (NoSuchFieldException e) {
                        try {
                            Field idField = obj.getClass().getSuperclass().getDeclaredField("id");
                            idField.setAccessible(true);
                            idField.set(obj, genId);
                        } catch (Exception e2) {
                            // Pas de champ id trouvé
                        }
                    }
                    System.out.println("[CRUD] insertData → id généré=" + genId + " dans " + table);
                }
            }
        }
        return obj;
    }

    // ── update ────────────────────────────────────────────────────────────────
    /**
     * Met à jour une ligne par son id.
     * Seuls les champs non-null (hors id) sont inclus dans le SET.
     */
    public void update(Object obj, int id) throws Exception {
        String table = tableNomDepuisClasse(obj);
        Field[] champs = obj.getClass().getDeclaredFields();

        StringBuilder set = new StringBuilder();
        List<Object> valeurs = new ArrayList<>();

        for (Field champ : champs) {
            champ.setAccessible(true);
            if (champ.getName().equals("id")) continue;
            Object valeur = champ.get(obj);
            if (valeur == null) continue;

            if (set.length() > 0) set.append(", ");

            if (!isPrimitiveOrWrapper(valeur.getClass()) && !(valeur instanceof String)) {
                // FK
                set.append("id_").append(toSnakeCase(champ.getName())).append(" = ?");
                Field idField = valeur.getClass().getDeclaredField("id");
                idField.setAccessible(true);
                valeurs.add(idField.get(valeur));
            } else {
                set.append(toSnakeCase(champ.getName())).append(" = ?");
                valeurs.add(valeur);
            }
        }

        if (set.length() == 0) {
            System.out.println("[CRUD] update → aucun champ à mettre à jour dans " + table);
            return;
        }

        String sql = "UPDATE " + table + " SET " + set + " WHERE id = ?";
        valeurs.add(id);

        try (Connection conn = DbConnection.getInstance().getConnexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (int i = 0; i < valeurs.size(); i++) {
                ps.setObject(i + 1, valeurs.get(i));
            }
            int rows = ps.executeUpdate();
            System.out.println("[CRUD] update → " + rows + " ligne(s) mise(s) à jour dans " + table + " (id=" + id + ")");
        }
    }

    // ── delete ────────────────────────────────────────────────────────────────
    /**
     * Supprime une ligne par son id.
     */
    public void delete(Object obj, int id) throws Exception {
        String table = tableNomDepuisClasse(obj);
        String sql = "DELETE FROM " + table + " WHERE id = ?";

        try (Connection conn = DbConnection.getInstance().getConnexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            System.out.println("[CRUD] delete → " + rows + " ligne(s) supprimée(s) dans " + table + " (id=" + id + ")");
        }
    }

    // ── Utilitaire type ───────────────────────────────────────────────────────
    private boolean isPrimitiveOrWrapper(Class<?> type) {
        return type.isPrimitive()
            || type == Integer.class   || type == int.class
            || type == Long.class      || type == long.class
            || type == Double.class    || type == double.class
            || type == Float.class     || type == float.class
            || type == Boolean.class   || type == boolean.class
            || type == Byte.class      || type == byte.class
            || type == Short.class     || type == short.class
            || type == Character.class || type == char.class
            || type == String.class
            || java.util.Date.class.isAssignableFrom(type)
            || java.sql.Timestamp.class.isAssignableFrom(type)
            || java.sql.Date.class.isAssignableFrom(type);
    }
}