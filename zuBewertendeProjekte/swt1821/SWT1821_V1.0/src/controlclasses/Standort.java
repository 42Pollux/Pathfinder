package controlclasses;

public class Standort {
	
	private String soName;
	private String strasse;
	private String ort;
	private String PLZ;

	public Standort(String name, String adresse) {
		this.soName=name;
		this.strasse=adresse;
		this.ort="Rostock";
	}
	
	public Standort(String name, String adresse, String ort) {
		this.soName=name;
		this.strasse=adresse;
		this.ort=ort;
	}
	
	public String getSoName() {
		return soName;
	}

	public void setSoName(String soName) {
		this.soName = soName;
	}
	
	public String toString() {
		
		return this.strasse + " " + this.PLZ + " " + this.ort + " " + "GERMANY";
	}
	
}
