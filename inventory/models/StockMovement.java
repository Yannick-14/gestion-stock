package inventory.models;

import java.sql.Timestamp;

/**
 * Mouvement de stock (entrée/sortie d'un article).
 * Table SQL: stock_movement
 * FK: id_article → article(id)
 * FK: id_type_stock_movement → type_stock_movement(id)
 */
public class StockMovement {

    private int id;
    private Article article;
    private TypeStockMovement typeStockMovement;
    private Timestamp createdAt;
    private int quantity;
    private double unitPrice;

    public StockMovement() {}

    public StockMovement(Article article, TypeStockMovement typeStockMovement,
                         int quantity, double unitPrice) {
        this.article            = article;
        this.typeStockMovement  = typeStockMovement;
        this.createdAt          = new Timestamp(System.currentTimeMillis());
        this.quantity           = quantity;
        this.unitPrice          = unitPrice;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Article getArticle() { return article; }
    public void setArticle(Article article) { this.article = article; }

    public TypeStockMovement getTypeStockMovement() { return typeStockMovement; }
    public void setTypeStockMovement(TypeStockMovement typeStockMovement) {
        this.typeStockMovement = typeStockMovement;
    }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    @Override
    public String toString() {
        return "StockMovement{id=" + id
             + ", article=" + (article != null ? article.getNameArticle() : "null")
             + ", type=" + (typeStockMovement != null ? typeStockMovement.getNameType() : "null")
             + ", quantity=" + quantity + ", unitPrice=" + unitPrice
             + ", createdAt=" + createdAt + "}";
    }
}
