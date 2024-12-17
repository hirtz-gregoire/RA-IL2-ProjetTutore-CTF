package display.views;

import display.modele.Modele;

/**
 * Interface pour les differents observateurs de MVC.Sujet
 */
public interface Observateur {
	public void actualiser(Modele modele) throws Exception;
}
