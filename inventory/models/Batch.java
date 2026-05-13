package inventory.models;

public class Batch {
    private int id;
    private int quantity;
    private double price;
    
    // Constructeur par défaut
    public Batch() {
    }
    
    // Constructeur avec paramètres
    public Batch(int quantity, double price) {
        this.quantity = quantity;
        this.price = price;
    }
    
    // Constructeur complet
    public Batch(int id, int quantity, double price) {
        this.id = id;
        this.quantity = quantity;
        this.price = price;
    }
    
    // Getters et Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    // Méthode toString() pour faciliter l'affichage
    @Override
    public String toString() {
        return "Batch{" +
                "id=" + id +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }
    
    // Méthode equals() et hashCode() (optionnel mais recommandé)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Batch batch = (Batch) o;
        return id == batch.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
