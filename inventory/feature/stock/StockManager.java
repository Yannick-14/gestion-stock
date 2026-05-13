package inventory.feature.stock;

import inventory.feature.repository.dao.GenericMethodCRUD;

import java.util.*;
import inventory.models.*;
// import java.util.stream.Collectors;

public class StockManager {
    private final GenericMethodCRUD crud = new GenericMethodCRUD();

    /**
     * Traite une demande de sortie de stock selon la méthode de gestion (FIFO, LIFO, CUMP).
     */
    public List<StockMovement> processExitRequest(StockMovement exitRequest) throws Exception {
        if (exitRequest.getArticle() == null) {
            throw new Exception("Article non spécifié");
        }

        Article article = exitRequest.getArticle();
        List<StockMovement> allMovements = crud.findAllData(new StockMovement());
        List<Batch> batches = resolveBatches(article, allMovements);

        // Vérifier la disponibilité
        int totalAvailable = batches.stream().mapToInt(b -> b.getQuantity()).sum();
        if (totalAvailable < exitRequest.getQuantity()) {
            throw new Exception("Stock insuffisant. Disponible: " + totalAvailable + ", Demandé: " + exitRequest.getQuantity());
        }

        // Déterminer la méthode de gestion
        String method = getStockMethod(article).toLowerCase();
        String transactionRef = exitRequest.getTransactionRef() != null ? 
                               exitRequest.getTransactionRef() : generateRef();

        return method.contains("cump") ? 
               processCUMP(batches, article, exitRequest, transactionRef) :
               processFIFOorLIFO(batches, article, exitRequest, transactionRef, method.contains("lifo"));
    }

    /**
     * Traitement CUMP : une seule ligne de sortie avec prix moyen.
     */
    private List<StockMovement> processCUMP(List<Batch> batches, Article article, 
                                            StockMovement exitRequest, String transactionRef) {
        double cump = calculateTotalValue(batches) / (double) batches.stream().mapToInt(b -> b.getQuantity()).sum();
        StockMovement movement = new StockMovement(article, exitRequest.getTypeStockMovement(), 
                                                   exitRequest.getQuantity(), cump);
        movement.setTransactionRef(transactionRef);
        return List.of(movement);
    }

    /**
     * Traitement FIFO/LIFO : plusieurs lignes de sortie selon les lots.
     */
    private List<StockMovement> processFIFOorLIFO(List<Batch> batches, Article article,
                                                   StockMovement exitRequest, String transactionRef, 
                                                   boolean isLIFO) {
        if (isLIFO) Collections.reverse(batches);

        List<StockMovement> results = new ArrayList<>();
        int remaining = exitRequest.getQuantity();

        for (Batch batch : batches) {
            if (remaining <= 0) break;

            int taken = Math.min(batch.getQuantity(), remaining);
            StockMovement movement = new StockMovement(article, exitRequest.getTypeStockMovement(), 
                                                       taken, batch.getPrice());
            movement.setTransactionRef(transactionRef);
            results.add(movement);
            remaining -= taken;
        }

        return results;
    }

    /**
     * Calcule l'état du stock (quantité et valeur totale).
     */
    public StockState calculateStockState(Article article, List<StockMovement> movements) {
        List<Batch> batches = resolveBatches(article, movements);
        int totalQty = batches.stream().mapToInt(b -> b.getQuantity()).sum();
        double totalValue = calculateTotalValue(batches);
        return new StockState(totalQty, totalValue);
    }

    /**
     * Reconstitue les lots disponibles en parcourant l'historique des mouvements.
     */
    private List<Batch> resolveBatches(Article article, List<StockMovement> allMovements) {
        List<Batch> batches = new ArrayList<>();

        allMovements.stream()
            .filter(m -> m.getArticle() != null && m.getArticle().getId() == article.getId())
            .sorted(Comparator.comparing(StockMovement::getCreatedAt))
            .forEach(movement -> {
                String type = getMovementType(movement).toLowerCase();
                
                if (type.contains("entr")) {
                    // Entrée : ajouter un nouveau lot
                    batches.add(new Batch(movement.getQuantity(), movement.getUnitPrice()));
                } else {
                    // Sortie : réduire les lots existants
                    reduceFromBatches(batches, movement.getQuantity());
                }
            });

        return batches;
    }

    /**
     * Réduit les quantités des lots lors d'une sortie.
     */
    private void reduceFromBatches(List<Batch> batches, int quantityToReduce) {
        Iterator<Batch> it = batches.iterator();
        while (it.hasNext() && quantityToReduce > 0) {
            Batch batch = it.next();
            int reduction = Math.min(batch.getQuantity(), quantityToReduce);
            batch.setQuantity(batch.getQuantity() - reduction);
            quantityToReduce -= reduction;
            if (batch.getQuantity() == 0) it.remove();
        }
    }

    /**
     * Calcule la valeur totale des lots.
     */
    private double calculateTotalValue(List<Batch> batches) {
        return batches.stream().mapToDouble(b -> b.getQuantity() * b.getPrice()).sum();
    }

    /**
     * Récupère la méthode de gestion du stock de l'article.
     */
    private String getStockMethod(Article article) {
        return article.getStockManagementMethod() != null ? 
               article.getStockManagementMethod().getNameMethod() : "CUMP";
    }

    /**
     * Récupère le type de mouvement.
     */
    private String getMovementType(StockMovement movement) {
        return movement.getTypeStockMovement() != null ? 
               movement.getTypeStockMovement().getNameType() : "";
    }

    /**
     * Génère une référence de transaction unique.
     */
    private String generateRef() {
        return "REF-" + System.currentTimeMillis();
    }
}
