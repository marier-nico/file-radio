package vues;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;

import controleurs.ApplicationRadio;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleFloatProperty;
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
import modeles.emetteur.GenerateurSon;
import modeles.emetteur.LecteurSon;
import modeles.passerelle.PasserelleFichier;

/**
 * Cette classe permet est le controleur de la vue de l'emetteur. Elle permet de
 * creer une vue et de gérer les évenements.
 * 
 * @author Charles-Antoine Demetriade et Nicolas Marier
 *
 */
public class ControleurVueEmetteur {

	@FXML
	private BorderPane borderPaneRoot;

	@FXML
	private JFXButton btnSelectionner;

	@FXML
	private JFXButton btnEnvoyer;

	@FXML
	private JFXButton btnAnnuler;

	@FXML
	private JFXSlider slider;

	@FXML
	private Label sliderLabel;

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

	private ApplicationRadio application = null;
	public static final String ADRESSE_VUE_EMETTEUR = "/vues/Vue_Emetteur.fxml";
	private final FileChooser fileChooser = new FileChooser();
	private File file;
	private int nbrMessage = 0;
	private GenerateurSon generateurSon;
	private LecteurSon lecteurSon;
	private FloatProperty dureeSonBit = new SimpleFloatProperty(1f);
	private Thread threadSon;
	private AnimationProgressBar animProgressBar;
	// private AnimationEnvoi animEnvoi;

	public void setApplication(ApplicationRadio application) {
		this.application = application;
	}

	public BorderPane getBorderPaneRoot() {
		return borderPaneRoot;
	}

	@FXML
	private void clickedBtnEnvoyer(ActionEvent event) {
		if (file != null) {
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
					// animEnvoi = new AnimationEnvoi(paneAnimation, dureeSonBit.get(), donnees);
				} catch (LineUnavailableException ex) {
					afficherErreur("la lecture du son",
							"Le son n'a pas pu être lu, car la sortie audio est indisponible. "
									+ "Tentez de libérer la sortie audio de votre système.",
							ex);
				} catch (IllegalStateException ex) {
					afficherErreur("la lecture du son", "Un autre son est déjà en lecture.", ex);
				}
				Label l = new Label("Envoi en cours de " + getEmplacementFichierSelct() + "...");
				ajoutLabel(l);
//				Label ll = new Label(getEmplacementFichierSelct() + " a été envoyé!");
//				ajoutLabel(ll);
			}
		}
	}

	private void ajoutLabel(Label l) {
		if (nbrMessage == 14) {
			vboxMessages.getChildren().remove(vboxMessages.getChildren().get(0));
			nbrMessage--;
		}
		vboxMessages.getChildren().add(l);
		nbrMessage++;
	}

	@FXML
	private void clickedBtnSelect(ActionEvent event) {
		fileChooser.setTitle("Veuiller sélectionner un fichier");
		file = fileChooser.showOpenDialog(application.getStage());
		labelProgress.setText(getEmplacementFichierSelct());
	}

	/**
	 * Cette méthode bind le slider avec tous les labels affichant des informations
	 * en lien avec celui-ci.
	 */
	public void bindSliderEtLabel() {
		slider.valueProperty().addListener((ov, old_val, new_val) -> {
			dureeSonBit.bind(slider.valueProperty());
			sliderLabel.textProperty().bind(slider.valueProperty().asString());
			DoubleProperty vitFich = new SimpleDoubleProperty(slider.getValue() * 8);
			labelVitesseFichier.textProperty().bind(vitFich.asString());
			if (file != null) {
				DoubleProperty tempsEstim;
				try {
					tempsEstim = new SimpleDoubleProperty(
							dureeSonBit.get() * (PasserelleFichier.lireOctets(file).length) * 8);
					labelTempsEstim.textProperty().bind(tempsEstim.asString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Cette méthode permet de créer un "hgrow" à la progressBar.
	 */
	public void bindProgressBar() {
		progressBar.prefWidthProperty().bind(hboxProgressBar.widthProperty());
	}

	@FXML
	void clickedBtnAnnuler(ActionEvent event) {
		if ((threadSon != null) && threadSon.isAlive()) {
			threadSon.stop();
			animProgressBar.stopProgressAnim();
			Label l = new Label("L'envoi a été annulé");
			ajoutLabel(l);
		}
	}

	public String getEmplacementFichierSelct() {
		String retour = "Rien";
		if (file != null) {
			retour = file.getAbsolutePath();
		}
		return retour;
	}

	private void afficherErreur(String emplacement, String detail, Exception ex) {
		Alert erreur = new Alert(AlertType.ERROR);
		erreur.setHeaderText("Erreur dans " + emplacement);
		erreur.setContentText(detail + "\n\n" + ex.getStackTrace());
		erreur.setTitle("Erreur");
		erreur.showAndWait();
	}
}
