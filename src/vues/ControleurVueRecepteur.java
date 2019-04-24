package vues;

import java.io.File;

import javax.sound.sampled.LineUnavailableException;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTextField;

import controleurs.ApplicationRadio;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import modeles.AnimationProgressBar;
import modeles.emetteur.LecteurSon;

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
	private JFXButton btnAnnuler;

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

	@FXML
	private JFXTextField textFieldTempsRecep;

	private ApplicationRadio application = null;
	public static final String ADRESSE_VUE_RECEPTEUR = "/vues/Vue_Recepteur.fxml";
	final DirectoryChooser directoryChooser = new DirectoryChooser();
	private File file;
	private int nbrMessage = 0;
	private FloatProperty dureeIntervalleRecep = new SimpleFloatProperty(0.0001f);
	private FloatProperty volumeUn = new SimpleFloatProperty(0);
	private FloatProperty volumeZeros = new SimpleFloatProperty(0);
	private LongProperty tempsReception = new SimpleLongProperty(0);
	private AnimationProgressBar animProgress;
	private Thread threadEcoute;

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
		if (file != null) {
			if ((threadEcoute != null) && (threadEcoute.isAlive())) {
				afficherErreur(AlertType.ERROR,
						"Un envoi est déjà en cours, veuillez l'anuler pour faire un autre envoi...");
			}
			if (tempsReception.get() == 0) {
				afficherErreur(AlertType.ERROR, "Il faut sélectionner un temps de réception avant d'enregistrer...");
			} else {
				threadEcoute = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							// TODO écouter son
						} catch (IllegalStateException ex) {
							ex.printStackTrace();
						}
					}
				});
				threadEcoute.start();
				ajoutLabel(new Label("Écoute en cours..."));
				animProgress = new AnimationProgressBar(progressBar, tempsReception.get(), 0.001);
			}
		}
	}

	@FXML
	void clickedBtnAnnuler(ActionEvent event) {
		if ((threadEcoute != null) && threadEcoute.isAlive()) {
			threadEcoute.stop();
			animProgress.stopProgressAnim();
			ajoutLabel(new Label("Arrêt de l'écoute..."));
			System.out.println("ok");
		}
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
		setEventTextField(textFieldVolumeUn, volumeUn);
		setEventTextField(textFieldVolumeZero, volumeZeros);
		
		textFieldTempsRecep.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (newValue.matches("-?\\d+(\\.\\d+)?")) {
					Long valeur = Long.parseLong(newValue);
					tempsReception.set(valeur);
				} else {
					tempsReception.set(0);
				}
			}
		});
	}

	private void setEventTextField(JFXTextField tf, FloatProperty val) {
		tf.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (newValue.matches("-?\\d+(\\.\\d+)?")) {
					float valeur = Float.parseFloat(newValue);
					val.set(valeur);
				} else {
					val.set(0);
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
		if ((volumeUn.get() > 0) && (volumeZeros.get() > 0) && (volumeUn.get() <= valMax)
				&& (volumeZeros.get() <= valMax)) {
			retour = true;
		}
		return retour;
	}

	private void ajoutLabel(Label l) {
		if (nbrMessage == 14) {
			vboxMessages.getChildren().remove(vboxMessages.getChildren().get(0));
			nbrMessage--;
		}
		vboxMessages.getChildren().add(l);
		nbrMessage++;
	}

	private void afficherErreur(AlertType ap, String message) {
		Alert erreur = new Alert(AlertType.ERROR);
		erreur.setHeaderText("Erreur");
		erreur.setContentText(message);
		erreur.setTitle("Erreur");
		erreur.showAndWait();
	}
}