package vues;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;

import com.jfoenix.controls.JFXButton;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
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
	private JFXButton btnCalibrer;

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
	private Circle cercleValidation;

	public static final String ADRESSE_VUE_EMETTEUR = "/vues/Vue_Emetteur.fxml";
	private final FileChooser fileChooser = new FileChooser();
	private File file;
	private GenerateurSon generateurSon;
	private LecteurSon lecteurSon;
	private FloatProperty dureeSonBit = new SimpleFloatProperty(1f);
	private DoubleProperty tempsEstim;
	private Thread threadSon;
	private AnimationProgressBar animProgressBar;
	private int nbValidationInfo = 0;

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
		if ((file != null) && (dureeSonBit.get() != 0)) {
			if ((threadSon != null) && (threadSon.isAlive())) {
				Alert erreur = new Alert(AlertType.ERROR);
				erreur.setHeaderText("Erreur");
				erreur.setContentText("Un envoi est déjà en cours, veuillez l'anuler pour faire un autre envoi...");
				erreur.setTitle("Erreur");
				erreur.showAndWait();
			} else {
				byte[] octetsFichier = null;
				try {
					octetsFichier = PasserelleFichier.lireOctets(file);
				} catch (IOException ex) {
					afficherErreur("la lecture du fichier",
							"Le fichier n'a pas pu être lu. Une erreur s'est produite pendant son ouverture. "
									+ "Il est possible que le fichier soit ouvert dans un autre programme.",
							ex);
					return;
				}
				RepresentationBinaire repr = new RepresentationBinaire(octetsFichier);
				generateurSon = new GenerateurSon(repr, dureeSonBit.get());
				byte[][] donnees = generateurSon.getDonneesSon();
				try {
					lecteurSon = new LecteurSon(donnees, dureeSonBit.get());
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
					animProgressBar = new AnimationProgressBar(progressBar, dureeSonBit.get(),
							octetsFichier.length * 8);
				} catch (LineUnavailableException ex) {
					afficherErreur("la lecture du son",
							"Le son n'a pas pu être lu, car la sortie audio est indisponible. "
									+ "Tentez de libérer la sortie audio de votre système.",
							ex);
				} catch (IllegalStateException ex) {
					afficherErreur("la lecture du son", "Un autre son est déjà en lecture.", ex);
				}
				ajoutLabel(new Label("Envoi en cours de " + getEmplacementFichierSelct(file) + "..."), vboxMessages);
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
		fileChooser.setTitle("Veuiller sélectionner un fichier");
		file = fileChooser.showOpenDialog(getApplication().getStage());
		labelProgress.setText(getEmplacementFichierSelct(file));
		float uptdate = dureeSonBit.get();
		textFieldTempsUnBit.setText((dureeSonBit.get() - uptdate) + "");
		textFieldTempsUnBit.setText((dureeSonBit.get() + uptdate) + "");
	}

	/**
	 * Gère l'événement du bouton calibrer.
	 * 
	 * @param event
	 */
	@FXML
	private void clickedCalibrer(ActionEvent event) {
		try {
			Calibreur.calibrerTout();
		} catch (IOException | LineUnavailableException e) {
			afficherErreur("calibration", "fichier introuvable ou micro non disponible", e);
		}
	}

	/**
	 * Cette méthode bind le textField avec tous les labels affichant des
	 * informations en lien avec celui-ci et la progressbar.
	 */
	public void bindTextFieldEtProgress() {
		bindProgressBar(progressBar, hboxProgressBar);

		textFieldTempsUnBit.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (newValue.matches("-?\\d+(\\.\\d+)?")) {
					float valeur = Float.parseFloat(newValue);
					dureeSonBit.set(valeur);
					DoubleProperty vitFich = new SimpleDoubleProperty(Math.round(valeur * 8));
					labelVitesseFichier.textProperty().bind(vitFich.asString());
					if (file != null) {
						try {
							tempsEstim = new SimpleDoubleProperty(
									Math.round(dureeSonBit.get() * (PasserelleFichier.lireOctets(file).length) * 8));
							labelTempsEstim.textProperty().bind(tempsEstim.asString());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				} else {
					dureeSonBit.set(0);
				}
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
}
