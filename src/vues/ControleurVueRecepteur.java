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
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
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
	private FloatProperty dureeIntervalleBit = new SimpleFloatProperty(1f);
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
		btnEcouter.setBackground(BG_ROUGE);
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
		if (validSelect) {
			if (validTFInterv && validTFMarge && validTFTemps) {
				if (validCalibrerUn && validCalibrerZeros) {
					if ((threadEcoute != null) && (threadEcoute.isAlive())) {
						afficherErreur("l'écoute", "Un message est déjà en écoute...");
					} else {
						threadEcoute = new Thread(new Runnable() {
							@Override
							public void run() {
								try {
									ecouteur.ecouter((long) (tempsReception.get() * 1000));
									ecouteur.reconstruire(dureeIntervalleBit.get() * 1000);
									PasserelleFichier.ecrireOctets(ecouteur.getReconstitueur().getRepresentationBinaire(),
											file);

									if (getExtensionFichier(file).contentEquals("text/plain")) {
										try {
											textFieldResultat.setText(PasserelleFichier.lireLignes(file).get(0));
										} catch (IOException e) {
											System.out.println(
													"Erreur lors de la lecture d'un fichier et l'afficahge du contenu...");
											e.printStackTrace();
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
				} else {
					afficherErreur("l'écoute (Calibration)", "Il faut calibrer les uns et les zéros avant d'écouter...");
				}
			} else {
				afficherErreur("l'écoute (Valeurs entrées)", "Il faut entrer des valeurs valides avant d'écouter...");
			}
		} else {
			afficherErreur("l'écoute (Fichier manquant)", "Il faut d'abord sélectionner un fichier avant d'écouter...");
		}
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

		// Il aurait été mieux de faire une méthode pour faire les tâche répétitives des
		// binds, sauf que mes variables booléennes ne sont et ne peuvent pas être final -> local
		// variable defined in an
		// enclosing scope must be final or effectively final...
		
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
				if (tempsReception.get() == 0) {
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
					dureeIntervalleBit.set(valeur);
					validTFInterv = true;
				} else {
					dureeIntervalleBit.set(0);
					validTFInterv = false;
				}
				if (dureeIntervalleBit.get() == 0) {
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
				if (margeAmplitude.get() == 0) {
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
		if (validSelect && validTFInterv && validTFTemps && validCalibrerUn && validCalibrerZeros && validTFMarge) {
			btnEcouter.setBackground(BG_VERT);
		} else {
			btnEcouter.setBackground(BG_ROUGE);
		}
	}
}