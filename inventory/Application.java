package inventory;

import inventory.ui.Navigation;
import inventory.ui.Window;

import javax.swing.*;

/**
 * Point d'entrée de l'application.
 * Lance la fenêtre principale avec la Navigation (sidebar + contenu).
 */
public class Application {

    public static void main(String[] args) {
        // Lancement dans l'Event Dispatch Thread (EDT) — obligatoire pour Swing
        SwingUtilities.invokeLater(() -> {
            // Créer et configurer la fenêtre
            Window mainWindow = new Window("Gestion de Stock");

            // La Navigation contient la sidebar + la zone de contenu
            Navigation navigation = new Navigation();

            // Injecter la navigation comme contenu principal de la fenêtre
            mainWindow.setContentPane(navigation);
            mainWindow.maximize();
            mainWindow.showWindow();
        });
    }
}