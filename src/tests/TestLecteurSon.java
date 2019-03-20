package tests;

import static org.junit.Assert.*;

import javax.sound.sampled.LineUnavailableException;

import org.junit.Test;

import modeles.emetteur.LecteurSon;

public class TestLecteurSon {
	byte[][] donnees = {{0, 1}};

	@Test
	public void testLecteurSon() {
		try {
			LecteurSon ls = new LecteurSon(donnees, 0.0001f);
		} catch (IllegalArgumentException | LineUnavailableException ex) {
			fail();
		}
	}
	
	@Test
	public void testLecteurSonInvalide() {
		byte[][] donneesInvalides1 = {{}, {}};
		byte[][] donneesInvalides2 = {};
		try {
			LecteurSon ls = new LecteurSon(null, 0.1f);
			fail();
		} catch (Exception ex) {}
		try {
			LecteurSon ls = new LecteurSon(donneesInvalides1, 0.1f);
			fail();
		} catch (Exception ex) {}
		try {
			LecteurSon ls = new LecteurSon(donneesInvalides2, 0.1f);
			fail();
		} catch (Exception ex) {}
		try {
			LecteurSon ls = new LecteurSon(donnees, 0);
			fail();
		} catch (Exception ex) {}
		
	}

	@Test
	public void testValiderDonneesSon() {
		byte[][] donneesInvalides1 = {{}, {}};
		byte[][] donneesInvalides2 = {};
		assertTrue(LecteurSon.validerDonneesSon(donnees));
		assertFalse(LecteurSon.validerDonneesSon(donneesInvalides1));
		assertFalse(LecteurSon.validerDonneesSon(donneesInvalides2));
	}

	@Test
	public void testValiderDuree() {
		assertTrue(LecteurSon.validerDuree(0.0000000001f));
		assertFalse(LecteurSon.validerDuree(0.0f));
	}

}
