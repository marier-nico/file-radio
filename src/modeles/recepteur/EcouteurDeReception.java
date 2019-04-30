package modeles.recepteur;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.Stream;

import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.quifft.QuiFFT;
import org.quifft.output.FFTFrame;
import org.quifft.output.FFTResult;
import org.quifft.output.FrequencyBin;

import modeles.passerelle.PasserelleFichier;
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
	/**
	 * Le volume minimal pour un un, après la calibration.
	 */
	private double volumeMinUn = 1;
	/**
	 * Le volume minimal pour un zéro, après la calibration.
	 */
	private double volumeMinZero = 1;
	/**
	 * La taille de la fenêtre pour calculer les FFT.
	 */
	public static final int WINDOW_SIZE = 64;
	/**
	 * Le pourcentage des fenêtres du FFT qui sont superposées.
	 */
	public static final int OVERLAP = 0;
	/**
	 * La fréquence à laquelle on envoie (c'est la même que celle que l'on reçoit)
	 */
	public static final double FREQUENCE_RECEPTION = 3300.0;

	/**
	 * Le nombre de temps par bit est important
	 * pour décoder l'information.
	 */
	private double tempsParBit = 0;

	/**
	 * Le nom du fichier temporaire dans lequel on écrit les sons
	 * que l'on reçoit.
	 */
	private static final String NOM_FICH_SON = "audio.wav";
	
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
		micro.captureAudioToFile(new File(NOM_FICH_SON));
		Thread.sleep(millisecondes);
		micro.close();
	}

	/**
	 * Cette méthode permet d'obtenir le reconstitueur de
	 * messages. Il est nécessaire pour recréer le fichier.
	 * 
	 * @return le reconstitudur de messages
	 */
	public ReconstitueurDeMessages getReconstitueur() {
		return rdm;
	}

	/**
	 * Cette variable est exclusivement utile à
	 * la méthode ci-dessous. Elle compte le nombre
	 * de bits semblables que l'on voit de suite.
	 */
	private long nbBitsVu = 0;
	
	/**
	 * Cette méthode permet de reconstruire le signal reçu du son vers une
	 * représentation binaire.
	 * 
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 */
	public void reconstruire() throws IOException, UnsupportedAudioFileException {
		FFTResult fft = getResultatFFT(WINDOW_SIZE, OVERLAP);
		System.out.println("Duration: " + fft.windowDurationMs);
		FFTFrame[] frames = fft.fftFrames;
		System.out.println("Il y a " + frames.length + " frames.");

		for (FFTFrame frame : frames) {
			Optional<Byte> resultat = analyserSignal(frame);
			if (resultat.isPresent()) {
				byte bitObserve = resultat.get();

				if (dernierBitVu.isPresent()) {
					if (bitObserve == dernierBitVu.get()) {
						nbBitsVu++;
					} else {
						long repetitionsBitPareil = Math
								.round(((double) nbBitsVu * fft.windowDurationMs / tempsParBit));
						for (int i = 0; i < repetitionsBitPareil; i++) {
							rdm.ajouterBit(dernierBitVu.get());
						}
						System.out.println("On a vu un " + dernierBitVu.get() + " pendant "
								+ (double) nbBitsVu * fft.windowDurationMs + " ms. Donc, " + repetitionsBitPareil
								+ " fois \"" + dernierBitVu.get() + "\"");
						dernierBitVu = Optional.of(bitObserve);
						nbBitsVu = 1;
					}
				} else {
					dernierBitVu = Optional.of(bitObserve);
					nbBitsVu++;
				}
			} else {
				// On ignore les valeurs qui ne sont pas un "1" ou un "0".
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

	/**
	 * Cette méthode permet la calibration du programme pour connaître la valeur
	 * minimal pour interpréter un bit donné.
	 * 
	 * @return le volume minimal pour un bit
	 * @throws Exception 
	 */
	private double calibrerVolumeBit(byte unOuZero, double diminutionSup) throws Exception {
		ecouter(500);
		FFTResult resultat = getResultatFFT(WINDOW_SIZE, OVERLAP);
		OptionalDouble bitMoyen = Stream.of(resultat.fftFrames)
			  .mapToDouble(f -> f.bins[indiceFreqVoulue].amplitude)
			  .average();
		
		double volumeBitMoyen = bitMoyen.orElseThrow(() -> new IllegalStateException("Impossible de déterminer le volume d'un \"" + unOuZero + "\".")) - diminutionSup;
		if(unOuZero == 1) {
			volumeMinUn = volumeBitMoyen;
		} else if(unOuZero == 0) {
			volumeMinZero = volumeBitMoyen;
		} else {
			throw new IllegalArgumentException("On peut seulement calibrer pour les uns et les zéros.");
		}
		
		return volumeBitMoyen;
	}

	/**
	 * Cette méthode permet de calibrer le programme en trouvant les volumes
	 * minimaux dans le contexte physique courant.
	 * 
	 * @throws Exception
	 */
	public void calibrer(double diminutionSup) throws Exception {
		Thread.sleep(250);
		System.out.println("Volume un : " + calibrerVolumeBit((byte) 1, diminutionSup));
		Thread.sleep(400);
		System.out.println("Volume zéro : " + calibrerVolumeBit((byte) 0, diminutionSup));
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
		QuiFFT quiFFT = new QuiFFT(new File(NOM_FICH_SON));
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
//		micro.captureAudioToFile(new File(NOM_FICH_SON));
//		Thread.sleep(10000);
//		micro.close();
		EcouteurDeReception edr = new EcouteurDeReception(100);
		edr.ecouter(8000);
		edr.reconstruire();
		System.out.println(edr.getReconstitueur().getRepresentationBinaire().toString());
		PasserelleFichier.ecrireOctets(edr.getReconstitueur().getRepresentationBinaire(), new File("recu.txt"));

	}
}
