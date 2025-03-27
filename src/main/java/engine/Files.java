package engine;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Files {
    static String cheminMaps = "ressources/maps";
    static String cheminGames = "ressources/parties";
    static String cheminSavesModels = "ressources/models";
    static String cheminModels = "src/main/java/ia/model";

    public static File[] getListFilesMaps() {
        return new File(cheminMaps).listFiles();
    }
    public static File getFileMapByName(String nomFichier) {
        File repertoireCartes  = new File(cheminMaps);
        for (File fichierCarte : repertoireCartes.listFiles()) {
            if (fichierCarte.getName().equals(nomFichier+".txt")) {
                return fichierCarte;
            }
        }
        return null;
    }
    public static File getFileModelByName(String nomFichier) {
        File repertoireModels  = new File(cheminSavesModels);
        for (File fichierModel : repertoireModels.listFiles()) {
            if (fichierModel.getName().replace(".txt", "").equals(nomFichier)) {
                return fichierModel;
            }
        }
        return null;
    }
    //Liste des modèles enregistrés
    public static List<File> getListSavesFilesModels() {
        ArrayList<File> files = new ArrayList<>();
        for (File fichier : Objects.requireNonNull(new File(cheminSavesModels).listFiles())) {
            if (fichier.getName().split("\\.")[1].equals("ctf")) {
                files.add(fichier);
            }
        }
        return files;
    }
    //Liste des classes de modèle existant pour l'apprentissage
    public static File[] getListFilesModels() {
        File[] modelsFiles = new File(cheminModels).listFiles();
        for (int i = 0; i < modelsFiles.length; i++) {
            if (modelsFiles[i].getName().replace(".txt", "").equals("Model")) {
                modelsFiles[i].delete();
                return modelsFiles;
            }
        }
        return null;
    }
    public static File[] getListFilesParties() {
        File repertoireCartes  = new File(cheminGames);
        return repertoireCartes.listFiles();
    }
    public static File getFileGameByName(String nomFichier) {
        return new File(cheminGames + nomFichier+".txt");
    }

    //Récupération du fichier de sauvegarde de l'apprentissage
    public static File getFileLearning() {
        return new File("out.stat");
    }
}
