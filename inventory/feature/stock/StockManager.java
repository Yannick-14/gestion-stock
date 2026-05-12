package inventory.feature.stock;

import inventory.feature.repository.dao.GenericMethodCRUD;
import inventory.models.Article;
import inventory.models.StockMovement;
import java.util.*;

/**
 * Service pour la gestion des stocks (FIFO, LIFO, CUMP).
 */
public class StockManager {
    private final GenericMethodCRUD crud = new GenericMethodCRUD();

    /**
     * Traite une sortie de stock en la découpant si nécessaire (FIFO/LIFO)
     * @return Liste des mouvements à insérer
     */
    public List<StockMovement> prepareExit(StockMovement exitRequest) throws Exception {
        Article article = exitRequest.getArticle();
        if (article == null) throw new Exception("Article non spécifié");

        String method = article.getStockManagementMethod() != null ? 
                        article.getStockManagementMethod().getNameMethod().toLowerCase() : "cump";

        List<StockMovement> allMovements = crud.findAllData(new StockMovement());
        List<Batch> batches = resolveBatches(article, allMovements);

        int totalAvailable = batches.stream().mapToInt(b -> b.quantity).sum();
        if (totalAvailable < exitRequest.getQuantity()) {
            throw new Exception("Stock insuffisant (disponible: " + totalAvailable + ")");
        }

        List<StockMovement> results = new ArrayList<>();
        int remaining = exitRequest.getQuantity();
        String ref = exitRequest.getTransactionRef();
        if (ref == null) ref = "REF-" + System.currentTimeMillis();

        if (method.contains("cump")) {
            double totalValue = batches.stream().mapToDouble(b -> b.quantity * b.price).sum();
            double cump = totalQtyToValue(batches) / (double) totalAvailable;
            
            StockMovement m = new StockMovement(article, exitRequest.getTypeStockMovement(), exitRequest.getQuantity(), cump);
            m.setTransactionRef(ref);
            results.add(m);
        } else {
            if (method.contains("lifo")) {
                Collections.reverse(batches);
            }

            for (Batch batch : batches) {
                if (remaining <= 0) break;
                
                int taken = Math.min(batch.quantity, remaining);
                StockMovement m = new StockMovement(article, exitRequest.getTypeStockMovement(), taken, batch.price);
                m.setTransactionRef(ref);
                results.add(m);
                
                remaining -= taken;
            }
        }

        return results;
    }

    private double totalQtyToValue(List<Batch> batches) {
        return batches.stream().mapToDouble(b -> b.quantity * b.price).sum();
    }

    /**
     * Calcule l'état actuel du stock (Qté et Valeur) pour un article.
     */
    public StockState calculateStockState(Article article, List<StockMovement> allMvmts) {
        List<Batch> batches = resolveBatches(article, allMvmts);
        int totalQty = batches.stream().mapToInt(b -> b.quantity).sum();
        double totalValue = totalQtyToValue(batches);
        
        return new StockState(totalQty, totalValue);
    }

    public static class StockState {
        public final int quantity;
        public final double totalValue;
        public StockState(int q, double v) { this.quantity = q; this.totalValue = v; }
    }

    private List<Batch> resolveBatches(Article article, List<StockMovement> allMvmts) {
        List<Batch> batches = new ArrayList<>();

        List<StockMovement> articleMvmts = allMvmts.stream()
            .filter(m -> m.getArticle() != null && m.getArticle().getId() == article.getId())
            .sorted(Comparator.comparing(StockMovement::getCreatedAt))
            .toList();

        for (StockMovement m : articleMvmts) {
            String type = m.getTypeStockMovement() != null ? 
                          m.getTypeStockMovement().getNameType().toLowerCase() : "";
            
            if (type.contains("entr")) {
                batches.add(new Batch(m.getQuantity(), m.getUnitPrice()));
            } else {
                int toReduce = m.getQuantity();
                Iterator<Batch> it = batches.iterator();
                while (it.hasNext() && toReduce > 0) {
                    Batch b = it.next();
                    int reduce = Math.min(b.quantity, toReduce);
                    b.quantity -= reduce;
                    toReduce -= reduce;
                    if (b.quantity == 0) it.remove();
                }
            }
        }
        return batches;
    }

    private static class Batch {
        int quantity;
        double price;
        Batch(int q, double p) { this.quantity = q; this.price = p; }
    }
}
