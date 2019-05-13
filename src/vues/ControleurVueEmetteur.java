package vues;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.sound.sampled.LineUnavailableException;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;

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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import modeles.AnimationProgressBar;
import modeles.RepresentationBinaire;
import modeles.emetteur.Calibreur;
import modeles.emetteur.GenerateurSon;
import modeles.emetteur.LecteurSon;
import modeles.passerelle.PasserelleFichier;

/**
 * Cette classe est le controleur de la vue de l'emetteur. Elle permet de creer
 * une vue et de gérer les évenements.
 * 
 * @author Charles-Antoine Demetriade
 *
 */
public class ControleurVueEmetteur extends Vue {

	@FXML
	private BorderPane borderPaneRoot;

	@FXML
	private JFXButton btnSelectionner;

	@FXML
	private JFXButton btnEnvoyer;

	@FXML
	private JFXButton btnAnnuler;

	@FXML
	private JFXButton btnEnregistrer;

	@FXML
	private JFXButton btnCalibrerUn;

	@FXML
	private JFXButton btnCalibrerZeros;

	@FXML
	private JFXTextField textFieldTempsUnBit;

	@FXML
	private Label labelProgress;

	@FXML
	private Label labelVitesseFichier;

	@FXML
	private Label labelTempsEstim;

	@FXML
	private VBox vboxMessages;

	@FXML
	private Pane paneAnimation;

	@FXML
	private ProgressBar progressBar;

	@FXML
	private HBox hboxProgressBar;

	@FXML
	private JFXTextArea textAreaMessage;

	public static final String ADRESSE_VUE_EMETTEUR = "/vues/Vue_Emetteur.fxml";
	private final FileChooser fileChooserSelect = new FileChooser();
	private File fileSelect;
	private FileChooser fileChooserEnreg = new FileChooser();
	private File fileEnreg;
	private GenerateurSon generateurSon;
	private LecteurSon lecteurSon;
	private FloatProperty dureeIntervalleBit = new SimpleFloatProperty(1f);
	private DoubleProperty tempsEstim;
	private Thread threadSon;
	private AnimationProgressBar animProgressBar;
	private boolean validSelect = false;
	private boolean validEnreg = false;
	private boolean validTextField = true;

