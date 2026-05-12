package inventory.models;

import java.sql.Timestamp;

/**
 * Article (produit en stock).
 * Table SQL: article
 * FK: id_stock_management_method → stock_management_method(id)
 */
public class Article {

    private int id;
    private String nameArticle;
    private StockManagementMethod stockManagementMethod;
    private Timestamp createdAt;

    public Article() {}

    public Article(String nameArticle, StockManagementMethod stockManagementMethod) {
        this.nameArticle           = nameArticle;
        this.stockManagementMethod = stockManagementMethod;
        this.createdAt             = new Timestamp(System.currentTimeMillis());
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNameArticle() { return nameArticle; }
    public void setNameArticle(String nameArticle) { this.nameArticle = nameArticle; }

    public StockManagementMethod getStockManagementMethod() { return stockManagementMethod; }
    public void setStockManagementMethod(StockManagementMethod stockManagementMethod) {
        this.stockManagementMethod = stockManagementMethod;
    }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Article{id=" + id + ", nameArticle='" + nameArticle
             + "', method=" + (stockManagementMethod != null ? stockManagementMethod.getNameMethod() : "null")
             + ", createdAt=" + createdAt + "}";
    }
}
