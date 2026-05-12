package inventory.models;

/**
 * Méthode de gestion du stock (CUMP, LIFO, FIFO…).
 * Table SQL: stock_management_method
 */
public class StockManagementMethod {

    private int id;
    private String nameMethod;

    public StockManagementMethod() {}

    public StockManagementMethod(String nameMethod) {
        this.nameMethod = nameMethod;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNameMethod() { return nameMethod; }
    public void setNameMethod(String nameMethod) { this.nameMethod = nameMethod; }

    @Override
    public String toString() {
        return nameMethod;
    }
}
