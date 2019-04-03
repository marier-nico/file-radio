package tests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import modeles.RepresentationBinaire;
import modeles.passerelle.PasserelleFichier;

public class TestPasserelleFichier {

	@Test
	public void testLireFichier() {
		try {
			File fich = PasserelleFichier.obtenirFichier(getClass().getResource("/tests/FichierLireTest.txt").getPath());
			byte[] octets = PasserelleFichier.lireOctets(fich);
			byte[] octetsAttendus = {97, 115, 100, 102};
			assertArrayEquals(octetsAttendus, octets);
		} catch (IOException e) {
			fail(e.toString());
		}
	}
	
	@Test
	public void testEcrireFichier() {
		try {
			File fich = PasserelleFichier.obtenirFichier("src/tests/FichierEcrireTest.txt");
			byte[] octets = {97, 115, 100, 102};
			RepresentationBinaire rb = new RepresentationBinaire(octets);
			PasserelleFichier.ecrireOctets(rb, fich);
			byte[] octetsLus = PasserelleFichier.lireOctets(fich);
			assertArrayEquals(octets, octetsLus);
			fich.delete();
		} catch (IOException ex) {
			fail(ex.toString());
		}
	}

}
