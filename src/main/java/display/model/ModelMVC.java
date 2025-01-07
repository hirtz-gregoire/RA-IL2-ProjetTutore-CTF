package display.model;

import display.views.View;

import java.util.ArrayList;
import java.util.List;

public abstract class ModelMVC {

    private static ModelMVC instance;

    private List<View> viewList = new ArrayList<>();

    protected ModelMVC() {
        if (instance != null) {
            throw new IllegalStateException("Instance already created");
        }
    }

    public static <T extends ModelMVC> T getInstance(Class<T> clazz) {
        if (instance == null) {
            synchronized (ModelMVC.class) {
                if (instance == null) {
                    try {
                        instance = clazz.getDeclaredConstructor().newInstance();
                    } catch (Exception e) {
                        throw new RuntimeException("Error creating instance", e);
                    }
                }
            }
        }
        return clazz.cast(instance);
    }

    public static void clearInstance(){
        instance = null;
    }


    public void addView(View view) {
        viewList.add(view);
    }

    public void removeView(View view) {
        viewList.remove(view);
    }

    public void updateViews() {
        for (View view : viewList) {
            view.update();
        }
    }
}

