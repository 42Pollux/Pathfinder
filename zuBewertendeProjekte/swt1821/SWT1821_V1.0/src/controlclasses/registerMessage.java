package controlclasses;

public class registerMessage extends MethodeMessage{

	private String body_mail;
	private char[] body_password;
	private String body_name;
	
	public registerMessage(String name, String mail, char[] cs) {
		
		super(7);
		this.body_name=name;
		this.body_mail=mail;
		this.body_password=cs;
	}

	public String getBody_mail() {
		return body_mail;
	}

	public void setBody_mail(String body_mail) {
		this.body_mail = body_mail;
	}

	public char[] getBody_password() {
		return body_password;
	}

	public void setBody_password(char[] body_password) {
		this.body_password = body_password;
	}

	public String getBody_name() {
		return body_name;
	}

	public void setBody_name(String body_name) {
		this.body_name = body_name;
	}
	
}
