package tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import modeles.RepresentationBinaire;
import modeles.recepteur.ReconstitueurDeMessages;

public class TestReconstitueurDeMessages {
	ReconstitueurDeMessages rdm;

	@Before
	public void testReconstitueurDeMessages() {
		rdm = new ReconstitueurDeMessages();
	}
	
	@Test
	public void testAjouterBit() {
		try {
			rdm.ajouterBit((byte) -1);
			fail();
		} catch (IllegalArgumentException ex) {
		}
		try {
			rdm.ajouterBit((byte) 2);
			fail();
		} catch (IllegalArgumentException ex) {
		}
		
		for(int i = 0; i < 6; i++)
			rdm.ajouterBit((byte) 0);
		
		rdm.ajouterBit((byte) 1);
		rdm.ajouterBit((byte) 1);
		
		RepresentationBinaire repr = rdm.getRepresentationBinaire();
		byte[] octets = repr.getOctets()[0].getBits();
		
		for(int i = 0; i < 6; i++)
			assertTrue(octets[i] == 0);
		
		assertTrue(octets[6] == 1);
		assertTrue(octets[7] == 1);
	}
	
	@Test
	public void testGetRepresentationBinaire() {
		rdm.ajouterBit((byte) 1);
		try {
			RepresentationBinaire repr = rdm.getRepresentationBinaire();
			fail();
		} catch (IllegalArgumentException ex) {
		}
		
		for(int i = 0; i < 7; i++) {
			rdm.ajouterBit((byte) 0);
		}
		RepresentationBinaire repr = rdm.getRepresentationBinaire();
		assertTrue(repr.getOctets()[0].getOctetEnDecimal() == -128);
	}

}
