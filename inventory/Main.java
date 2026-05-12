package inventory;

import javax.swing.SwingUtilities;
import inventory.ui.Window;
import inventory.ui.screen.TestFormScreen;

/**
 * Point d'entrée principal — Lancement de l'interface de test.
 */
public class Main {

    public static void main(String[] args) {
        // Lancer l'interface dans le thread Swing
        SwingUtilities.invokeLater(() -> {
            Window window = new Window("Test de généralisation UI");
            window.setContentPane(new TestFormScreen());
            window.showWindow();
        });
    }
}
