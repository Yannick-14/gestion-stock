package inventory;

import inventory.ui.Window;
import javax.swing.*;

public class Application {
    public static void main(String[] args) {
        // Lancer l'interface graphique dans l'EDT (Event Dispatch Thread)
        SwingUtilities.invokeLater(() -> {
            Window mainWindow = new Window();
            
            // Créer un panel simple pour tester (remplace MyPanel qui n'existe pas)
            JPanel testPanel = new JPanel();
            testPanel.add(new JLabel("Inventory Management System"));
            testPanel.add(new JButton("Test Button"));
            
            mainWindow.setContentPane(testPanel);
            mainWindow.showWindow();
        });
    }
}