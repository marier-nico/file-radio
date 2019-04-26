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
	 * Le reconstitueur de messages sera utilisé
	 * pour refaire le message reçu.
	 */
	private ReconstitueurDeMessages rdm;
	
	private int indiceFreqVoulue;
	private double volumeMinUn = -1;
	private double volumeMinZero = -1;
	public static final int WINDOW_SIZE = 1024;
	public static final int OVERLAP = 0;
	public static final double FREQUENCE_RECEPTION = 3300.0;
	
	/**
	 * Ce constructeur permet d'initialiser le
	 * reconstitueur de messages.
	 * @throws Exception 
	 */
	public EcouteurDeReception() throws Exception {
		rdm = new ReconstitueurDeMessages();
		indiceFreqVoulue = getIndiceBin(WINDOW_SIZE, OVERLAP, FREQUENCE_RECEPTION);
		
	}
	
	/**
	 * Cette méthode permet de voir les données entrantes
	 * et d'utiliser le reconstitueur de messages pour refaire
	 * le message initial.
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
	
	public void reconstruire(double volMinZero, double volMinUn) throws IOException, UnsupportedAudioFileException {
		FFTResult fft = null;
		QuiFFT quiFFT = new QuiFFT(new File("audio.wav"));
		quiFFT.windowSize(1024);
		quiFFT.windowOverlap(0);
		fft = quiFFT.fullFFT();
		System.out.println("Duration: " + fft.windowDurationMs);
		FFTFrame[] frames = fft.fftFrames;
		System.out.println("Il y a " + frames.length + " frames.");
		for(FFTFrame frame : frames) {
			analyserSignal(frame);
		}
	}
	
	int compteurBitPareil = 0;
	int dernierBitVu = -1;
	/**
	 * Cette méthode permet d'analyser une partie du signal pour
	 * savoir si un 1 ou un 0 a été reçu. Elle retourne ensuite
	 * ce bit reçu de façon optionnelle. Si rien n'a été reçu, un
	 * Optional vide est retourné.
	 * 
	 * @return un Optional qui contient le bit reçu ou rien, si rien n'a été reçu
	 */
	private Optional<Byte> analyserSignal(FFTFrame frame) {
		if(validerVolumeMin(volumeMinUn) || validerVolumeMin(volumeMinZero)) {
			throw new IllegalStateException("Il faut calibrer le programme.");
		}
		FrequencyBin bin = frame.bins[indiceFreqVoulue];
		int bit = -1;
		if(bin.amplitude >= volumeMinUn) {
			bit = 1;
		} else if(bin.amplitude >= volumeMinZero) {
			bit = 0;
		}
		
		if(bit == dernierBitVu)
			compteurBitPareil++;
		else
			compteurBitPareil = 0;
		dernierBitVu = bit;
		
		if(compteurBitPareil == 7) {
			System.out.print(dernierBitVu);
		}
		try {
			Thread.sleep(104);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Optional.empty();
	}
	
	// TODO: à faire
	private double getVolumeMinUn() throws LineUnavailableException, InterruptedException {
		ecouter(500);
		return 0;
	}
	
	private double getVolumeMinZero() throws LineUnavailableException, InterruptedException {
		ecouter(500);
		return 0;
	}
	
	public void calibrer() {
		
	}
	
	private int getIndiceBin(int windowSize, double overlap, double frequence) throws Exception {
		ecouter(150);
		FFTResult fft = null;
		QuiFFT quiFFT = new QuiFFT(new File("audio.wav"));
		quiFFT.windowSize(windowSize);
		quiFFT.windowOverlap(overlap);
		fft = quiFFT.fullFFT();
		if(fft.fftFrames[0].bins.length < 2)
			throw new IllegalStateException("Pas assez de bins.");
		FrequencyBin premierBin = fft.fftFrames[0].bins[0];
		FrequencyBin deuxiemeBin = fft.fftFrames[0].bins[1];
		double incertitude = deuxiemeBin.frequency - premierBin.frequency;
		int indice = 0;
		for(FrequencyBin bin : fft.fftFrames[0].bins) {
			if(bin.frequency >= (frequence - incertitude) && bin.frequency <= (frequence + incertitude)) {
				return indice;
			} else {
				indice++;
			}
		}
		throw new Exception("Fréquence introuvable.");
	}
	
	private static boolean validerVolumeMin(double volumeMin) {
		return volumeMin < 0;
	}
	
	public static void main(String[] args) throws Exception {
//		MicrophoneAnalyzer micro = new MicrophoneAnalyzer(Type.WAVE);
//		micro.open();
//		micro.captureAudioToFile(new File("audio.wav"));
//		Thread.sleep(10000);
//		micro.close();
		EcouteurDeReception edr = new EcouteurDeReception();
		edr.ecouter(5000);
		edr.reconstruire(-18, -9);
	}
}
