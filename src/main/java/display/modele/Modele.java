package display.modele;

import display.views.Observateur;
import display.views.ViewsEnum;
import engine.Files;
import engine.Team;
import ia.model.DecisionTree;
import ia.model.Model;
import ia.model.Random;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Modele {
    private ArrayList<Observateur> observateurs;
    //La vue courante affichée à l'écran
    private ViewsEnum vue;
    private int tps;
    private String carte;
    private int nbEquipes;
    private String[] modelsEquipes;
    private int tempsReaparition;
    private int nbJoueurs;
    private int vitesseDeplacement;
    private Long seed;

    public Modele(ViewsEnum view){
        observateurs = new ArrayList<Observateur>();
        //la vue de base est le menu simulation
        vue = view;
        tps = 1;
        //Informations de bases
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
    public String[] getModelsEquipesString() {
        return modelsEquipes;
    }
    public void setModelsEquipesString(String[] modelsEquipes) {
        this.modelsEquipes = modelsEquipes;
    }
    public Model[] getModelsEquipes() throws IOException {
        Model[] models = new Model[nbEquipes];
        for (int numEquipe = 0; numEquipe < nbEquipes; numEquipe++) {
            models[numEquipe] = getModelEquipeIndex(numEquipe);
        }
        return models;
    }
    public void setModelsEquipes(Model[] modelsEquipes) {
        for (int numEquipe = 0; numEquipe < nbEquipes; numEquipe++) {
            setModelEquipeIndex(numEquipe, modelsEquipes[numEquipe]);
        }
    }
    public Model getModelEquipeIndex(int numEquipe) throws IOException {
        Model model;
        BufferedReader reader = new BufferedReader(new FileReader(Files.getFileModelByName(modelsEquipes[numEquipe])));
        String modelString = reader.readLine();
        switch (modelString) {
            case "Random" -> model = new Random();
            case "DecisionTree" -> model = new DecisionTree();
            default -> model = new Random();
        }
        return model;
    }
    public void setModelEquipeIndex(int numEquipe, Model model) {
        String modelString;
        switch (model) {
            case Random random -> modelString = "Random";
            case DecisionTree decisionTree -> modelString = "DecisionTree";
            default -> modelString = "DecisionTree";
        }
        modelsEquipes[numEquipe] = modelString;
    }
    public String getModelEquipeIndexString(int index) {
        return modelsEquipes[index];
    }
    public void setModelEquipeIndexString(String model, int index) {
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

    public void setSeed(long seed) {this.seed = seed;}
    public Long getSeed() {return seed;}
}
