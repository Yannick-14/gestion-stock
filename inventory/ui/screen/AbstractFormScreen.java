package inventory.ui.screen;

import inventory.ui.components.button.Button;
import inventory.ui.components.form.Form;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Classe de base abstraite pour tous les écrans contenant un formulaire.
 * Elle gère le design, le titre, le scroll et la liste d'actions (boutons).
 *
 * Pour créer un nouvel écran, il suffit d'hériter de cette classe :
 * public class MyScreen extends AbstractFormScreen<MyModel> { ... }
 */
public abstract class AbstractFormScreen<T> extends JPanel {

    /**
     * Représente une action (bouton) dans le formulaire.
     */
    public record FormAction(String label, Button.Style style, ActionListener listener) {}

    protected Form<T> form;
    protected JPanel actionPanel;
    protected String title;
    protected java.util.List<FormAction> actions;

    public AbstractFormScreen(String title, T modelInstance) {
        this.title = title;
        this.form = new Form<>(modelInstance);
        initializeUI();
    }

    /**
     * Permet d'initialiser ou de remplacer les actions du formulaire.
     * Cette méthode peut être appelée en toute sécurité depuis le constructeur de la classe fille.
     */
    protected void setActions(java.util.List<FormAction> actionList) {
        actionPanel.removeAll();
        for (FormAction action : actionList) {
            addAction(action.label(), action.style(), action.listener());
        }
        actionPanel.revalidate();
        actionPanel.repaint();
    }

    /**
     * Construit l'interface graphique commune.
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 252));

        // 1. Titre de la page
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(16, 20, 12, 20));
        add(lblTitle, BorderLayout.NORTH);

        // 2. Zone de contenu (Formulaire + Boutons)
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(16, 30, 20, 30));
        content.setOpaque(false);

        // Ajout du formulaire automatique
        content.add(form);
        content.add(Box.createVerticalStrut(20));

        // Zone des boutons d'action (CENTRÉS par défaut)
        actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        actionPanel.setOpaque(false);
        content.add(actionPanel);

        // Rendre le tout scrollable si nécessaire
        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);
        
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Permet d'ajouter dynamiquement un bouton d'action.
     */
    protected void addAction(String text, Button.Style style, ActionListener listener) {
        Button btn = new Button(text, style);
        btn.addActionListener(listener);
        actionPanel.add(btn);
        actionPanel.add(Box.createHorizontalStrut(12)); // Espace entre les boutons
    }

    /**
     * Version simplifiée d'addAction avec style PRIMARY par défaut.
     */
    protected void addAction(String text, ActionListener listener) {
        addAction(text, Button.Style.PRIMARY, listener);
    }

    public Form<T> getForm() {
        return form;
    }
}
