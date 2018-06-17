package controlclasses;

import java.util.Random;

public class Gruppe {
	
	private final String kuerzel;	//	Gruppenk�rzel
	private feel atmosphaere;		//	Gruppenatmosph�re
	private Standort Treffpunkt;
	
	
	public String getKuerzel() {	// 	hier abfrage f�r �berpr�fungen/aktualisierungen
		return kuerzel;
	}
	
	public feel getAtmosphaere() {	//	Bei Berechnung zur Selektion der DB
		return atmosphaere;
	}

	
	
	public void setAtmosphaere(feel atmosphaere) {
		this.atmosphaere = atmosphaere;
	}

	public Gruppe(){
		this.kuerzel = kuerzelInit(); 			//	K�rzel wird erstellt
		this.atmosphaere=feel.Gruppenarbeit; 	//	Gruppenarbeit als standardeinstellung
	}
	
	private String kuerzelInit(){ 				//	Initialisierung des K�rzels bei Gruppenerstellung
		String ret ="";
		final String alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		 final int N = alphabet.length();		//	hier alphabet zu nutzender Zeichen f�r K�rzel
		 
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
