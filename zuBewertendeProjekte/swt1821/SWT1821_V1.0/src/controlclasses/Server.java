package controlclasses;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Server {
	
	static private ServerSocket server;
	private Connection con = null;
	
	public void DatabaseHelper(){
		try{
			Class.forName("com.mysql.jdbc.Driver");	
		}catch(ClassNotFoundException ex){
			System.err.println("Der Datenbanktreiber com.mysql.jdbc.Driver wurde nicht gefunden");
			System.exit(-1);
		}
	}
	
	public void connect(){
		try{
			con =  DriverManager.getConnection( "jdbc:mysql://139.30.96.135/swt1821DB?user=root&password=root"); 
		}catch(SQLException ex){
			System.err.println("Die Verbindung zur Datenbank konnte nicht hergestellt werden. Abbruch.");
			System.exit(-1);
		}
	}
	
	public void close(){
		try{
			con.close();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
		
    public Server() throws Exception {
    	
    	try {
    		server = new ServerSocket(3306);
    		server.setSoTimeout(100000000);        
    		
    	}catch(SocketException e) {
    		e.printStackTrace();
    		
    	}catch(IOException e) {
    		e.printStackTrace();
    	}
    }
    public void execute(MethodeMessage object) throws Exception {
    	switch(object.getMethodeNumber()){ 		// Funktionsauswahl der zu erwarteten Methode des Servers aka 
    											//	neuen standort hinzufügen/Standort ändern/SO abfragen etc	
    	case 0:
    		
    		Statement stmtLogin = con.createStatement();	// Connectionstatement erstellen
    		String queryLogin = "SELECT eMail, passwort FROM Nutzer WHERE eMail=\""+((loginMessage) object).getBody_Mail()+"\""; //eMail+Passwort abfrage
			ResultSet rsLogin = stmtLogin.executeQuery( queryLogin );
			Answer a = new Answer();		// Antwort Objekt erstellen
			
			if(rsLogin.getString(2)==((loginMessage) object).getBody_password().toString()) // Vergleich Passwort mit Datenbank Passwort
			{
				
				a.write(1);	//Antwort = true
				System.out.println("eingeloggt");// antworte dass passwort richtig ist, fortfahren mit login
			}
			else {
				
				a.write(0);	//Antwort = false
			}    		
    		
			stmtLogin.close();	// Input&Output Stream schließen
			rsLogin.close();
			
			OutputStreamWriter antwortanclient = new OutputStreamWriter(a);	//Outputstream schickt Antwort zurück, bei True - Login erfolgreich - bei false nicht
			antwortanclient.flush();
			
			
			a.close();	// Antwort zur Neugenerierung schließen
			con.close();
			break;
			
    	case 1: 
    		
    		Statement stmtSOUpdate = con.createStatement();
    		String querySOUpdate = "SELECT standort FROM nutzer WHERE eMail=\""+((soAktMessage)object).getBody_mail()+"\"" ;
			ResultSet rsSOUpdate = stmtSOUpdate.executeQuery( querySOUpdate );
			
			rsSOUpdate.updateString(1, ((soAktMessage)object).getBody_standort().toString()); // bitte auch aktualisierung per Ortung
			
			stmtSOUpdate.close();
			rsSOUpdate.close();
			con.close();
			
    		/* 
    		 * aktualisiere Standort Eintrag mit derzeitiger Geo-Location (aus API GeoLocationAPI)
    		 */
    		break;
    		
    	case 2:
    		
    		Statement stmtGruppeBei = con.createStatement();
    		String queryGruppeBei = "SELECT * FROM gruppe WHERE kuerzel= \""+((gruppeBeitretenMessage)object).getBody_kuerzel()+"\"";
    		ResultSet rsGruppeBei = stmtGruppeBei.executeQuery(queryGruppeBei);
    		if(rsGruppeBei.next()==true) {
    			ResultSet rsGruppeBei2;
    			rsGruppeBei2 = con.createStatement().executeQuery("SELECT * FROM ist_in");
    			rsGruppeBei2.moveToInsertRow();
    			rsGruppeBei2.updateString(1, ((gruppeBeitretenMessage)object).getBody_kuerzel());
    			rsGruppeBei2.updateString(2, ((gruppeBeitretenMessage)object).getBody_mail());
    			rsGruppeBei2.close();
    		}
    			
    		rsGruppeBei.close();
    		stmtGruppeBei.close();
    		con.close();
    		
    		/*
    		 * gucke ob Gruppe vorhanden ist
    		 * wenn ja, überprüfe ob nutzer in Gruppe vorhanden
    		 * wenn ja - gehe in Gruppenraum
    		 * wenn nein - update DB um nutzer in Gruppe (relation tabelle) und betritt Gruppenraum
    		 */
    		break;
    		
    	case 3: 
    		Gruppe neueGruppe = new Gruppe();
    		
    		Statement stmtGruppeNeu = con.createStatement();
    		String queryGruppeNeu1 = "SELECT * FROM gruppe";
    		String queryGruppeNeu2 = "SELECT * FROM ist_in";
    		
    		ResultSet rsGruppeNeu1;
    		rsGruppeNeu1 = stmtGruppeNeu.executeQuery(queryGruppeNeu1);
    		rsGruppeNeu1.moveToInsertRow();
    		rsGruppeNeu1.updateString(1, neueGruppe.getKuerzel());
    		
    		ResultSet rsGruppeNeu2;
    		rsGruppeNeu2 = stmtGruppeNeu.executeQuery(queryGruppeNeu2);
    		rsGruppeNeu2.moveToInsertRow();
    		rsGruppeNeu2.updateString(1, neueGruppe.getKuerzel());
    		rsGruppeNeu2.updateString(2, ((gruppeErstellenMessage)object).getBody_mail());
    		
    		rsGruppeNeu1.close();
    		rsGruppeNeu2.close();
    		stmtGruppeNeu.close();
    		con.close();
    		
    		/*
    		 * Generiere Gruppe (setzekuerzel() wird ausgeführt)
    		 * gruppe.getkuerzel() gibt kuerzel um in db einzutragen
    		 * 
    		 * Update DB um Gruppe mit Kürzel 
    		 * trage Nutzer in Gruppe ein (relation Tabelle)
    		 */
    		break;
    		
    	case 4: 
    		
    		Statement stmtGruppeAus = con.createStatement();
    		ResultSet rsGruppeAus = stmtGruppeAus.executeQuery("SELECT * FROM ist_in WHERE kuerzel=\""+((gruppeVerlassenMessage)object).getBody_kuerzel()+"\"");
    		rsGruppeAus.moveToCurrentRow();
    		rsGruppeAus.deleteRow();
    		
    		rsGruppeAus.close();
    		stmtGruppeAus.close();
    		con.close();
    		
    		/*
    		 * Tritt aus Gruppenraum aus
    		 * speichere Kürzel
    		 * suche Gruppe mit Kürzel und trage Nutzer aus (DELETE aus relation Tabelle)
    		 */
    		
    		break;
    		
    	case 5: 
    		
    		Statement stmtVehikel = con.createStatement();
    		String queryVehikel = "SELECT eMail, vehikel FROM Nutzer WHERE eMail =\""+((vehikelMessage)object).getBody_mail()+"\"";
    		ResultSet rsVehikel = stmtVehikel.executeQuery(queryVehikel);
    		
    		rsVehikel.moveToCurrentRow();
    		rsVehikel.updateString(2, ((vehikelMessage)object).getSender().getVehikel().toString());
    		con.close();

    		/*
    		 * update DB Nutzerspalte das Vehikel auf neuen Eintrag
    		 */
    		break;
    		
    	case 6: 
    		
    		((logoutMessage)object).getSender().clientSocket.close();
    		
    		break;
    		
    	case 7:
    		
    		Statement stmtRegister = con.createStatement();
    		String queryRegister1 = "SELECT * FROM nutzer";
    		String queryRegister2 = " WHERE eMail=\""+((registerMessage)object).getBody_mail()+"\"";
    		
    		ResultSet rsRegister1 = stmtRegister.executeQuery(queryRegister1+queryRegister2);
    		
    		if(rsRegister1.next()==true) {
    			rsRegister1.close();
    			throw new Exception("Nutzer schon vorhanden");
    		}
    		else {
    			
    			ResultSet rsRegister2 = stmtRegister.executeQuery("SELECT eMail, name, passwort FROM nutzer");
    			rsRegister2.moveToInsertRow();
    			rsRegister2.updateString(1, ((registerMessage)object).getBody_mail());
    			rsRegister2.updateString(1, ((registerMessage)object).getBody_name());
    			rsRegister2.updateObject(1, ((registerMessage)object).getBody_password());
    			rsRegister2.close();
    		}
    		
    		rsRegister1.close();
    		stmtRegister.close();
    		con.close();
    		
    		/*
    		 * überprüfe ob EMail schon in Tabelle Nutzer vorhanden
    		 * wenn ja - fehler
    		 * wenn nein, input EMail und Passwort als neuen nutzer in DB Nutzer 
    		 * mit Vehikel zu Fuß, Standort aktuell und Name der gewählt wurde
    		 */
    		break;
    		
    	default: 
    		throw new Exception("Message nicht verwertbar");
    		
    	}
    }
    
    public void laufen() 
    {
    
    		while(true) 
    		{
    			try {
    		System.out.println("Server gestartet.");
    		
    		Socket client = server.accept();	// 	warte auf anmeldende clients
    		ObjectInputStream input = new ObjectInputStream(client.getInputStream()); 	// 	empfängt Input Stream vom Clienten
    		ObjectOutputStream output = new ObjectOutputStream(client.getOutputStream());	//	sendet Outputstream an Client zurück	
    		    		
    		System.out.println(server.getInetAddress());		
    		System.out.println("waiting for client at " + server.getLocalPort());		// 	ausgabe server für uns zur kontrolle 
    		
    											
    								
    		
    		execute((MethodeMessage) input.readObject()); 								// 	wählt Methode durch InputStream hat
    		output.writeObject(new loginMessage(null, null));    		
    	
    		client.close();
    		}
    		catch(Exception e) {
    	    		e.printStackTrace();
    	    		System.exit(1);
    	    }
    		}
    	
    }    
    
	public static void main(String args[]) throws Exception {
		
		Server s = new Server();
		s.laufen();
		
		
	}
    
}