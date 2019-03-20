package modeles.emetteur;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * Cette classe sert à la lecture des données obtenues à
 * l'aide du générateur de sons.
 * 
 * @author Nicolas Marier
 * @see modeles.emetteur.GenerateurSon
 *
 */
public class LecteurSon {
	/**
	 * Les données des sons à faire jouer, obtenues
	 * à partir du générateur de sons
	 * 
	 * @see modeles.emetteur.GenerateurSon
	 */
	private byte[][] donneesSons;
	/**
	 * On veut une référence au format audio pour
	 * pouvoir envoyer des sons dans le système.
	 * 
	 * @see javax.sound.sampled.AudioFormat
	 */
	private AudioFormat af;
	/**
	 * On veut pouvoir accéder au SourceDataLine pour
	 * accéder aux sorties audio du système.
	 * 
	 * @see javax.sound.sampled.SourceDataLine
	 */
	private SourceDataLine sdl;
	/**
	 * On garde une référence sur la durée d'un son représentant
	 * un bit, pour écrire correctement sur la sortie audio.
	 */
	private float dureeEnSec;
	
	/**
	 * On veut savoir si un son est présentement en train d'être lu.
	 */
	private static boolean lectureEnCours;
	
	/**
	 * Le constructeur permet d'initialiser le lecteur de sons
	 * aux bonnes valeurs, qui doivent correspondre aux valeurs
	 * utilisées par le générateur de sons.
	 * 
	 * @param donneesSons les donneés sonores à faire jouer
	 * @param dureeEnSec la durée du son pour un bit en secondes
	 * @throws LineUnavailableException si la sortie audio n'est pas disponible
	 */
	public LecteurSon(byte[][] donneesSons, float dureeEnSec) throws LineUnavailableException {
		if(!validerDonneesSon(donneesSons) || !validerDuree(dureeEnSec))
			throw new IllegalArgumentException("Les données doivent contenir de l'information et la durée doit être supérieure à 0.");
		this.donneesSons = donneesSons;
		this.dureeEnSec = dureeEnSec;
		lectureEnCours = false;
		creerAudioFormat();
		creerSourceDataLine();
	}
	
	/**
	 * Cette méthode permet de lire les sons produits par le générateur de sons
	 * et de les faire jouer dans le système.
	 * 
	 * @throws LineUnavailableException si la sortie audio n'est pas disponible
	 */
	public void lireSons() throws LineUnavailableException, IllegalStateException {
		if(lectureEnCours)
			throw new IllegalStateException("Un son est déjà en lecture");
		preparerDataLineAEnvoi();
		lectureEnCours = true;
		for(byte[] donneesSon : donneesSons) {
			sdl.write(donneesSon, 0, (int) (af.getSampleRate() * dureeEnSec));
		}
		lectureEnCours = false;
		fermerDataLine();
	}
	
	/**
	 * Cette méthode permet de modifier les données sonores qui seront utilisés
	 * lors de la lecture du son.
	 * 
	 * @param donneesSons les donneés sonores à faire jouer
	 */
	public void setDonneesSons(byte[][] donneesSons) {
		if(validerDonneesSon(donneesSons))
			this.donneesSons = donneesSons;
	}
	
	/**
	 * Cette méthode permet d'initialiser l'objet AudioFormat avec les bonnes valeurs.
	 */
	private void creerAudioFormat() {
		float freqEchantillon = 44100;
		int tailleEchantillonEnBits = 8;
		int cannaux = 1;
		boolean bytesSignes = true;
		boolean bigEndian = false;
		
		af = new AudioFormat((float) freqEchantillon , tailleEchantillonEnBits, cannaux, bytesSignes, bigEndian);
	}
	
	/**
	 * Cette méthode permet d'obtenir un accès à la sortie audio du système.
	 * 
	 * @throws LineUnavailableException si la sortie audio n'est pas disponible
	 */
	private void creerSourceDataLine() throws LineUnavailableException {
		sdl = AudioSystem.getSourceDataLine(af);
	}
	
	/**
	 * Cette méthode ouvre la sortie audio et la prépare à émettre un signal.
	 * 
	 * @throws LineUnavailableException si la sortie audio n'est pas disponible
	 */
	private void preparerDataLineAEnvoi() throws LineUnavailableException {
		sdl.open();
		sdl.start();
	}
	
	/**
	 * Cette méthode permet de fermer la sortie audio pour qu'elle puisse être
	 * réutilisée par d'autres processus après la transmission. 
	 */
	private void fermerDataLine() {
		sdl.drain();
		sdl.stop();
	}
	
	/**
	 * Cette méthode permet de valider si les données sonores sont valides.
	 * Des données sont valides si elles contiennent de l'information.
	 * 
	 * @param donnees les données sonores
	 * @return validite true si les données sont valides ou false sinon
	 */
	public static boolean validerDonneesSon(byte[][] donnees) {
		return donnees != null && donnees.length > 0 && donnees[0].length > 0;
	}
	
	/**
	 * Cette méthode permet de valider si la durée en secondes est valide.
	 * Pour être valide, la durée doit être supérieure à 0 secondes.
	 * 
	 * @param duree la durée en secondes
	 * @return validite true si la durée est valide et false sinon
	 */
	public static boolean validerDuree(float duree) {
		return duree > 0;
	}
}
