package modele;

import views.Observateur;
import views.ViewsEnum;

import java.util.ArrayList;

public class Modele {
    private ArrayList<Observateur> observateurs;
    //La vue courante affichée à l'écran
    private ViewsEnum vue;
    private int tps;

    public Modele(){
        this.observateurs = new ArrayList<Observateur>();
        //la vue de base est le menu simulation
        this.vue = ViewsEnum.VueSimulationMain;
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
}
