package display.views;

import display.controllers.Controller;
import display.model.ModelMVC;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class View {

    protected ModelMVC model;
    protected Pane pane;
    private List<View> childrenViews = new ArrayList<View>();

    public View(ModelMVC modelMVC) {
        this.model = modelMVC;
    }

    protected void update(){
        for(View view : childrenViews){
            view.update();
        }
    }

    protected void addChildrenView(View view){
        if (view == this){
            throw new IllegalArgumentException("You cannot add children views twice");
        }
        this.childrenViews.add(view);
    }

    protected void removeChildrenView(View view){
        this.childrenViews.remove(view);
    }

    public Pane getPane() {
        return pane;
    }

    protected static Pane loadFxml(String fileName, ModelMVC model) throws IOException {
        FXMLLoader loader = new FXMLLoader(View.class.getResource("fxml/"+fileName+".fxml"));
        Pane pane = loader.load();
        Controller ctrl = loader.getController();
        if (ctrl != null){
            ctrl.setModelMVC(model);
        }
        return pane;
    }

}
