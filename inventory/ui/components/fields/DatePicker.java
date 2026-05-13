package inventory.ui.components.fields;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;
// import java.awt.event.ActionEvent;
// import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * Composant de sélection de date avec calendrier popup.
 */
public class DatePicker extends JPanel {
    private final JTextField textField;
    private final JButton calendarButton;
    private LocalDate selectedDate;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public DatePicker() {
        this(LocalDate.now());
    }

    public DatePicker(LocalDate defaultDate) {
        setLayout(new BorderLayout(0, 0));
        setOpaque(false);

        selectedDate = defaultDate;
        
        // TextField stylisé
        textField = new JTextField(selectedDate != null ? selectedDate.format(formatter) : "");
        textField.setEditable(false);
        textField.setBackground(Color.WHITE);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textField.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        
        // Bouton avec icône (ou texte si pas d'image)
        calendarButton = new JButton("📅");
        calendarButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        calendarButton.setFocusable(false);
        calendarButton.setBackground(new Color(240, 240, 240));
        calendarButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 1, 1, new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));
        
        calendarButton.addActionListener(e -> showCalendarPopup());

        add(textField, BorderLayout.CENTER);
        add(calendarButton, BorderLayout.EAST);
        
        // Taille préférée pour s'aligner avec FieldInput
        setPreferredSize(new Dimension(350, 32));
        setMaximumSize(new Dimension(400, 32));
    }

    private void showCalendarPopup() {
        JPopupMenu popup = new JPopupMenu();
        popup.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        popup.add(new CalendarPanel(this, popup));
        popup.show(textField, 0, textField.getHeight());
    }

    public String getDate() {
        return textField.getText();
    }

    public void setDate(LocalDate date) {
        this.selectedDate = date;
        textField.setText(date != null ? date.format(formatter) : "");
        firePropertyChange("date", null, date);
    }

    public JTextField getTextField() {
        return textField;
    }

    /**
     * Panneau interne affichant le calendrier.
     */
    private class CalendarPanel extends JPanel {
        private final DatePicker parent;
        private final JPopupMenu popup;
        private LocalDate currentView;

        public CalendarPanel(DatePicker parent, JPopupMenu popup) {
            this.parent = parent;
            this.popup = popup;
            this.currentView = parent.selectedDate != null ? parent.selectedDate : LocalDate.now();
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);
            buildCalendar();
        }

        private void buildCalendar() {
            removeAll();
            
            // Header: Mois Année + Navigation
            JPanel header = new JPanel(new BorderLayout());
            header.setBackground(new Color(245, 245, 245));
            header.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            JButton prev = createNavButton("<");
            JButton next = createNavButton(">");
            
            String monthName = currentView.getMonth().getDisplayName(TextStyle.FULL, Locale.FRENCH);
            JLabel label = new JLabel(monthName.substring(0, 1).toUpperCase() + monthName.substring(1) + " " + currentView.getYear(), SwingConstants.CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 13));
            
            prev.addActionListener(e -> { currentView = currentView.minusMonths(1); buildCalendar(); });
            next.addActionListener(e -> { currentView = currentView.plusMonths(1); buildCalendar(); });
            
            header.add(prev, BorderLayout.WEST);
            header.add(label, BorderLayout.CENTER);
            header.add(next, BorderLayout.EAST);
            add(header, BorderLayout.NORTH);

            // Grille: Jours
            JPanel grid = new JPanel(new GridLayout(0, 7, 2, 2));
            grid.setBackground(Color.WHITE);
            grid.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            String[] days = {"Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim"};
            for (String d : days) {
                JLabel l = new JLabel(d, SwingConstants.CENTER);
                l.setFont(new Font("Segoe UI", Font.BOLD, 10));
                l.setForeground(new Color(120, 120, 120));
                grid.add(l);
            }

            LocalDate first = currentView.withDayOfMonth(1);
            int startOffset = first.getDayOfWeek().getValue() - 1; // 0 pour Lundi
            for (int i = 0; i < startOffset; i++) grid.add(new JLabel(""));

            int daysInMonth = currentView.lengthOfMonth();
            for (int day = 1; day <= daysInMonth; day++) {
                // final int d = day;
                JButton btn = new JButton(String.valueOf(day));
                btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                btn.setFocusPainted(false);
                btn.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                btn.setContentAreaFilled(false);
                btn.setOpaque(true);
                btn.setBackground(Color.WHITE);

                LocalDate dateObj = currentView.withDayOfMonth(day);
                if (dateObj.equals(parent.selectedDate)) {
                    btn.setBackground(new Color(0, 120, 215));
                    btn.setForeground(Color.WHITE);
                    btn.setContentAreaFilled(true);
                } else if (dateObj.equals(LocalDate.now())) {
                    btn.setBorder(BorderFactory.createLineBorder(new Color(0, 120, 215), 1));
                }

                btn.addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { if (!dateObj.equals(parent.selectedDate)) btn.setBackground(new Color(230, 240, 250)); }
                    @Override public void mouseExited(MouseEvent e) { if (!dateObj.equals(parent.selectedDate)) btn.setBackground(Color.WHITE); }
                });

                btn.addActionListener(e -> {
                    parent.setDate(dateObj);
                    popup.setVisible(false);
                });
                grid.add(btn);
            }

            add(grid, BorderLayout.CENTER);
            revalidate();
            repaint();
        }

        private JButton createNavButton(String text) {
            JButton b = new JButton(text);
            b.setFont(new Font("Monospaced", Font.BOLD, 14));
            b.setFocusPainted(false);
            b.setBorderPainted(false);
            b.setContentAreaFilled(false);
            b.setCursor(new Cursor(Cursor.HAND_CURSOR));
            return b;
        }
    }
}
