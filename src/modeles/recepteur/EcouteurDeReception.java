package modeles.recepteur;

import java.util.Optional;

public class EcouteurDeReception {
	/**
	 * Le reconstitueur de messages sera utilisé
	 * pour refaire le message reçu.
	 */
	ReconstitueurDeMessages rdm;
	
	/**
	 * Ce constructeur permet d'initialiser le
	 * reconstitueur de messages.
	 */
	public EcouteurDeReception() {
		rdm = new ReconstitueurDeMessages();
	}
	
	/**
	 * Cette méthode permet de voir les données entrantes
	 * et d'utiliser le reconstitueur de messages pour refaire
	 * le message initial.
	 */
	public void ecouter() {
		/* 
		 * TODO: Ici, on implémentera la lecture des
		 * données entrantes.
		 */
		Thread th = new Thread(() -> {
			while(true) {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Cette méthode permet d'analyser une partie du signal pour
	 * savoir si un 1 ou un 0 a été reçu. Elle retourne ensuite
	 * ce bit reçu de façon optionnelle. Si rien n'a été reçu, un
	 * Optional vide est retourné.
	 * 
	 * @return un Optional qui contient le bit reçu ou rien, si rien n'a été reçu
	 */
	private Optional<Byte> analyserSignal() {
		/*
		 * TODO: Ici, on implémentera la prise de décision à savoir
		 * si il faut décoder un 1 ou un 0.
		 */
		return Optional.empty();
	}
}
