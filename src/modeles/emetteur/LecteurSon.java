package modeles.emetteur;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class LecteurSon {

	private SourceDataLine sdl;
	
	public LecteurSon(AudioFormat af) throws LineUnavailableException {
		sdl = AudioSystem.getSourceDataLine(af);
		ouvrir();
	}
	
	public void lire(byte[] byteTab) {
		//TODO
	}
	
	public void ouvrir() throws LineUnavailableException {
		sdl.open();
		sdl.start();
	}
	
	public void fermer() {
		sdl.drain();
		sdl.stop();
	}
}
