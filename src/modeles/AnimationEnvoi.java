package modeles;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Duration;

// En phase de test
public class AnimationEnvoi {

	private Pane pane;
	private Timeline timeline = new Timeline();
	private double tempsEstim = 0;
	private Line ligne1 = new Line(-100, 0, 77, 0);
	private Line ligne0 = new Line(0, 0, 77, 0);
	private double posXinit = 100;
	private byte[][] tabBits;
	private Group gr = null;

	public AnimationEnvoi(Pane p, float dureeSon, byte[][] tabBits) {
		pane = p;
		this.tabBits = tabBits;
		
		if (gr != null) {
			gr.getChildren().clear();
		}
		constructionDessin();
		pane.getChildren().add(gr);
		
		KeyValue keyValue = new KeyValue(pane.translateXProperty(), 1);
		tempsEstim = dureeSon*tabBits.length*1000;
		KeyFrame keyFrame = new KeyFrame(new Duration(tempsEstim), keyValue);
		timeline.getKeyFrames().add(keyFrame);
		timeline.play();
	}
	
	public void stopProgressAnim() {
		timeline.stop();
	}
	
	private void constructionDessin() {
		gr = new Group();
		ligne1.setLayoutX(posXinit);
		ligne1.setLayoutY(23.5);
		ligne1.setFill(Color.WHITE);
		ligne0.setLayoutX(posXinit + posXinit);
		ligne0.setLayoutY(69.75);
		ligne0.setFill(Color.WHITE);
		gr.getChildren().addAll(ligne1, ligne0);
		
//		for (int i = 0; i <= tabBits.length; i++) {
//			for (int j = 0; j <= tabBits[i].length; j++) {
//				if (tabBits[i][j] == 0) {
//					ligne0.setLayoutX(posXinit + (j*100));
//					gr.getChildren().add(ligne1);
//				} else {
//					ligne1.setLayoutX(posXinit + (j*100));
//					gr.getChildren().add(ligne0);
//				}
//			}
//		}
	}
}
