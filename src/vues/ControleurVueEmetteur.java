package vues;

import controleurs.ApplicationRadio;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;

public class ControleurVueEmetteur {

	@FXML
	private BorderPane borderPaneRoot;

	@FXML
	private Button btnSelectionner;

	@FXML
	private Button btnEnvoyer;
	
	@FXML
    private Slider slider;
	
	@FXML
    private Label freqLabel;
	
	@FXML
    private Label sliderLabel;


	private ApplicationRadio application = null;
	public static final String ADRESSE_VUE_EMETTEUR = "/vues/Vue_Emetteur.fxml";

	public void setApplication(ApplicationRadio application) {
		this.application = application;
	}

	public BorderPane getBorderPaneRoot() {
		return borderPaneRoot;
	}

	@FXML
	private void clickedBtnEnvoyer(ActionEvent event) {
		//TODO
		System.out.println("Envoyer!!");
	}

	@FXML
	private void clickedBtnSelect(ActionEvent event) {
		//TODO
		System.out.println("Sélectionner...");
	}
	
	public void bindSlider() {
		slider.valueProperty().addListener((ov, old_val, new_val) -> {
		    freqLabel.textProperty().bind(slider.valueProperty().asString());
		    sliderLabel.textProperty().bind(freqLabel.textProperty());
		});
	}
}
