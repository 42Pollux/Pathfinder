package veranstaltungBeitreten;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JFrame;

import main.*;
import startseite.BevorstehendeVeranstaltungen;

public class VeranstaltungBeitreten extends javax.swing.JPanel {

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

	// Variablendeklaration der Panel Elemente 
	private javax.swing.JButton jButtonOK;
	private javax.swing.JLabel jVeranstaltungsnummer;
	private javax.swing.JLabel jPasswort;
	private javax.swing.JLabel jMeetIgelBild;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPasswordField jPasswordField;
	private javax.swing.JTextField jTextFieldVeranstNr;
	private javax.swing.JButton jButtonZurueck;

	// neue Eingabemaske
	public VeranstaltungBeitreten(JFrame frame, String username) {
		initComponents(frame);

		// Beim Klicken des Buttons wird Einladungsbenachrichtigung geöffnet,
		// falls Zugangsdaten korrekt
		jButtonOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// eingegebene Daten auslesen
				int nummer = Integer.parseInt(jTextFieldVeranstNr.getText().trim());
				String passwort = new String(jPasswordField.getPassword());

				// Prüfen, ob Nutzer dieser Veranstaltung bereits beigetreten
				// ist
				try (Connection conn = connect()) {
					String sql0 = "Select count(*) from besucht where vid = ? and username = ? and (status = ? or status = ?)";
					PreparedStatement st0 = conn.prepareStatement(sql0, Statement.RETURN_GENERATED_KEYS);
					st0.setInt(1, nummer);
					st0.setString(2, username);
					st0.setInt(3, 0);
					st0.setInt(4, 1);
					ResultSet rs0 = st0.executeQuery();
					rs0.next();
					int ergebnis0 = rs0.getInt(1);

					String sql4 = "SELECT count(*) from veranstaltung where id = ?";
					PreparedStatement st4 = conn.prepareStatement(sql4, Statement.RETURN_GENERATED_KEYS);
					st4.setInt(1, nummer);
					ResultSet rs4 = st4.executeQuery();
					rs4.next();
					int ergebnis4 = rs4.getInt(1);

					String sql1 = "Select count(*) from besucht where vid = ? and username = ? and status = 2";
					PreparedStatement st1 = conn.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS);
					st1.setInt(1, nummer);
					st1.setString(2, username);
					ResultSet rs1 = st1.executeQuery();
					rs1.next();
					int ergebnis1 = rs1.getInt(1);

					// Prüfen, ob in besucht-Tabelle Veranstaltungs-ID mit
					// aktuellem
					// Nutzer als Eingeladener vorhanden ist
					if (ergebnis0 == 1) {
						String sql5 = "SELECT count(*) from veranstaltung where id = ? and vpasswort = ?";
						PreparedStatement st5 = conn.prepareStatement(sql5, Statement.RETURN_GENERATED_KEYS);
						st5.setInt(1, nummer);
						st5.setString(2, passwort);
						ResultSet rs5 = st5.executeQuery();
						rs5.next();
						// Prüfen, ob in Veranstaltungstabelle Veranstaltungs-ID
						// mit gegebenem
						// Passwort vorhanden ist
						if (rs5.getInt(1) == 1) {
							frame.getContentPane().removeAll();
							frame.getContentPane().add(new Einladungsbenachrichtigung(frame, username, nummer));
							frame.setTitle("Einladungsbenachrichtigung");
							frame.pack();
						} else {
							// öffne Dialog: Passwort falsch
							VerErrPW error = new VerErrPW(frame);
							error.setModal(true);
							error.setLocationRelativeTo(frame);
							error.setTitle("Falsches Passwort");
							error.setVisible(true);
						}
						// falls in besucht-Tabelle Veranstaltungs-ID mit
						// aktuellem
						// Nutzer als Eingeladener nicht vorhanden ist
						// falls Veranstaltungsnummer nicht existiert
					} else if (ergebnis4 == 0) {
						VerErrNr error = new VerErrNr(frame);
						error.setModal(true);
						error.setLocationRelativeTo(frame);
						error.setTitle("Falsche Nummer");
						error.setVisible(true);
					} else if (ergebnis1 == 1) {
						// öffne Dialog: Du bist dieser Veranstaltung bereits
						// beigetreten oder hast abgesagt
						VerAlrIn error = new VerAlrIn(frame);
						error.setModal(true);
						error.setLocationRelativeTo(frame);
						error.setTitle("Veranstaltungs Error");
						error.setVisible(true);
					} else {
						VerErrNichtEingeladen error1 = new VerErrNichtEingeladen(frame);
						error1.setModal(true);
						error1.setLocationRelativeTo(frame);
						error1.setTitle("Nicht Eingeladen");
						error1.setVisible(true);
					}

				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		});

		jButtonZurueck.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.getContentPane().removeAll();
				frame.getContentPane().add(new BevorstehendeVeranstaltungen(frame, username));
				frame.setTitle("Startseite");
				frame.pack();

			}
		});
	}

	// Initialisieren der Eingabemaske
	private void initComponents(JFrame frame) {

		jPanel1 = new javax.swing.JPanel();
		jVeranstaltungsnummer = new javax.swing.JLabel();
		jTextFieldVeranstNr = new javax.swing.JTextField();
		jButtonOK = new javax.swing.JButton();
		jButtonZurueck = new javax.swing.JButton();
		jPasswordField = new javax.swing.JPasswordField();
		jPasswort = new javax.swing.JLabel();
		jMeetIgelBild = new javax.swing.JLabel();

		// Festlegen von Hintergrund und Größe der Panels
		setBackground(new java.awt.Color(53, 90, 154));
		setPreferredSize(new java.awt.Dimension(691, 559));
		jPanel1.setBackground(new java.awt.Color(246, 250, 252));
		jPanel1.setPreferredSize(new java.awt.Dimension(691, 559));

		// Labels: Text und Schrift festlegen
		jVeranstaltungsnummer.setFont(new java.awt.Font("Tahoma", 1, 16));
		jVeranstaltungsnummer.setText("Veranstaltungsnummer");
		jButtonOK.setFont(new java.awt.Font("Tahoma", 1, 16));
		jButtonOK.setText("O.K.");
		jButtonZurueck.setFont(new java.awt.Font("Tahoma", 1, 16));
		jButtonZurueck.setText("Zurück");
		jPasswort.setFont(new java.awt.Font("Tahoma", 1, 16));
		jPasswort.setText("Passwort");

		// Speichern und Weiter mit Enter
		frame.getRootPane().setDefaultButton(jButtonOK);

		// MeetIgel Logo
		jMeetIgelBild.setIcon(new javax.swing.ImageIcon(getClass().getResource("/bilder/rsz_1rsz_meetigel.png")));

		// Hinzufügen der einzelnen Komponenten zu den Panels
		javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel1);
		jPanel1.setLayout(jPanel4Layout);
		// Horizontale Ausrichtung der Elemente
		jPanel4Layout.setHorizontalGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel4Layout.createSequentialGroup().addGap(196, 196, 196).addGroup(jPanel4Layout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(jPasswort, javax.swing.GroupLayout.PREFERRED_SIZE, 114,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGroup(jPanel4Layout.createSequentialGroup().addGap(43, 43, 43).addComponent(jMeetIgelBild,
								javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addComponent(jTextFieldVeranstNr, javax.swing.GroupLayout.PREFERRED_SIZE, 211,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(jVeranstaltungsnummer, javax.swing.GroupLayout.PREFERRED_SIZE, 202,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
								.addGroup(javax.swing.GroupLayout.Alignment.LEADING,
										jPanel4Layout.createSequentialGroup()
												.addComponent(jButtonZurueck, javax.swing.GroupLayout.PREFERRED_SIZE, 98,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
														javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addComponent(jButtonOK, javax.swing.GroupLayout.PREFERRED_SIZE,
														77, javax.swing.GroupLayout.PREFERRED_SIZE))
								.addComponent(jPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, 211,
										javax.swing.GroupLayout.PREFERRED_SIZE)))
						.addContainerGap(199, Short.MAX_VALUE)));
		// Vertikale Ausrichtung der Elemente 
		jPanel4Layout.setVerticalGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel4Layout.createSequentialGroup().addGap(59, 59, 59)
						.addComponent(jMeetIgelBild, javax.swing.GroupLayout.PREFERRED_SIZE, 89,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(jVeranstaltungsnummer)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(jTextFieldVeranstNr, javax.swing.GroupLayout.PREFERRED_SIZE, 26,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(18, 18, 18).addComponent(jPasswort)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(jPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(18, 18, 18)
						.addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(jButtonZurueck).addComponent(jButtonOK))
						.addContainerGap(153, Short.MAX_VALUE)));

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
						layout.createSequentialGroup().addContainerGap(39, Short.MAX_VALUE).addComponent(jPanel1,
								javax.swing.GroupLayout.PREFERRED_SIZE, 606, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(40, 40, 40)));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout
						.createSequentialGroup().addGap(28, 28, 28).addComponent(jPanel1,
								javax.swing.GroupLayout.PREFERRED_SIZE, 485, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap(42, Short.MAX_VALUE)));
	}
}
