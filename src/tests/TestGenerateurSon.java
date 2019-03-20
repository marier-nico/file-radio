package tests;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import modeles.emetteur.GenerateurSon;
import modeles.emetteur.RepresentationBinaire;

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
		byte[] donnees1 = {0, 21, 42, 62};
		for(int i = 0; i < 13; i++) {
			assertTrue(Arrays.equals(donnees[i], donnees0));
		}
		assertTrue(Arrays.equals(donnees[13], donnees1));
	}

	@Test
	public void testGetDonneesSon() {
		byte[][] donneesAttendues = new byte[14][];
		byte[] donnees0 = {0, 7, 14, 20};
		byte[] donnees1 = {0, 21, 42, 62};
		for(int i = 0; i < 13; i++) {
			donneesAttendues[i] = donnees0;
		}
		donneesAttendues[13] = donnees1;
		assertTrue(Arrays.deepEquals(donneesAttendues, gs.getDonneesSon()));
	}

}
