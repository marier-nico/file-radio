package modeles.recepteur;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.quifft.QuiFFT;
import org.quifft.output.FFTFrame;
import org.quifft.output.FFTResult;
import org.quifft.output.FrequencyBin;

import modeles.recepteur.libson.microphone.MicrophoneAnalyzer;

public class EcouteurDeReception {
	/**
	 * Le reconstitueur de messages sera utilisé pour refaire le message reçu.
	 */
	private ReconstitueurDeMessages rdm;

	/**
	 * L'indice dans les bins où se trouve celui représentant la
	 * FREQUENCE_RECEPTION.
	 */
	private int indiceFreqVoulue;
	/**
	 * On stoque le dernier bit que l'on voit.
	 */
	Optional<Byte> dernierBitVu = Optional.empty();
	// TODO: Remettre -1
	/**
	 * Le volume minimal pour un un, après la calibration.
	 */
	private double volumeMinUn = -13;
	// TODO: Remettre -1
	/**
	 * Le volume minimal pour un zéro, après la calibration.
	 */
	private double volumeMinZero = -25;
	/**
	 * La taille de la fenêtre pour calculer les FFT.
	 */
	public static final int WINDOW_SIZE = 512;
	/**
	 * Le pourcentage des fenêtres du FFT qui sont superposées.
	 */
	public static final int OVERLAP = 0;
	/**
	 * La fréquence à laquelle on envoie (c'est la même que celle que l'on reçoit)
	 */
	public static final double FREQUENCE_RECEPTION = 3300.0;

	private double tempsParBit = 0;

	// TODO: nom du fichier constante
	/**
	 * Ce constructeur permet d'initialiser le reconstitueur de messages.
	 * 
	 * @throws Exception
	 */
	public EcouteurDeReception(double tempsParBit) throws Exception {
		rdm = new ReconstitueurDeMessages();
		indiceFreqVoulue = getIndiceBin(FREQUENCE_RECEPTION);
		this.tempsParBit = tempsParBit;

	}

	/**
	 * Cette méthode permet de voir les données entrantes et d'utiliser le
	 * reconstitueur de messages pour refaire le message initial.
	 * 
	 * @throws LineUnavailableException
	 * @throws InterruptedException
	 */
	public void ecouter(long millisecondes) throws LineUnavailableException, InterruptedException {
		MicrophoneAnalyzer micro = new MicrophoneAnalyzer(Type.WAVE);
		micro.open();
		micro.captureAudioToFile(new File("audio.wav"));
		Thread.sleep(millisecondes);
		micro.close();
	}

	/**
	 * Cette méthode permet de reconstruire le signal reçu du son vers une
	 * représentation binaire.
	 * 
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 */
	long nbBitsVu = 0;

	public void reconstruire() throws IOException, UnsupportedAudioFileException {
		FFTResult fft = getResultatFFT(WINDOW_SIZE, OVERLAP);
		System.out.println("Duration: " + fft.windowDurationMs);
		FFTFrame[] frames = fft.fftFrames;
		System.out.println("Il y a " + frames.length + " frames.");

		for (FFTFrame frame : frames) {
			Optional<Byte> resultat = analyserSignal(frame);
			if (resultat.isPresent()) {
				byte b = resultat.get();

				if (dernierBitVu.isPresent()) {
					if (b == dernierBitVu.get()) {
						nbBitsVu++;
					} else {
						System.out.println("On a vu un " + dernierBitVu.get() + " pendant "
								+ (double) nbBitsVu * fft.windowDurationMs + " ms. Donc, "
								+ Math.round(((double) nbBitsVu * fft.windowDurationMs / tempsParBit)) + " "
								+ dernierBitVu.get());
						dernierBitVu = Optional.of(b);
						nbBitsVu = 1;
					}
				} else {
					dernierBitVu = Optional.of(b);
					nbBitsVu++;
				}
			} else {
				// TODO: quand on a pas de bit, on continue?
			}
		}
	}

