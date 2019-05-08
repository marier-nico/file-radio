package modeles.recepteur;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
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
	private double volumeMinUn = -15;
	/**
	 * Le volume minimal pour un zéro, après la calibration.
	 */
	private double volumeMinZero = -20;
	/**
	 * La taille de la fenêtre pour calculer les FFT.
	 */
	public static final int WINDOW_SIZE = 8;
	/**
	 * Le pourcentage des fenêtres du FFT qui sont superposées.
	 */
	public static final int OVERLAP = 0;
	/**
	 * La fréquence à laquelle on envoie (c'est la même que celle que l'on reçoit)
	 */
	public static final double FREQUENCE_RECEPTION = 3300.0;

	/**
	 * Le nom du fichier temporaire dans lequel on écrit les sons que l'on reçoit.
	 */
	private static final String NOM_FICH_SON = "audio.wav";

	/**
	 * L'objet nous permettant d'écrire les fichiers sonores.
	 */
	private MicrophoneAnalyzer micro = null;

	/**
	 * Ce constructeur permet d'initialiser le reconstitueur de messages.
	 * 
	 * @throws Exception
	 */
	public EcouteurDeReception() throws Exception {
		indiceFreqVoulue = getIndiceBin(FREQUENCE_RECEPTION);
	}

	/**
	 * Cette méthode permet de voir les données entrantes et d'utiliser le
	 * reconstitueur de messages pour refaire le message initial.
	 * 
	 * @throws LineUnavailableException
	 * @throws InterruptedException
	 */
	public void ecouter(long millisecondes) throws LineUnavailableException, InterruptedException {
		micro = new MicrophoneAnalyzer(Type.WAVE);
		micro.open();
		micro.captureAudioToFile(new File(NOM_FICH_SON));
		Thread.sleep(millisecondes);
		micro.close();
	}

	/**
	 * Cette méthode permet d'arrêter l'écoute. Si l'écoute n'est pas en cours, il
	 * ne se passe rien.
	 */
	public void arretEcoute() {
		if (micro != null)
			micro.close();
	}

	/**
	 * Cette méthode permet d'obtenir le reconstitueur de messages. Il est
	 * nécessaire pour recréer le fichier.
	 * 
	 * @return le reconstitudur de messages
	 */
	public ReconstitueurDeMessages getReconstitueur() {
		return rdm;
	}

	/**
	 * Cette méthode permet de reconstruire le signal reçu du son vers une
	 * représentation.
	 * 
	 * @param tempsParBit le temps pour un bit (en millisecondes)
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 */
	public void reconstruire(double tempsParBit) throws IOException, UnsupportedAudioFileException {
		if (tempsParBit <= 0) {
			throw new IllegalStateException("Le temps par bit doit être plus grand que 0.");
		}

		rdm = new ReconstitueurDeMessages();
		FFTResult fft = getResultatFFT(WINDOW_SIZE, OVERLAP);
		FFTFrame[] frames = fft.fftFrames;
		System.out.println("Il y a " + frames.length + " frames.");

		double tempsDebutReception = 0;
		int indiceDebut = 0;
		double tempsFinReception = 0;
		int indiceFin = 0;

		for (int i = 0; i < frames.length; i++) {
			double moyenne = Stream.of(frames)
				  .skip(i)
				  .limit(4)
				  .mapToDouble(f -> f.bins[indiceFreqVoulue].amplitude)
				  .average().orElseGet(() -> 0);

			Optional<Byte> bitRecu = analyserSignal(moyenne);
			if (bitRecu.isPresent()) {
				System.out.println("Moyenne début: " + moyenne);
				tempsDebutReception = frames[i].frameStartMs;
				indiceDebut = i;
				break;
			}
		}

		for (int i = frames.length - 1; i >= 0; i--) {
			double moyenne = Stream.of(frames)
					  .skip(i)
					  .limit(4)
					  .mapToDouble(f -> f.bins[indiceFreqVoulue].amplitude)
					  .average().orElseGet(() -> 0);
			
			Optional<Byte> bitRecu = analyserSignal(frames[i]);
			if (bitRecu.isPresent()) {
				System.out.println("Moyenne fin: " + moyenne);
				tempsFinReception = frames[i].frameEndMs;
				indiceFin = i;
				break;
			}
		}
		System.out.println("TEMPS DEBUT: " + tempsDebutReception);
		System.out.println("TEMPS FIN: " + tempsFinReception);

		FFTFrame[] framesImportantes = Arrays.copyOfRange(frames, indiceDebut, indiceFin + 1);
		System.out.println("FRAMES IMOIRTANTS: " + framesImportantes.length);
		double nbBits = (tempsFinReception - tempsDebutReception) / tempsParBit;
		double framesParBit = (double) framesImportantes.length / nbBits;
		System.out.println("FRAMES PAR BIT: " + framesParBit);
		System.out.println("TEMPS PAR BIT: " + tempsParBit);

		int bitEnCours = 1;
		long indiceMin = 0;
		while (bitEnCours <= nbBits) {
			System.out.println("Bit en cours: " + bitEnCours);
			System.out.println("Inice min: " + indiceMin);
			long indiceMax = Math.round((double) bitEnCours * framesParBit);
			System.out.println("Indice max: " + indiceMax);
			System.out.println("Indice max for: " + (indiceMax - 1));

			double sommeCourante = 0;
			int nbValeursPourSomme = 0;
			for (long i = indiceMin; i < indiceMax && i < framesImportantes.length; i++) {
				sommeCourante += framesImportantes[(int) i].bins[indiceFreqVoulue].amplitude;
				nbValeursPourSomme++;
			}
			double moyenne = sommeCourante / (double) nbValeursPourSomme;

			Optional<Byte> bitRecu = analyserSignal(moyenne);

			if (bitRecu.isPresent()) {
				System.out.println("Bit reçu: " + bitRecu.get() + "(" + moyenne + ")");
				rdm.ajouterBit(bitRecu.get());
			} else {
				System.out.println("RIEN REÇU: " + moyenne);
				rdm.ajouterBit((byte) 0);
			}
			System.out.println("---------");

			bitEnCours++;
			indiceMin = indiceMax;
		}
		System.out.println("------------------------------------------------");
	}

	/**
	 * Cette méthode permet d'analyser une partie du signal pour savoir si un 1 ou
	 * un 0 a été reçu. Elle retourne ensuite ce bit reçu de façon optionnelle. Si
	 * rien n'a été reçu, un Optional vide est retourné.
	 * 
	 * @return un Optional qui contient le bit reçu ou rien, si rien n'a été reçu
	 */
	private Optional<Byte> analyserSignal(FFTFrame frame) {
		FrequencyBin bin = frame.bins[indiceFreqVoulue];
		return analyserSignal(bin.amplitude);
	}

	private Optional<Byte> analyserSignal(double amplitude) {
		if (!validerVolumeMin(volumeMinUn) || !validerVolumeMin(volumeMinZero)) {
			throw new IllegalStateException("Il faut calibrer le programme.");
		}

		byte bit = -1;
		if (amplitude >= volumeMinUn) {
			bit = 1;
		} else if (amplitude >= volumeMinZero) {
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
	public double calibrerVolumeBit(byte unOuZero, double diminutionSup) throws Exception {
		ecouter(1000);
		FFTResult resultat = getResultatFFT(WINDOW_SIZE, OVERLAP);
		OptionalDouble bitPetit = Stream.of(resultat.fftFrames).mapToDouble(f -> f.bins[indiceFreqVoulue].amplitude)
				.min();

		double volumeBitMoyen = bitPetit.orElseThrow(
				() -> new IllegalStateException("Impossible de déterminer le volume d'un \"" + unOuZero + "\"."))
				- diminutionSup;
		if (unOuZero == 1) {
			 volumeMinUn = volumeBitMoyen;
		} else if (unOuZero == 0) {
			 volumeMinZero = volumeBitMoyen;
		} else {
			throw new IllegalArgumentException("On peut seulement calibrer pour les uns et les zéros.");
		}

		return volumeBitMoyen;
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

	public double getVolumeUn() {
		return volumeMinUn;
	}

	public double getVolumeZero() {
		return volumeMinZero;
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

}
