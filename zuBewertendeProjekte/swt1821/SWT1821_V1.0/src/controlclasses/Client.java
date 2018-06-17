package controlclasses;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
	
	private String email;   		// email - schlüssel in DB
	
	private String name;			// name des Nutzers
	private Vehikel vehikel;		// ausgewähltes Vehikel - bestimmt Fahrtzeit
	private Standort standort;		//  implizierter Standort - einzulesen über google GEOlocation api?
	
	public Socket clientSocket;		// Socket zur Kommunikation mit Server
	
	public int getMethode(int i) {
		//Funktion zur Übergabe der ausgewählten Methode des Clients an server
		return i;
	}
	
	/*
	 * public static void main(String args[]) {
	 
		try {
			
			Socket client = new Socket("139.30.96.135",3306);
			DataOutputStream output = new DataOutputStream(client.getOutputStream());
			output.writeInt(new loginMessage("marc.kruse@gmx.de", "1234").getMethodeNumber());
			
			DataInputStream input = new DataInputStream(client.getInputStream());
			System.out.println(input.read());
			client.close();
			
		} catch (UnknownHostException e) {
			System.out.println("Unknown host localhost");
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("NO I/O");
			System.exit(1);
		} 
		
		
	}

	*/
	
	// Konstruktoren
	
	public Client() {
		
	}
	
	public Client(String name, String email, char[] pw) {
		setEmail(email);
		setName(name);
		setPassword(pw);
	}
	
	public Client(String email, char[] pw) {
		setEmail(email);
		setPassword(pw);
	}
	
	
	//Getter and Setters
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

/* 									// getPassword zu unsicher
 * 	public char[] getPassword() {
 
		return password;
	}
*/
	public void setPassword(char[] password) {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Vehikel getVehikel() {
		return vehikel;
	}

	public void setVehikel(Vehikel vehikel) {
		this.vehikel = vehikel;
	}

	public Standort getStandort() {
		return standort;
	}

	public void setStandort(Standort standort) {
		this.standort = standort;
	}

}
