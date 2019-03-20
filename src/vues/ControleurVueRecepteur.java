package vues;

import java.io.File;

import controleurs.ApplicationRadio;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;

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
    
    @FXML
    private VBox vboxMessages;
    
    @FXML
    private Label labelProgress;

	private ApplicationRadio application = null;
	public static final String ADRESSE_VUE_RECEPTEUR = "/vues/Vue_Recepteur.fxml";
	final DirectoryChooser directoryChooser = new DirectoryChooser();
	private File file;
	private int nbrMessage = 0;

	public void setApplication(ApplicationRadio application) {
		this.application = application;
	}

	public BorderPane getBorderPaneRoot() {
		return borderPaneRoot;
	}

	@FXML
	private void clickedBtnEnregistrer(ActionEvent event) {
		Label l = new Label("Message enregistré dans " + getEmplacementFichierSelct() + " !");
		if (nbrMessage == 12) {
			vboxMessages.getChildren().remove(vboxMessages.getChildren().get(0));
			nbrMessage--;
		}
		vboxMessages.getChildren().add(l);
		nbrMessage++;
	}

	@FXML
	private void clickedBtnSelect(ActionEvent event) {
		directoryChooser.setTitle("Veiller sélectionner un emplacement de destination");
		file = directoryChooser.showDialog(application.getStage());
		labelProgress.setText(getEmplacementFichierSelct());
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
