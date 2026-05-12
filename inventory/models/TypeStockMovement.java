package inventory.models;

import inventory.feature.annotation.FormIgnore;

/**
 * Type de mouvement de stock (Entrée, Sortie).
 * Table SQL: type_stock_movement
 */
public class TypeStockMovement {

    @FormIgnore
    private int id;
    private String nameType;

    public TypeStockMovement() {}

    public TypeStockMovement(String nameType) {
        this.nameType = nameType;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNameType() { return nameType; }
    public void setNameType(String nameType) { this.nameType = nameType; }

    @Override
    public String toString() {
        return nameType;
    }
}
