package controlclasses;

public class loginMessage extends MethodeMessage{

	private String body_Mail;			//	übergebene email aus loginfenster
	private String body_password;		// 	übergebenes passwort aus loginfenster
	
	public loginMessage(String mail, String string) {
		super(0);						// 	loginmessage hat number 0 
										//	(auswertung in execute des servers)
		this.body_Mail=mail;
		this.body_password=string;
				
	}

	public String getBody_Mail() {
		return body_Mail;
	}

	public void setBody_Mail(String body_Mail) {
		this.body_Mail = body_Mail;
	}

	public String getBody_password() {
		return body_password;
	}

	public void setBody_password(String body_password) {
		this.body_password = body_password;
	}

	
	
	
	
}
