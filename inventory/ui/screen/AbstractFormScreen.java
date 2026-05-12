package inventory.ui.screen;

import inventory.ui.components.button.Button;
import inventory.ui.components.form.Form;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe de base abstraite pour tous les écrans contenant un formulaire.
 * Elle gère le design, le titre, le scroll et la liste d'actions (boutons).
 * 
 * Inclut par défaut les boutons "Réinitialiser" et "Enregistrer".
 * Pour ajouter des actions supplémentaires, utiliser setActions().
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
    protected List<FormAction> additionalActions;
    protected T modelInstance; // Garder une référence à l'instance du modèle

    /**
     * Constructeur principal.
     * Initialise l'interface avec les boutons par défaut (Réinitialiser, Enregistrer).
     */
    public AbstractFormScreen(String title, T modelInstance) {
        this.title = title;
        this.modelInstance = modelInstance;
        this.form = new Form<>(modelInstance);
        this.additionalActions = new ArrayList<>();
        initializeUI();
        addDefaultActions();
    }

    /**
     * Méthode abstraite à implémenter pour définir la logique d'enregistrement.
     * Chaque écran doit fournir sa propre implémentation de save.
     */
    protected abstract void saveArticle();

    /**
     * Réinitialise le formulaire avec les valeurs par défaut.
     * Peut être surchargée pour personnaliser le comportement de reset.
     */
    protected void resetForm() {
        try {
            // Supprimer l'ancien formulaire
            Container parent = form.getParent();
            if (parent != null) {
                int index = -1;
                for (int i = 0; i < parent.getComponentCount(); i++) {
                    if (parent.getComponent(i) == form) {
                        index = i;
                        break;
                    }
                }
                
                if (index != -1) {
                    parent.remove(form);
                    
                    // Créer une nouvelle instance propre du modèle
                    T newInstance = createNewModelInstance();
                    
                    // Créer un nouveau formulaire avec cette instance
                    this.form = new Form<>(newInstance);

                    // Ajouter le nouveau formulaire à la même position
                    parent.add(form, index);
                    parent.revalidate();
                    parent.repaint();
                }
            }
            
            JOptionPane.showMessageDialog(this,
                "Formulaire réinitialisé avec succès.",
                "Réinitialisation", JOptionPane.INFORMATION_MESSAGE);
                
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la réinitialisation : " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Crée une nouvelle instance du modèle en utilisant la réflexion.
     * Cette méthode peut être surchargée pour personnaliser la création.
     */
    @SuppressWarnings("unchecked")
    protected T createNewModelInstance() throws Exception {
        return (T) modelInstance.getClass().getDeclaredConstructor().newInstance();
    }

    /**
     * Ajoute les boutons d'action par défaut : Réinitialiser et Enregistrer.
     */
    private void addDefaultActions() {
        // Bouton Réinitialiser (style SECONDARY)
        addAction("Réinitialiser", Button.Style.SECONDARY, e -> resetForm());
        
        // Espacement entre les boutons
        actionPanel.add(Box.createHorizontalStrut(8));
        
        // Bouton Enregistrer (style PRIMARY)
        addAction("Enregistrer", Button.Style.PRIMARY, e -> saveArticle());
    }

    /**
     * Permet d'ajouter des actions supplémentaires en plus des boutons par défaut.
     * Les boutons par défaut (Réinitialiser, Enregistrer) sont conservés.
     */
    protected void setActions(List<FormAction> actionList) {
        // Sauvegarder les actions supplémentaires
        this.additionalActions = new ArrayList<>(actionList);
        
        // Reconstruire le panel d'actions
        rebuildActionPanel();
    }

    /**
     * Reconstruit le panel d'actions avec les boutons par défaut ET supplémentaires.
     */
    private void rebuildActionPanel() {
        actionPanel.removeAll();
        
        // Ajouter les actions supplémentaires (si elles existent)
        if (!additionalActions.isEmpty()) {
            for (FormAction action : additionalActions) {
                addAction(action.label(), action.style(), action.listener());
                actionPanel.add(Box.createHorizontalStrut(8));
            }
        }
        
        // Réinitialiser les boutons par défaut
        addDefaultActions();
        
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
