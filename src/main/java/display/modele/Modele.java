package display.modele;

import display.views.Observateur;
import display.views.ViewsEnum;
import engine.Files;

import java.io.File;
import java.util.ArrayList;

public class Modele {
    private ArrayList<Observateur> observateurs;
    //La vue courante affichée à l'écran
    private ViewsEnum vue;
    private int tps;
    private String carte;
    private int nbEquipes;
    private String[] modelsEquipes;
    private String partie;
    private int tempsReaparition;
    private int nbJoueurs;
    private int vitesseDeplacement;

    public Modele(ViewsEnum view){
        observateurs = new ArrayList<Observateur>();
        //la vue de base est le menu simulation
        vue = view;
        tps = 1;
        //Informations de bases
        carte = Files.getListeFichiersCartes()[0].getName().replace(".txt", ""); //Carte de base
        nbEquipes = 2;
        modelsEquipes = new String[nbEquipes];
        for (int index = 0; index < nbEquipes; index++) {
            modelsEquipes[index] = Files.getListeFichiersModels()[0].getName().replace(".txt", ""); //Premier model pour toutes les équipes
        }
        partie = null;
        tempsReaparition = 5;
        nbJoueurs = 3;
        vitesseDeplacement = 1;
    }
    //Ajoute un observateur à la liste
    public void enregistrerObservateur(Observateur observateur) {
        observateurs.add(observateur);
    }
    //Supprime un observateur a la liste
    public void supprimerObservateur(Observateur observateur) {
        int i = observateurs.indexOf(observateur);
        if (i >= 0) {
            observateurs.remove(i);
        }
    }
    //Informe tous les observateurs des modifications en appelant leurs methodes actualiser
    public void notifierObservateurs() throws Exception {
        for (Observateur observer : observateurs) {
            observer.actualiser(this);
        }
    }
    public ViewsEnum getVue() {
        return vue;
    }
    public void setVue(ViewsEnum vue) {
        this.vue = vue;
    }
    public int getTps() {
        return tps;
    }
    public void setTps(int tps) {
        this.tps = tps;
    }
    public String getCarte() {
        return carte;
    }
    public void setCarte(String carte) {
        this.carte = carte;
    }
    public int getNbEquipes() {
        return nbEquipes;
    }
    public void setNbEquipes(int nbEquipes) {
        this.nbEquipes = nbEquipes;
    }
    public String[] getModelsEquipes() {
        return modelsEquipes;
    }
    public void setModelsEquipes(String[] modelsEquipes) {
        this.modelsEquipes = modelsEquipes;
    }
    public String getModelEquipeIndex(int index) {
        return modelsEquipes[index];
    }
    public void setModelEquipeIndex(String model, int index) {
        modelsEquipes[index] = model;
    }
    public int getTempsReaparition() {
        return tempsReaparition;
    }

    public void setTempsReaparition(int tempsReaparition) {
        this.tempsReaparition = tempsReaparition;
    }

    public int getNbJoueurs() {
        return nbJoueurs;
    }

    public void setNbJoueurs(int nbJoueurs) {
        this.nbJoueurs = nbJoueurs;
    }

    public int getVitesseDeplacement() {
        return vitesseDeplacement;
    }

    public void setVitesseDeplacement(int vitesseDeplacement) {
        this.vitesseDeplacement = vitesseDeplacement;
    }
    public String getPartie() {
        return partie;
    }

    public void setPartie(String partie) {
        this.partie = partie;
    }
}
