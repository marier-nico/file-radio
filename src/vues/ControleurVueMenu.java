package vues;

import com.jfoenix.controls.JFXButton;

import controleurs.ApplicationRadio;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.layout.VBox;

/**
 * 
 * Cette classe permet est le controleur de la vue du menu. Elle creer une vue
 * menu permettant de sélectionner la vue émetteur ou récepteur. Il y a aussi
 * l'option de changer le thême des vues.
 * 
 * @author Charles-Antoine Demetriade
 *
 */
public class ControleurVueMenu {

	@FXML
	private RadioMenuItem darkNGreen;

	@FXML
	private RadioMenuItem powderBlue;

	@FXML
	private VBox vboxRoot;

	@FXML
	private JFXButton btnEmettre;

	@FXML
	private JFXButton btnReception;

	private String themeCourant = DARK_N_GREEN;
	private static final String DARK_N_GREEN = "/styles/DarkNGreen.css";
	private static final String POWDER_BLUE = "/styles/PowderBlue.css";
	private static final String NOWEL = "/styles/Nowel.css";
	private static final String HALLOWEEN = "/styles/Halloween.css";
	private static final String QUEBEC = "/styles/Qc.css";
	private ApplicationRadio application = null;
	public static final String ADRESSE_VUE_MENU = "/vues/Vue_Menu.fxml";

	public void setApplication(ApplicationRadio application) {
		this.application = application;
	}

	public VBox getVboxRoot() {
		return vboxRoot;
	}

	@FXML
	private void clickedBtnEmettre(ActionEvent event) {
		try {
			application.showVueEmetteur();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void clickedBtnReception(ActionEvent event) {
		try {
			application.showVueRecepteur();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void selectedPowderBlue(ActionEvent event) {
		if (((RadioMenuItem) event.getSource()).isSelected()) {
			darkNGreen.setSelected(false);
			powderBlue.setDisable(true);
			darkNGreen.setDisable(false);
			application.getSceneCourante().getStylesheets().setAll(this.getClass().getResource(POWDER_BLUE).toString());
			themeCourant = POWDER_BLUE;
		}
	}

	@FXML
	private void selectedDarkNGreen(ActionEvent event) {
		if (((RadioMenuItem) event.getSource()).isSelected()) {
			powderBlue.setSelected(false);
			powderBlue.setDisable(false);
			darkNGreen.setDisable(true);
			application.getSceneCourante().getStylesheets()
					.setAll(this.getClass().getResource(DARK_N_GREEN).toString());
			themeCourant = DARK_N_GREEN;
		}
	}

	public String getThemeCourant() {
		return themeCourant;
	}
}
