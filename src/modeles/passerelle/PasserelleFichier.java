package modeles.passerelle;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import modeles.emetteur.RepresentationBinaire;

public class PasserelleFichier {
	
	public static byte[] lireOctets(File fichier) throws IOException {
		return Files.readAllBytes(fichier.toPath());
	}
	
	public static void ecrireOctets(byte[] octets) {
		//TODO: Sprint 2-3
	}
	
	public static void ecrireOctets(RepresentationBinaire binaire) {
		//TODO: Sprint 2-3
	}
	
	public static File obtenirFichier(String chemin) {
		return new File(chemin);
	}
}
