package veranstaltungErstellen;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.text.JTextComponent;

//Klasse, die Textfeld-Eingaben auf Zulässigkeit überprüft
public class Verifier {

	// Variablendeklaration für Connection
	private final String host = "meta.informatik.uni-rostock.de";
	private final String port = "5432";
	private final String database = "postgres";
	private final String user = "postgres";
	private final String password = "swat1824";

	private final String url = "jdbc:postgresql://" + host + ":" + port + "/" + database;

	// Connection erstellen
	public Connection connect() {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return conn;
	}

	// Überprüft, ob Komponente nur Zahlen enthält
	public static boolean numbers(JTextComponent eingabe) {
		String s = eingabe.getText().trim();
		return s.matches("^[0-9]+");
	}

	// Überprüft, ob Komponente nur Buchstaben enthält
	public static boolean letters(JTextComponent eingabe) {
		String s = eingabe.getText().trim();
		return s.matches("^[a-z, A-Z]+");
	}

	// Überprüft, ob Komponente nur Zahlen und Buchstaben enthält
	public static boolean numbersAndLetters(JTextComponent eingabe) {
		String s = eingabe.getText().trim();
		return s.matches("^[a-z, A-Z, 0-9]+");
	}

	// Überprüft, ob Komponente ein Datum enthält
	public static boolean date(JTextComponent eingabe) {
		String s = eingabe.getText().trim();
		return (s.length() == 10 && s.substring(0, 1).matches("^[0-9]+") && s.charAt(2) == '.'
				&& s.substring(3, 4).matches("^[0-9]+") && s.charAt(5) == '.' && s.substring(6, 9).matches("^[0-9]+"));
	}

	// Überprüft, ob Komponente einen in der Datenbank vorhandenen Ort enthält
	public static boolean ort(JTextComponent eingabe) {
		String ort = '"' + eingabe.getText().trim() + '"';
		try (Connection conn = new Verifier().connect()) {
			String sql1 = "SELECT count(*) from koordinaten where ortsname = ?";
			PreparedStatement st1 = conn.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS);
			st1.setString(1, ort);
			ResultSet rs1 = st1.executeQuery();
			rs1.next();
			if(rs1.getInt(1)>=1)
				return true;
			else return false;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// Testen, ob plz in Datenbank vorhanden
		// wenn ja, testen, ob einer der zugehörigen Orte dem eingegebenen
		// entspricht
		return true;
	}
	
	// Überprüft, ob Eingabe ein in der Datenbank vorhandenen Ort ist
		public static boolean ort(String eingabe) {
			String ort = '"' + eingabe.trim() + '"';
			try (Connection conn = new Verifier().connect()) {
				String sql1 = "SELECT count(*) from koordinaten where ortsname = ?";
				PreparedStatement st1 = conn.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS);
				st1.setString(1, ort);
				ResultSet rs1 = st1.executeQuery();
				rs1.next();
				if(rs1.getInt(1)>=1)
					return true;
				else return false;
			} catch (SQLException e) {
				e.printStackTrace();
			}
			// Testen, ob plz in Datenbank vorhanden
			// wenn ja, testen, ob einer der zugehörigen Orte dem eingegebenen
			// entspricht
			return true;
		}

}
