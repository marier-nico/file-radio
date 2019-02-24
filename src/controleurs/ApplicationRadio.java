package controleurs;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import vues.ControleurVueEmetteur;
import vues.ControleurVueMenu;

public class ApplicationRadio extends Application {
	
	private Scene scene;
	private Stage stage;
	private ControleurVueEmetteur vueEmetteur;
	private ControleurVueMenu vueMenu;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		stage = primaryStage;
		showVueMenu(stage);
	}
	
	private void showVueMenu(Stage stage) throws Exception {
		FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/vues/Vue_Menu.fxml"));
		loader.load();
		vueMenu = loader.getController();
		vueMenu.setApplication(this);
		
		VBox root = vueMenu.getVboxRoot();
		scene = new Scene(root);
		scene.getStylesheets().setAll(this.getClass().getResource("/styles/DarkNGreen.css").toString());
		//scene.getStylesheets().setAll(this.getClass().getResource("/styles/BlueNRed.css").toString());
		stage.setTitle("Menu");
		stage.setResizable(false);
		stage.setScene(scene);
		stage.show();
	}
	
	public void showVueEmetteur() throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(ControleurVueEmetteur.ADRESSE_VUE_EMETTEUR));
		loader.load();
		vueEmetteur = loader.getController();
		vueEmetteur.setApplication(this);
		vueEmetteur.bindSlider();
		
		BorderPane root = vueEmetteur.getBorderPaneRoot();
		scene = new Scene(root);
		scene.getStylesheets().setAll(this.getClass().getResource("/styles/DarkNGreen.css").toString());
		//scene.getStylesheets().setAll(this.getClass().getResource("/styles/BlueNRed.css").toString());
		stage.setTitle("Émetteur");
		stage.setResizable(false);
		stage.setScene(scene);
		stage.show();
	}

	public Stage getStage() {
		return stage;
	}
	
}

