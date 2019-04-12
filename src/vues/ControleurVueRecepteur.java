package vues;

import java.io.File;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;

import controleurs.ApplicationRadio;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;

/**
 * 
 * Cette classe permet est le controleur de la vue du récepteur. Elle permet de
 * creer une vue et de gérer les évenements.
 * 
 * @author Charles-Antoine Demetriade
 *
 */
public class ControleurVueRecepteur {
	
	@FXML
    private BorderPane borderPaneRoot;
	
    @FXML
    private JFXButton btnSelectionner;

    @FXML
    private JFXButton btnEnregistrer;
    
    @FXML
    private JFXButton btnEcouter;
    
    @FXML
    private VBox vboxMessages;
    
    @FXML
    private Label labelProgress;
    
    @FXML
    private JFXSlider slider;
    
    @FXML
    private Label labelSlider;
    
    @FXML
    private ProgressBar progressBar;
    
    @FXML
    private HBox hboxProgressBar;


	private ApplicationRadio application = null;
	public static final String ADRESSE_VUE_RECEPTEUR = "/vues/Vue_Recepteur.fxml";
	final DirectoryChooser directoryChooser = new DirectoryChooser();
	private File file;
	private int nbrMessage = 0;
	private FloatProperty dureeIntervalleRecep = new SimpleFloatProperty(0.0001f);

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
		directoryChooser.setTitle("Veuiller sélectionner un emplacement de destination");
		file = directoryChooser.showDialog(application.getStage());
		labelProgress.setText(getEmplacementFichierSelct());
	}
	
	@FXML
    void clickedBtnEcouter(ActionEvent event) {
		//TODO
    }
	
	public void bindSliderEtLabel() {
		slider.valueProperty().addListener((ov, old_val, new_val) -> {
			dureeIntervalleRecep.bind(slider.valueProperty());
			labelSlider.textProperty().bind(slider.valueProperty().asString());
		});
	}
	
	/**
	 * Cette méthode permet de créer un "hgrow" à la progressBar.
	 */
	public void bindProgressBar() {
		progressBar.prefWidthProperty().bind(hboxProgressBar.widthProperty());
	}
	
	public String getEmplacementFichierSelct() {
		String retour = "Rien";
		if (file != null) {
			retour = file.getAbsolutePath();
		}
		return retour;
	}
}