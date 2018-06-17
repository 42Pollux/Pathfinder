package controlclasses;

public abstract class MethodeMessage extends Object {
	
	private int methodeNumber;

	public MethodeMessage(int i) {
		this.methodeNumber=i;
	}
	
	
	public int getMethodeNumber() {
		
		/*
		 * 0- login überprüfen lassen
		 * 1- standort aktualisieren
		 * 2- Gruppe beitreten
		 * 3- Gruppe erstellen
		 * 4- Gruppe verlassen
		 * 5- Vehikel ändern
		 * 6- logout
		 * 7- Registrieren
		 */
		return methodeNumber;
	}

	public void setMethodeNumber(int methodeNumber) {
		this.methodeNumber = methodeNumber;
	}


	

}
