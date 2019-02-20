package modeles.emetteur;

public class OctetBinaire {
	private byte[] bits;
	
	public OctetBinaire(byte b) {
		bits = new byte[8];
		calculerBits(b);
	}
	
	private void calculerBits(byte b) {
		String octetEnBinaire = Integer.toBinaryString(b);
		
		int i = 0;
		for(char bit : octetEnBinaire.toCharArray()) {
			bits[i] = Byte.parseByte("" + bit);
		}
	}
	
	//TODO: OctetBinaireFactory(Signal[] signaux)
}
