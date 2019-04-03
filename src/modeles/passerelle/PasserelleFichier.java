package modeles.passerelle;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

import modeles.OctetBinaire;
import modeles.RepresentationBinaire;

/**
 * Cette classe permet de lire le contenu d'un fichier et d'obtenir
 * les octets en base 10 qui constituent ce fichier.
 * 
 * @author Nicolas Marier
 */
public class PasserelleFichier {
	
	/**
	 * Cette méthode permet de lire les octets en décimal du fichier en paramètre.
	 * 
	 * @param fichier le fichier à lire
	 * @return octets les octets en décimal qui constituent le fichier
	 * @throws IOException si une exception est rencontrée lors de la lecture
	 */
	public static byte[] lireOctets(File fichier) throws IOException {
		return Files.readAllBytes(fichier.toPath());
	}
	
	public static void ecrireOctets(RepresentationBinaire repr, File fichier) throws IOException {
		FileOutputStream fos = new FileOutputStream(fichier);
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		for(OctetBinaire octet : repr) {
			bos.write(octet.getOctetEnDecimal());
		}
		bos.flush();
		bos.close();
	}
	
	/**
	 * Cette méthode permet de trouver un fichier à lire selon le chemin.
	 * 
	 * @param chemin le chemin pour accéder au fichier
	 * @return fichier le fichier qui se trouve au chemin spécifié
	 */
	public static File obtenirFichier(String chemin) {
		return new File(chemin);
	}
}
