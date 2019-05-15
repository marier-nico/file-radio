package modeles;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.control.ProgressBar;
import javafx.util.Duration;

/**
 * Cette classe permet d'associer la progression d'un progress bar avec le temps
 * restant à l'envoi. Cela permet à l'utilisateur de visualiser l'état de
 * l'envoi.
 * 
 * @author Charles-Antoine Demetriade
 *
 */
public class AnimationProgressBar {

	private ProgressBar progressBar;
	private Timeline timeline = new Timeline();
	private double tempsEstim = 0;

	/**
	 * Construit l'animation de la progressBar.
	 * 
	 * @param pb
	 * @param dureeSon
	 * @param nbrBits
	 */
	public AnimationProgressBar(ProgressBar pb, float dureeSon, double nbrBits) {
		progressBar = pb;
		progressBar.setProgress(0);
		KeyValue keyValue = new KeyValue(progressBar.progressProperty(), 1);
		tempsEstim = dureeSon * nbrBits * 1000;
		KeyFrame keyFrame = new KeyFrame(new Duration(tempsEstim), keyValue);
		timeline.getKeyFrames().add(keyFrame);
		timeline.play();
	}

	/**
	 * Cette méthode arrête l'animation de la progression.
	 */
	public void stopProgressAnim() {
		timeline.stop();
	}
}
