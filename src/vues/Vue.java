package vues;

import java.io.File;


import controleurs.ApplicationRadio;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;

/**
 * Cette classe sert à donner un type et à regrouper les méthodes des vues.
 * 
 * @author Charles-Antoine Demetriade
 *
 */
public class Vue {

	private ApplicationRadio application = null;
	private int nbrMessage = 0;
	public static final Background BG_VERT = new Background(new BackgroundFill(Color.web("#34a853"), CornerRadii.EMPTY, Insets.EMPTY));
	public static final Background BG_ROUGE = new Background(new BackgroundFill(Color.web("#f85959"), CornerRadii.EMPTY, Insets.EMPTY));

	/**
	 * Permet de sélectionner l'application radio.
	 * 
	 * @param application
	 */
	public void setApplication(ApplicationRadio application) {
		this.application = application;
	}

	/**
	 * Permet d'obtenir l'applicationRadio.
	 * 
	 * @return
	 */
	public ApplicationRadio getApplication() {
		return application;
	}

	/**
	 * Permet d'afficher un label dans un vBox.
	 * 
	 * @param l
	 * @param vboxMessages
	 */
	public void ajoutLabel(Label l, VBox vboxMessages) {
		if (nbrMessage == 14) {
			vboxMessages.getChildren().remove(vboxMessages.getChildren().get(0));
			nbrMessage--;
		}
		vboxMessages.getChildren().add(l);
		nbrMessage++;
	}

	/**
	 * Permet de simuler un "hgrow" à la progressBar.
	 */
	public void bindProgressBar(ProgressBar pg, HBox hbox) {
		pg.prefWidthProperty().bind(hbox.widthProperty());
	}

	/**
	 * Permet d'obtenir l'emplacement du fichier sélectionné.
	 * 
	 * @param file
	 * @return
	 */
	public String getEmplacementFichierSelct(File file) {
		String retour = "Rien...";
		if (file != null) {
			retour = file.getAbsolutePath();
		}
		return retour;
	}

	/**
	 * Permet d'afficher une erreur.
	 * 
	 * @param emplacement
	 * @param detail
	 * @param ex
	 */
	public void afficherErreur(String emplacement, String detail, Exception ex) {
		Alert erreur = new Alert(AlertType.ERROR);
		erreur.setHeaderText("Erreur dans " + emplacement);
		erreur.setContentText(detail + "\n\n" + ex.getStackTrace());
		erreur.setTitle("Erreur");
		erreur.showAndWait();
	}

	/**
	 * Permet d'afficher une erreur.
	 * 
	 * @param emplacement
	 * @param detail
	 */
	public void afficherErreur(String emplacement, String detail) {
		Alert erreur = new Alert(AlertType.WARNING);
		erreur.setHeaderText("Erreur dans " + emplacement);
		erreur.setContentText(detail);
		erreur.setTitle("Erreur");
		erreur.showAndWait();
	}
}
