package controlclasses;

public class soAktMessage extends MethodeMessage{
	
	private String body_mail;
	private Standort body_standort;

	public String getBody_mail() {
		return body_mail;
	}

	public void setBody_mail(String body_mail) {
		this.body_mail = body_mail;
	}

	public Standort getBody_standort() {
		return body_standort;
	}

	public void setBody_standort(Standort body_standort) {
		this.body_standort = body_standort;
	}

	public soAktMessage(String mail, Standort so) {	// 	Konstruktor
	
		super(1);			// Execute(1) am server
		this.body_mail=mail;
		this.body_standort=so;
		
	}
}
