package inventory.ui.screen;

import inventory.ui.components.button.Button;
import inventory.ui.components.form.Form;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Écran formulaire générique.
 * On passe un objet modèle et ses options (pour les selects),
 * ainsi qu'un callback appelé avec l'instance remplie lors de la validation.
 *
 * Usage:
 *   PanelForm<Article> screen = new PanelForm<>(
 *       "Nouvel article", Article.class, options,
 *       article -> crud.insertData(article)
 *   );
 */
public class PanelForm<T> extends JPanel {

    private final Form<T> form;

    public PanelForm(String title, Class<T> modelClass,
                     Map<String, List<?>> options,
                     Consumer<T> onSubmit) {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 252));

        // Titre
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(16, 20, 8, 20));
        add(lblTitle, BorderLayout.NORTH);

        // Formulaire centré
        form = new Form<>(modelClass, options);
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
        center.setOpaque(false);
        center.add(form);
        center.add(Box.createVerticalStrut(16));

        // Bouton soumettre
        Button btnSubmit = new Button("Enregistrer");
        btnSubmit.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnSubmit.addActionListener(e -> {
            try {
                T instance = modelClass.getDeclaredConstructor().newInstance();
                T filled   = form.getData(instance);
                onSubmit.accept(filled);
                JOptionPane.showMessageDialog(this, "Enregistrement réussi !", "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage(), "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
        center.add(btnSubmit);

        add(new JScrollPane(center), BorderLayout.CENTER);
    }

    /** Constructeur sans options. */
    public PanelForm(String title, Class<T> modelClass, Consumer<T> onSubmit) {
        this(title, modelClass, null, onSubmit);
    }

    public Form<T> getForm() { return form; }
}
