package modeles;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Cette classe représente un octet en base 2. Elle sert à convertir entre un
 * byte en décimal et un octet.
 *
 * @author Nicolas Marier
 *
 */
public class OctetBinaire implements Iterator<Byte>, Iterable<Byte> {
	/**
	 * Les différents chiffres de la représentation en base 2.
	 */
	private byte[] bits;

	/**
	 * Le nombre original en représentation décimale 
	 */
	private byte representationDecimale;
	
	/**
	 * Le bit courant pour l'itération
	 */
	private int bitCourant;

	public static final byte BITS_DANS_OCTET = 8;

	/**
	 * Ce constructeur permet de faire un octet en base 2 à partir d'un byte en
	 * décimal.
	 * 
	 * @param b le byte en décimal
	 */
	public OctetBinaire(byte b) {
		bits = new byte[BITS_DANS_OCTET];
		bitCourant = 0;
		representationDecimale = b;
		calculerBits(b);
	}

	/**
	 * Cette méthode prend un byte décimal et inscrit les chiffres de la
	 * représentation en base 2 dans un array.
	 * 
	 * @param b le byte en décimal
	 */
	private void calculerBits(byte b) {
		byte[] bits = decABin(b);
		this.bits = bits;
	}

	/**
	 * Convertit un entier de base 10 vers en base 2. Retourne un tableau de cette
	 * conversion sur 8 bits.
	 * 
	 * @param b                 le byte en décimal
	 * @param representationBin le tableau de bits
	 * @param index             du tableau
	 * @return un tableau de bits représentant la conversion du byte b en base 2.
	 */
	private byte[] decABin(byte b, byte[] representationBin, int index) {
		boolean octetNegatif = false;
		if (b == 0) {
			return representationBin;
		}
		if (b < 0) {
			b = (byte) Math.abs(b);
			octetNegatif = true;
		}
		byte reste = (byte) (b - (2 * (Math.floor(b / 2))));
		representationBin[index - 1] = reste;
		byte quotient = (byte) Math.floor(b / 2);
		index--;
		decABin(quotient, representationBin, index);
		if (octetNegatif) {
			representationBin = complementerADeux(representationBin);
		}
		return representationBin;
	}

	/**
	 * Cette méthode est une surchage pour appeler la méthode decABin pour pouvoir
	 * initier l'array de bits.
	 * 
	 * @param b le byte en décimal
	 * @return un tableau de bits représentant la conversion du byte b en base 2.
	 */
	public byte[] decABin(byte b) {
		return decABin(b, new byte[8], BITS_DANS_OCTET);
	}

	/**
	 * Cette méthode retourne les chiffres en base 2 qui représentent le byte en
	 * décimal.
	 * 
	 * @return bits les chiffres de la base 2
	 */
	public byte[] getBits() {
		return bits;
	}

	/**
	 * On calcule la complémentation à deux de notre octet et on change les
	 * bits de notre octet binaire.
	 * @param bits les bits à complémenter
	 * @return byte[] les bits une fois complémentés
	 */
	public byte[] complementerADeux(byte[] bits) {
		byte[] bitsCopie = bits.clone();
		boolean premierUnRencontre = false;
		for(int i = BITS_DANS_OCTET - 1; i >=0; i--) {
			if(bitsCopie[i] == 0) {
				if(premierUnRencontre) {
					bitsCopie[i] = 1;
				}
			} else {
				if(premierUnRencontre)
					bitsCopie[i] = 0;
				else
					premierUnRencontre = true;
			}
		}
		return bitsCopie;
	}
	
	/**
	 * Cette méthode permet d'obtenir la valeur de l'octet en décimal.
	 * 
	 * @return representationDecimale l'octet en représentation décimale
	 */
	public byte getOctetEnDecimal() {
		return representationDecimale;
	}

	// TODO: OctetBinaireFactory(Signal[] signaux)

	@Override
	public String toString() {
		return Arrays.toString(bits).replaceAll("[\\[\\], ]", "");
	}

	/**
	 * Cette méthode compare deux octets binaires. Deux octets binaires sont égaux
	 * si leurs bits sont égaux.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 * @return true si les octets sont égaux sinon faux
	 */
	@Override
	public boolean equals(Object obj) {
		boolean egaux = false;

		if (obj instanceof OctetBinaire) {
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
		boolean aProchain = bitCourant < bits.length;
		if (!aProchain)
			bitCourant = 0;
		return aProchain;
	}

	/**
	 * Cette méthode permet d'obtenir le prochain bit de l'octet.
	 * 
	 * @see java.util.Iterator#next()
	 * @return Byte le bit suivant dans l'octet
	 */
	@Override
	public Byte next() {
		if (!this.hasNext()) {
			throw new NoSuchElementException();
		}

		return bits[bitCourant++];
	}
}
