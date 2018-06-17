package controlclasses;

import java.util.Random;

public class Gruppe {
	
	private final String kuerzel;	//	Gruppenkürzel
	private feel atmosphaere;		//	Gruppenatmosphäre
	private Standort Treffpunkt;
	
	
	public String getKuerzel() {	// 	hier abfrage für Überprüfungen/aktualisierungen
		return kuerzel;
	}
	
	public feel getAtmosphaere() {	//	Bei Berechnung zur Selektion der DB
		return atmosphaere;
	}

	
	
	public void setAtmosphaere(feel atmosphaere) {
		this.atmosphaere = atmosphaere;
	}

	public Gruppe(){
		this.kuerzel = kuerzelInit(); 			//	Kürzel wird erstellt
		this.atmosphaere=feel.Gruppenarbeit; 	//	Gruppenarbeit als standardeinstellung
	}
	
	private String kuerzelInit(){ 				//	Initialisierung des Kürzels bei Gruppenerstellung
		String ret ="";
		final String alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		 final int N = alphabet.length();		//	hier alphabet zu nutzender Zeichen für Kürzel
		 
		 Random r = new Random();
		 for (int i = 0; i < 4; i++) {
		 ret = ret + alphabet.charAt(r.nextInt(N));
		  }
		return "";
	}

	public Standort getTreffpunkt() {
		return Treffpunkt;
	}

	public void setTreffpunkt(Standort treffpunkt) {
		Treffpunkt = treffpunkt;
	}

	
}
