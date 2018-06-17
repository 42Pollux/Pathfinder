package treffpunkt;

import java.util.ArrayList;

import treffpunkt.Ort;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Mittelpunkt {

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

	// Mittelpunkt berechnen und nächstgelegene Stadt in der Datenbank suchen
	public String treffpunkt(int vid, int termintyp) {
		int maximum;
		int sNummer = 0;
		try (Connection conn = connect()) {
			if (termintyp > 1) {
				// Termin mit meisten Votes ermitteln
				String sql1 = "SELECT max(votes) FROM termin WHERE vid =" + vid;
				Statement s1 = conn.createStatement();
				ResultSet rs1 = s1.executeQuery(sql1);
				rs1.next();
				maximum = rs1.getInt(1);
				// Terminnummer mit meisten Votes ermitteln
				String sql2 = "SELECT terminnummer FROM termin WHERE vid = ? AND votes = ?";
				PreparedStatement ps1 = conn.prepareStatement(sql2);
				ps1.setInt(1, vid);
				ps1.setInt(2, maximum);
				ResultSet rs2 = ps1.executeQuery();
				rs2.next();
				sNummer = rs2.getInt(1);
				System.out.println(sNummer);
			} else
				sNummer = 1;
		} catch (SQLException se) {
			se.printStackTrace();
		}
		ArrayList<String> orteString = new ArrayList<String>();
		// Standorte des meistgevoteten Termins
		switch (sNummer) {
		case 1:
			try (Connection conn = connect()) {
				String sql = "SELECT standort1 FROM besucht WHERE status = 2 AND vid =" + vid;
				Statement s = conn.createStatement();
				ResultSet rs = s.executeQuery(sql);
				while (rs.next()) {
					orteString.add(rs.getString(1));
				}
			} catch (SQLException se) {
				se.printStackTrace();
			}
			break;
		case 2:
			try (Connection conn = connect()) {
				String sql = "SELECT standort2 FROM besucht WHERE status = 2 AND vid =" + vid;
				Statement s = conn.createStatement();
				ResultSet rs = s.executeQuery(sql);
				while (rs.next()) {
					orteString.add(rs.getString(1));
				}
			} catch (SQLException se) {
				se.printStackTrace();
			}
			break;
		case 3:
			try (Connection conn = connect()) {
				String sql = "SELECT standort3 FROM besucht WHERE status = 2 AND vid =" + vid;
				Statement s = conn.createStatement();
				ResultSet rs = s.executeQuery(sql);
				while (rs.next()) {
					orteString.add(rs.getString(1));
				}
			} catch (SQLException se) {
				se.printStackTrace();
			}
			break;
		}

		ArrayList<Ort> orte = new ArrayList<Ort>();
		// Ermitteln von Laenge und Breite der Standorte
		try (Connection conn = connect()) {
			String sql;
			for (int i = 0; i < orteString.size(); i++) {
				sql = "SELECT lon, lat FROM Koordinaten WHERE ortsname LIKE '%" + orteString.get(i) + "%'";
				Statement s = conn.createStatement();
				ResultSet rs = s.executeQuery(sql);
				while (rs.next()) {
					Ort x = new Ort(orteString.get(i), rs.getDouble(1), rs.getDouble(2));
					orte.add(x);
				}
				rs.close();
				s.close();
			}

		} catch (SQLException se) {
			se.printStackTrace();
		}
		// exakten Mittelpunkt bestimmen
		double[] exakt = exakt(orte);

		double lon = exakt[0];
		double lat = exakt[1];
		// iterativ vergroessernd nahegelegene Orte finden
		try (Connection conn = connect()) {
			double lon1 = lon;
			double lon2 = lon;
			double lat1 = lat;
			double lat2 = lat;

			String sql = "SELECT * FROM Koordinaten WHERE Lon BETWEEN ? and ?" + "AND Lat BETWEEN ? and ?"
					+ "AND Stadt = TRUE";
			PreparedStatement st;

			for (double k = 0; k < 1; k = k + 0.05) {
				st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

				lon1 -= k;
				lon2 += k;
				lat1 -= k;
				lat2 += k;
				st.setDouble(1, lon1);
				st.setDouble(2, lon2);
				st.setDouble(3, lat1);
				st.setDouble(4, lat2);
				ResultSet rs = st.executeQuery();

				while (rs.next()) {
					Ort x = new Ort(rs.getString(1), rs.getDouble(2), rs.getDouble(3));
					return x.name;
				}
			}
		} catch (SQLException se) {
			se.printStackTrace();
		}
		return null;
	}

	private double[] exakt(ArrayList<Ort> orte) {
		double lat = 0;
		double lon = 0;

		for (int i = 0; i < orte.size(); i++) {
			lat += orte.get(i).lat;
			lon += orte.get(i).lon;
		}

		lat /= orte.size();
		lon /= orte.size();

		double[] exakt = { lon, lat };
		return exakt;
	}
}
