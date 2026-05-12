package inventory.ui.components.form;

import inventory.ui.components.fields.FieldInput;
import inventory.ui.components.fields.FieldSelect;
import inventory.feature.repository.dao.GenericMethodCRUD;

import javax.swing.*;
import java.util.List;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Formulaire générique COMPLÈTEMENT DYNAMIQUE.
 * 
 * Plus besoin de passer les options en paramètre !
 * 
 * Fonctionne ainsi :
 *  1. Analyse les propriétés de la classe
 *  2. Détecte si un champ est un objet (ex: StockManagementMethod)
 *  3. Charge automatiquement la liste via GenericMethodCRUD
 *  4. Crée le champ approprié (FieldInput ou FieldSelect)
 * 
 * Usage :
 *   Article article = new Article();  // Instance, pas la classe !
 *   Form<Article> form = new Form<>(article);
 */
public class Form<T> extends JPanel {
    private final Class<T> modelClass;
    private final GenericMethodCRUD crud;
    private T instance;
    private final Map<String, JPanel> fields = new LinkedHashMap<>();


    public Form(T instance) {
        this.modelClass = (Class<T>) instance.getClass();
        this.crud = new GenericMethodCRUD();
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        
        buildForm();
    }

    /**
     * Constructeur avec classe (compatibilité rétroactive)
     * Utilise la réflexion pour créer une instance temporaire
     * @param modelClass La classe à analyser (ex: Article.class)
     * @deprecated Préférer new Form(new Article()) pour la clarté
     */
    @Deprecated
    public Form(Class<T> modelClass) {
        this.modelClass = modelClass;
        this.crud = new GenericMethodCRUD();
        try {
            this.instance = modelClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Impossible de créer une instance de " + modelClass.getName(), e);
        }
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        
        buildForm();
    }

    /**
     * Constructeur avec classe et options (ancien style, compatibilité)
     * @deprecated Utiliser new Form(new Article()) à la place
     */
    @Deprecated
    public Form(Class<T> modelClass, Map<String, List<?>> options) {
        this.modelClass = modelClass;
        this.crud = new GenericMethodCRUD();
        try {
            this.instance = modelClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Impossible de créer une instance de " + modelClass.getName(), e);
        }
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        
        buildForm();
        
        // Les options sont ignorées, on utilise la détection automatique
    }

    /**
     * Construit le formulaire en analysant dynamiquement les propriétés
     */
    private void buildForm() {
        Field[] declaredFields = modelClass.getDeclaredFields();
        
        for (Field field : declaredFields) {
            String fieldName = field.getName();
            String fieldLabel = formatLabel(fieldName);
            
            // Ignorer certains champs
            if (shouldIgnoreField(fieldName)) {
                continue;
            }
            
            JPanel fieldPanel = createField(field, fieldLabel);
            if (fieldPanel != null) {
                fields.put(fieldName, fieldPanel);
                add(fieldPanel);
            }
        }
    }

    /**
     * Crée le champ approprié selon le type du field
     */
    private JPanel createField(Field field, String label) {
        Class<?> fieldType = field.getType();
        
        // Est-ce que ce field est un objet (classe personnalisée) ?
        if (isCustomObject(fieldType)) {
            // Charger les données pour ce type
            List<?> items = loadDataForType(fieldType);
            
            if (!items.isEmpty()) {
                return createSelectField(label, items);
            }
        }
        
        // Sinon c'est un champ texte simple
        return createInputField(label);
    }

    /**
     * Vérifie si un type est un objet métier (pas String, int, etc.)
     */
    private boolean isCustomObject(Class<?> clazz) {
        return !clazz.isPrimitive()
            && !clazz.equals(String.class)
            && !clazz.equals(Integer.class)
            && !clazz.equals(Long.class)
            && !clazz.equals(Double.class)
            && !clazz.equals(Float.class)
            && !clazz.equals(Boolean.class)
            && !clazz.equals(java.sql.Timestamp.class)
            && !clazz.equals(java.util.Date.class);
    }

    /**
     * Charge les données pour un type donné
     * Crée une instance temporaire du type et récupère tous les enregistrements
     */
    private List<?> loadDataForType(Class<?> typeClass) {
        try {
            // Créer une instance temporaire du type
            Object tempInstance = typeClass.getDeclaredConstructor().newInstance();
            
            // Utiliser CRUD pour charger tous les enregistrements de ce type
            return crud.findAllData(tempInstance);
        } catch (Exception e) {
            System.err.println("[Form] Erreur lors du chargement des données pour " + typeClass.getName() + " : " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Crée un FieldSelect avec la liste chargée
     */
    private JPanel createSelectField(String label, List<?> items) {
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
    public T getData(T targetInstance) throws IllegalAccessException {
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
                value = fieldSelect.getSelectedValue();
            }

            // Utiliser la réflexion pour setter le champ
            if (value != null && !value.toString().isEmpty()) {
                try {
                    Field field = modelClass.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    field.set(targetInstance, value);
                } catch (NoSuchFieldException e) {
                    System.err.println("[Form] Champ non trouvé : " + fieldName);
                }
            }
        }
        
        return targetInstance;
    }

    /**
     * Vérifie si un field doit être ignoré
     */
    private boolean shouldIgnoreField(String fieldName) {
        return fieldName.equals("serialVersionUID") 
            || fieldName.equals("id") 
            || fieldName.equals("createdAt");
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

    /**
     * Permet de récupérer le composant associé à un champ par son nom.
     * Utile pour forcer des valeurs ou ajouter des listeners spécifiques.
     */
    public JPanel getFieldComponent(String fieldName) {
        return fields.get(fieldName);
    }
}