	/**
	 * Initialise la couleur du bouton Envoyer.
	 */
	public void initCouleurEmetteur() {
		btnEnvoyer.setBackground(BG_ROUGE);
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
	 * Gère l'événement du bouton envoyer. Permet d'envoyer un fichier.
	 * 
	 * @param event
	 */
	@FXML
	private void clickedBtnEnvoyer(ActionEvent event) {
		if ((validEnreg || validSelect) && validTextField) {
			File fileTemp;
			if (validEnreg) {
				fileTemp = fileEnreg;
			} else {
				fileTemp = fileSelect;
			}

			if ((threadSon != null) && (threadSon.isAlive())) {
				Alert erreur = new Alert(AlertType.ERROR);
				erreur.setHeaderText("Erreur");
				erreur.setContentText("Un envoi est déjà en cours, veuillez l'anuler pour faire un autre envoi...");
				erreur.setTitle("Erreur");
				erreur.showAndWait();
			} else {
				byte[] octetsFichier = null;
				try {
					octetsFichier = PasserelleFichier.lireOctets(fileTemp);
				} catch (IOException ex) {
					afficherErreur("la lecture du fichier",
							"Le fichier n'a pas pu être lu. Une erreur s'est produite pendant son ouverture. "
									+ "Il est possible que le fichier soit ouvert dans un autre programme.",
							ex);
					return;
				}
				RepresentationBinaire repr = new RepresentationBinaire(octetsFichier);
				generateurSon = new GenerateurSon(repr, dureeIntervalleBit.get());
				byte[][] donnees = generateurSon.getDonneesSon();
				try {
					lecteurSon = new LecteurSon(donnees, dureeIntervalleBit.get());
					threadSon = new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								lecteurSon.lireSons();
							} catch (IllegalStateException ex) {
								afficherErreur("la lecture du son", "Un autre son est déjà en lecture.", ex);
							} catch (LineUnavailableException ex) {
								afficherErreur("la lecture du son",
										"Le son n'a pas pu être lu, car la sortie audio est indisponible. "
												+ "Tentez de libérer la sortie audio de votre système.",
										ex);
							}
						}
					});
					threadSon.start();
					animProgressBar = new AnimationProgressBar(progressBar, dureeIntervalleBit.get(),
							octetsFichier.length * 8);
				} catch (LineUnavailableException ex) {
					afficherErreur("la lecture du son",
							"Le son n'a pas pu être lu, car la sortie audio est indisponible. "
									+ "Tentez de libérer la sortie audio de votre système.",
							ex);
				} catch (IllegalStateException ex) {
					afficherErreur("la lecture du son", "Un autre son est déjà en lecture.", ex);
				}
				ajoutLabel(new Label("Envoi en cours de " + getEmplacementFichierSelct(fileTemp) + "..."),
						vboxMessages);
			}
		}
	}

	/**
	 * Gère l'événement du bouton sélectionner. Permet de sélectionner un fichier à
	 * envoyer.
	 * 
	 * @param event
	 */
	@FXML
	private void clickedBtnSelect(ActionEvent event) {
		fileChooserSelect.setTitle("Veuiller sélectionner un fichier");
		fileSelect = fileChooserSelect.showOpenDialog(getApplication().getStage());
		labelProgress.setText(getEmplacementFichierSelct(fileSelect));
		validSelect = true;
		validEnreg = false;
		if (fileSelect == null) {
			validSelect = false;
		}
		actualiserValidation();
		bindTextFieldEtProgress();
		updateTextField();
	}

	/**
	 * Envoi des "uns" en son pendant 2 secondes pour synchroniser les vues.
	 * 
	 * @param event
	 */
	@FXML
	private void clickedCalibrerUns(ActionEvent event) {
		try {
			Calibreur.calibrerUns();
			ajoutLabel(new Label("Volume Un calibré"), vboxMessages);
		} catch (IOException | LineUnavailableException e) {
			afficherErreur("calibration", "fichier introuvable ou micro non disponible", e);
		}
	}

	/**
	 * Envoi des "zéros" en son pendant 2 secondes pour synchroniser les vues.
	 * 
	 * @param event
	 */
	@FXML
	private void clickedCalibrerZeros(ActionEvent event) {
		try {
			Calibreur.calibrerZeros();
			ajoutLabel(new Label("Volume Zero calibré"), vboxMessages);
		} catch (IOException | LineUnavailableException e) {
			afficherErreur("calibration", "fichier introuvable ou micro non disponible", e);
		}
	}

	/**
	 * Permeet d'enregistrer un fichier texte depuis directement la vue emetteur.
	 * 
	 * @param event
	 */
	@FXML
	private void clickedEnregistrer(ActionEvent event) {
		fileChooserEnreg.setTitle("Veuiller sélectionner un emplacement de destination");
		fileEnreg = fileChooserEnreg.showSaveDialog(getApplication().getStage());
		labelProgress.setText(getEmplacementFichierSelct(fileEnreg));

		PrintWriter writer;
		try {
			writer = new PrintWriter(getEmplacementFichierSelct(fileEnreg), "UTF-8");
			writer.print(textAreaMessage.getText());
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			afficherErreur("enregistrer fichier", "impossible de creer le fichier", e);
		}
		validEnreg = true;
		validSelect = false;
		if (fileEnreg == null) {
			validEnreg = false;
		}
		actualiserValidation();
		ajoutLabel(new Label(getEmplacementFichierSelct(fileEnreg) + " est prêt pour l'envoi"), vboxMessages);
		bindTextFieldEtProgress();
		updateTextField();
	}

	/**
	 * Permet d'actualiser le temps d'envoi.
	 */
	private void updateTextField() {
		float uptdate = dureeIntervalleBit.get();
		textFieldTempsUnBit.setText((dureeIntervalleBit.get() - uptdate) + "");
		textFieldTempsUnBit.setText((dureeIntervalleBit.get() + uptdate) + "");
	}

	/**
	 * Cette méthode bind la progressbar au bordure pour simuler un hgrow.
	 */
	public void bindProgressBar() {
		bindProgressBar(progressBar, hboxProgressBar);
	}
	
	/**
	 * Cette méthode bind le textField avec tous les labels affichant des
	 * informations en lien avec celui-ci et la progressbar.
	 */
	private void bindTextFieldEtProgress() {

		textFieldTempsUnBit.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (newValue.matches("-?\\d+(\\.\\d+)?")) {
					float valeur = Float.parseFloat(newValue);
					dureeIntervalleBit.set(valeur);
					DoubleProperty vitFich = new SimpleDoubleProperty(Math.round(valeur * 8));
					// TODO lui faire afficher la bonne chose
					labelVitesseFichier.textProperty().bind(vitFich.asString());
					if (validSelect) {
						validTextField = true;
						try {
							tempsEstim = new SimpleDoubleProperty(Math.round(
									dureeIntervalleBit.get() * (PasserelleFichier.lireOctets(fileSelect).length) * 8));
							labelTempsEstim.textProperty().bind(tempsEstim.asString());
						} catch (IOException e) {
							e.printStackTrace();
						}
					} else if (validEnreg) {
						validTextField = true;
						try {
							tempsEstim = new SimpleDoubleProperty(Math.round(
									dureeIntervalleBit.get() * (PasserelleFichier.lireOctets(fileEnreg).length) * 8));
							labelTempsEstim.textProperty().bind(tempsEstim.asString());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				} else {
					dureeIntervalleBit.set(0);
					validTextField = false;
				}
				actualiserValidation();
			}
		});
	}

	/**
	 * Gère l'événement du bouton annuler. Permet d'annuler l'envoie.
	 * 
	 * @param event
	 */
	@FXML
	private void clickedBtnAnnuler(ActionEvent event) {
		if ((threadSon != null) && threadSon.isAlive()) {
			threadSon.stop();
			animProgressBar.stopProgressAnim();
			ajoutLabel(new Label("L'envoi a été annulé"), vboxMessages);
		}
	}

	/**
	 * Vérifi si tout les éléments ont été sélectionné avant l'envoi du fichier. Si
	 * c'est le cas, elle modifi la couleur de la pastille sur la vue pour indiquer
	 * que l'application est oppérationnel ou pas pour l'envoi.
	 */
	private void actualiserValidation() {
		if ((validEnreg || validSelect) && validTextField) {
			btnEnvoyer.setBackground(BG_VERT);
		} else {
			btnEnvoyer.setBackground(BG_ROUGE);
		}
	}
}
