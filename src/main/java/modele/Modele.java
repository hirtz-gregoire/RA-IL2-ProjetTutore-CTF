package modele;

import views.Observateur;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Modele {
    //Liste des observateurs
    private ArrayList<Observateur> observateurs;
    //La vue courante
    private String vue;

    public Modele(){
        this.observateurs = new ArrayList<Observateur>();
        //la vue de base est la simulation
        this.vue = "simulation_menu";
    }
    //Ajoute un observateur a la liste
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
    //Informe tous les observateurs de la liste des modifications des mesures meteo en appelant leurs methodes actualiser
    public void notifierObservateurs() {
        for (Observateur observer : this.observateurs) {
            //A ENELVER JE SAIS PAS PIRUQUOI YA CA
            try {
                observer.actualiser(this);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public String getVue() {
        return this.vue;
    }
    public void setVue(String vue) {
        this.vue = vue;
    }
}
