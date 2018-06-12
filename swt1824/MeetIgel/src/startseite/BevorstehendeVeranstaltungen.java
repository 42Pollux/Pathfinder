package startseite;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.text.SimpleDateFormat;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import login.Login;
import optionen.*;

import veranstaltungBeitreten.VeranstaltungBeitreten;
import veranstaltungErstellen.*;

public class BevorstehendeVeranstaltungen extends javax.swing.JPanel {

	// Variablendeklaration für Panel Komponenten
	private javax.swing.JButton jButtonVeranstaltungErstellen;
	private javax.swing.JButton jButtonVeranstBeitreten;
	private javax.swing.JButton jButtonOptionen;
	private javax.swing.JButton jAusloggen;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JPanel jPanelButtonAuswahl;
	private javax.swing.JPanel jPanelFuerAnderePanels;
	private javax.swing.JPanel jPanelBevorstehendeVeranst;
	private javax.swing.JPanel jPanel4;
	private javax.swing.JScrollPane jScrollPane1;

	// Klassenvariablen
	final String username;
	final JFrame frame;

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

	// neue Eingabemaske
	public BevorstehendeVeranstaltungen(JFrame frame, final String username) {
		this.username = username;
		this.frame = frame;
		initComponents();

		// Weiterleitung zu Veranstaltung erstellen
		jButtonVeranstaltungErstellen.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				frame.getContentPane().removeAll();
				frame.getContentPane().add(new VeranstaltungErstellen(frame, username));
				frame.setTitle("Veranstaltung Erstellen");
				frame.pack();
			}
		});
		add(jButtonVeranstaltungErstellen);

		// Weiterleitung zu Veranstaltung beitreten
		jButtonVeranstBeitreten.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				frame.getContentPane().removeAll();
				frame.getContentPane().add(new VeranstaltungBeitreten(frame, username));
				frame.setTitle("Veranstaltung Beitreten");
				frame.pack();
			}
		});

		// Weiterleiten zu den Optionen
		jButtonOptionen.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				frame.getContentPane().removeAll();
				frame.getContentPane().add(new PasswortAendern(frame, username));
				frame.setTitle("Passwort ändern");
				frame.pack();
			}
		});

		// Ausloggen -> Weiterleitung zum Login
		jAusloggen.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				frame.getContentPane().removeAll();
				frame.getContentPane().add(new Login(frame));
				frame.setTitle("Login");
				frame.pack();
			}
		});
	}

	// Initialisieren der Eingabemaske
	private void initComponents() {

		jScrollPane1 = new javax.swing.JScrollPane();
		jPanelFuerAnderePanels = new javax.swing.JPanel();
		jPanelButtonAuswahl = new javax.swing.JPanel();
		jButtonVeranstaltungErstellen = new javax.swing.JButton();
		jButtonVeranstBeitreten = new javax.swing.JButton();
		jButtonOptionen = new javax.swing.JButton();
		jAusloggen = new javax.swing.JButton();
		jPanelBevorstehendeVeranst = new javax.swing.JPanel();
		jLabel1 = new javax.swing.JLabel();
		jPanel4 = new javax.swing.JPanel();

		// Hintergrund der Panels festlegen
		setPreferredSize(new java.awt.Dimension(691, 559));
		setBackground(new java.awt.Color(233, 244, 248));
		jPanelFuerAnderePanels.setBackground(new java.awt.Color(53, 90, 154));
		jPanelButtonAuswahl.setBackground(new java.awt.Color(246, 250, 252));
		jPanelBevorstehendeVeranst.setBackground(new java.awt.Color(246, 250, 252));
		jPanel4.setBackground(new java.awt.Color(246, 250, 252));
		jButtonVeranstaltungErstellen.setText("Veranstaltung erstellen");
		jButtonVeranstBeitreten.setText("Veranstaltung beitreten");
		jButtonOptionen.setText("Optionen");
		jAusloggen.setText("Ausloggen");
		jLabel1.setText("Bevorstehende Veranstaltungen");

		// aktuelles Datum abspeichern
		Date heute = null;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String heutigesDatum = formatter.format(new Date());
		try {
			heute = new SimpleDateFormat("yyyy-MM-dd").parse(heutigesDatum);
		} catch (ParseException e2) {
			e2.printStackTrace();
		}

		// Anzahl bevorstehender Veranstaltungen des Nutzer auslesen, um
		// Scrollpane vorzubereiten
		try (Connection conn = connect()) {
			int countVeranstaltungen;
			String sql1 = "SELECT count(*) from besucht where (username = ? and status = ?)";
			PreparedStatement st1 = conn.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS);
			st1.setString(1, username);
			st1.setInt(2, 2);
			ResultSet rs1 = st1.executeQuery();
			rs1.next();
			countVeranstaltungen = rs1.getInt(1);
			Veranstaltung[] events = new Veranstaltung[countVeranstaltungen];

			// bevorstehende Veranstaltungen auslesen
			String sql2 = "SELECT * from besucht inner join veranstaltung on besucht.vid=veranstaltung.id where (besucht.username = ? and status = ?)";
			PreparedStatement st2 = conn.prepareStatement(sql2, Statement.RETURN_GENERATED_KEYS,
					ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			st2.setString(1, username);
			st2.setInt(2, 2);
			ResultSet rs2 = st2.executeQuery();
			rs2.next();
			for (int j = 0; j < countVeranstaltungen; j++) {
				rs2.absolute(j + 1);
				events[j] = new Veranstaltung();
				events[j].setTitel(rs2.getString("titel"));
				// Termin anzeigen, falls er schon festgelegt ist
				if (rs2.getInt("termintyp") == 1 && rs2.getDate("endtermin") != null) {
					events[j].setTermin(new java.util.Date(rs2.getDate("endtermin").getTime()));
				}
				if (rs2.getInt("treffpunkttyp") == 1 && rs2.getString("ort") != null) {
					events[j].setTreffpunkt(rs2.getString("ort").substring(1, rs2.getString("ort").length()-1));
				}
				events[j].setId(rs2.getInt("id"));

				// Veranstaltung löschen, falls bei Erstellung vor Eingabe der
				// Frist
				// abgebrochen wurde
				if (rs2.getDate("frist") == null) {
					String sql3 = "Delete from besucht where vid = ?";
					PreparedStatement st3 = conn.prepareStatement(sql3, Statement.RETURN_GENERATED_KEYS);
					st3.setInt(1, events[j].getId());
					st3.executeUpdate();

					String sql4 = "Delete from termin where vid = ?";
					PreparedStatement st4 = conn.prepareStatement(sql4, Statement.RETURN_GENERATED_KEYS);
					st4.setInt(1, events[j].getId());
					st4.executeUpdate();

					String sql5 = "Delete from veranstaltung where id = ?";
					PreparedStatement st5 = conn.prepareStatement(sql5, Statement.RETURN_GENERATED_KEYS);
					st5.setInt(1, events[j].getId());
					st5.executeUpdate();

					continue;
				}

				events[j].setFrist(new java.util.Date(rs2.getDate("frist").getTime()));

				events[j].setTermintyp(rs2.getInt("termintyp"));
				events[j].setTreffpunkttyp(rs2.getInt("treffpunkttyp"));
				events[j].setOrganisator(rs2.getString("veranstalter"));
				events[j].setAbgeschlossen(rs2.getBoolean("abgeschlossen"));
				events[j].setBeschreibung(rs2.getString("beschreibung"));
			}

			// Scrollpane für bevorstehende veranstaltungen einrichten
			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new GridBagLayout());
			buttonPanel.setMinimumSize(new Dimension(400, 400));
			buttonPanel.setBackground(Color.WHITE);

			JScrollPane pane = new JScrollPane();
			pane.setSize(new Dimension(440, 500));

			// GridBagConstraint für Buttons
			GridBagConstraints constraint = new GridBagConstraints();
			constraint.anchor = GridBagConstraints.WEST;
			constraint.fill = GridBagConstraints.HORIZONTAL;
			constraint.gridx = 0;
			constraint.gridy = GridBagConstraints.RELATIVE;
			constraint.weightx = 1.0f;
			constraint.weighty = 0.0f;

			for (int i = 0; i < countVeranstaltungen; i++) {
				// Veranstaltung anzeigen, falls die Frist nicht null ist und
				// die Veranstaltung somit gelöscht wurde
				if (events[i].getFrist() != null) {
					JButton veranstaltungsButton = new JButton();
					if (events[i].getTermin() != null && events[i].getTreffpunkt() != null && !events[i].getTreffpunkt().equals(""))
						veranstaltungsButton.setText(events[i].getTitel() + " - " + events[i].getTreffpunkt() + " - "
								+ new java.sql.Date(events[i].getTermin().getTime()).toString());
					else if (events[i].getTermin() != null)
						veranstaltungsButton.setText(events[i].getTitel() + " - "
								+ new java.sql.Date(events[i].getTermin().getTime()).toString());
					else
						veranstaltungsButton.setText(events[i].getTitel());
					veranstaltungsButton.setPreferredSize(new Dimension(40, 100));

					// Veranstaltung nicht anzeigen und löschen, falls sie in
					// der Vergangenheit liegt
					if (events[i].getTermintyp() == 1 && events[i].getTermin() != null
							&& events[i].getTermin().compareTo(heute) < 0) {

						veranstaltungsButton.setVisible(false);

						String sql3 = "Delete from besucht where vid = ?";
						PreparedStatement st3 = conn.prepareStatement(sql3, Statement.RETURN_GENERATED_KEYS);
						st3.setInt(1, events[i].getId());
						st3.executeUpdate();

						String sql4 = "Delete from termin where vid = ?";
						PreparedStatement st4 = conn.prepareStatement(sql4, Statement.RETURN_GENERATED_KEYS);
						st4.setInt(1, events[i].getId());
						st4.executeUpdate();

						String sql5 = "Delete from veranstaltung where id = ?";
						PreparedStatement st5 = conn.prepareStatement(sql5, Statement.RETURN_GENERATED_KEYS);
						st5.setInt(1, events[i].getId());
						st5.executeUpdate();

						continue;

						// Veranstaltung hervorheben, wenn Frist abgelaufen
					} else if (events[i].getFrist().compareTo(heute) < 0 && !events[i].getAbgeschlossen()
							&& events[i].getOrganisator().equals(this.username)) {
						veranstaltungsButton.setBackground(Color.ORANGE);
						veranstaltungsButton
								.setText(events[i].getTitel() + " - Frist abgelaufen! - Für Optionen klicken");
					}

					buttonPanel.add(veranstaltungsButton, constraint);

					Veranstaltung veran = events[i];

					// Bei Klick auf eine Veranstaltung zu ihren Details bzw.
					// der
					// Zusammenfassung weiterleiten
					veranstaltungsButton.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							if (!veranstaltungsButton.getBackground().equals(Color.ORANGE)) {
								Veranstaltungsdetails details = new Veranstaltungsdetails(frame, veran, username);
								details.setLocationRelativeTo(frame);
								details.setModal(true);
								details.setTitle("Veranstaltungsdetails");
								details.setVisible(true);
							} else {
								Treffpunkt zusammenfassung = new Treffpunkt(frame, username, veran);
								frame.getContentPane().removeAll();
								frame.setTitle("Treffpunkt");
								frame.getContentPane().add(zusammenfassung);
								frame.pack();
							}
						}
					});
				}

				pane.setViewportView(buttonPanel);
				this.jPanelBevorstehendeVeranst.add(pane);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Anordnung der Elemente
		// Panel mit den Optionen, Ausloggen, Veranstaltung erstellen/löschen
		// Buttons
		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanelButtonAuswahl);
		jPanelButtonAuswahl.setLayout(jPanel1Layout);
		// Horizontale Ausrichtung der Elemente auf dem Panel
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
						.addContainerGap().addGroup(jPanel1Layout
								.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
								.addComponent(jButtonOptionen, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(jButtonVeranstaltungErstellen, javax.swing.GroupLayout.Alignment.LEADING,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)
								.addComponent(jButtonVeranstBeitreten, javax.swing.GroupLayout.DEFAULT_SIZE, 217,
										Short.MAX_VALUE)
								.addComponent(jAusloggen, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addContainerGap()));
		// Vertikale Anordnung der Elemente auf dem Panel
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel1Layout.createSequentialGroup().addContainerGap()
						.addComponent(jButtonVeranstaltungErstellen, javax.swing.GroupLayout.PREFERRED_SIZE, 44,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(18, 18, 18)
						.addComponent(jButtonVeranstBeitreten, javax.swing.GroupLayout.PREFERRED_SIZE, 44,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(18, 18, 18)
						.addComponent(jButtonOptionen, javax.swing.GroupLayout.PREFERRED_SIZE, 44,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(18, 18, 18).addComponent(jAusloggen, javax.swing.GroupLayout.PREFERRED_SIZE, 44,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(0, 295, Short.MAX_VALUE)));

		// Panel mit den Buttons der Bevorstehenden Veranstaltungen
		javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanelBevorstehendeVeranst);
		jPanelBevorstehendeVeranst.setLayout(jPanel3Layout);
		jPanel3Layout.setHorizontalGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel3Layout.createSequentialGroup().addContainerGap().addComponent(jScrollPane1,
						javax.swing.GroupLayout.PREFERRED_SIZE, 430, javax.swing.GroupLayout.PREFERRED_SIZE)

		));
		jPanel3Layout.setVerticalGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING,
						javax.swing.GroupLayout.PREFERRED_SIZE, 500, Short.MAX_VALUE));

		// Derzeitig leeres Panel
		javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
		jPanel4.setLayout(jPanel4Layout);
		jPanel4Layout.setHorizontalGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)

				.addGap(0, 0, Short.MAX_VALUE));
		jPanel4Layout.setVerticalGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 0, Short.MAX_VALUE));

		// Anordnung der drei Panel auf diesem Panel
		javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanelFuerAnderePanels);
		jPanelFuerAnderePanels.setLayout(jPanel2Layout);
		// Horizontale Ausrichtung der Panels
		jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel2Layout.createSequentialGroup().addGap(7, 7, 7)
						.addComponent(jPanelButtonAuswahl, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(jPanelBevorstehendeVeranst, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))

						.addGap(7, 7, 7)));
		// Vertikale Ausrichtung der Panels
		jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
				.addGroup(jPanel2Layout.createSequentialGroup().addGap(6, 6, 6).addGroup(jPanel2Layout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(jPanel2Layout.createSequentialGroup()
								.addComponent(jPanelBevorstehendeVeranst, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(1, 1, 1).addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addComponent(jPanelButtonAuswahl, javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addGap(8, 8, 8)));

		// Panel das alle obigen Panels enthaelt
		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				jPanelFuerAnderePanels, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
				Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				jPanelFuerAnderePanels, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
				Short.MAX_VALUE));
	}
}
