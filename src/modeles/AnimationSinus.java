package modeles;

import java.util.function.DoubleUnaryOperator;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class AnimationSinus {

	private DoubleProperty frequence = new SimpleDoubleProperty(2);
	private final DoubleUnaryOperator sin1 = x -> -Math.sin(frequence.getValue() * Math.PI * x / 100) * 40 + 45;
	private ZoneDessin plot1 = new ZoneDessin(sin1, 1800, 500, -500, 2000);
	private ZoneDessin plot2 = new ZoneDessin(sin1, 1800, 500, -500, 2000);
	private TranslateTransition animation1;
	private TranslateTransition animation2;

	public void startAnimationSinus(Pane pane) {

		
		pane.getChildren().add(plot1.getCanvas());
		pane.getChildren().add(plot2.getCanvas());

		animation1 = new TranslateTransition(Duration.seconds(6), plot1.getCanvas());
		animation1.setCycleCount(1);
		animation1.setFromX(-1000);
		animation1.setToX(1000);
		animation1.setAutoReverse(false);
		animation1.setInterpolator(Interpolator.LINEAR);
		animation1.play();
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		animation2 = new TranslateTransition(Duration.seconds(6), plot2.getCanvas());
		animation2.setCycleCount(1);
		animation2.setFromX(-1000);
		animation2.setToX(1000);
		animation2.setAutoReverse(false);
		animation2.setInterpolator(Interpolator.LINEAR);
		animation2.play();
	}
	
	public void stopAnimationSinus() {
		animation1.stop();
		animation2.stop();
	}
	
	public DoubleProperty getFrequence() {
		return frequence;
	}
	
	public static class ZoneDessin {
		private final Canvas canvas;

		public ZoneDessin(DoubleUnaryOperator fonction, double longueur, double hauteur, double xDepart, double xFin) {

			this.canvas = new Canvas(longueur, hauteur);
			GraphicsContext gc = canvas.getGraphicsContext2D();
			gc.moveTo(xDepart, fonction.applyAsDouble(xDepart));
			for (double x = xDepart; x < xFin; x++) {
				gc.lineTo(x, fonction.applyAsDouble(x));
			}
			gc.setStroke(Color.WHITE);
			gc.stroke();
		}

		public Canvas getCanvas() {
			return canvas;
		}
	}
}