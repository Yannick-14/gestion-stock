package inventory.ui.screen;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @deprecated Remplacé par AbstractFormScreen pour une approche par héritage plus flexible.
 */
@Deprecated
public class PanelForm<T> extends AbstractFormScreen<T> {

    public PanelForm(String title, T modelInstance, Consumer<T> onSubmit) {
        super(title, modelInstance);
        addAction("Enregistrer", e -> {
            try {
                T data = form.getData(modelInstance);
                onSubmit.accept(data);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    /** Constructeur de compatibilité */
    public PanelForm(String title, Class<T> modelClass, Map<String, List<?>> options, Consumer<T> onSubmit) {
        this(title, createInstance(modelClass), onSubmit);
    }

    private static <T> T createInstance(Class<T> cls) {
        try { return cls.getConstructor().newInstance(); }
        catch (Exception e) { throw new RuntimeException(e); }
    }
}
