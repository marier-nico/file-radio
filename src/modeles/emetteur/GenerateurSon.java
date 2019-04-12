package modeles.emetteur;

import modeles.OctetBinaire;
import modeles.RepresentationBinaire;

/**
 * Cette classe permet de générer les sons qui représentent les bits de la
 * représentation binaire du fichier choisi.
 * 
 * @author Nicolas Marier
 *
 */
public class GenerateurSon {
	/**
	 * La représentation binaire à partir de laquelle on veut calculer les bits.
	 * 
	 * @see modeles.RepresentationBinaire
	 */
	private RepresentationBinaire repr;
	/**
	 * Les données sonores représentant le son d'un bit égal à 0.
	 */
	private byte[] donneesSon0;
	/**
	 * Les données sonores représentant le son d'un bit égal à 1.
	 */
	private byte[] donneesSon1;

	/**
	 * Le constructeur permet d'initialiser la représentation binaire ainsi que la
	 * durée de chaque bit si les paramètres sont valides.
	 * 
	 * @param repr       la représentation binaire du fichier à encoder en son
	 * @param dureeEnSec la durée d'un son pour un seul bit
	 */
	public GenerateurSon(RepresentationBinaire repr, float dureeEnSec) {
		if (!validerRepresentation(repr) || !validerDureeEnSec(dureeEnSec))
			throw new IllegalArgumentException(
					"La représentation binaire ne doit pas être nulle et la durée doit être plus grande que 0 secondes");
		this.repr = repr;
		calculerSonsPourBits(dureeEnSec);
	}

	/**
	 * Cette méthode permet de calculer les données sonores pour le son représentant
	 * un bit égal à 0 ou 1. En utilisant ceci, on évite de recalculer les données
	 * sonores pour chaque bit envoyé. On économise également la mémoire, car on
	 * utilise une référence aux sons calculés, alors on n'en garde que deux en
	 * mémoire.
	 * 
	 * @param dureeEnSec la durée du son représentant chaque bit
	 */
	public void calculerSonsPourBits(float dureeEnSec) {
		if(!validerDureeEnSec(dureeEnSec))
			throw new IllegalArgumentException("La durée doit être plus grande que 0 secondes");
		float qualit = 44100;
		int freq = 25000;
		int volumeMin = 20;
		int volumeMax = 90;
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
	 * Cette méthode permet de changer la représentation binaire à utiliser pour
	 * trouver les données sonores.
	 * 
	 * @param repr la représentation binaire du fichier
	 */
	public void setRepresentationBinaire(RepresentationBinaire repr) {
		if(validerRepresentation(repr))
			this.repr = repr;
	}

	/**
	 * Cette méthode permet d'obtenir les données sonores représentant l'ensemble
	 * des bits représentant un fichier. On fait jouer ces données sous forme de
	 * sons pour les envoyer à travers une radio.
	 * 
	 * @return donnees les données sonores qui commposent le fichier
	 */
	public byte[][] getDonneesSon() {
		byte[][] donnees = new byte[repr.getOctets().length * OctetBinaire.BITS_DANS_OCTET][];
		int i = 0;
		for (OctetBinaire octet : repr) {
			for (Byte bit : octet) {
				if (bit == 0)
					donnees[i] = donneesSon0;
				else if (bit == 1)
					donnees[i] = donneesSon1;
				i++;
			}
		}
		return donnees;
	}

	/**
	 * Cette méthode permet de valider si la représentation binaire est valide. Une
	 * représentation binaire est valide si elle n'est pas nulle.
	 * 
	 * @param repr la représentation binaire
	 * @return validite true si la représentation est valide ou faux sinon
	 */
	public static boolean validerRepresentation(RepresentationBinaire repr) {
		return repr != null;
	}

	/**
	 * Cette méthode permet de valider si la durée en secondes est valide. La durée
	 * est valide si elle est plus grande que 0 secondes.
	 * 
	 * @param duree la durée en secondes
	 * @return validite true si la durée est valide ou faux sinon
	 */
	public static boolean validerDureeEnSec(float duree) {
		return duree > 0;
	}
}
