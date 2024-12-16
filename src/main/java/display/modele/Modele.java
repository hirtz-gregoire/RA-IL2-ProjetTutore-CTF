package display.modele;

import display.views.Observateur;
import display.views.ViewsEnum;

import java.util.ArrayList;

public class Modele {
    private ArrayList<Observateur> observateurs;
    //La vue courante affichée à l'écran
    private ViewsEnum vue;
    private int tps;
    private String carte;
    private String modelEquipe1;
    private String modelEquipe2;
    private int tempsReaparition;
    private int nbJoueurs;
    private int vitesseDeplacement;

    public Modele(ViewsEnum view){
        this.observateurs = new ArrayList<Observateur>();
        //la vue de base est le menu simulation
        this.vue = view;
        //Informations de bases
        this.carte = "dust";
        this.tempsReaparition = 5;
        this.nbJoueurs = 3;
        this.vitesseDeplacement = 5;
    }
    //Ajoute un observateur à la liste
    public void enregistrerObservateur(Observateur observateur) {
        this.observateurs.add(observateur);
    }
    //Supprime un observateur a la liste
    public void supprimerObservateur(Observateur observateur) {
        int i = this.observateurs.indexOf(observateur);
        if (i >= 0) {
            this.observateurs.remove(i);
        }
    }
    //Informe tous les observateurs des modifications en appelant leurs methodes actualiser
    public void notifierObservateurs() throws Exception {
        for (Observateur observer : this.observateurs) {
            observer.actualiser(this);
        }
    }
    public ViewsEnum getVue() {
        return this.vue;
    }
    public void setVue(ViewsEnum vue) {
        this.vue = vue;
    }
    public int getTps() {
        return this.tps;
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
    public String getModelEquipe1() {
        return modelEquipe1;
    }
    public void setModelEquipe1(String modelEquipe1) {
        this.modelEquipe1 = modelEquipe1;
    }
    public String getModelEquipe2() {
        return modelEquipe2;
    }
    public void setModelEquipe2(String modelEquipe2) {
        this.modelEquipe2 = modelEquipe2;
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
}
