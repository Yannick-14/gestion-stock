package inventory.ui.components.table;

import inventory.ui.components.button.Button;

// import java.util.function.Function;

/**
 * Colonne personnalisée pour PanelList
 */
public interface CustomColumn<T> {

    String getName();

    /**
     * Retourne la valeur à afficher dans la cellule
     */
    Object getValue(T item);

    /**
     * Si la colonne est une colonne bouton (action)
     */
    default boolean isButtonColumn() {
        return false;
    }

    /**
     * Retourne le style du bouton (si c'est une colonne bouton)
     */
    default Button.Style getButtonStyle() {
        return Button.Style.PRIMARY;
    }

    /**
     * Action à exécuter quand on clique sur le bouton (si c'est une colonne bouton)
     */
    default void onButtonClick(T item) {
        // par défaut rien
    }
}
