package vues;

import java.io.File;

import controleurs.ApplicationRadio;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

public class ControleurVueRecepteur {
	
	@FXML
    private BorderPane borderPaneRoot;

    @FXML
    private Label freqLabel;

    @FXML
    private Label sliderLabel;

    @FXML
    private Slider slider;

    @FXML
    private Button btnSelectionner;

    @FXML
    private Button btnEnregistrer;

	private ApplicationRadio application = null;
	public static final String ADRESSE_VUE_RECEPTEUR = "/vues/Vue_Recepteur.fxml";
	final DirectoryChooser directoryChooser = new DirectoryChooser();
	private File file;

	public void setApplication(ApplicationRadio application) {
		this.application = application;
	}

	public BorderPane getBorderPaneRoot() {
		return borderPaneRoot;
	}

	@FXML
	private void clickedBtnEnregistrer(ActionEvent event) {
		//TODO
		System.out.println("Enregistrer!!");
		System.out.println(getEmplacementFichierSelct());
	}

	@FXML
	private void clickedBtnSelect(ActionEvent event) {
		System.out.println("Sélectionner...");
		directoryChooser.setTitle("Veiller sélectionner un emplacement de destination");
		file = directoryChooser.showDialog(application.getStage());
	}
	
	public void bindSlider() {
		slider.valueProperty().addListener((ov, old_val, new_val) -> {
		    freqLabel.textProperty().bind(slider.valueProperty().asString());
		    sliderLabel.textProperty().bind(freqLabel.textProperty());
		});
	}
	
	public String getEmplacementFichierSelct() {
		String retour = "rien";
		if (file != null) {
			retour = file.getAbsolutePath();
		}
		return retour;
	}
}
