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
		byte[] bits = {0,0,0,1,0,1,0};
		assertArrayEquals(bits, ob.getBits());
	}
	
	@SuppressWarnings("unlikely-arg-type")
	@Test
	public void testEquals() {
		byte b = 0;
		byte b2 = 1;
		OctetBinaire ob1 = new OctetBinaire(b);
		OctetBinaire ob2 = new OctetBinaire(b2);
		assertFalse(ob1.equals(ob2));
		
		b = 1;
		OctetBinaire ob3 = new OctetBinaire(b);
		assertTrue(ob3.equals(ob2));
		
		assertFalse(ob3.equals(new Integer(2)));
	}
	
	@Test
	public void testToString() {
		byte b = 15;
		OctetBinaire ob = new OctetBinaire(b);
		assertTrue(ob.toString().equals("0001111"));
	}
}