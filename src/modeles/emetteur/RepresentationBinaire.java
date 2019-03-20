package modeles.emetteur;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Cette classe permet de représenter un fichier avec ses octets en base 2.
 * 
 * @author Nicolas Marier
 */
public class RepresentationBinaire implements Iterable<OctetBinaire>, Iterator<OctetBinaire> {
	
	/**
	 * Cet array contient tous les octets en base 2 du fichier.
	 */
	private OctetBinaire[] octets;
	
	/**
	 * L'octet courant est utile pour pouvoir itérer sur les octets du fichier.
	 */
	private int octetCourant;
	
	/**
	 * Cette méthode permet de prendre un fichier
	 * et de calculer sa représentation en base 2.
	 * 
	 * @param bytes les octets en décimal du fichier
	 * @throws IllegalArgumentException si les octets ne sont pas valides.
	 */
	public RepresentationBinaire(byte[] bytes) {
		if(!validerOctets(bytes)) {
			throw new IllegalArgumentException("Il doit y avoir des octets");
		}
		octets = new OctetBinaire[bytes.length];
		octetCourant = 0;
		
		int i = 0;
		for(byte b : bytes) {
			octets[i] = new OctetBinaire(b);
			i++;
		}
	}
	
	/**
	 * Cette méthode permet d'obtenir tous les octets en base 2 du fichier.
	 * 
	 * @return octets les octets en base 2 du fichier
	 */
	public OctetBinaire[] getOctets() {
		return octets;
	}

	/**
	 * Cette méthode permet de voir s'il reste des octets dans le fichier.
	 * 
	 * @see java.util.Iterator#hasNext()
	 * @return true si il reste un prochain octet false sinon
	 */
	@Override
	public boolean hasNext() {
		boolean aProchain = octetCourant < octets.length;
		if(!aProchain)
			octetCourant = 0;
		return aProchain; 
	}

	/**
	 * Cette méthode permet d'obtenir le prochain octet en base 2 du fichier.
	 * 
	 * @see java.util.Iterator#next()
	 * @return octet l'octet en base 2 suivant dans le fichier
	 */
	@Override
	public OctetBinaire next() {
		if(!this.hasNext()) {
			throw new NoSuchElementException();
		}
		
		return octets[octetCourant++];
	}

	/**
	 * Cette méthode permet d'obtenir l'itérateur pour itérer sur le fichier.
	 * 
	 * @see java.lang.Iterable#iterator()
	 * @return iterator l'itérateur du fichier
	 */
	@Override
	public Iterator<OctetBinaire> iterator() {
		return this;
	}
	
	/**
	 * Cette méthode permet de valider les octets en paramètre.
	 * Des octets sont valides s'ils ne sont pas nuls et s'il y en a un ou plus.
	 * 
	 * @param octets les octets en décimal du fichier
	 * @return true si les octets sont valides false sinon
	 */
	private static boolean validerOctets(byte[] octets) {
		return octets != null && octets.length >= 1;
	}
}
