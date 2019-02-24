package modeles.emetteur;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RepresentationBinaire implements Iterable<OctetBinaire>, Iterator<OctetBinaire> {
	
	private OctetBinaire[] octets;
	private int octetCourant;
	
	public RepresentationBinaire(byte[] bytes) {
		octets = new OctetBinaire[bytes.length];
		octetCourant = -1;
		
		int i = 0;
		for(byte b : bytes) {
			octets[i] = new OctetBinaire(b);
			i++;
		}
	}
	
	public OctetBinaire[] getOctets() {
		return octets;
	}

	@Override
	public boolean hasNext() {
		return (octetCourant + 1) < octets.length; 
	}

	@Override
	public OctetBinaire next() {
		if(!this.hasNext()) {
			throw new NoSuchElementException();
		}
		
		return octets[++octetCourant];
	}

	@Override
	public Iterator<OctetBinaire> iterator() {
		return this;
	}
}
