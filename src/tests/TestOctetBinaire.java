package tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import modeles.emetteur.OctetBinaire;

public class TestOctetBinaire {
	
	OctetBinaire ob;

	@Before
	public void testOctetBinaireValide() {
		try {
			byte b = 0;
			ob = new OctetBinaire(b);
		} catch (IllegalArgumentException ex) {
			fail();
		}
		
		try {
			byte b = 127;
			ob = new OctetBinaire(b);
		} catch (IllegalArgumentException ex) {
			fail();
		}
	}
	
	@Test
	public void testOctetBinaireInvalide() {
		try {
			byte b = -1;
			ob = new OctetBinaire(b);
			fail();
		} catch (IllegalArgumentException ex) {}
	}
	
	@Test
	public void testCalculerBits() {
		byte b = 10;
		ob = new OctetBinaire(b);
		byte[] bits = {1,0,1,0,0,0,0};
		assertArrayEquals(bits, ob.getBits());
	}
}