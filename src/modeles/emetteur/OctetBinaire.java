package modeles.emetteur;

public class OctetBinaire {
	private byte[] bits;
	
	public OctetBinaire(byte b) {
		if(!validerByte(b)) {
			throw new IllegalArgumentException("Le byte ne peut pas être négatif");
		} else {
			bits = new byte[8];
			calculerBits(b);
		}
		
	}
	
	private void calculerBits(byte b) {
		String octetEnBinaire = Integer.toBinaryString(b);
		
		int i = 0;
		for(char bit : octetEnBinaire.toCharArray()) {
			bits[i] = Byte.parseByte("" + bit);
		}
	}
	
	private static boolean validerByte(byte b) {
		return b >= 0;
	}
	
	//TODO: OctetBinaireFactory(Signal[] signaux)
}
