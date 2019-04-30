package modeles.emetteur;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;

import modeles.RepresentationBinaire;
import modeles.passerelle.PasserelleFichier;

/**
 * Cette classe permet de calibrer le système en
 * échantillonnant le volume reçu des uns et des zéros.
 * 
 * @author Nicolas Marier
 *
 */
public class Calibreur {
	/**
	 * Le temps pour un bit. Si le temps est de
	 * 0.125 secondes pour un bit, l'envoi d'un
	 * octet durera une seconde.
	 */
	public static final float TEMPS_PAR_BIT = 0.125f;
	
	/**
	 * Cette méthode permet de calibrer les volumes
	 * pour les uns et ensuite pour les zéros. L'ordre
	 * est très important pour ceci.
	 * 
	 * @throws IOException
	 * @throws LineUnavailableException
	 */
	public static void calibrerTout() throws IOException, LineUnavailableException {
		calibrerUns();
		calibrerZeros();
	}
	
	/**
	 * Cette méthode permet de calibrer les uns en
	 * envoyant seulement des uns pendant une seconde.
	 * 
	 * @throws IOException
	 * @throws LineUnavailableException
	 */
	public static void calibrerUns() throws IOException, LineUnavailableException {
		File fich = PasserelleFichier.obtenirFichier("calibrer/uns");
		calibrerAvecFichier(fich);
	}
	
	/**
	 * Cette méthode permet de calibrer les uns en
	 * envoyant seulement des uns pendant une seconde.
	 * 
	 * @throws IOException
	 * @throws LineUnavailableException
	 */
	public static void calibrerZeros() throws IOException, LineUnavailableException {
		File fich = PasserelleFichier.obtenirFichier("calibrer/zeros");
		calibrerAvecFichier(fich);
	}
	
	/**
	 * Cette méthode permet de centraliser la logique
	 * commune aux autre méthodes servant à la calibration. 
	 * 
	 * @param fich le fichier pour le calibrage
	 * @throws IOException
	 * @throws LineUnavailableException
	 */
	private static void calibrerAvecFichier(File fich) throws IOException, LineUnavailableException {
		RepresentationBinaire repr = new RepresentationBinaire(PasserelleFichier.lireOctets(fich));
		GenerateurSon gs = new GenerateurSon(repr, TEMPS_PAR_BIT);
		LecteurSon ls = new LecteurSon(gs.getDonneesSon(), TEMPS_PAR_BIT);
		ls.lireSons();
	}
}
