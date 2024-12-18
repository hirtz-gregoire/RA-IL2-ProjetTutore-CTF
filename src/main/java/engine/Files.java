package engine;

import java.io.File;

public class Files {
    public static File[] getListeFichiersCartes() {
        File repertoireCartes  = new File("file:ressources/maps");
        return repertoireCartes.listFiles();
    }
    public static File[] getListeFichiersModels() {
        File repertoireCartes  = new File("file:ressources/models");
        return repertoireCartes.listFiles();
    }
    public static File[] getListeFichiersParties() {
        File repertoireCartes  = new File("file:ressources/parties");
        return repertoireCartes.listFiles();
    }
}
