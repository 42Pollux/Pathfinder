package controlclasses;

public class gruppeErstellenMessage extends MethodeMessage{

	private String body_mail;
	
	public gruppeErstellenMessage(String mail) {
		super(3);
		this.body_mail=mail;
	}

	public String getBody_mail() {
		return body_mail;
	}

	public void setBody_mail(String body_mail) {
		this.body_mail = body_mail;
	}
	
	

}
