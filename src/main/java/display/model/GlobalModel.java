package display.model;

import display.App;
import display.views.ViewType;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;

public class GlobalModel {

    // SINGLETON

    private static GlobalModel instance;
    public static GlobalModel getInstance() {
        if (instance == null) {
            instance = new GlobalModel(App.DEFAULT_VIEWTYPE);
        }
        return instance;
    }

    // CLASS

    private ViewType currentViewType;
    private ArrayList<ViewType> viewHistory = new ArrayList<>();

    private BorderPane racine = new BorderPane();
    private final int nbTeamMax = 8;

    public GlobalModel(ViewType viewType) {
        this.currentViewType = viewType;
    }

    public Pane getPane() {
        try{
            racine.setCenter(ViewType.getViewInstance(this.currentViewType, this).getView().getPane());
        }catch (IOException e){
            e.printStackTrace();
        }
        return racine;
    }

    public void updateRacine() {
        Stage stage = (Stage) racine.getScene().getWindow();
        Scene scene = stage.getScene();
        try {
            //System.out.println("Updating racine");
            this.racine.setCenter(ViewType.getViewInstance(this.currentViewType, this).getView().getPane());

            this.racine.setTop(null);
            if (this.currentViewType != ViewType.MainMenu){
                this.racine.setTop(ViewType.getViewInstance(ViewType.Top, this).getView().getPane());
            }
            scene.setRoot(racine);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public ViewType getCurrentViewType() {
        return this.currentViewType;
    }

    public void setCurrentViewType(ViewType viewType) {
        this.currentViewType = viewType;
    }
    public int getNbTeamMax() {
        return nbTeamMax;
    }

    public ArrayList<ViewType> getViewHistory() {
        return viewHistory;
    }
}
