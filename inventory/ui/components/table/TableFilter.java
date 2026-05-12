package inventory.ui.components.table;

import java.util.List;

/**
 * Configuration d'un filtre pour PanelList.
 * On passe une liste de TableFilter au PanelList pour définir les filtres disponibles.
 */
public class TableFilter {

    public enum FilterType { TEXT, SELECT }

    private final String fieldName;   // Nom de l'attribut Java dans l'objet
    private final String label;       // Label affiché dans l'UI
    private final FilterType type;    // Type de champ filtre
    private final List<?> options;    // Options pour SELECT (peut être null si TEXT)

    /** Filtre texte simple */
    public TableFilter(String fieldName, String label) {
        this(fieldName, label, FilterType.TEXT, null);
    }

    /** Filtre select avec liste d'options */
    public TableFilter(String fieldName, String label, List<?> options) {
        this(fieldName, label, FilterType.SELECT, options);
    }

    private TableFilter(String fieldName, String label, FilterType type, List<?> options) {
        this.fieldName = fieldName;
        this.label     = label;
        this.type      = type;
        this.options   = options;
    }

    public String getFieldName() { return fieldName; }
    public String getLabel()     { return label; }
    public FilterType getType()  { return type; }
    public List<?> getOptions()  { return options; }
}
