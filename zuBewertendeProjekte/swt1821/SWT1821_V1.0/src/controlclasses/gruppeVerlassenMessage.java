package controlclasses;

public class gruppeVerlassenMessage extends MethodeMessage{
	
	public gruppeVerlassenMessage(String mail, String kuerzel) {
		super(4);
		this.body_kuerzel=kuerzel;
		this.body_mail=mail;
	}
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
	private String body_mail;
	private String body_kuerzel;
	
	

}
