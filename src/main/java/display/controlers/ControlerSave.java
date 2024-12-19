package display.controlers;

import display.modele.ModeleMVC;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;

public class ControlerSave {

    public static final String DEFAULT_SAVE_PATH = "ressources/parties";

    ModeleMVC modeleMVC;

    public ControlerSave(ModeleMVC modeleMVC) {
        this.modeleMVC = modeleMVC;
    }

    public void handle(MouseEvent e) {
        // Étape 1 : Afficher une boîte de dialogue pour obtenir le nom du fichier
        TextInputDialog dialog = new TextInputDialog("nouvelle_partie");
        dialog.setTitle("Sauvegarder la Partie");
        dialog.setHeaderText("Sauvegarde du jeu");
        dialog.setContentText("Veuillez entrer le nom du fichier :");

        Optional<String> result = dialog.showAndWait();

        // Vérification si l'utilisateur a entré un nom
        if (result.isPresent()) {
            String fileName = result.get();

            // Étape 2 : Créer le chemin complet pour le fichier
            File saveDir = new File(DEFAULT_SAVE_PATH);
            if (!saveDir.exists()) {
                saveDir.mkdirs(); // Crée le dossier s'il n'existe pas
            }

            File saveFile = new File(saveDir, fileName + ".txt");

            try {
                // Étape 3 : Écrire les données du modèle dans le fichier
                FileWriter writer = new FileWriter(saveFile);
                writer.write(""+ modeleMVC.getSeed()+"\n");
                writer.write(""+ modeleMVC.getCarte()+"\n");
                writer.write("");
                for (String model : modeleMVC.getModelsEquipesString()) {
                    writer.write(model+";");
                }
                writer.write("\n");
                writer.write(""+ modeleMVC.getNbJoueurs()+"\n");
                writer.write(""+ modeleMVC.getVitesseDeplacement()+"\n");
                writer.write(""+ modeleMVC.getTempsReaparition()+"\n");
                writer.close();
                System.out.println("Partie sauvegardée avec succès : " + saveFile.getAbsolutePath());
            } catch (IOException ex) {
                System.err.println("Erreur lors de la sauvegarde : " + ex.getMessage());
            }
        } else {
            System.out.println("Sauvegarde annulée par l'utilisateur.");
        }
    }
}
