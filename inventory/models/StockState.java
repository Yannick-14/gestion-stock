package inventory.models;

public class StockState {
    public int quantity;
    public double totalValue;

    public void setQuantity(int quantity) { this.quantity = quantity; }
    public int getQuantity() { return quantity; }

    public void setTotalValue(double total) { this.totalValue = total; }
    public double getTotalValue() { return totalValue; }

    public StockState() {}
    public StockState(int quantity, double totalValue) {
        this.setQuantity(quantity);
        this.setTotalValue(totalValue);
    }

    @Override
    public String toString() {
        return "StockState{qty=" + this.getQuantity() + ", value=" + String.format("%.2f", this.getTotalValue()) + "}";
    }
}
