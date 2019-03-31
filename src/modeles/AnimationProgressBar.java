package modeles;


import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.control.ProgressBar;
import javafx.util.Duration;

public class AnimationProgressBar {
	
	
	private ProgressBar progressBar;
	private Timeline timeline = new Timeline();
	
	public AnimationProgressBar(ProgressBar pb, float dureeSon, double nbrBits) {
		progressBar = pb;
		progressBar.setProgress(0);
		KeyValue keyValue = new KeyValue(progressBar.progressProperty(), 1);
		KeyFrame keyFrame = new KeyFrame(new Duration(dureeSon*nbrBits*1000), keyValue);
		timeline.getKeyFrames().add(keyFrame);
		timeline.play();
	}
	
	public void stopProgressAnim() {
		timeline.stop();
	}
}
