package modeles.emetteur;

public class GenerateurSon {
	private RepresentationBinaire repr;
	private static byte[] donneesSon0;
	private static byte[] donneesSon1;
	
	public GenerateurSon(RepresentationBinaire repr, float dureeEnSec) {
		this.repr = repr;
		calculerSonsPourBits(dureeEnSec);
	}
	
	public static void calculerSonsPourBits(float dureeEnSec) {
		float qualit = 44100;
		int freq = 1000;
		int volumeMin = 50;
		int volumeMax = 150;
		int nbVals = (int) (qualit * dureeEnSec);
		donneesSon0 = new byte[nbVals];
		donneesSon1 = new byte[nbVals];
		for (int i = 0; i < nbVals; i++) {
			double angle = i / (qualit / freq) * 2.0 * Math.PI;
			donneesSon0[i] = (byte) (Math.sin(angle) * volumeMin);
			donneesSon1[i] = (byte) (Math.sin(angle) * volumeMax);
		}
	}
	
	public byte[][] getDonneesSon() {
		byte[][] donnees = new byte[repr.getOctets().length * OctetBinaire.BITS_DANS_OCTET][];
		int i = 0;
		for(OctetBinaire octet : repr) {
			for(Byte bit : octet) {
				if(bit == 0)
					donnees[i] = donneesSon0;
				else if(bit == 1)
					donnees[i] = donneesSon1;
				i++;
			}
		}
		return donnees;
	}
}
