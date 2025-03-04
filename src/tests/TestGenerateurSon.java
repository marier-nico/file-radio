package tests;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import modeles.RepresentationBinaire;
import modeles.emetteur.GenerateurSon;

public class TestGenerateurSon {
	
	static GenerateurSon gs;
	static RepresentationBinaire rb;
	
	@BeforeClass
	public static void setUp() {
		byte[] octets = {0, 1};
		rb = new RepresentationBinaire(octets);
	}

	@Before
	public void testGenerateurSon() {
		try {
			gs = new GenerateurSon(rb, 0.0001f);
		} catch (IllegalArgumentException ex) {
			fail();
		}
	}
	
	@Test
	public void testGenerateurSonInvalide() {
		try {
			gs = new GenerateurSon(rb, 0);
			fail();
		} catch (IllegalArgumentException ex) {}
		try {
			gs = new GenerateurSon(rb, -0.00000001f);
			fail();
		} catch (IllegalArgumentException ex) {}
		try {
			gs = new GenerateurSon(null, 1);
			fail();
		} catch (IllegalArgumentException ex) {}
		try {
			gs = new GenerateurSon(null, -1);
			fail();
		} catch (IllegalArgumentException ex) {}
	}

	@Test
	public void testCalculerSonsPourBits() {
		try {
			gs.calculerSonsPourBits(0);
			fail();
		} catch (IllegalArgumentException ex) {}
		
		float duree = 0.0001f;
		gs.calculerSonsPourBits(duree);
		byte[][] donnees = gs.getDonneesSon();
		byte[] donnees0 = {0, 7, 14, 20};
		byte[] donnees1 = {0, 14, 28, 41};
		for(int i = 0; i < 15; i++) {
			assertTrue(Arrays.equals(donnees[i], donnees0));
		}
		assertTrue(Arrays.equals(donnees[15], donnees1));
	}

	@Test
	public void testGetDonneesSon() {
		byte[][] donneesAttendues = new byte[16][];
		byte[] donnees0 = {0, 7, 14, 20};
		byte[] donnees1 = {0, 14, 28, 41};
		for(int i = 0; i < 15; i++) {
			donneesAttendues[i] = donnees0;
		}
		donneesAttendues[15] = donnees1;
		assertTrue(Arrays.deepEquals(donneesAttendues, gs.getDonneesSon()));
	}
	
	@Test
	public void testRepresentationBinaire() {
		byte[] octets = {0, 0, 1, 13};
		byte[][] donnees1 = gs.getDonneesSon();
		gs.setRepresentationBinaire(null);
		byte[][] donnees2 = gs.getDonneesSon();
		RepresentationBinaire rbin = new RepresentationBinaire(octets);
		gs.setRepresentationBinaire(rbin);
		byte[][] donnees3 = gs.getDonneesSon();
		
		assertTrue(Arrays.deepEquals(donnees1, donnees2));
		assertFalse(Arrays.deepEquals(donnees2, donnees3));
	}

}
