package display.model;

import display.views.View;

import java.util.HashMap;
import java.util.Map;

public abstract class ModelMVC {

    // Map pour stocker une instance par type de modèle
    private static final Map<Class<? extends ModelMVC>, ModelMVC> instances = new HashMap<>();

    protected View view;
    private GlobalModel globalModel;

    // Constructeur protégé pour les sous-classes
    protected ModelMVC(GlobalModel globalModel) {
        if (instances.containsKey(this.getClass())) {
            throw new IllegalStateException("Instance already created for this class: " + this.getClass());
        }
        this.globalModel = globalModel;
    }

    // Méthode pour obtenir ou créer une instance
    @SuppressWarnings("unchecked")
    public static <T extends ModelMVC> T getInstance(Class<T> clazz, GlobalModel globalModel) {
        synchronized (instances) {
            if (!instances.containsKey(clazz)) {
                try {
                    // Crée une nouvelle instance en appelant le constructeur avec GlobalModel
                    T instance = clazz.getDeclaredConstructor(GlobalModel.class).newInstance(globalModel);
                    instances.put(clazz, instance);
                } catch (Exception e) {
                    throw new RuntimeException("Error creating instance for class: " + clazz, e);
                }
            }
            return (T) instances.get(clazz);
        }
    }

    // Méthode pour supprimer une instance spécifique
    public static void clearInstance(Class<? extends ModelMVC> clazz) {
        synchronized (instances) {
            instances.remove(clazz);
        }
    }

    // Méthode pour supprimer toutes les instances
    public static void clearAllInstances() {
        synchronized (instances) {
            instances.clear();
        }
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public void updateViews() {
        view.update();
    }

    // Getter pour GlobalModel
    public GlobalModel getGlobalModel() {
        return globalModel;
    }
}
