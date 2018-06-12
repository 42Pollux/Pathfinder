package login;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class LoginCheck {

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

	// Überprüft ob der Nutzer in der Datenbank existiert
	public String checkIt(String user, char[] password) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
		try (Connection conn = connect()) {
			
			// alternative SQL-Anfrage: String sql1 = "SELECT count(*) from
			// users where username ='" + user + "', '" + password + "';";
			
			
			
			
			
			// Prüfen, ob Nutzer in Nutzer-Tabelle existiert
			String sql1 = "SELECT count(*) from users where username = ? or email = ?";
			PreparedStatement st1 = conn.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS);
			st1.setString(1, user);
			st1.setString(2, user);
			ResultSet rs1 = st1.executeQuery();
			rs1.next();
			if (rs1.getInt(1) == 1) {
				//hash password
				SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
				//SecureRandom generator
		        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
		        //Create array for salt
		        byte[] salt = new byte[16];
		        
		        //Get a users salt
		        String sqlGetSalt = "SELECT salt from users where username = ? or email = ?";
				PreparedStatement stGetSalt = conn.prepareStatement(sqlGetSalt, Statement.RETURN_GENERATED_KEYS);
				stGetSalt.setString(1, user);
				stGetSalt.setString(2, user);
				ResultSet rsGetSalt = stGetSalt.executeQuery();
				rsGetSalt.next();
				salt = rsGetSalt.getBytes(1);
//		        sr.nextBytes(salt);
		        byte[] testsalt = new byte[16];

				KeySpec ks = new PBEKeySpec(password,salt,1024,128);
				SecretKey s = f.generateSecret(ks);
				Key k = new SecretKeySpec(s.getEncoded(),"AES");
				String hashedpassword = k.toString();
				
				
				
				// Prüfen, ob in Nutzer-Tabelle genau einmal der aktuelle User mit
				// dem eingegebenen Passwort existiert
				String sql2 = "SELECT count(*) from users where (username = ? or email = ?) and passwort = ?";
				PreparedStatement st2 = conn.prepareStatement(sql2, Statement.RETURN_GENERATED_KEYS);
				st2.setString(1, user);
				st2.setString(2, user);
				st2.setString(3, hashedpassword);
				ResultSet rs2 = st2.executeQuery();
				rs2.next();
				if (rs2.getInt(1) == 1) {
					return "Accepted";
				} else {
					return "Declined";
				}
			} else {
				return "User unknown";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}