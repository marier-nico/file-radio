package modeles.emetteur;

import java.util.Arrays;

/**
 * Cette classe représente un octet en base 2.
 * Elle sert à convertir entre un byte en décimal et un octet.
 *
 * @author Nicolas Marier
 *
 */
public class OctetBinaire {
	/**
	 * Les différents chiffres de la représentation
	 * en base 2.
	 */
	private byte[] bits;
	
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
		
		bits = new byte[7];
		calculerBits(b);
	}
	
	/**
	 * Cette méthode prend un byte décimal et inscrit les chiffres
	 * de la représentation en base 2 dans un array.
	 * 
	 * @param b le byte en décimal
	 */
	private void calculerBits(byte b) {
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
}
