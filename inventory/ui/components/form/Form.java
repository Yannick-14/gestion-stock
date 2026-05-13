package inventory.ui.components.form;

import inventory.ui.components.fields.FieldInput;
import inventory.ui.components.fields.FieldSelect;
import inventory.ui.components.fields.FieldDate;
import inventory.feature.repository.dao.GenericMethodCRUD;

import javax.swing.*;
import java.util.List;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;
import inventory.feature.annotation.FormIgnore;

/**
 * Formulaire générique COMPLÈTEMENT DYNAMIQUE.
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
    }

    private void buildForm() {
        Field[] declaredFields = modelClass.getDeclaredFields();
        
        for (Field field : declaredFields) {
            String fieldName = field.getName();
            String fieldLabel = formatLabel(fieldName);
            
            if (shouldIgnoreField(field)) {
                continue;
            }
            
            JPanel fieldPanel = createField(field, fieldLabel);
            if (fieldPanel != null) {
                fields.put(fieldName, fieldPanel);
                add(fieldPanel);
            }
        }
    }

    private JPanel createField(Field field, String label) {
        Class<?> fieldType = field.getType();
        
        if (isCustomObject(fieldType)) {
            List<?> items = loadDataForType(fieldType);
            
            if (!items.isEmpty()) {
                return createSelectField(label, items);
            }
        }
        
        if (isDateType(fieldType)) {
            return new FieldDate(label);
        }
        
        return createInputField(label);
    }

    private boolean isDateType(Class<?> type) {
        return type.equals(java.util.Date.class) 
            || type.equals(java.sql.Date.class) 
            || type.equals(java.sql.Timestamp.class);
    }

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

    private List<?> loadDataForType(Class<?> typeClass) {
        try {
            Object tempInstance = typeClass.getDeclaredConstructor().newInstance();
            return crud.findAllData(tempInstance);
        } catch (Exception e) {
            System.err.println("[Form] Erreur lors du chargement des données pour " + typeClass.getName() + " : " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private JPanel createSelectField(String label, List<?> items) {
        return new FieldSelect<>(label, items);
    }

    private JPanel createInputField(String label) {
        return new FieldInput(label);
    }

    /**
     * Remplit l'objet avec les données du formulaire
     * AVEC CONVERSION DE TYPES
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
            } else if (fieldPanel instanceof FieldDate) {
                FieldDate fieldDate = (FieldDate) fieldPanel;
                value = fieldDate.getText();
            }

            // Utiliser la réflexion pour setter le champ avec conversion de type
            if (value != null && !value.toString().isEmpty()) {
                try {
                    Field field = modelClass.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    
                    // Convertir la valeur selon le type du champ
                    Object convertedValue = convertValue(value, field.getType());
                    field.set(targetInstance, convertedValue);
                    
                } catch (NoSuchFieldException e) {
                    System.err.println("[Form] Champ non trouvé : " + fieldName);
                } catch (IllegalArgumentException e) {
                    System.err.println("[Form] Erreur de conversion pour le champ " + fieldName + " : " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        
        return targetInstance;
    }

    /**
     * Convertit une valeur String en type approprié
     */
    private Object convertValue(Object value, Class<?> targetType) {
        if (value == null) return null;
        
        // Si la valeur est déjà du bon type, la retourner telle quelle
        if (targetType.isInstance(value)) {
            return value;
        }
        
        String stringValue = value.toString().trim();
        if (stringValue.isEmpty()) return null;
        
        try {
            // Types primitifs et leurs wrappers
            if (targetType == int.class || targetType == Integer.class) {
                return Integer.parseInt(stringValue);
            } else if (targetType == long.class || targetType == Long.class) {
                return Long.parseLong(stringValue);
            } else if (targetType == double.class || targetType == Double.class) {
                return Double.parseDouble(stringValue);
            } else if (targetType == float.class || targetType == Float.class) {
                return Float.parseFloat(stringValue);
            } else if (targetType == boolean.class || targetType == Boolean.class) {
                return Boolean.parseBoolean(stringValue);
            } else if (targetType == short.class || targetType == Short.class) {
                return Short.parseShort(stringValue);
            } else if (targetType == byte.class || targetType == Byte.class) {
                return Byte.parseByte(stringValue);
            } else if (targetType == String.class) {
                return stringValue;
            } else if (targetType == java.sql.Timestamp.class) {
                return java.sql.Timestamp.valueOf(stringValue);
            } else if (targetType == java.util.Date.class) {
                return java.sql.Date.valueOf(stringValue);
            }
        } catch (NumberFormatException e) {
            System.err.println("[Form] Erreur de conversion de '" + stringValue + "' vers " + targetType.getSimpleName());
        }
        
        // Pour les autres types, retourner la valeur telle quelle
        return value;
    }

    private boolean shouldIgnoreField(Field field) {
        return field.getName().equals("serialVersionUID") 
            || field.isAnnotationPresent(FormIgnore.class);
    }

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

    public void setFieldVisible(String fieldName, boolean visible) {
        JPanel panel = fields.get(fieldName);
        if (panel != null) {
            panel.setVisible(visible);
            revalidate();
            repaint();
        }
    }

    public void addFieldListener(String fieldName, Consumer<Object> listener) {
        JPanel panel = fields.get(fieldName);
        if (panel == null) return;

        if (panel instanceof FieldSelect) {
            FieldSelect<?> select = (FieldSelect<?>) panel;
            select.getComboBox().addActionListener(e -> listener.accept(select.getSelectedValue()));
        } else if (panel instanceof FieldInput) {
            FieldInput input = (FieldInput) panel;
            input.getTextField().getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                private void update() { listener.accept(input.getText()); }
                public void insertUpdate(javax.swing.event.DocumentEvent e) { update(); }
                public void removeUpdate(javax.swing.event.DocumentEvent e) { update(); }
                public void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
            });
        } else if (panel instanceof FieldDate) {
            FieldDate fieldDate = (FieldDate) panel;
            fieldDate.getDatePicker().getTextField().getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                private void update() { listener.accept(fieldDate.getText()); }
                public void insertUpdate(javax.swing.event.DocumentEvent e) { update(); }
                public void removeUpdate(javax.swing.event.DocumentEvent e) { update(); }
                public void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
            });
        }
    }

    public JPanel getFieldComponent(String fieldName) {
        return fields.get(fieldName);
    }
}
