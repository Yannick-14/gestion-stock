package inventory.feature.repository.dao;

import java.util.List;

public class GenericMethodDAO<T> extends GenericMethodCRUD {

    public GenericMethodDAO() {}

    public T insert(T obj) {
        try {
            return this.insertData(obj);
        } catch (Exception e) {
            System.err.println("[DAO] Échec d'insertion: " + e.getMessage());
            e.printStackTrace();
            return obj;
        }
    }

    public List<T> findAll(T obj) {
        try {
            return this.findAllData(obj);
        } catch (Exception e) {
            System.err.println("[DAO] Échec de findAll: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public T findById(T obj, int id) {
        try {
            return this.read(obj, id);
        } catch (Exception e) {
            System.err.println("[DAO] Échec de findById id=" + id + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // @param whereClause ex: "WHERE name_method = 'FIFO' ORDER BY id"
    public List<T> findWithFilter(T obj, String whereClause) {
        try {
            return this.findDataWithRequest(obj, whereClause);
        } catch (Exception e) {
            System.err.println("[DAO] Échec de findWithFilter: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public void updateById(T obj, int id) {
        try {
            this.update(obj, id);
        } catch (Exception e) {
            System.err.println("[DAO] Échec de updateById id=" + id + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void deleteById(T obj, int id) {
        try {
            this.delete(obj, id);
        } catch (Exception e) {
            System.err.println("[DAO] Échec de deleteById id=" + id + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
