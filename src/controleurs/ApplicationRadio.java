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

	private Scene scene;
	private Stage stage;
	private ControleurVueEmetteur vueEmetteur;
	private ControleurVueRecepteur vueRecepteur;
	private ControleurVueMenu vueMenu;

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
		FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/vues/Vue_Menu.fxml"));
		loader.load();
		vueMenu = loader.getController();
		vueMenu.setApplication(this);

		VBox root = vueMenu.getVboxRoot();
		scene = new Scene(root);
		scene.getStylesheets().setAll(this.getClass().getResource(vueMenu.getThemeCourant()).toString());
		stage.setTitle("Menu");
		stage.setResizable(true);
		stage.setScene(scene);
		stage.show();
	}

	/**
	 * Affiche la vue émetteur.
	 * 
	 * @throws Exception
	 */
	public void showVueEmetteur() throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(ControleurVueEmetteur.ADRESSE_VUE_EMETTEUR));
		loader.load();
		vueEmetteur = loader.getController();
		vueEmetteur.setApplication(this);
		vueEmetteur.bindSlider();

		BorderPane root = vueEmetteur.getBorderPaneRoot();
		scene = new Scene(root);
		scene.getStylesheets().setAll(this.getClass().getResource(vueMenu.getThemeCourant()).toString());
		stage.setTitle("Émetteur");
		stage.setResizable(false);
		stage.setScene(scene);
		stage.show();
		setOptionRetour();
	}

	/**
	 * Affiche la vue récepteur.
	 * 
	 * @throws Exception
	 */
	public void showVueRecepteur() throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(ControleurVueRecepteur.ADRESSE_VUE_RECEPTEUR));
		loader.load();
		vueRecepteur = loader.getController();
		vueRecepteur.setApplication(this);

		BorderPane root = vueRecepteur.getBorderPaneRoot();
		scene = new Scene(root);
		scene.getStylesheets().setAll(this.getClass().getResource(vueMenu.getThemeCourant()).toString());
		stage.setTitle("Récepteur");
		stage.setResizable(false);
		stage.setScene(scene);
		stage.show();
		setOptionRetour();
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
	public Scene getScene() {
		return scene;
	}

	/*
	 * Permet de revenir à la vue menu en appuyant sur la touche Escape.
	 */
	private void setOptionRetour() {
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
