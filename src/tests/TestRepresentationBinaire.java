package tests;

import static org.junit.Assert.*;

import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;

import modeles.emetteur.OctetBinaire;
import modeles.emetteur.RepresentationBinaire;

public class TestRepresentationBinaire {
	
	RepresentationBinaire rb;
	byte[] octets = {0, 1, 2, 3};

	@Before
	public void testRepresentationBinaire() {
		rb = new RepresentationBinaire(octets);
	}
	
	@Test
	public void testRepresentationBinaireInvalide() {
		try {
			rb = new RepresentationBinaire(null);
			fail();
		} catch (IllegalArgumentException ex) {
			
		}
		
		byte[] b = {};
		try {
			rb = new RepresentationBinaire(b);
			fail();
		} catch (IllegalArgumentException ex) {
			
		}
	}
	
	@Test
	public void testGetOctets() {
		OctetBinaire[] ob = new OctetBinaire[octets.length];
		for(int i = 0; i < octets.length; i++) {
			ob[i] = new OctetBinaire(octets[i]);
		}
		assertArrayEquals(ob, rb.getOctets());
	}
	
	@Test
	public void testHasNextEtNext() {
		for(int i = 0; i < octets.length; i++) {
			assertTrue(rb.hasNext());
			assertTrue(rb.next().equals(new OctetBinaire(octets[i])));
		}
		try {
			rb.next();
			fail();
		} catch (NoSuchElementException ex) {
		}
		assertTrue(rb.hasNext());
	}
	
	@Test
	public void testIterator() {
		assertTrue(rb.iterator() == rb);
	}

}
