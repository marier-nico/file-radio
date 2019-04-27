package modeles.recepteur;

import java.util.ArrayList;
import java.util.List;

import modeles.OctetBinaire;
import modeles.RepresentationBinaire;

public class ReconstitueurDeMessages {
	/**
	 * L'octet en construction est la progression courante
	 * de la réception d'un octet. Ceci peut être un octet incomplet.
	 */
	private byte[] octetEnConstruction;
	/**
	 * L'indice auquel on ajoute les bits dans l'octet en construction
	 */
	private int indiceConstructionCourant;
	/**
	 * La liste des octets qui ont été complétés, qui représentent de
	 * l'information.
	 */
	private List<OctetBinaire> octetsComplets;
	
	/**
	 * Ce constructeur sert à initialiser les variables de classe à
	 * des valeurs connues.
	 */
	public ReconstitueurDeMessages() {
		octetEnConstruction = new byte[OctetBinaire.BITS_DANS_OCTET];
		indiceConstructionCourant = 0;
		octetsComplets = new ArrayList<>();
	}
	
	/**
	 * Cette méthode se charge d'ajouter les bits reçus à
	 * l'octet en construction, de sorte que des octets soient
	 * formés à partir de l'information reçue.
	 * 
	 * @param b le bit à ajouter à l'octet en construction
	 */
	public void ajouterBit(byte b) {
		if(OctetBinaire.validerBit(b)) {
			octetEnConstruction[indiceConstructionCourant++] = b;
			if(indiceConstructionCourant >= OctetBinaire.BITS_DANS_OCTET) {
				octetsComplets.add(new OctetBinaire(octetEnConstruction.clone()));
				indiceConstructionCourant = 0;
			}
		} else {
			throw new IllegalArgumentException("Le bit reçu est invalide.");
		}
	}
	
	/**
	 * Cette méthode construit une représentation binaire
	 * avec les données qui ont été transmises.
	 * 
	 * @return la RepresentationBinaire du fichier reçu
	 */
	public RepresentationBinaire getRepresentationBinaire() {
		if(octetsComplets.size() <= 0)
			throw new IllegalArgumentException("Il n'y a aucun octet à représenter.");
		return new RepresentationBinaire(octetsComplets.toArray(new OctetBinaire[octetsComplets.size()]));
	}
}
