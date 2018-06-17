package controlclasses;

public class logoutMessage extends MethodeMessage{

	private Client sender;
	
	public logoutMessage(Client sender) {
	
		super(6);
		this.setSender(sender);
	}

	public Client getSender() {
		return sender;
	}

	public void setSender(Client sender) {
		this.sender = sender;
	}

	
}
