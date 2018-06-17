package veranstaltungErstellen;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Veranstaltung {

	private int id;
	private String vPasswort = "";
	private String organisator;

	private String titel = "";
	private String beschreibung = "";

	private int treffpunkttyp; // 0 für Treffpunkt offen, 1 für Treffpunkt
								// festgelegt, 2 für Treffpunkt terminabhängig festgelegt
	private int termintyp; // 1 für Termin festgelegt, 2 für 2 Termine zur Wahl,
							// 3 für 3 Termine zur Wahl
	private String treffpunkt = "";

	private Date termin;
	private Date frist;
	private boolean abgeschlossen = false; // false, wenn der Organisator die
											// Zusammenfassung
											// (Klasse Treffpunkt) noch nicht
											// ausgefüllt hat
											// true, wenn die Zusammenfassung
											// ausgefüllt und
											// die Teilnehmer benachrichtigt
											// sind

	private Date[] terminauswahl = new Date[3];

	private int[] terminvotes = new int[3];
	private String[] zugehoerigeTreffpunkte = new String[3];
	private String[] zugehoerigeStandorte = new String[3];

	public boolean getAbgeschlossen() {
		return abgeschlossen;
	}

	public void setAbgeschlossen(boolean abgeschlossen) {
		this.abgeschlossen = abgeschlossen;
	}

	public int getTermintyp() {
		return termintyp;
	}

	public void setTermintyp(int termintyp) {
		this.termintyp = termintyp;
	}

	public int getTreffpunkttyp() {
		return treffpunkttyp;
	}

	public void setTreffpunkttyp(int treffpunkttyp) {
		this.treffpunkttyp = treffpunkttyp;
	}

	public Date getTermin() {
		return termin;
	}

	public void setTermin(Date termin) {
		this.termin = termin;
	}

	public String getOrganisator() {
		return organisator;
	}

	public void setOrganisator(String organisator) {
		this.organisator = organisator;
	}

	public Date[] getTerminauswahl() {
		return terminauswahl;
	}

	public void setTerminauswahl(Date termin, int stelle) {
		this.terminauswahl[stelle] = termin;
	}

	public String[] getZugehoerigeTreffpunkte() {
		return zugehoerigeTreffpunkte;
	}

	public void setZugehoerigeTreffpunkte(String zugehoerigerTreffpunkt, int stelle) {
		this.zugehoerigeTreffpunkte[stelle] = zugehoerigerTreffpunkt;
	}

	public String[] getZugehoerigeStandorte() {
		return zugehoerigeStandorte;
	}

	public void setZugehoerigeStandorte(String zugehoerigerStandort, int stelle) {
		this.zugehoerigeStandorte[stelle] = zugehoerigerStandort;
	}
	
	public int[] getTerminvotes() {
		return terminvotes;
	}

	public void setTerminvotes(int stelle) {
		this.terminvotes[stelle]++;
	}

	public Date getFrist() {
		return frist;
	}

	public void setFrist(Date frist) {
		this.frist = frist;
	}

	public String toString() {
		return titel + " \n" + termin + " \n" + treffpunkt;
	}

	public String getTitel() {
		return titel;
	}

	public void setTitel(String titel) {
		this.titel = titel;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getBeschreibung() {
		return beschreibung;
	}

	public void setBeschreibung(String beschreibung) {
		this.beschreibung = beschreibung;
	}

	public String getvPasswort() {
		return vPasswort;
	}

	public void setvPasswort(String vPasswort) {
		this.vPasswort = vPasswort;
	}

	public String getTreffpunkt() {
		return treffpunkt;
	}

	public void setTreffpunkt(String treffpunkt) {
		this.treffpunkt = treffpunkt;
	}

}
