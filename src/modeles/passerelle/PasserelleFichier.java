package modeles.passerelle;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.util.List;

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
	
	/**
	 * Cette méthode permet d'écrire des octets dans un fichier passé en paramètre.
	 * 
	 * @param repr la représentation du fichier à écrire
	 * @param fichier le fichier où écrire
	 * @throws IOException s'il est impossible d'écrire dans le fichier
	 */
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
	 * Cette méthode permet de lire les lignes du fichier en paramètre.
	 * 
	 * @param fichier le fichier à lire
	 * @return les lignes du fichier
	 * @throws IOException
	 */
	public static List<String> lireCaracteres(File fichier) throws IOException {
		return Files.readAllLines(fichier.toPath());
	}
	
	/**
	 * Cette méthode permet d'écrire des lignes dans un fichier.
	 * 
	 * @param fichier le fichier où écrire les caractères
	 * @param lignes les lignes à écrire
	 * @throws IOException
	 */
	public static void ecrireCaracteres(File fichier, Iterable<? extends CharSequence> lignes) throws IOException {
		Files.write(fichier.toPath(), lignes, StandardOpenOption.WRITE);
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
