package inventory.models;

public class StockState {
    public final int quantity;
    public final double totalValue;

    public StockState(int quantity, double totalValue) {
        this.quantity = quantity;
        this.totalValue = totalValue;
    }

    @Override
    public String toString() {
        return "StockState{qty=" + quantity + ", value=" + String.format("%.2f", totalValue) + "}";
    }
}
