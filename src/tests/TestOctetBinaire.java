package tests;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;

import modeles.OctetBinaire;

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
		
		byte[] bits = {1, 0, 0, 1, 0, 0, 0, 1};
		try {
			OctetBinaire ob1 = new OctetBinaire(bits);
		} catch (IllegalArgumentException ex) {
			fail();
		}
	}
	
	@Test
	public void testOctetBinaireInvalide() {
		try {
			ob = new OctetBinaire(null);
			fail();
		} catch(IllegalArgumentException ex) {
		}
		
		byte[] bits = {1, 0, 0 ,1, 1, 1, 0, 0, 1};
		try {
			ob = new OctetBinaire(bits);
			fail();
		} catch(IllegalArgumentException ex) {
		}
		
		byte[] bits2 = {0, 0, 1, 1, 1, 0, 0};
		try {
			ob = new OctetBinaire(bits2);
			fail();
		} catch(IllegalArgumentException ex) {
		}
		
		byte[] bits3 = {0, 0, 1, 1, 1, 0, 2};
		try {
			ob = new OctetBinaire(bits3);
			fail();
		} catch(IllegalArgumentException ex) {
		}
	}
	
	@Test
	public void testCalculerBits() {
		byte b = 10;
		ob = new OctetBinaire(b);
		byte[] bits = {0, 0, 0, 0, 1, 0, 1, 0 };
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
		assertTrue(ob.toString().equals("00001111"));
	}

	@Test
	public void testHasNextEtNext() {
		byte[] bits = ob.getBits();
		for (int i = 0; i < bits.length; i++) {
			assertTrue(ob.hasNext());
			assertTrue(ob.next().equals(bits[i]));
		}
		try {
			ob.next();
			fail();
		} catch (NoSuchElementException ex) {
		}
		assertTrue(ob.hasNext());
	}

	@Test
	public void testIterator() {
		assertTrue(ob.iterator() == ob);
	}

	@Test
	public void testDecABin() {
		byte[] tabByte1 = ob.decABin((byte) 28);
		byte[] tabByte11 = {0,0,0,1,1,1,0,0};
		byte[] tabByte2 = ob.decABin((byte) -28);
		byte[] tabByte22 = {1,1,1,0,0,1,0,0};
		assertTrue(Arrays.equals(tabByte1, tabByte11));
		assertTrue(Arrays.equals(tabByte2, tabByte22));
	}
	
	@Test
	public void testGetOctetEnDecimal() {
		assertTrue(ob.getOctetEnDecimal() == 127);
		
		byte[] bits = {0, 0, 0, 0, 0, 1, 1, 0};
		ob = new OctetBinaire(bits);
		assertTrue(ob.getOctetEnDecimal() == 6);
		
		byte[] bits2 = {1, 1, 1, 1, 0, 1, 1, 0};
		ob = new OctetBinaire(bits2);
		assertTrue(ob.getOctetEnDecimal() == -10);
	}
	
	@Test
	public void testValiderBit() {
		assertTrue(OctetBinaire.validerBit((byte) 0));
		assertTrue(OctetBinaire.validerBit((byte) 1));
		assertFalse(OctetBinaire.validerBit((byte) -1));
		assertFalse(OctetBinaire.validerBit((byte) 2));
	}
}