	/**
	 * Cette méthode permet d'analyser une partie du signal pour savoir si un 1 ou
	 * un 0 a été reçu. Elle retourne ensuite ce bit reçu de façon optionnelle. Si
	 * rien n'a été reçu, un Optional vide est retourné.
	 * 
	 * @return un Optional qui contient le bit reçu ou rien, si rien n'a été reçu
	 */
	private Optional<Byte> analyserSignal(FFTFrame frame) {
		if (!validerVolumeMin(volumeMinUn) || !validerVolumeMin(volumeMinZero)) {
			throw new IllegalStateException("Il faut calibrer le programme.");
		}

		FrequencyBin bin = frame.bins[indiceFreqVoulue];
		byte bit = -1;
		if (bin.amplitude >= volumeMinUn) {
			bit = 1;
			System.out.println("Amplitude 1 : " + bin.amplitude);
		} else if (bin.amplitude >= volumeMinZero) {
			System.out.println("Amplitude 0 : " + bin.amplitude);
			bit = 0;
		}

		if (bit != -1)
			return Optional.of(bit);
		return Optional.empty();
	}

	// TODO: à faire
	/**
	 * Cette méthode permet la calibration du programme pour connaître la valeur
	 * minimal pour interpréter un un.
	 * 
	 * @return le volume minimal pour un un
	 * @throws LineUnavailableException
	 * @throws InterruptedException
	 */
	private double getVolumeMinUn() throws LineUnavailableException, InterruptedException {
		ecouter(500);

		return 0;
	}

	/**
	 * Cette méthode permet la calibration du programme pour connaître la valeur
	 * minimal pour interpréter un un.
	 * 
	 * @return le volume minimal pour un zéro
	 * @throws LineUnavailableException
	 * @throws InterruptedException
	 */
	private double getVolumeMinZero() throws LineUnavailableException, InterruptedException {
		ecouter(500);
		return 0;
	}

	// TODO: Calibrer les volumes et l'indice des bins
	/**
	 * Cette méthode permet de calibrer le programme en trouvant les volumes
	 * minimaux dans le contexte physique courant.
	 * 
	 * @throws Exception
	 */
	public void calibrer() throws Exception {
		int indice = getIndiceBin(3300);
		ecouter(1000);
		FFTFrame[] frames = getResultatFFT(WINDOW_SIZE, OVERLAP).fftFrames;
		for (FFTFrame frame : frames) {
			System.out.println(frame.bins[indice].amplitude);
		}

	}

	/**
	 * Cette méthode permet de trouver l'indice du bin représentant la fréquence que
	 * l'on cherche
	 * 
	 * @param frequence fréquence cherchée
	 * @return indice du bin
	 * @throws Exception
	 */
	private int getIndiceBin(double frequence) throws Exception {
		ecouter(150);

		FFTFrame[] frames = getResultatFFT(WINDOW_SIZE, OVERLAP).fftFrames;
		FFTFrame premiereFrame = frames[0];
		if (premiereFrame.bins.length < 2)
			throw new IllegalStateException("Pas assez de bins.");

		FrequencyBin premierBin = premiereFrame.bins[0];
		FrequencyBin deuxiemeBin = premiereFrame.bins[1];
		double incertitude = deuxiemeBin.frequency - premierBin.frequency;

		int indice = 0;
		for (FrequencyBin bin : premiereFrame.bins) {
			if (bin.frequency >= (frequence - incertitude) && bin.frequency <= (frequence + incertitude)) {
				return indice;
			} else {
				indice++;
			}
		}
		throw new Exception("Fréquence introuvable.");
	}

	/**
	 * Cette méthode calcule la FFT et retourne le résultat
	 * 
	 * @param windowSize taille fenêtre FFT
	 * @param overlap    pourcentage de la fenêtre qui est par dessus la précédente
	 * @return le résultat de la FFT
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 */
	private FFTResult getResultatFFT(int windowSize, double overlap) throws IOException, UnsupportedAudioFileException {
		FFTResult fft = null;
		QuiFFT quiFFT = new QuiFFT(new File("audio.wav"));
		quiFFT.windowSize(windowSize);
		quiFFT.windowOverlap(overlap);
		fft = quiFFT.fullFFT();
		return fft;
	}

	/**
	 * Cette méthode permet de valider un volume (pour un un ou un zéro). Le volume
	 * doit être inférieur à 0 (en dB).
	 * 
	 * @param volumeMin le volume à valider
	 * @return si le volume est valide
	 */
	private static boolean validerVolumeMin(double volumeMin) {
		return volumeMin < 0;
	}

	public static void main(String[] args) throws Exception {
//		MicrophoneAnalyzer micro = new MicrophoneAnalyzer(Type.WAVE);
//		micro.open();
//		micro.captureAudioToFile(new File("audio.wav"));
//		Thread.sleep(10000);
//		micro.close();
		EcouteurDeReception edr = new EcouteurDeReception(1000);
		edr.ecouter(5000);
		edr.reconstruire();
	}
}
