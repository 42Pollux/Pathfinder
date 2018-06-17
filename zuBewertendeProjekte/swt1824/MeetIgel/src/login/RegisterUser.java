package login;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JFrame;

public class RegisterUser {

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

	// Nutzer in Datenbank registrieren
	public void register(String username, String email, String password, byte[] salt, JFrame frame) {
		try (Connection conn = connect()) {
			String sql = "INSERT INTO users VALUES(?,?,?,?)";
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, username);
			st.setString(2, email);
			st.setString(3, password);
			st.setBytes(4, salt);
			st.executeUpdate();
			st.close();

			// Nutzer in besucht eintragen, falls er irgendwo eingeladen ist
			String sql4 = "SELECT count(*) from besucht where email = ? and username = ?";
			PreparedStatement st4 = conn.prepareStatement(sql4, Statement.RETURN_GENERATED_KEYS);
			st4.setString(1, email);
			st4.setString(2, "unregistrierter Nutzer");
			ResultSet rs4 = st4.executeQuery();
			rs4.next();

			// Von "Unregistrierter Nutzer" zum neuen Nutzernamen in besucht-Tabelle wechseln
			if (rs4.getInt(1) == 1) {
				String sql0 = "Update besucht set username = ? where email = ? and username = ?";
				PreparedStatement st0 = conn.prepareStatement(sql0, Statement.RETURN_GENERATED_KEYS);
				st0.setString(1, username);
				st0.setString(2, email);
				st0.setString(3, "unregistrierter Nutzer");
				st0.executeUpdate();
			}

		} catch (SQLException e) {
			e.printStackTrace();
			// Dialog öffnen, falls Nutzername oder E-Mail schon vorhanden
			RegErrUser reu = new RegErrUser(frame);
			reu.setModal(true);
			reu.setLocationRelativeTo(frame);
			reu.setTitle("Benutzer Error");
			reu.setVisible(true);
		}
	}
}
