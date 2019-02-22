package modeles.emetteur;

public class RepresentationBinaire {
	
	private OctetBinaire[] octets;
	
	public RepresentationBinaire(byte[] bytes) {
		octets = new OctetBinaire[bytes.length];
		
		int i = 0;
		for(byte b : bytes) {
			octets[i] = new OctetBinaire(b);
			i++;
		}
	}
	
}
