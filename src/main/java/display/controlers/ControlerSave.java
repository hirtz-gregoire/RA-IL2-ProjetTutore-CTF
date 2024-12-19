package display.controlers;

import display.modele.Modele;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;

public class ControlerSave {

    public static final String DEFAULT_SAVE_PATH = "ressources/parties";

    Modele modele;

    public ControlerSave(Modele modele) {
        this.modele = modele;
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
                writer.write(""+modele.getSeed()+"\n");
                writer.write(""+modele.getCarte()+"\n");
                writer.write("");
                for (String model : modele.getModelsEquipesString()) {
                    writer.write(model+";");
                }
                writer.write("\n");
                writer.write(""+modele.getNbJoueurs()+"\n");
                writer.write(""+modele.getVitesseDeplacement()+"\n");
                writer.write(""+modele.getTempsReaparition()+"\n");
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
