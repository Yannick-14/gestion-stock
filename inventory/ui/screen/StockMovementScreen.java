package inventory.ui.screen;

import inventory.feature.repository.dao.GenericMethodCRUD;
import inventory.models.Article;
import inventory.models.StockMovement;
import inventory.models.TypeStockMovement;
import inventory.ui.components.form.Form;
import inventory.ui.components.button.Button;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Écran de mouvement de stock.
 * Le formulaire est généré depuis la classe StockMovement.
 *
 * Particularité :
 * - typeStockMovement est forcé à "Entrée" (id=1) — on ne passe qu'un seul élément
 *   dans la liste d'options, donc le select n'affiche que ce type.
 * - article est sélectionnable parmi les articles en base.
 */
public class StockMovementScreen extends JPanel {

    private final GenericMethodCRUD crud = new GenericMethodCRUD();
    private Form<StockMovement>     form;

    public StockMovementScreen() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 252));

        // Titre
        JLabel lblTitle = new JLabel("Entrée en stock");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(16, 20, 12, 20));
        add(lblTitle, BorderLayout.NORTH);

        // Chargement articles
        List<Article> articles = loadArticles();

        // typeStockMovement forcé à "Entrée"
        TypeStockMovement entree = buildEntreeType();

        // Options
        Map<String, List<?>> options = new HashMap<>();
        options.put("article",           articles);
        options.put("typeStockMovement", Arrays.asList(entree)); // forcé Entrée

        // Formulaire
        form = new Form<>(StockMovement.class, options);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(16, 30, 16, 30));
        content.setOpaque(false);
        content.add(form);
        content.add(Box.createVerticalStrut(16));

        // Bouton
        Button btnSave = new Button("Enregistrer le mouvement");
        btnSave.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnSave.addActionListener(e -> saveMovement());
        content.add(btnSave);

        add(new JScrollPane(content), BorderLayout.CENTER);
    }

    private void saveMovement() {
        try {
            StockMovement movement = new StockMovement();
            movement = form.getData(movement);
            movement.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
            int id = crud.insertData(movement);
            JOptionPane.showMessageDialog(this,
                "Mouvement de stock enregistré ! (id=" + id + ")",
                "Succès", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de l'enregistrement : " + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private List<Article> loadArticles() {
        try {
            return crud.findAllData(new Article());
        } catch (Exception e) {
            System.err.println("[StockMovementScreen] Impossible de charger les articles : " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Construit le TypeStockMovement "Entrée" :
     * On essaie de le charger depuis la BDD (id=1), sinon on le crée en mémoire.
     */
    private TypeStockMovement buildEntreeType() {
        try {
            List<TypeStockMovement> types = crud.findAllData(new TypeStockMovement());
            for (TypeStockMovement t : types) {
                if (t.getNameType() != null && t.getNameType().toLowerCase().contains("entr")) {
                    return t;
                }
            }
        } catch (Exception e) {
            System.err.println("[StockMovementScreen] Impossible de charger les types : " + e.getMessage());
        }
        // Fallback
        TypeStockMovement entree = new TypeStockMovement("Entrée");
        entree.setId(1);
        return entree;
    }
}
