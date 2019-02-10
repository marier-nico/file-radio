package controleurs;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import vues.ControleurVueEmetteur;

public class ApplicationRadio extends Application {
	
	private Scene scene;
	private ControleurVueEmetteur vueEmetteur;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		showVueEmetteur(stage);
	}
	
	private void showVueEmetteur(Stage stage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/vues/Vue_Emetteur.fxml"));
		loader.load();
		vueEmetteur = loader.getController();
		vueEmetteur.setApplication(this);
		
		BorderPane root = vueEmetteur.getBorderPaneRoot();
		scene = new Scene(root);
		stage.setTitle("Vue Émetteur");
		stage.setResizable(false);
		stage.setScene(scene);
		stage.show();
	}
	

}
