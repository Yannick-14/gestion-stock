package inventory.ui.components.table;

import inventory.ui.components.button.Button;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

/**
 * Composant tableau générique avec filtres appliqués au clic d'un bouton.
 * 
 * Usage:
 *   PanelList<Article> list = new PanelList<>(new Article(), articles, filters);
 */
public class PanelList<T> extends JPanel {

    private final Class<T> modelClass;
    private final String[] columnNames;
    private final Field[] fields;

    private CardLayout cardLayout;
    private JPanel cardPanel;

    private DefaultTableModel tableModel;
    private JTable table;
    private List<T> allData;
    private List<T> currentData;

    private JLabel emptyLabel;
    // private JPanel tableContainerPanel;

    private final List<TableFilter> filters;
    private final List<JComponent> filterInputs = new ArrayList<>();
    private final List<CustomColumn<T>> customColumns;

    private static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    @SuppressWarnings("unchecked")
    public PanelList(T prototype, List<T> data, List<TableFilter> filters, List<CustomColumn<T>> customColumns) {
        this.modelClass = (Class<T>) prototype.getClass();
        this.allData = new ArrayList<>(data);
        this.filters = filters != null ? filters : new ArrayList<>();
        this.customColumns = customColumns != null ? customColumns : new ArrayList<>();

        // Champs existants (sans id)
        List<Field> visibleFields = new ArrayList<>();
        for (Field f : modelClass.getDeclaredFields()) {
            if (!f.getName().equals("id") && !f.getName().equals("serialVersionUID")) {
                visibleFields.add(f);
            }
        }
        this.fields = visibleFields.toArray(new Field[0]);
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
    public PanelList(T prototype, List<T> data, List<TableFilter> filters) {
        this(prototype, data, filters, null);
    }

    public PanelList(T prototype, List<T> data) {
        this(prototype, data, null, null);
    }

    // ── Construction des noms de colonnes ─────────────────────────────────────
    private String[] buildColumnNames() {
        String[] baseNames = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            baseNames[i] = splitCamelCase(fields[i].getName());
        }

        String[] allNames = new String[baseNames.length + customColumns.size()];
        System.arraycopy(baseNames, 0, allNames, 0, baseNames.length);

        for (int i = 0; i < customColumns.size(); i++) {
            allNames[baseNames.length + i] = customColumns.get(i).getName();
        }
        return allNames;
    }

