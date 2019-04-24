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
	
	/**
	 * Ce constructeur permet d'initialiser le
	 * reconstitueur de messages.
	 */
	public EcouteurDeReception() {
		rdm = new ReconstitueurDeMessages();
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
		quiFFT.windowSize(4096);
		quiFFT.windowOverlap(0);
		fft = quiFFT.fullFFT();
		System.out.println("Duration: " + fft.windowDurationMs);
		FFTFrame[] frames = fft.fftFrames;
		System.out.println("Il y a " + frames.length + " frames.");
		for(FFTFrame frame : frames) {
			analyserSignal(frame, volMinZero, volMinUn);
		}
	}
	
	/**
	 * Cette méthode permet d'analyser une partie du signal pour
	 * savoir si un 1 ou un 0 a été reçu. Elle retourne ensuite
	 * ce bit reçu de façon optionnelle. Si rien n'a été reçu, un
	 * Optional vide est retourné.
	 * 
	 * @return un Optional qui contient le bit reçu ou rien, si rien n'a été reçu
	 */
	private Optional<Byte> analyserSignal(FFTFrame frame, double volMinZero, double volMinUn) {
		FrequencyBin bin = frame.bins[1690];
		System.out.println("Amplitude: " + bin.amplitude);
		//System.out.println("Freq: " + bin.frequency);
		//Modifier amplitudes pour que ça marche
		if(bin.amplitude >= volMinUn) {
			System.out.print("1");
		} else if(bin.amplitude >= volMinZero) {
			System.out.print("0");
		}
		return Optional.empty();
	}
	
	public static void main(String[] args) throws LineUnavailableException, InterruptedException, IOException, UnsupportedAudioFileException {
//		MicrophoneAnalyzer micro = new MicrophoneAnalyzer(Type.WAVE);
//		micro.open();
//		micro.captureAudioToFile(new File("audio.wav"));
//		Thread.sleep(10000);
//		micro.close();
		EcouteurDeReception edr = new EcouteurDeReception();
		edr.ecouter(10000);
		edr.reconstruire(-22, -18);
	}
}
