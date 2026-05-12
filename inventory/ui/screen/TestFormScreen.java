package inventory.ui.screen;

import inventory.models.Article;
import inventory.models.StockManagementMethod;
import inventory.ui.components.button.Button;
import inventory.ui.components.form.Form;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Écran de test pour valider la généralisation des composants UI.
 */
public class TestFormScreen extends JPanel {

    public TestFormScreen() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 242, 245));

        // Titre
        JLabel title = new JLabel("Test de Formulaire Générique (Article)");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(title, BorderLayout.NORTH);

        // Formulaire
        Form<Article> articleForm = new Form<>(Article.class);

        // Simulation d'options pour le champ StockManagementMethod
        List<StockManagementMethod> methods = new ArrayList<>();
        methods.add(new StockManagementMethod("FIFO"));
        methods.add(new StockManagementMethod("LIFO"));
        methods.add(new StockManagementMethod("CUMP"));

        Map<String, List<?>> options = new HashMap<>();
        options.put("stockManagementMethod", methods);

        articleForm.generateFields(options);
        articleForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JScrollPane scrollPane = new JScrollPane(articleForm);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        // Actions
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);
        actions.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        Button btnSave = new Button("Enregistrer", e -> {
            try {
                Article article = articleForm.getData(new Article());
                JOptionPane.showMessageDialog(this, "Données récupérées :\n" + article);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        Button btnCancel = new Button("Annuler");
        btnCancel.setSecondary();

        actions.add(btnCancel);
        actions.add(btnSave);
        add(actions, BorderLayout.SOUTH);
    }
}
