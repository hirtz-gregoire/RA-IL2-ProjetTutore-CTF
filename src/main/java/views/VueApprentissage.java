package views;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Callback;

/**
 * Une Vue du MVC - correspond au JLabel contenant la valeur du compteur
 */
public class VueApprentissage extends StackPane implements Observateur {

	/**
	 * Constructeur - positionne la valeur 0 au centre du JLabel
	 */
	public VueApprentissage() {
		super();
	}

	/**
	 * Actualisation du Label avec la nouvelle valeur du compteur obtenue grace au modele mod
	 * Methode lancee a chaque modification du modele
	 */
	@Override
	public void actualiser(Modele modele) {
		this.getChildren().clear();
		//on n'utilise la vue que si la vue est en liste
		if (modele.getVue().equals("apprentissage")) {

			TreeItem<Tache> projet = modele.createArbre(modele.getProjet());

			TreeView treeView = new TreeView<Tache>(projet);
			treeView.setMaxWidth(400);

			treeView.setCellFactory(new Callback<TreeView<Tache>, TreeCell<Tache>>() {
				@Override
				public TreeCell call(TreeView treeView) {

					TreeCell<Tache> cell = new TreeCell<Tache>() {
						@Override
						protected void updateItem(Tache tache, boolean empty) {
							// une TreeCell peut changer de tâche, donc changer de TreeItem
							super.updateItem(tache, empty);
							if (tache != null) {
								String type = "Tâche : ";
								if (tache.estProjet()) {
									type = "Projet : ";
								}
								else if (tache.estArchive()) {
									type = "Archive : ";
								}
								else if (tache.estListe()) {
									type = "Liste : ";
								}
								setText(type + tache.getTitre());
								setBackground(new Background(new BackgroundFill(tache.getColor(), CornerRadii.EMPTY, Insets.EMPTY)));
								setFont(new Font("Arial", 20));
								setPrefHeight(40);
							}
							else {
								setText("");
								setStyle("");
								setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
								setFont(new Font("Arial", 20));
							}
						}
					};
					cell.setOnMouseClicked(new EventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent mouseEvent) {

							//On calcule le nombre de cellules à afficher afin de définir la hauteur du treeView
							// On initialise à 1, car on compte la cellule représentant le projet
							int nbCells = 1;

							for (TreeItem<Tache> t : (ObservableList<TreeItem<Tache>>) treeView.getRoot().getChildren()) {
								nbCells += getNbCellulesVisible(t);
							}

							treeView.setPrefHeight(nbCells * 40);

							if (mouseEvent.getClickCount() == 2) {
								TreeCell<Tache> cellClick = (TreeCell) mouseEvent.getSource();

								// On teste si la cellule cliquée est nulle avant de tester si la cellule cliquée est une tâche
								if (cellClick.getItem() != null) {
									if (cellClick.getItem().estTache()) {
										modele.setTacheCourante(cellClick.getItem());
										modele.setVue("description");
										modele.notifierObservateurs();
										modele.setVue("liste");
										modele.notifierObservateurs();
									}
								}
							}
						}
					});
					return cell;
				}
			});
			this.getChildren().add(treeView);
		}
	}

	/**
	 * Méthode récursive pour calculer le nombre de cellules affiché à l'écran
	 * @param item la cellule parente
	 * @return le nombre de cellules affichées
	 */
	private int getNbCellulesVisible(TreeItem<Tache> item) {
		int count = 1; // On initialise à, car on compte la cellule parente
		if (item.isExpanded()) {
			for (TreeItem<Tache> tacheTreeItem : item.getChildren()) {
				count += getNbCellulesVisible(tacheTreeItem);
			}
		}
		return count;
	}
}