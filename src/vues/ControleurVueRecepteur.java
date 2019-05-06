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
 * Cette classe est le controleur de la vue du récepteur. Elle permet de creer
 * une vue et de gérer les évenements.
 * 
 * @author Charles-Antoine Demetriade
 *
 */
public class ControleurVueRecepteur extends Vue {

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

	@FXML
	private JFXTextField textFieldResultat;

	@FXML
	private Label volumeUn;

	@FXML
	private Label volumeZeros;

	public static final String ADRESSE_VUE_RECEPTEUR = "/vues/Vue_Recepteur.fxml";
	final FileChooser fileChooser = new FileChooser();
	private File file;
	private FloatProperty dureeIntervalleRecep = new SimpleFloatProperty(1f);
	private LongProperty tempsReception = new SimpleLongProperty(1);
	private AnimationProgressBar animProgress;
	private Thread threadEcoute;
	private EcouteurDeReception ecouteur;

	/**
	 * Construit un controleurVueRecepteur en instanciant un EcouteurDeReception.
	 */
	public ControleurVueRecepteur() {
		try {
			// TODO transférer cette instanciation dans le clicked btn écouter
			ecouteur = new EcouteurDeReception();
		} catch (Exception e) {
			afficherErreur("écoute", "une erreur est survenue lors de l'écoute", e);
		}
	}

	/**
	 * Permet d'obtenir le borderPaneRoot de la vue.
	 * 
	 * @return borderPaneRoot
	 */
	public BorderPane getBorderPaneRoot() {
		return borderPaneRoot;
	}

	/**
	 * Gère l'événement du bouton sélectionner. Permet de sélectionner un
	 * emplacement pour enregistrer le fichier.
	 * 
	 * @param event
	 */
	@FXML
	private void clickedBtnSelect(ActionEvent event) {
		fileChooser.setTitle("Veuiller sélectionner un emplacement de destination");
		file = fileChooser.showSaveDialog(getApplication().getStage());
		labelProgress.setText(getEmplacementFichierSelct(file));
	}

	/**
	 * Gère l'événement du bouton écouter. Permet de lancer l'écoute du fichier.
	 * 
	 * @param event
	 */
	@FXML
	private void clickedBtnEcouter(ActionEvent event) {
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
							PasserelleFichier.ecrireOctets(ecouteur.getReconstitueur().getRepresentationBinaire(),
									file);
						} catch (IllegalStateException | LineUnavailableException | InterruptedException | IOException
								| UnsupportedAudioFileException ex) {
							ex.printStackTrace();
						}
					}
				});
				threadEcoute.start();
				ajoutLabel(new Label("Écoute en cours..."), vboxMessages);
				animProgress = new AnimationProgressBar(progressBar, tempsReception.get() * 1000, 0.001);
				// TODO Afficher message recue dans textFieldResultat
			}
		}
	}

	/**
	 * Gère l'événement du bouton annuler. Permet d'annuler l'écoute du fichier.
	 * 
	 * @param event
	 */
	@FXML
	private void clickedBtnAnnuler(ActionEvent event) {
		if ((threadEcoute != null) && threadEcoute.isAlive()) {
			ecouteur.arretEcoute();
			threadEcoute.stop();
			animProgress.stopProgressAnim();
			ajoutLabel(new Label("Arrêt de l'écoute..."), vboxMessages);
		}
	}

	/**
	 * Gère l'événement du bouton calibrer.
	 * 
	 * @param event
	 */
	@FXML
	private void clickedCalibrer(ActionEvent event) {
		try {
			ecouteur.calibrer(3);
			ajoutLabel(new Label("Calibration en cours..."), vboxMessages);
			// TODO modifier label volume
			// volumeUn.setText();
			// volumeZeros.setText();
		} catch (Exception e) {
			afficherErreur("calibration", e.getMessage(), e);
		}
	}

	/**
	 * Permet de bind les TextField en filtrant ce qu'ils recoivent et bind la
	 * progressBar.
	 */
	public void bindTextFieldEtProgress() {
		bindProgressBar(progressBar, hboxProgressBar);

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
}