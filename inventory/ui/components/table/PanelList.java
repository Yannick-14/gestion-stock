package inventory.ui.components.table;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Composant tableau générique.
 * On passe un objet T pour en déduire les colonnes par réflexion.
 * On peut passer des filtres (TableFilter) pour filtrer l'affichage.
 *
 * Usage:
 *   PanelList<Article> list = new PanelList<>(new Article(), articles, filters);
 */
public class PanelList<T> extends JPanel {

    private final Class<T>      modelClass;
    private final String[]      columnNames;
    private final Field[]       fields;       // champs de la classe (hors id)

    private DefaultTableModel   tableModel;
    private JTable              table;
    private List<T>             allData;

    // Composants filtre
    private final List<TableFilter>   filters;
    private final List<JComponent>    filterInputs = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public PanelList(T prototype, List<T> data, List<TableFilter> filters) {
        this.modelClass = (Class<T>) prototype.getClass();
        this.allData    = new ArrayList<>(data);
        this.filters    = filters != null ? filters : new ArrayList<>();

        // Extraire les champs (hors id)
        List<Field> visibleFields = new ArrayList<>();
        for (Field f : modelClass.getDeclaredFields()) {
            if (!f.getName().equals("id")) visibleFields.add(f);
        }
        this.fields      = visibleFields.toArray(new Field[0]);
        this.columnNames = buildColumnNames();

        setLayout(new BorderLayout(0, 8));
        setBackground(new Color(245, 247, 252));

        if (!this.filters.isEmpty()) {
            add(buildFilterPanel(), BorderLayout.NORTH);
        }
        add(buildTablePanel(), BorderLayout.CENTER);
        refreshTable(allData);
    }

    /** Constructeur sans filtres */
    public PanelList(T prototype, List<T> data) {
        this(prototype, data, null);
    }

    // ── Construction des noms de colonnes ─────────────────────────────────────

    private String[] buildColumnNames() {
        String[] names = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            names[i] = splitCamelCase(fields[i].getName());
        }
        return names;
    }

    // ── Panel filtres ─────────────────────────────────────────────────────────

    private JPanel buildFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        panel.setBackground(new Color(235, 238, 248));
        panel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        for (TableFilter filter : filters) {
            JLabel lbl = new JLabel(filter.getLabel() + ":");
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            panel.add(lbl);

            JComponent input;
            if (filter.getType() == TableFilter.FilterType.SELECT && filter.getOptions() != null) {
                JComboBox<Object> combo = new JComboBox<>();
                combo.addItem("-- Tous --");
                for (Object opt : filter.getOptions()) combo.addItem(opt);
                combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                combo.addActionListener(e -> applyFilters());
                input = combo;
            } else {
                JTextField tf = new JTextField(12);
                tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                tf.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                    public void insertUpdate(javax.swing.event.DocumentEvent e) { applyFilters(); }
                    public void removeUpdate(javax.swing.event.DocumentEvent e) { applyFilters(); }
                    public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilters(); }
                });
                input = tf;
            }
            filterInputs.add(input);
            panel.add(input);
        }

        return panel;
    }

    // ── Panel tableau ─────────────────────────────────────────────────────────

    private JScrollPane buildTablePanel() {
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        styleTable();
        return new JScrollPane(table);
    }

    private void styleTable() {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(28);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(200, 215, 255));
        table.setBackground(Color.WHITE);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(52, 120, 246));
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);
    }

    // ── Données ───────────────────────────────────────────────────────────────

    /** Met à jour les données complètes et rafraîchit. */
    public void setData(List<T> data) {
        this.allData = new ArrayList<>(data);
        applyFilters();
    }

    /** Rafraîchit le tableau avec une liste filtrée. */
    private void refreshTable(List<T> data) {
        tableModel.setRowCount(0);
        for (T item : data) {
            Object[] row = new Object[fields.length];
            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                try {
                    Object val = fields[i].get(item);
                    row[i] = val != null ? val.toString() : "";
                } catch (IllegalAccessException e) {
                    row[i] = "";
                }
            }
            tableModel.addRow(row);
        }
    }

    // ── Filtrage ──────────────────────────────────────────────────────────────

    private void applyFilters() {
        List<T> filtered = new ArrayList<>(allData);
        for (int fi = 0; fi < filters.size(); fi++) {
            TableFilter tf   = filters.get(fi);
            JComponent  comp = filterInputs.get(fi);
            String filterVal = getFilterValue(comp);
            if (filterVal == null || filterVal.isEmpty() || filterVal.equals("-- Tous --")) continue;

            final String fVal = filterVal.toLowerCase();
            List<T> temp = new ArrayList<>();
            for (T item : filtered) {
                String cellVal = getFieldStringValue(item, tf.getFieldName());
                if (cellVal != null && cellVal.toLowerCase().contains(fVal)) {
                    temp.add(item);
                }
            }
            filtered = temp;
        }
        refreshTable(filtered);
    }

    private String getFilterValue(JComponent comp) {
        if (comp instanceof JTextField) return ((JTextField) comp).getText().trim();
        if (comp instanceof JComboBox) {
            Object sel = ((JComboBox<?>) comp).getSelectedItem();
            return sel != null ? sel.toString() : "";
        }
        return "";
    }

    private String getFieldStringValue(T item, String fieldName) {
        try {
            Field f = modelClass.getDeclaredField(fieldName);
            f.setAccessible(true);
            Object val = f.get(item);
            return val != null ? val.toString() : "";
        } catch (Exception e) {
            return "";
        }
    }

    // ── Utilitaire ────────────────────────────────────────────────────────────

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

    public JTable getTable() { return table; }
}
