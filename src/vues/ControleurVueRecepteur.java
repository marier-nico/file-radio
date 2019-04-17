package vues;

import java.io.File;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTextField;

import controleurs.ApplicationRadio;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
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
    private JFXButton btnArreter;
    
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
    
    @FXML
    private JFXTextField textFieldVolumeUn;

    @FXML
    private JFXTextField textFieldVolumeZero;


	private ApplicationRadio application = null;
	public static final String ADRESSE_VUE_RECEPTEUR = "/vues/Vue_Recepteur.fxml";
	final DirectoryChooser directoryChooser = new DirectoryChooser();
	private File file;
	private int nbrMessage = 0;
	private FloatProperty dureeIntervalleRecep = new SimpleFloatProperty(0.0001f);
	private DoubleProperty volumeUn = new SimpleDoubleProperty(0);
	private DoubleProperty volumeZeros = new SimpleDoubleProperty(0);

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
	
	@FXML
    void clickedBtnArreter(ActionEvent event) {
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
	
	public void bindTextView() {
		textFieldVolumeUn.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.matches("-?\\d+(\\.\\d+)?")) {
                	double val = Double.parseDouble(newValue);
                	volumeUn.set(val);
                }
            }
        });
		textFieldVolumeZero.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.matches("-?\\d+(\\.\\d+)?")) {
                	double val = Double.parseDouble(newValue);
                	volumeZeros.set(val);
                }
            }
        });
	}
	
	public String getEmplacementFichierSelct() {
		String retour = "Rien";
		if (file != null) {
			retour = file.getAbsolutePath();
		}
		return retour;
	}
	
	private boolean validerVolume(double valMax) {
		boolean retour = false;
		if ((volumeUn.get() > 0) && (volumeZeros.get() > 0) && (volumeUn.get() <= valMax) && (volumeZeros.get() <= valMax)) {
			retour = true;
		}
		return retour;
	}
}