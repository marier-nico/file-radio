package modeles.emetteur;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Cette classe représente un octet en base 2.
 * Elle sert à convertir entre un byte en décimal et un octet.
 *
 * @author Nicolas Marier
 *
 */
public class OctetBinaire implements Iterator<Byte>, Iterable<Byte> {
	/**
	 * Les différents chiffres de la représentation
	 * en base 2.
	 */
	private byte[] bits;
	
	/**
	 * Le bit courant pour l'itération
	 */
	private int bitCourant;
	
	public static final byte BITS_DANS_OCTET = 7;
	
	/**
	 * Ce constructeur permet de faire un octet en base 2
	 * à partir d'un byte en décimal.
	 * 
	 * @param b le byte en décimal
	 */
	public OctetBinaire(byte b) {
		if(!validerByte(b)) {
			throw new IllegalArgumentException("Le byte ne peut pas être négatif");
		}
		
		bits = new byte[BITS_DANS_OCTET];
		bitCourant = 0;
		calculerBits(b);
	}
	
	/**
	 * Cette méthode prend un byte décimal et inscrit les chiffres
	 * de la représentation en base 2 dans un array.
	 * 
	 * @param b le byte en décimal
	 */
	private void calculerBits(byte b) {
		if(!validerByte(b))
			throw new IllegalArgumentException("Le byte ne peut pas être négatif");
		String octetEnBinaire = Integer.toBinaryString(b);
		octetEnBinaire = (new StringBuilder(octetEnBinaire).reverse().toString());
		int i = bits.length - 1;
		for(char bit : octetEnBinaire.toCharArray()) {
			bits[i] = Byte.parseByte("" + bit);
			i--;
		}
	}
	
	/**
	 * Cette méthode retourne les chiffres en base 2
	 * qui représentent le byte en décimal.
	 * 
	 * @return bits les chiffres de la base 2
	 */
	public byte[] getBits() {
		return bits;
	}
	
	/**
	 * Cette méthode permet la validation du byte en décimal.
	 * 
	 * @param b le byte en décimal
	 * @return true si le byte est valide sinon faux
	 */
	private static boolean validerByte(byte b) {
		return b >= 0;
	}
	
	//TODO: OctetBinaireFactory(Signal[] signaux)
	
	@Override
	public String toString() {
		return Arrays.toString(bits).replaceAll("[\\[\\], ]", "");
	}
	
	/**
	 * Cette méthode compare deux octets binaires.
	 * Deux octets binaires sont égaux si leurs bits sont égaux.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 * @return true si les octets sont égaux sinon faux
	 */
	@Override
	public boolean equals(Object obj) {
		boolean egaux = false;
		
		if(obj instanceof OctetBinaire) {
			OctetBinaire autre = (OctetBinaire) obj;
			egaux = Arrays.equals(bits, autre.bits);
		}
		
		return egaux;
	}

	/**
	 * Cette méthode permet d'obtenir l'itérateur pour itérer sur l'octet.
	 * 
	 * @see java.lang.Iterable#iterator()
	 * @return iterator l'itérateur de l'octet
	 */
	@Override
	public Iterator<Byte> iterator() {
		return this;
	}

	/**
	 * Cette méthode permet de voir s'il reste des bits dans l'octet.
	 * 
	 * @see java.util.Iterator#hasNext()
	 * @return true si il reste un prochain bit false sinon
	 */
	@Override
	public boolean hasNext() {
		return bitCourant < bits.length;
	}

	/**
	 * Cette méthode permet d'obtenir le prochain bit de l'octet.
	 * 
	 * @see java.util.Iterator#next()
	 * @return Byte le bit suivant dans l'octet
	 */
	@Override
	public Byte next() {
		if(!this.hasNext()) {
			throw new NoSuchElementException();
		}
		
		return bits[bitCourant++];
	}
}