    private JPanel buildFilterPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 8));
        mainPanel.setBackground(new Color(235, 238, 248));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Panel gauche : champs de filtre
        JPanel filterFieldsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        filterFieldsPanel.setBackground(new Color(235, 238, 248));

        for (TableFilter filter : filters) {
            JLabel lbl = new JLabel(filter.getLabel() + ":");
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            filterFieldsPanel.add(lbl);

            JComponent input;
            if (filter.getType() == TableFilter.FilterType.SELECT && filter.getOptions() != null) {
                JComboBox<Object> combo = new JComboBox<>();
                combo.addItem("-- Tous --");
                for (Object opt : filter.getOptions()) {
                    combo.addItem(opt);
                }
                combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                combo.setPreferredSize(new Dimension(140, 30));
                input = combo;
            } else {
                JTextField tf = new JTextField(12);
                tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                tf.setPreferredSize(new Dimension(140, 30));
                input = tf;
            }
            filterInputs.add(input);
            filterFieldsPanel.add(input);
        }

        // Panel droit : boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        buttonPanel.setBackground(new Color(235, 238, 248));

        Button filterButton = new Button("🔍 Filtrer", Button.Style.PRIMARY);
        filterButton.addActionListener(e -> applyFilters());

        Button resetButton = new Button("↻ Réinitialiser", Button.Style.SECONDARY);
        resetButton.addActionListener(e -> resetFilters());

        buttonPanel.add(filterButton);
        buttonPanel.add(resetButton);

        // Assemblage
        mainPanel.add(filterFieldsPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.EAST);

        return mainPanel;
    }

    // ── Panel tableau ─────────────────────────────────────────────────────────

    private JPanel buildTablePanel() {
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                int customColIndex = col - fields.length;
                if (customColIndex >= 0 && customColIndex < customColumns.size()) {
                    return customColumns.get(customColIndex).isButtonColumn();
                }
                return false;
            }
        };
        table = new JTable(tableModel);
        styleTable();

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(Color.WHITE);

        // ✅ Card 1 : Tableau AVEC scroll
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBackground(Color.WHITE);
        cardPanel.add(scrollPane, "table");

        // ✅ Card 2 : Message "Aucune donnée" (pas besoin de scroll)
        emptyLabel = new JLabel("Aucune donnée à afficher pour " + modelClass.getSimpleName(), SwingConstants.CENTER);
        emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        emptyLabel.setForeground(new Color(255, 200, 0));
        emptyLabel.setOpaque(true);
        emptyLabel.setBackground(new Color(255, 242, 204));
        cardPanel.add(emptyLabel, "empty");

        cardLayout.show(cardPanel, "table");

        return cardPanel;
    }

    private void styleTable() {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(35);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(200, 215, 255));
        table.setBackground(Color.WHITE);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(Color.WHITE);
        header.setForeground(Color.GRAY);
        header.setReorderingAllowed(false);

        // Appliquer les renderers/editors pour les colonnes personnalisées
        for (int i = 0; i < customColumns.size(); i++) {
            CustomColumn<T> colDef = customColumns.get(i);
            if (colDef.isButtonColumn()) {
                int tableColIndex = fields.length + i;
                TableColumn tableCol = table.getColumnModel().getColumn(tableColIndex);
                
                ButtonColumnRenderer renderer = new ButtonColumnRenderer();
                renderer.setStyle(colDef.getButtonStyle());
                tableCol.setCellRenderer(renderer);

                // L'éditeur gère le clic réel
                ButtonColumnEditor editor = new ButtonColumnEditor(obj -> {
                    int row = table.getSelectedRow();
                    T item = getItemAtRow(row);
                    if (item != null) {
                        colDef.onButtonClick(item);
                    }
                });
                editor.setStyle(colDef.getButtonStyle());
                tableCol.setCellEditor(editor);
            }
        }
    }

    // ── Données ───────────────────────────────────────────────────────────────

    /** Met à jour les données complètes et rafraîchit. */
    public void setData(List<T> data) {
        this.allData = new ArrayList<>(data);
        resetFilters();
    }

    /** Rafraîchit le tableau avec une liste filtrée. */
    private void refreshTable(List<T> data) {
        this.currentData = new ArrayList<>(data);
        tableModel.setRowCount(0);

        if (data.isEmpty()) {
            cardLayout.show(cardPanel, "empty");
        } else {
            cardLayout.show(cardPanel, "table");
            
            for (T item : data) {
                Object[] row = new Object[fields.length + customColumns.size()];

                for (int i = 0; i < fields.length; i++) {
                    fields[i].setAccessible(true);
                    try {
                        Object val = fields[i].get(item);
                        row[i] = formatCellValue(val);
                    } catch (Exception e) {
                        row[i] = "";
                    }
                }

                int base = fields.length;
                for (int i = 0; i < customColumns.size(); i++) {
                    row[base + i] = customColumns.get(i).getValue(item);
                }

                tableModel.addRow(row);
            }
        }
    }

    // Formater la valeur d'une cellule de table data
    private String formatCellValue(Object value) {
        if (value == null) {
            return "";
        }

        // Formater les dates
        if (value instanceof Date) {
            return DATETIME_FORMAT.format((Date) value);
        }
        
        return value.toString();
    }

    // ── Filtrage ──────────────────────────────────────────────────────────────

    /**
     * Applique les filtres au clic du bouton "Filtrer"
     */
    private void applyFilters() {
        List<T> filtered = new ArrayList<>(allData);
        
        for (int fi = 0; fi < filters.size(); fi++) {
            TableFilter tf = filters.get(fi);
            JComponent comp = filterInputs.get(fi);
            String filterVal = getFilterValue(comp);

            // Ignorer les filtres vides
            if (filterVal == null || filterVal.isEmpty() || filterVal.equals("-- Tous --")) {
                continue;
            }

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

    /**
     * Réinitialise les filtres et affiche toutes les données
     */
    private void resetFilters() {
        // Vider tous les champs de filtre
        for (JComponent comp : filterInputs) {
            if (comp instanceof JTextField) {
                ((JTextField) comp).setText("");
            } else if (comp instanceof JComboBox) {
                ((JComboBox<?>) comp).setSelectedIndex(0);
            }
        }

        // Afficher toutes les données
        refreshTable(allData);
    }

    private String getFilterValue(JComponent comp) {
        if (comp instanceof JTextField) {
            return ((JTextField) comp).getText().trim();
        }
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
            
            if (val == null) return "";
            
            return formatCellValue(val);
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

    public JTable getTable() { 
        return table; 
    }

    public T getItemAtRow(int row) {
        if (row >= 0 && row < currentData.size()) {
            return currentData.get(row);
        }
        return null;
    }

    public List<T> getCurrentData() {
        return new ArrayList<>(currentData);
    }

    public List<T> getAllData() {
        return new ArrayList<>(allData);
    }
}
