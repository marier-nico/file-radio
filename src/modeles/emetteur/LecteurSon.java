package modeles.emetteur;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class LecteurSon {
	private byte[][] donneesSons;
	private AudioFormat af;
	private SourceDataLine sdl;
	private float dureeEnSec;
	
	public LecteurSon(byte[][] donneesSons, float dureeEnSec) throws LineUnavailableException {
		this.donneesSons = donneesSons;
		this.dureeEnSec = dureeEnSec;
		creerAudioFormat();
		creerSourceDataLine();
	}
	
	public void lireSons() throws LineUnavailableException {
		preparerDataLineAEnvoi();
		for(byte[] donneesSon : donneesSons) {
			sdl.write(donneesSon, 0, (int) (af.getSampleRate() * dureeEnSec));
		}
		fermerDataLine();
	}
	
	private void creerAudioFormat() {
		float freqEchantillon = 44100;
		int tailleEchantillonEnBits = 8;
		int cannaux = 1;
		boolean bytesSignes = true;
		boolean bigEndian = false;
		
		af = new AudioFormat((float) freqEchantillon , tailleEchantillonEnBits, cannaux, bytesSignes, bigEndian);
	}
	
	private void creerSourceDataLine() throws LineUnavailableException {
		sdl = AudioSystem.getSourceDataLine(af);
	}
	
	private void preparerDataLineAEnvoi() throws LineUnavailableException {
		sdl.open();
		sdl.start();
	}
	
	private void fermerDataLine() {
		sdl.drain();
		sdl.stop();
	}
}
