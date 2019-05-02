package vues;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;

import controleurs.ApplicationRadio;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.LongProperty;
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
import javafx.stage.FileChooser;
import modeles.AnimationProgressBar;
import modeles.passerelle.PasserelleFichier;
import modeles.recepteur.EcouteurDeReception;

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
	private JFXButton btnEcouter;

	@FXML
	private JFXButton btnAnnuler;

	@FXML
	private JFXButton btnCalibrer;

	@FXML
	private VBox vboxMessages;

	@FXML
	private Label labelProgress;

	@FXML
	private ProgressBar progressBar;

	@FXML
	private HBox hboxProgressBar;

	@FXML
	private JFXTextField textFieldTempsRecep;

	@FXML
	private JFXTextField textFieldInterv;

	private ApplicationRadio application = null;
	public static final String ADRESSE_VUE_RECEPTEUR = "/vues/Vue_Recepteur.fxml";
	final FileChooser fileChooser = new FileChooser();
	private File file;
	private int nbrMessage = 0;
	private FloatProperty dureeIntervalleRecep = new SimpleFloatProperty(1f);
	private LongProperty tempsReception = new SimpleLongProperty(1);
	private AnimationProgressBar animProgress;
	private Thread threadEcoute;
	private EcouteurDeReception ecouteur;
	
	public ControleurVueRecepteur() {
		try {
			ecouteur  = new EcouteurDeReception();
		} catch (Exception e) {
		afficherErreur("écoute", "une erreur est survenue lors de l'écoute", e);
		}
	}

	public void setApplication(ApplicationRadio application) {
		this.application = application;
	}

	public BorderPane getBorderPaneRoot() {
		return borderPaneRoot;
	}

	@FXML
	private void clickedBtnSelect(ActionEvent event) {
		fileChooser.setTitle("Veuiller sélectionner un emplacement de destination");
		file = fileChooser.showSaveDialog(application.getStage());
		labelProgress.setText(getEmplacementFichierSelct());
	}

	@FXML
	void clickedBtnEcouter(ActionEvent event) {
		if (file != null) {
			if ((threadEcoute != null) && (threadEcoute.isAlive())) {
				afficherErreur("écoute", "un message est déjà en écoute");
			}
			if (tempsReception.get() == 0) {
				afficherErreur("manque de données", "il faut sélectionner un tems de reception anvant d'enregistrer");
			} else {
				threadEcoute = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							// plante après reécouter...
							ecouteur.ecouter(tempsReception.get() * 1000);
							ecouteur.reconstruire(dureeIntervalleRecep.get() * 1000);
							PasserelleFichier.ecrireOctets(ecouteur.getReconstitueur().getRepresentationBinaire(), file);
						} catch (IllegalStateException | LineUnavailableException | InterruptedException | IOException | UnsupportedAudioFileException ex) {
							ex.printStackTrace();
						}
					}
				});
				threadEcoute.start();
				ajoutLabel(new Label("Écoute en cours..."));
				animProgress = new AnimationProgressBar(progressBar, tempsReception.get() * 1000, 0.001);
			}
		}
	}

	@FXML
	void clickedBtnAnnuler(ActionEvent event) {
		if ((threadEcoute != null) && threadEcoute.isAlive()) {
			ecouteur.arretEcoute();
			threadEcoute.stop();
			animProgress.stopProgressAnim();
			ajoutLabel(new Label("Arrêt de l'écoute..."));
		}
	}

	@FXML
	void clickedCalibrer(ActionEvent event) {
		try {
			ecouteur.calibrer(3);
		} catch (Exception e) {
			afficherErreur("calibration", e.getMessage(), e);
		}
	}

	/**
	 * Cette méthode permet de créer un "hgrow" à la progressBar.
	 */
	public void bindProgressBar() {
		progressBar.prefWidthProperty().bind(hboxProgressBar.widthProperty());
	}

	public void bindTextView() {
		textFieldTempsRecep.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (newValue.matches("-?\\d+(\\.\\d+)?")) {
					long valeur = Long.parseLong(newValue);
					tempsReception.set(valeur);
				} else {
					tempsReception.set(0);
				}
			}
		});

		textFieldInterv.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (newValue.matches("-?\\d+(\\.\\d+)?")) {
					float valeur = Float.parseFloat(newValue);
					dureeIntervalleRecep.set(valeur);
				} else {
					dureeIntervalleRecep.set(0);
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

	private void ajoutLabel(Label l) {
		if (nbrMessage == 14) {
			vboxMessages.getChildren().remove(vboxMessages.getChildren().get(0));
			nbrMessage--;
		}
		vboxMessages.getChildren().add(l);
		nbrMessage++;
	}

	private void afficherErreur(String emplacement, String detail, Exception ex) {
		Alert erreur = new Alert(AlertType.ERROR);
		erreur.setHeaderText("Erreur dans " + emplacement);
		erreur.setContentText(detail + "\n\n" + ex.getStackTrace().toString());
		ex.printStackTrace();
		erreur.setTitle("Erreur");
		erreur.showAndWait();
	}
	
	private void afficherErreur(String emplacement, String detail) {
		Alert erreur = new Alert(AlertType.ERROR);
		erreur.setHeaderText("Erreur dans " + emplacement);
		erreur.setContentText(detail);
		erreur.setTitle("Erreur");
		erreur.showAndWait();
	}
}