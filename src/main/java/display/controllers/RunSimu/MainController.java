package display.controllers.RunSimu;

import display.model.ModelMVC;
import display.model.RunSimuModel;
import display.views.RunSimu.EnumRunSimu;
import display.views.ViewType;
import ia.model.ModelEnum;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import engine.Engine;
import javafx.fxml.FXML;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class MainController extends RunSimu {

    public static final String DEFAULT_SAVE_PATH = "ressources/parties";

    @FXML
    private ImageView imgPlayPause;
    @FXML
    private Label seed;

    public void btnPlayPauseClicked() {
        RunSimuModel model = (RunSimuModel) this.model;
        if (model.getEngine().isPresent()){
            Engine engine = model.getEngine().get();

            if (engine.isGameFinished() != null){
                System.out.println("Restart Game");
            }else{
                boolean isRunning = model.isRunning();
                String imagePath = isRunning ? "/display/views/icone/icons8-play-90.png" : "/display/views/icone/icons8-pause-100.png";
                imgPlayPause.setImage(new Image(getClass().getResourceAsStream(imagePath)));
                if (isRunning){
                    engine.setTps(0);
                }else{
                    engine.setTps(model.getSaveTps());
                }
                model.switchIsRunning();
            }
        }
    }


    public void btnMulti2Clicked(){
        RunSimuModel model = (RunSimuModel) this.model;
        if (model.getEngine().isPresent()) {
            Engine engine = model.getEngine().get();

            if(engine.isGameFinished() != null) {
                System.out.println("NO X2");
            }else{
                int tps = model.getSaveTps();
                if(tps * 2 > 0){
                    tps = tps *2;
                }
                model.setSaveTps(tps);

                if (model.isRunning()) {
                    engine.setTps(tps);
                }

                model.updateViews();
            }
        }
    }

    public void btnDiv2Clicked(){
        RunSimuModel model = (RunSimuModel) this.model;
        if (model.getEngine().isPresent()){
            Engine engine = model.getEngine().get();

            if(engine.isGameFinished() != null) {
                System.out.println("NO /2");
            }else {
                int tps = model.getSaveTps();
                if (tps > 1) {
                    tps = tps / 2;
                    model.setSaveTps(tps);
                }

                if (model.isRunning()) {
                    engine.setTps(tps);
                }

                model.updateViews();
            }
        }
    }

    public void btnRestart() throws IOException {
        RunSimuModel model = (RunSimuModel) this.model;
        model.restart();

        model.update();
        model.getGlobalModel().updateRacine();
    }

    public void btnNewSeed() throws IOException {
        RunSimuModel model = (RunSimuModel) this.model;
        model.setSeed(new Random().nextLong());

        btnRestart();
    }

    public void btnExit(){
        RunSimuModel model = (RunSimuModel) this.model;

        model.getEngine().get().stop();
        ModelMVC.clearInstance(RunSimuModel.class);

        model.setEnumRunSimu(EnumRunSimu.Mode);
        model.getGlobalModel().setCurrentViewType(ViewType.MainMenu);

        model.update();
        model.getGlobalModel().updateRacine();
    }

    public void handleSeedClick(MouseEvent event) {
        // Copier le texte du label dans le presse-papiers
        String seedText = seed.getText();
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(seedText);
        clipboard.setContent(content);

        System.out.println("SEED copiée dans le presse-papiers : " + seedText);
    }

    public void btnSave(){
        RunSimuModel model = (RunSimuModel) this.model;

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Sauvegarde");
        dialog.setHeaderText("Nom de la sauvegarde");
        dialog.setContentText("Entrez le nom de la sauvegarde :");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(saveName -> {
            String fileName = result.get();

            File saveDir = new File(DEFAULT_SAVE_PATH);
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }

            File saveFile = new File(saveDir, fileName + ".txt");

            try {
                // Étape 3 : Écrire les données du modèle dans le fichier
                FileWriter writer = new FileWriter(saveFile);
                writer.write(""+ model.getSeed()+"\n");
                writer.write(""+ model.getMap().getMapPath()+"\n");
                writer.write("");
                System.out.println(model.getModelList());
                for (List<ModelEnum> mEquipe : model.getModelList()) {
                    for (ModelEnum m : mEquipe) {
                        writer.write(ModelEnum.getEnumValue(m)+";");
                    }
                }
                writer.write("\n");
                writer.write(""+ model.getNbPlayers()+"\n");
                writer.write(""+ model.getSpeedPlayers()+"\n");
                writer.write(""+ model.getRespawnTime()+"\n");
                writer.write(""+ model.getMaxTurns()+"\n");
                writer.close();
                System.out.println("Partie sauvegardée avec succès : " + saveFile.getAbsolutePath());
            } catch (IOException ex) {
                System.err.println("Erreur lors de la sauvegarde : " + ex.getMessage());
            }
        });
    }
}
