package inventory.ui.components.form;

import inventory.ui.components.fields.FieldInput;
import inventory.ui.components.fields.FieldSelect;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Formulaire générique entièrement piloté par réflexion.
 *
 * Usage:
 *   Form<Article> form = new Form<>(Article.class, options);
 *   // options = Map<fieldName, List<?>> pour les champs objet (select)
 *
 * - Champs primitifs/String → FieldInput (texte)
 * - Champs d'un type métier ou avec une liste d'options → FieldSelect
 * - Champs id, createdAt → ignorés automatiquement
 */
public class Form<T> extends JPanel {

    private final Class<T>                  modelClass;
    private final Map<String, JComponent>   fieldComponents = new HashMap<>();

    /**
     * @param modelClass Classe du modèle
     * @param options    Map fieldName → List d'items pour les selects (peut être null)
     */
    public Form(Class<T> modelClass, Map<String, List<?>> options) {
        this.modelClass = modelClass;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        generateFields(options);
    }

    /** Constructeur sans options (uniquement des champs texte). */
    public Form(Class<T> modelClass) {
        this(modelClass, null);
    }

    // ── Génération automatique ────────────────────────────────────────────────

    private void generateFields(Map<String, List<?>> options) {
        for (Field field : modelClass.getDeclaredFields()) {
            String name = field.getName();

            // Champs systèmes ignorés
            if (name.equals("id") || name.equals("createdAt")) continue;

            String label = splitCamelCase(name);

            if (options != null && options.containsKey(name)) {
                // On a une liste d'options fournie → Select
                addSelectInput(name, label, options.get(name));
            } else if (isMetierType(field.getType())) {
                // Type métier sans options → on affiche un warning (pas de champ)
                System.out.println("[Form] Warning: aucune option pour le champ métier : " + name);
            } else {
                // Primitif ou String → Input texte
                addTextInput(name, label);
            }
        }
    }

    // ── Ajout de champs ───────────────────────────────────────────────────────

    public void addTextInput(String fieldName, String label) {
        FieldInput input = new FieldInput(label);
        input.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        input.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldComponents.put(fieldName, input);
        add(input);
        add(Box.createVerticalStrut(4));
    }

    public <S> void addSelectInput(String fieldName, String label, List<S> items) {
        FieldSelect<S> select = new FieldSelect<>(label, items);
        select.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));
        select.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldComponents.put(fieldName, select);
        add(select);
        add(Box.createVerticalStrut(4));
    }

    // ── Lecture des données ───────────────────────────────────────────────────

    /**
     * Remplit une instance T avec les valeurs saisies dans le formulaire.
     * @param instance Instance vide du modèle à peupler
     */
    public T getData(T instance) throws Exception {
        for (Map.Entry<String, JComponent> entry : fieldComponents.entrySet()) {
            Field field = modelClass.getDeclaredField(entry.getKey());
            field.setAccessible(true);
            JComponent comp = entry.getValue();
            if (comp instanceof FieldInput) {
                String val = ((FieldInput) comp).getText();
                setFieldValue(field, instance, val);
            } else if (comp instanceof FieldSelect) {
                Object val = ((FieldSelect<?>) comp).getSelectedValue();
                field.set(instance, val);
            }
        }
        return instance;
    }

    // ── Utilitaires internes ──────────────────────────────────────────────────

    private void setFieldValue(Field field, Object instance, String value) throws Exception {
        Class<?> type = field.getType();
        if (type == String.class) {
            field.set(instance, value);
        } else if (type == int.class || type == Integer.class) {
            field.set(instance, value.isEmpty() ? 0 : Integer.parseInt(value));
        } else if (type == double.class || type == Double.class) {
            field.set(instance, value.isEmpty() ? 0.0 : Double.parseDouble(value));
        } else if (type == long.class || type == Long.class) {
            field.set(instance, value.isEmpty() ? 0L : Long.parseLong(value));
        }
    }

    /** Retourne true si le type est un type métier (classe de notre domaine). */
    private boolean isMetierType(Class<?> type) {
        return !type.isPrimitive()
            && !type.getName().startsWith("java.lang")
            && !type.getName().startsWith("java.sql")
            && !type.getName().startsWith("java.util");
    }

    private String splitCamelCase(String s) {
        String result = s.replaceAll(
            String.format("%s|%s|%s",
                "(?<=[A-Z])(?=[A-Z][a-z])",
                "(?<=[^A-Z])(?=[A-Z])",
                "(?<=[A-Za-z])(?=[^A-Za-z])"),
            " "
        );
        return result.substring(0, 1).toUpperCase() + result.substring(1).toLowerCase();
    }

    public Map<String, JComponent> getFieldComponents() {
        return fieldComponents;
    }
}
