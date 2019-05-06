package controleurs;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import vues.ControleurVueEmetteur;
import vues.ControleurVueMenu;
import vues.ControleurVueRecepteur;

/**
 * Cette classe permet le lancement de l'application Radio. Elle relie toute les
 * vues ensembles. Elle affiche d'abord la vue menu. Pour la suite, elle dispose
 * des méthodes permettant d'afficher les autres vues.
 * 
 * @author Charles-Antoine Demetriade
 */
public class ApplicationRadio extends Application {

	private Scene sceneCourante;
	private Scene sceneMenu;
	private Scene sceneEmetteur;
	private Scene sceneRecepteur;
	private Stage stage;
	private ControleurVueEmetteur vueEmetteur;
	private ControleurVueRecepteur vueRecepteur;
	private ControleurVueMenu vueMenu;
	private boolean redimensionnable = true;

	/**
	 * Démarre l'application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Affiche la vue menu pour lancer l'application.
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		stage = primaryStage;
		showVueMenu(stage);
	}

	/**
	 * Affiche la vue menu.
	 * 
	 * @param stage
	 * @throws Exception
	 */
	private void showVueMenu(Stage stage) throws Exception {
		if (sceneMenu == null) {
		FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/vues/Vue_Menu.fxml"));
		loader.load();
		vueMenu = loader.getController();
		vueMenu.setApplication(this);
		VBox root = vueMenu.getVboxRoot();
		sceneMenu = new Scene(root);
		sceneMenu.getStylesheets().setAll(this.getClass().getResource(vueMenu.getThemeCourant()).toString());
		}
		demarrageStage("Menu", redimensionnable, sceneMenu);
	}

	/**
	 * Affiche la vue émetteur.
	 * 
	 * @throws Exception
	 */
	public void showVueEmetteur() throws Exception {
		if (sceneEmetteur == null) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(ControleurVueEmetteur.ADRESSE_VUE_EMETTEUR));
		loader.load();
		vueEmetteur = loader.getController();
		vueEmetteur.setApplication(this);
		vueEmetteur.bindTextFieldEtProgress();
		BorderPane root = vueEmetteur.getBorderPaneRoot();
		sceneEmetteur = new Scene(root);
		setOptionRetour(sceneEmetteur);
		}
		sceneEmetteur.getStylesheets().setAll(this.getClass().getResource(vueMenu.getThemeCourant()).toString());
		demarrageStage("Émetteur", redimensionnable, sceneEmetteur);
	}

	/**
	 * Affiche la vue récepteur.
	 * 
	 * @throws Exception
	 */
	public void showVueRecepteur() throws Exception {
		if (sceneRecepteur == null) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(ControleurVueRecepteur.ADRESSE_VUE_RECEPTEUR));
		loader.load();
		vueRecepteur = loader.getController();
		vueRecepteur.setApplication(this);
		vueRecepteur.bindTextFieldEtProgress();
		BorderPane root = vueRecepteur.getBorderPaneRoot();
		sceneRecepteur = new Scene(root);
		setOptionRetour(sceneRecepteur);
		}
		sceneRecepteur.getStylesheets().setAll(this.getClass().getResource(vueMenu.getThemeCourant()).toString());
		demarrageStage("Récepteur", redimensionnable, sceneRecepteur);
	}
	
	/**
	 * Configure et démarre le stage.
	 * 
	 * @param titre du stage
	 * @param redimension vrai ou faux
	 * @param scene de la vue
	 */
	private void demarrageStage(String titre, boolean redimension, Scene scene) {
		stage.setTitle(titre);
		stage.setResizable(redimension);
		stage.setScene(scene);
		stage.show();
		setSceneCourante(scene);
	}

	/**
	 * Obtenir le stage de l'application.
	 * 
	 * @return stage
	 */
	public Stage getStage() {
		return stage;
	}

	/**
	 * Obtenir la scene de l'application.
	 * 
	 * @return scene
	 */
	public Scene getSceneCourante() {
		return sceneCourante;
	}
	
	public void setSceneCourante(Scene sceneCour) {
		sceneCourante = sceneCour;
	}

	/*
	 * Permet de revenir à la vue menu en appuyant sur la touche Escape.
	 */
	private void setOptionRetour(Scene scene) {
		scene.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.ESCAPE) {
				try {
					showVueMenu(stage);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
	}
}
