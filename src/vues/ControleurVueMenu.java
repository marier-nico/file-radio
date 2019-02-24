package vues;

import controleurs.ApplicationRadio;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class ControleurVueMenu {

	@FXML
	private VBox vboxRoot;
	
	@FXML
    private Button btnEmettre;

    @FXML
    private Button btnReception;

	private ApplicationRadio application = null;
	public static final String ADRESSE_VUE_MENU = "/vues/Vue_Menu.fxml";

	public void setApplication(ApplicationRadio application) {
		this.application = application;
	}

	public VBox getVboxRoot() {
		return vboxRoot;
	}
	
	@FXML
    void clickedBtnEmettre(ActionEvent event) {
		try {
			application.showVueEmetteur();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    @FXML
    void clickedBtnReception(ActionEvent event) {
    	//TODO
    }

    //TODO ajout menu de sélection du theme
}
