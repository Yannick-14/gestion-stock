package inventory.models;

import java.sql.Timestamp;
import inventory.feature.annotation.FormIgnore;

/**
 * Mouvement de stock (entrée/sortie d'un article).
 * Table SQL: stock_movement
 * FK: id_article → article(id)
 * FK: id_type_stock_movement → type_stock_movement(id)
 */
public class StockMovement {

    @FormIgnore
    private int id;
    private Article article;
    private TypeStockMovement typeStockMovement;
    @FormIgnore
    private Timestamp createdAt;
    private int quantity;
    private double unitPrice;
    @FormIgnore
    private String transactionRef;

    public StockMovement() {}

    public StockMovement(Article article, TypeStockMovement typeStockMovement,
                         int quantity, double unitPrice) {
        this.article = article;
        this.typeStockMovement = typeStockMovement;
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.quantity = quantity;
        this.unitPrice = unitPrice;
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

    public String getTransactionRef() { return transactionRef; }
    public void setTransactionRef(String transactionRef) { this.transactionRef = transactionRef; }

    public boolean isMovementIn() {
        return typeStockMovement != null && 
               typeStockMovement.getNameType() != null && 
               typeStockMovement.getNameType().toLowerCase().contains("entr");
    }

    @Override
    public String toString() {
        return "StockMovement{id=" + id
             + ", article=" + (article != null ? article.getNameArticle() : "null")
             + ", type=" + (typeStockMovement != null ? typeStockMovement.getNameType() : "null")
             + ", quantity=" + quantity + ", unitPrice=" + unitPrice
             + ", createdAt=" + createdAt + "}";
    }
}
