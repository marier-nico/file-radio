package vues;

import controleurs.ApplicationRadio;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;

public class ControleurVueEmetteur {
	
	@FXML
    private BorderPane borderPaneRoot;
	
	private ApplicationRadio application = null;
	public static final String ADRESSE_VUE_EMETTEUR = "/vues/Vue_Emetteur.fxml";

	public void setApplication(ApplicationRadio application) {
		this.application = application;
	}

	public BorderPane getBorderPaneRoot() {
		return borderPaneRoot;
	}
}
