package modeles;


import com.jfoenix.controls.JFXProgressBar;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.control.ProgressBar;
import javafx.util.Duration;

public class AnimationProgressBar {
	
	private ProgressBar progressBar;
	private Timeline timeline = new Timeline();
	private double tempsEstim = 0;
	
	public AnimationProgressBar(ProgressBar pb, float dureeSon, double nbrBits) {
		progressBar = pb;
		progressBar.setProgress(0);
		KeyValue keyValue = new KeyValue(progressBar.progressProperty(), 1);
		tempsEstim = dureeSon*nbrBits*1000;
		KeyFrame keyFrame = new KeyFrame(new Duration(tempsEstim), keyValue);
		timeline.getKeyFrames().add(keyFrame);
		timeline.play();
	}
	
	public void stopProgressAnim() {
		timeline.stop();
	}
}
