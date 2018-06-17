package controlclasses;

public class gruppeBeitretenMessage extends MethodeMessage{
	
	private String body_mail;
	private String body_kuerzel;
	
	public gruppeBeitretenMessage(String mail, String kuerzel) {
		
		super(2); 					//	Execute(2) serverweit
		this.body_kuerzel=kuerzel;
		this.body_mail=mail;
		
	}
	
	// Getters und Setters

	public String getBody_mail() {
		return body_mail;
	}

	public void setBody_mail(String body_mail) {
		this.body_mail = body_mail;
	}

	public String getBody_kuerzel() {
		return body_kuerzel;
	}

	public void setBody_kuerzel(String body_kuerzel) {
		this.body_kuerzel = body_kuerzel;
	}
	

}
