package modeles.emetteur;

/**
 * Cette classe permet de générer les sons qui représentent
 * les bits de la représentation binaire du fichier choisi.
 * 
 * @author Nicolas Marier
 *
 */
public class GenerateurSon {
	/**
	 * La représentation binaire à partir de laquelle on veut
	 * calculer les bits.
	 * 
	 * @see modeles.emetteur.RepresentationBinaire
	 */
	private RepresentationBinaire repr;
	/**
	 * Les données sonores représentant le son d'un bit égal à 0.
	 */
	private static byte[] donneesSon0;
	/**
	 * Les données sonores représentant le son d'un bit égal à 1.
	 */
	private static byte[] donneesSon1;
	
	/**
	 * Le constructeur permet d'initialiser la représentation binaire
	 * ainsi que la durée de chaque bit.
	 * 
	 * @param repr la représentation binaire du fichier à encoder en son
	 * @param dureeEnSec la durée d'un son pour un seul bit
	 */
	public GenerateurSon(RepresentationBinaire repr, float dureeEnSec) {
		this.repr = repr;
		calculerSonsPourBits(dureeEnSec);
	}
	
	/**
	 * Cette méthode permet de calculer les données sonores pour le son
	 * représentant un bit égal à 0 ou 1. En utilisant ceci, on évite de
	 * recalculer les données sonores pour chaque bit envoyé. On économise
	 * également la mémoire, car on utilise une référence aux sons calculés,
	 * alors on n'en garde que deux en mémoire.
	 * 
	 * @param dureeEnSec la durée du son représentant chaque bit
	 */
	public static void calculerSonsPourBits(float dureeEnSec) {
		float qualit = 44100;
		int freq = 1000;
		int volumeMin = 50;
		int volumeMax = 150;
		int nbVals = (int) (qualit * dureeEnSec);
		donneesSon0 = new byte[nbVals];
		donneesSon1 = new byte[nbVals];
		for (int i = 0; i < nbVals; i++) {
			double angle = i / (qualit / freq) * 2.0 * Math.PI;
			donneesSon0[i] = (byte) (Math.sin(angle) * volumeMin);
			donneesSon1[i] = (byte) (Math.sin(angle) * volumeMax);
		}
	}
	
	/**
	 * Cette méthode permet d'obtenir les données sonores représentant
	 * l'ensemble des bits représentant un fichier. On fait jouer ces données
	 * sous forme de sons pour les envoyer à travers une radio.
	 * 
	 * @return donnees les données sonores qui commposent le fichier
	 */
	public byte[][] getDonneesSon() {
		byte[][] donnees = new byte[repr.getOctets().length * OctetBinaire.BITS_DANS_OCTET][];
		int i = 0;
		for(OctetBinaire octet : repr) {
			for(Byte bit : octet) {
				if(bit == 0)
					donnees[i] = donneesSon0;
				else if(bit == 1)
					donnees[i] = donneesSon1;
				i++;
			}
		}
		return donnees;
	}
}
