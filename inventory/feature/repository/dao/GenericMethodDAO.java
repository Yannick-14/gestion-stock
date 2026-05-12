package inventory.feature.repository.dao;

import java.util.List;

/**
 * DAO générique.
 * Hérite de GenericMethodCRUD et expose les opérations CRUD.
 * Le nom de table est déduit automatiquement depuis la classe de l'objet (camelCase → snake_case).
 * Plus besoin de passer un String table manuellement.
 */
public class GenericMethodDAO<T> extends GenericMethodCRUD {

    public GenericMethodDAO() {}

    // ── INSERT ────────────────────────────────────────────────────────────────
    /**
     * Insère un objet et retourne l'id généré (-1 si non disponible).
     */
    public int insert(T obj) {
        try {
            return this.insertData(obj);
        } catch (Exception e) {
            System.err.println("[DAO] Échec d'insertion: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    // ── SELECT ALL ────────────────────────────────────────────────────────────
    /**
     * Récupère toutes les lignes de la table correspondant à la classe de l'objet.
     */
    public List<T> findAll(T obj) {
        try {
            return this.findAllData(obj);
        } catch (Exception e) {
            System.err.println("[DAO] Échec de findAll: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // ── SELECT BY ID ──────────────────────────────────────────────────────────
    /**
     * Récupère une ligne par son id.
     */
    public T findById(T obj, int id) {
        try {
            return this.read(obj, id);
        } catch (Exception e) {
            System.err.println("[DAO] Échec de findById id=" + id + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // ── SELECT WITH FILTER ────────────────────────────────────────────────────
    /**
     * Récupère des lignes avec une clause SQL libre.
     * @param whereClause ex: "WHERE name_method = 'FIFO' ORDER BY id"
     */
    public List<T> findWithFilter(T obj, String whereClause) {
        try {
            return this.findDataWithRequest(obj, whereClause);
        } catch (Exception e) {
            System.err.println("[DAO] Échec de findWithFilter: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────
    /**
     * Met à jour une ligne par son id.
     * Seuls les champs non-null (hors id) sont mis à jour.
     */
    public void updateById(T obj, int id) {
        try {
            this.update(obj, id);
        } catch (Exception e) {
            System.err.println("[DAO] Échec de updateById id=" + id + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ── DELETE ────────────────────────────────────────────────────────────────
    /**
     * Supprime une ligne par son id.
     */
    public void deleteById(T obj, int id) {
        try {
            this.delete(obj, id);
        } catch (Exception e) {
            System.err.println("[DAO] Échec de deleteById id=" + id + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
