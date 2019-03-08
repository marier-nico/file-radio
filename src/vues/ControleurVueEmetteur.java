package vues;

import java.io.File;

import controleurs.ApplicationRadio;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

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

	@FXML
	private Label labelProgress;

	@FXML
	private VBox vboxMessages;

	private ApplicationRadio application = null;
	public static final String ADRESSE_VUE_EMETTEUR = "/vues/Vue_Emetteur.fxml";
	private final FileChooser fileChooser = new FileChooser();
	private File file;
	private int nbrMessage = 0;

	public void setApplication(ApplicationRadio application) {
		this.application = application;
	}

	public BorderPane getBorderPaneRoot() {
		return borderPaneRoot;
	}

	@FXML
	private void clickedBtnEnvoyer(ActionEvent event) {
		Label l = new Label(getEmplacementFichierSelct() + " a été envoyé!");
		if (nbrMessage == 12) {
			vboxMessages.getChildren().remove(vboxMessages.getChildren().get(0));
			nbrMessage--;
		}
		vboxMessages.getChildren().add(l);
		nbrMessage++;
	}

	@FXML
	private void clickedBtnSelect(ActionEvent event) {
		fileChooser.setTitle("Veiller sélectionner un fichier");
		file = fileChooser.showOpenDialog(application.getStage());
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
