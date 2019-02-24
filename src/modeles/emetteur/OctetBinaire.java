package modeles.emetteur;

import java.util.Arrays;

public class OctetBinaire {
	private byte[] bits;
	
	public OctetBinaire(byte b) {
		if(!validerByte(b)) {
			throw new IllegalArgumentException("Le byte ne peut pas être négatif");
		}
		
		bits = new byte[7];
		calculerBits(b);
	}
	
	private void calculerBits(byte b) {
		String octetEnBinaire = Integer.toBinaryString(b);
		octetEnBinaire = (new StringBuilder(octetEnBinaire).reverse().toString());
		int i = bits.length - 1;
		for(char bit : octetEnBinaire.toCharArray()) {
			bits[i] = Byte.parseByte("" + bit);
			i--;
		}
	}
	
	public byte[] getBits() {
		return bits;
	}
	
	private static boolean validerByte(byte b) {
		return b >= 0;
	}
	
	//TODO: OctetBinaireFactory(Signal[] signaux)
	
	@Override
	public String toString() {
		return Arrays.toString(bits).replaceAll("[\\[\\], ]", "");
	}
}
