package engine;

import java.io.File;

public class Files {
    public static File[] getListFilesMaps() {
        File repertoireCartes  = new File("ressources/maps");
        return repertoireCartes.listFiles();
    }
    public static File getFileMapsByName(String nomFichier) {
        File repertoireCartes  = new File("ressources/maps");
        for (File fichierCarte : repertoireCartes.listFiles()) {
            if (fichierCarte.getName().equals(nomFichier)) {
                return fichierCarte;
            }
        }
        return null;
    }
    public static File[] getListFilesModels() {
        File repertoireCartes  = new File("ressources/models");
        return repertoireCartes.listFiles();
    }
    public static File[] getListFilesParties() {
        File repertoireCartes  = new File("ressources/parties");
        return repertoireCartes.listFiles();
    }
    public static File getFileGameByName(String nomFichier) {
        return new File("ressources/parties/" + nomFichier+".txt");
    }
}
