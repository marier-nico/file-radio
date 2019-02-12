package vues;

import controleurs.ApplicationRadio;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

public class ControleurVueEmetteur {

	@FXML
	private BorderPane borderPaneRoot;

	@FXML
	private Button btnSelectionner;

	@FXML
	private Button btnEnvoyer;

	private ApplicationRadio application = null;
	public static final String ADRESSE_VUE_EMETTEUR = "/vues/Vue_Emetteur.fxml";

	public void setApplication(ApplicationRadio application) {
		this.application = application;
	}

	public BorderPane getBorderPaneRoot() {
		return borderPaneRoot;
	}

	@FXML
	void clickedBtnEnvoyer(ActionEvent event) {
		//TODO
	}

	@FXML
	void clickedBtnSelect(ActionEvent event) {
		//TODO
	}
}
