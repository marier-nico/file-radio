package vues;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
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
	private JFXButton btnCalibrerUn;

	@FXML
	private JFXButton btnCalibrerZeros;

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
	private JFXTextField textFieldMarge;

	@FXML
	private Label volumeUn;

	@FXML
	private Label volumeZeros;

	public static final String ADRESSE_VUE_RECEPTEUR = "/vues/Vue_Recepteur.fxml";
	final FileChooser fileChooser = new FileChooser();
	private File file;
	private FloatProperty dureeIntervalleRecep = new SimpleFloatProperty(1f);
	private FloatProperty tempsReception = new SimpleFloatProperty(1f);
	private FloatProperty margeAmplitude = new SimpleFloatProperty(1f);
	private AnimationProgressBar animProgress;
	private Thread threadEcoute;
	private EcouteurDeReception ecouteur;
	private boolean validSelect = false;
	private boolean validTFInterv = true;
	private boolean validTFTemps = true;
	private boolean validTFMarge = true;
	private boolean validCalibrerUn = false;
	private boolean validCalibrerZeros = false;

	/**
	 * Construit un controleurVueRecepteur en instanciant un EcouteurDeReception.
	 */
	public ControleurVueRecepteur() {
		try {
			ecouteur = new EcouteurDeReception();
		} catch (Exception e) {
			afficherErreur("écoute", "une erreur est survenue lors de l'écoute", e);
		}
	}

	/**
	 * Initialise la couleur du bouton Envoyer.
	 */
	public void initCouleurRecepteur() {
		Background b2 = new Background(new BackgroundFill(Color.web("#f85959"), CornerRadii.EMPTY, Insets.EMPTY));
		btnEcouter.setBackground(b2);
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
		validSelect = true;
		if (file == null) {
			validSelect = false;
		}
		actualiserValidation();
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
							ecouteur.ecouter((long) (tempsReception.get() * 1000));
							ecouteur.reconstruire(dureeIntervalleRecep.get() * 1000);
							PasserelleFichier.ecrireOctets(ecouteur.getReconstitueur().getRepresentationBinaire(),
									file);

							if (getExtensionFichier(file).contentEquals("text/plain")) {
								try {
									textFieldResultat.setText(PasserelleFichier.lireLignes(file).get(0));
								} catch (IOException e) {
									afficherErreurr("Lecture fichier", "Afficher contenu fichier", e);
								}
							} else {
								System.out.println("non");
								System.out.println(getExtensionFichier(file));
							}
						} catch (IllegalStateException | LineUnavailableException | InterruptedException | IOException
								| UnsupportedAudioFileException ex) {
							ex.printStackTrace();
						}
					}
				});
				threadEcoute.start();
				ajoutLabel(new Label("Écoute en cours..."), vboxMessages);
				animProgress = new AnimationProgressBar(progressBar, tempsReception.get() * 1000, 0.001);
			}
		}
	}
	
	public void afficherErreurr(String emplacement, String detail, Exception ex) {
		Alert erreur = new Alert(AlertType.ERROR);
		erreur.setHeaderText("Erreur dans " + emplacement);
		erreur.setContentText(detail + "\n\n" + ex.getStackTrace());
		erreur.setTitle("Erreur");
		erreur.showAndWait();
	}

	/**
	 * Permet d'obtenir l'extension d'un fichier.
	 * 
	 * @param file
	 * @return
	 */
	private static String getExtensionFichier(File file) {
		String retour = "Rien";
		if (file != null) {
			try {
				retour = Files.probeContentType(file.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return retour;
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
			try {
				ecouteur = new EcouteurDeReception();
			} catch (Exception e) {
				afficherErreur("écoute", "une erreur est survenue lors de l'écoute", e);
			}
		}
	}

	/**
	 * Écoute des "uns" en son pendant 0.5 secondes pour calibrer l'amplitude des
	 * "uns".
	 * 
	 * @param event
	 */
	@FXML
	void clickedCalibrerUns(ActionEvent event) {
		try {
			ecouteur.calibrerVolumeBit((byte) 1, margeAmplitude.get());
			ajoutLabel(new Label("Volume Un calibré"), vboxMessages);
			volumeUn.setText(ecouteur.getVolumeUn() + "");
		} catch (Exception e) {
			afficherErreur("calibration", e.getMessage(), e);
		}
		validCalibrerUn = true;
		actualiserValidation();
	}

	/**
	 * Écoute des "zéros" en son pendant 0.5 secondes pour calibrer l'amplitude des
	 * "Zéros".
	 * 
	 * @param event
	 */
	@FXML
	void clickedCalibrerZeros(ActionEvent event) {
		try {
			ecouteur.calibrerVolumeBit((byte) 0, margeAmplitude.get());
			ajoutLabel(new Label("Volume Zero calibré"), vboxMessages);
			volumeZeros.setText(ecouteur.getVolumeZero() + "");
		} catch (Exception e) {
			afficherErreur("calibration", e.getMessage(), e);
		}
		validCalibrerZeros = true;
		actualiserValidation();
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
					float valeur = Float.parseFloat(newValue);
					tempsReception.set(valeur);
					validTFTemps = true;
				} else {
					tempsReception.set(0);
					validTFTemps = false;
				}
				actualiserValidation();
			}
		});

		textFieldInterv.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (newValue.matches("-?\\d+(\\.\\d+)?")) {
					float valeur = Float.parseFloat(newValue);
					dureeIntervalleRecep.set(valeur);
					validTFInterv = true;
				} else {
					dureeIntervalleRecep.set(0);
					validTFInterv = false;
				}
				actualiserValidation();
			}
		});

		textFieldMarge.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (newValue.matches("-?\\d+(\\.\\d+)?")) {
					float valeur = Float.parseFloat(newValue);
					margeAmplitude.set(valeur);
					validTFMarge = true;
				} else {
					margeAmplitude.set(0);
					validTFMarge = false;
				}
				actualiserValidation();
			}
		});
	}

	/**
	 * Vérifi si tout les éléments ont été sélectionné avant l'écoute du fichier. Si
	 * c'est le cas, elle modifi la couleur de la pastille sur la vue pour indiquer
	 * que l'application est oppérationnel ou pas pour l'écoute.
	 */
	private void actualiserValidation() {
		Background b1 = new Background(new BackgroundFill(Color.web("#34a853"), CornerRadii.EMPTY, Insets.EMPTY));
		Background b2 = new Background(new BackgroundFill(Color.web("#f85959"), CornerRadii.EMPTY, Insets.EMPTY));
		if (validSelect && validTFInterv && validTFTemps && validCalibrerUn && validCalibrerZeros && validTFMarge) {
			btnEcouter.setBackground(b1);
		} else {
			btnEcouter.setBackground(b2);
		}
	}
}