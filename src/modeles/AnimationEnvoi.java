package modeles;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.util.Duration;

// En phase de test
public class AnimationEnvoi {

	private Pane pane;
	private Timeline timeline = new Timeline();
	private double tempsEstim = 0;
	private Line ligne1 = new Line(-100, 0, 77, 0);
	private Line ligne0 = new Line(-100, 0, 77, 0);
	double posXinit = 100;
	byte[] tabBits;

	public AnimationEnvoi(Pane p, float dureeSon, byte[] tabBits) {
		pane = p;
		this.tabBits = tabBits;
		
		Canvas canvas = new Canvas(1000, 200);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		
		pane.getChildren().clear();
		constructionDessin(gc);
		KeyValue keyValue = new KeyValue(pane.translateXProperty(), 1);
		tempsEstim = dureeSon*tabBits.length*1000;
		KeyFrame keyFrame = new KeyFrame(new Duration(tempsEstim), keyValue);
		timeline.getKeyFrames().add(keyFrame);
		timeline.play();
	}
	
	public void stopProgressAnim() {
		timeline.stop();
	}
	
	private void constructionDessin(GraphicsContext gc) {
		ligne1.setLayoutX(posXinit);
		ligne1.setLayoutY(23.5);
		ligne0.setLayoutX(posXinit);
		ligne0.setLayoutY(69.75);
		
		for (int i = 0; i <= tabBits.length; i++) {
			if (tabBits[i] == 0) {
				ligne0.setLayoutX(posXinit + (i*100));
				pane.getChildren().add(ligne1);
			} else {
				ligne1.setLayoutX(posXinit + (i*100));
				pane.getChildren().add(ligne0);
			}
		}
	}
}
