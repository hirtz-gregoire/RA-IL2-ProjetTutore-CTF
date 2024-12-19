package engine;

import java.io.File;

public class Files {
    public static File[] getListFilesMaps() {
        File repertoireCartes  = new File("ressources/maps");
        return repertoireCartes.listFiles();
    }
    public static File getFileMapByName(String nomFichier) {
        File repertoireCartes  = new File("ressources/maps");
        for (File fichierCarte : repertoireCartes.listFiles()) {
            if (fichierCarte.getName().replace(".txt", "").equals(nomFichier)) {
                return fichierCarte;
            }
        }
        return null;
    }
    public static File getFileModelByName(String nomFichier) {
        File repertoireModels  = new File("ressources/models");
        for (File fichierModel : repertoireModels.listFiles()) {
            if (fichierModel.getName().replace(".txt", "").equals(nomFichier)) {
                return fichierModel;
            }
        }
        return null;
    }
    //Liste des modèles enregistrés
    public static File[] getListSavesFilesModels() {
        File repertoireCartes = new File("ressources/models");
        return repertoireCartes.listFiles();
    }
    //Liste des classes de modèle existant pour l'apprentissage
    public static File[] getListFilesModels() {
        File[] modelsFiles = new File("src/main/java/ia/model").listFiles();
        for (int i = 0; i < modelsFiles.length; i++) {
            if (modelsFiles[i].getName().replace(".txt", "").equals("Model")) {
                modelsFiles[i].delete();
                return modelsFiles;
            }
        }
        return null;
    }
    public static File[] getListFilesParties() {
        File repertoireCartes  = new File("ressources/parties");
        return repertoireCartes.listFiles();
    }
    public static File getFileGameByName(String nomFichier) {
        return new File("ressources/parties/" + nomFichier+".txt");
    }
}
