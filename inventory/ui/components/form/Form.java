package inventory.ui.components.form;

import inventory.ui.components.fields.FieldInput;
import inventory.ui.components.fields.FieldSelect;

import javax.swing.*;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;

/**
 * Formulaire générique qui génère des champs automatiquement
 * depuis les propriétés d'une classe.
 * 
 * Supporte :
 *  - String → FieldInput
 *  - List<T> dans options → FieldSelect<T>
 */
public class Form<T> extends JPanel {

    private final Class<T> modelClass;
    private final Map<String, List<?>> options;
    private final Map<String, JPanel> fields = new HashMap<>();

    public Form(Class<T> modelClass, Map<String, List<?>> options) {
        this.modelClass = modelClass;
        this.options = options != null ? options : new HashMap<>();
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        
        buildForm();
    }

    /**
     * Construit le formulaire en analysant les propriétés de la classe
     */
    private void buildForm() {
        Field[] declaredFields = modelClass.getDeclaredFields();
        
        for (Field field : declaredFields) {
            String fieldName = field.getName();
            String fieldLabel = formatLabel(fieldName);
            
            // Ignorer certains champs
            if (fieldName.equals("serialVersionUID") || 
                fieldName.equals("id") || 
                fieldName.equals("createdAt")) {
                continue;
            }
            
            JPanel fieldPanel = createField(fieldName, fieldLabel);
            if (fieldPanel != null) {
                fields.put(fieldName, fieldPanel);
                add(fieldPanel);
            }
        }
    }

    /**
     * Crée le champ approprié selon le type et les options disponibles
     */
    private JPanel createField(String fieldName, String label) {
        // Vérifier si ce champ a des options (une liste)
        if (options.containsKey(fieldName)) {
            List<?> items = options.get(fieldName);
            return createSelectField(fieldName, label, items);
        }
        
        // Sinon créer un champ texte
        return createInputField(label);
    }

    /**
     * Crée un FieldSelect pour un objet avec liste
     */
    @SuppressWarnings("unchecked")
    private JPanel createSelectField(String fieldName, String label, List<?> items) {
        return new FieldSelect<>(label, items);
    }

    /**
     * Crée un FieldInput pour un champ texte
     */
    private JPanel createInputField(String label) {
        return new FieldInput(label);
    }

    /**
     * Remplit l'objet avec les données du formulaire
     */
    public T getData(T instance) throws IllegalAccessException {
        for (Map.Entry<String, JPanel> entry : fields.entrySet()) {
            String fieldName = entry.getKey();
            JPanel fieldPanel = entry.getValue();
            Object value = null;

            // Récupérer la valeur selon le type de champ
            if (fieldPanel instanceof FieldInput) {
                FieldInput fieldInput = (FieldInput) fieldPanel;
                value = fieldInput.getText();
            } else if (fieldPanel instanceof FieldSelect) {
                FieldSelect<?> fieldSelect = (FieldSelect<?>) fieldPanel;
                value = fieldSelect.getSelectedValue(); // ← Retourne l'objet complet !
            }

            // Utiliser la réflexion pour setter le champ
            if (value != null) {
                try {
                    Field field = modelClass.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    field.set(instance, value);
                } catch (NoSuchFieldException e) {
                    System.err.println("[Form] Champ non trouvé : " + fieldName);
                }
            }
        }
        
        return instance;
    }

    /**
     * Formate le nom d'un champ (camelCase → Label)
     * Ex: "nameArticle" → "Name article"
     */
    private String formatLabel(String fieldName) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < fieldName.length(); i++) {
            char c = fieldName.charAt(i);
            if (Character.isUpperCase(c) && i > 0) {
                result.append(" ");
            }
            result.append(i == 0 ? Character.toUpperCase(c) : c);
        }
        return result.toString();
    }
}
