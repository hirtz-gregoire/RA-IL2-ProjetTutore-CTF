package display.views;

import display.modele.ModeleMVC;

/**
 * Interface pour les differents observateurs de MVC.Sujet
 */
public interface Observateur {
	public void actualiser(ModeleMVC modeleMVC) throws Exception;
}
