package controlclasses;

public class vehikelMessage extends MethodeMessage{
	
	private String body_mail;
	private Vehikel vehikel;
	private Client sender;
	
	public vehikelMessage(Vehikel vehikel, String mail, Client sender) {
		
		super(5); 			// execute(5) serverweit
		this.vehikel=vehikel;
		this.body_mail=mail;
		this.sender=sender;
		
	}

	public Client getSender() {
		return sender;
	}

	public void setSender(Client sender) {
		this.sender = sender;
	}

	public String getBody_mail() {
		return body_mail;
	}

	public void setBody_mail(String body_mail) {
		this.body_mail = body_mail;
	}

	public Vehikel getVehikel() {
		return vehikel;
	}

	public void setVehikel(Vehikel vehikel) {
		this.vehikel = vehikel;
	}

}
