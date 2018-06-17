package veranstaltungErstellen;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import mail.Mail;
import mail.MailAccounts;
import startseite.BevorstehendeVeranstaltungen;
import treffpunkt.Mittelpunkt;

public class Treffpunkt extends javax.swing.JPanel {

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

	// Variablendeklaration
	private javax.swing.JButton jButtonSetzen;
	private javax.swing.JButton jButtonAnnehmen;
	private javax.swing.JButton jButtonSpeichern;
	private javax.swing.JLabel jRueckmeldungenEingegangenText;
	private javax.swing.JLabel jAlternativenOrtText;
	private javax.swing.JLabel berechneterTreffpunkt;
	private javax.swing.JLabel jVeranstaltungsText;
	private javax.swing.JLabel jEinladung;
	private javax.swing.JTextField ortSetzen;
	private javax.swing.JButton jButtonZurueck;

	String veranstaltungstext = "Deine Veranstaltung __ wird am __  in __ stattfinden.";

	// Klassenvariable
	private Veranstaltung veran;
	boolean ortvorhanden = false;

	// neue Eingabemaske
	public Treffpunkt(JFrame frame, final String username, Veranstaltung veran) {
		this.veran = veran;

		// String ort = "__";
		String titel = "__";
		// String nTeilnehmer = "__";
		String termin = "__";

		try (Connection conn = connect()) {
			String sql1 = "SELECT * from veranstaltung where id = ?";
			PreparedStatement st1 = conn.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS);
			st1.setInt(1, veran.getId());
			ResultSet rs1 = st1.executeQuery();
			rs1.next();

			String ort;
			titel = rs1.getString("titel");
			jButtonAnnehmen = new javax.swing.JButton();
			jButtonAnnehmen.setText("Annehmen");
			if (rs1.getString("ort") != null) {
				String ortMit = rs1.getString("ort");
				ort = ortMit.substring(1, ortMit.length() - 1);
			} else {
				treffpunkt.Mittelpunkt mp = new Mittelpunkt();
				ort = mp.treffpunkt(veran.getId(), veran.getTermintyp()).substring(1,
						mp.treffpunkt(veran.getId(), veran.getTermintyp()).length() - 1);
				
			}

			// berechneterTreffpunkt.setBorder(javax.swing.BorderFactory.createLineBorder(new
			// java.awt.Color(135, 174, 212)));

			berechneterTreffpunkt = new JLabel(ort);

			String sql2 = "Select Max(votes) from termin where vid = ?";
			PreparedStatement st2 = conn.prepareStatement(sql2, Statement.RETURN_GENERATED_KEYS);
			st2.setInt(1, veran.getId());
			ResultSet rs2 = st2.executeQuery();
			rs2.next();
			int max = rs2.getInt(1);

			if (veran.getTermintyp() > 1) {

				String sql3 = "select datum from termin where votes = ? and vid = ?";
				PreparedStatement st3 = conn.prepareStatement(sql3, Statement.RETURN_GENERATED_KEYS);
				st3.setInt(1, max);
				st3.setInt(2, veran.getId());
				ResultSet rs3 = st3.executeQuery();
				rs3.next();
				while (rs3.next()) {
					termin = (rs3.getDate(1)).toString();
					rs3.next();
				}

			} else {
				String sql4 = "select endtermin from veranstaltung where id = ?";
				PreparedStatement st4 = conn.prepareStatement(sql4, Statement.RETURN_GENERATED_KEYS);
				st4.setInt(1, veran.getId());
				ResultSet rs4 = st4.executeQuery();
				rs4.next();
				termin = rs4.getDate(1).toString();
			}

			// String sqlNteil = "Select count() as c from besucht where vid =
			// ?";
			// PreparedStatement stNteil = conn.prepareStatement(sqlNteil,
			// Statement.RETURN_GENERATED_KEYS);
			// stNteil.setInt(1, veran.getId());
			// ResultSet rsNteil = stNteil.executeQuery();
			// rsNteil.next();
			// int max = rsNteil.getInt(1);

			veranstaltungstext = "Deine Veranstaltung \"" + titel + "\" wird am " + termin
					+ " in __ stattfinden.";
			// jVeranstaltungstext.setText(String.format(veranstaltungstext,
			// "__"));

		} catch (SQLException e) {
			e.printStackTrace();
		}

		initComponents(veranstaltungstext);

		// // Termin berechnen
		// if (veran.getTermintyp() > 1) {
		// try (Connection conn = connect()) {
		// String sql1 = "Select Max(votes) from termin where vid = ?";
		// PreparedStatement st1 = conn.prepareStatement(sql1,
		// Statement.RETURN_GENERATED_KEYS);
		// st1.setInt(1, veran.getId());
		// ResultSet rs1 = st1.executeQuery();
		// rs1.next();
		// String sql2 = "select datum from termin where votes = ?";
		// PreparedStatement st2 = conn.prepareStatement(sql1,
		// Statement.RETURN_GENERATED_KEYS);
		// st2.setInt(1, rs1.getInt(1));
		// ResultSet rs2 = st2.executeQuery();
		// rs2.next();
		// veran.setTermin(rs2.getDate(1));
		// } catch (SQLException ex) {
		// ex.printStackTrace();
		// }
		// }

		// Annehmen geklickt -> Lückentext aktualisieren
		jButtonAnnehmen.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				jVeranstaltungsText.setText(veranstaltungstext.substring(0, veranstaltungstext.length() - 15)
						+ berechneterTreffpunkt.getText() + " stattfinden.");
				veran.setTreffpunkt(berechneterTreffpunkt.getText());
				ortvorhanden = true;
				try (Connection conn = connect()) {
					String sql1 = "UPDATE VERANSTALTUNG set ort = ? where id = ?";
					PreparedStatement st1 = conn.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS);
					st1.setString(1, "\"" + berechneterTreffpunkt.getText() + "\"");
					st1.setInt(2, veran.getId());
					st1.executeUpdate();

				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		});

		// Setzen geklickt -> Lückentext aktualisieren, Ort in Datenbank als
		// Treffpunkt speichern (falls Eingabe gültig)
		jButtonSetzen.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try (Connection conn = connect()) {
					if (Verifier.ort(ortSetzen)) {
						String sql1 = "UPDATE VERANSTALTUNG set ort = ? where id = ?";
						PreparedStatement st1 = conn.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS);
						st1.setString(1, "\"" + ortSetzen.getText() + "\"");
						st1.setInt(2, veran.getId());
						st1.executeUpdate();
						veran.setTreffpunkt(ortSetzen.getText());
						jVeranstaltungsText.setText(veranstaltungstext.substring(0, veranstaltungstext.length() - 15)

								+ ortSetzen.getText() + " stattfinden.");
						ortvorhanden = true;
					} else {
						OrtUnknown ortunknown = new OrtUnknown(frame);
						ortunknown.setModal(true);
						ortunknown.setLocationRelativeTo(frame);
						ortunknown.setTitle("Unbekannter Ort");
						ortunknown.setVisible(true);
					}

				} catch (SQLException ex) {
					ex.printStackTrace();
				}

			}
		});

		// öffnet BevorstehendeVeranstaltungen, wenn Abschließen-Button
		// gedrückt wird, Teilnehmer müssen benachrichtigt werden,
		// Veranstaltung muss aktualisiert werden
		jButtonSpeichern.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (ortvorhanden) {

					try (Connection conn = connect()) {
						String sql3 = "Update Veranstaltung set abgeschlossen = true, treffpunkttyp = 1, termintyp = 1 where id = ?";
						PreparedStatement st3 = conn.prepareStatement(sql3, Statement.RETURN_GENERATED_KEYS);
						st3.setInt(1, veran.getId());
						st3.executeUpdate();

						String sql1 = "SELECT count(*) from besucht where vid = ? and status = 2";
						PreparedStatement st1 = conn.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS);
						st1.setInt(1, veran.getId());
						ResultSet rs1 = st1.executeQuery();
						rs1.next();
						String recipients = "";
						if (rs1.getInt(1) > 0) {
							String sql2 = "SELECT email from besucht where vid = ? and status = 2";
							PreparedStatement st2 = conn.prepareStatement(sql2, Statement.RETURN_GENERATED_KEYS);
							st2.setInt(1, veran.getId());
							ResultSet rs2 = st2.executeQuery();
							rs2.next();
							recipients = rs2.getString(1);
							int index = 1;
							while (rs2.next()) {
								String r = rs2.getString(1);
								if (!(r.trim().equals("") || r.equals("organisator"))) {
									recipients = recipients + ", " + r;
								}
								index++;
							}
						}

						if (recipients.trim() != "") {
							String subject = "MeetIgel-Veranstaltung \"" + veran.getTitel()
									+ "\": Planung abgeschlossen.";
							String text = "Hallo, \n\ndie Planung der Veranstaltung \"" + veran.getTitel()
									+ "\" ist abgeschlossen. " + "\nSie findet am " +  new java.sql.Date(veran.getTermin().getTime()) + " in "
									+ veran.getTreffpunkt() + " statt." + "\n\nViel Spaß \n\nDein MeetIgel-Team";
							try {
								Mail.send(MailAccounts.GOOGLEMAIL, recipients, subject, text);
							} catch (AddressException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (MessagingException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}

					} catch (SQLException ex) {
						ex.printStackTrace();
					}
					frame.getContentPane().removeAll();
					frame.getContentPane().add(new BevorstehendeVeranstaltungen(frame, username));
					frame.setTitle("Startseite");
					frame.pack();
				} else {
					OrtUnknown ortunknown = new OrtUnknown(frame);
					ortunknown.setModal(true);
					ortunknown.setLocationRelativeTo(frame);
					ortunknown.setTitle("Unbekannter Ort");
					ortunknown.setVisible(true);
				}
			}
		});

		// Zurück zur Startseite/BevorstehendeVeranstaltungen beim Klicken des
		// Buttons
		jButtonZurueck.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				frame.getContentPane().removeAll();
				frame.getContentPane().add(new BevorstehendeVeranstaltungen(frame, username));
				frame.setTitle("Startseite");
				frame.pack();
			}
		});

	}

	// Initialisieren der Eingabemaske
	private void initComponents(String veranstaltungstext) {

		jRueckmeldungenEingegangenText = new javax.swing.JLabel();
		jAlternativenOrtText = new javax.swing.JLabel();
		ortSetzen = new javax.swing.JTextField();
		jButtonSetzen = new javax.swing.JButton();
		jButtonZurueck = new javax.swing.JButton();
		jVeranstaltungsText = new javax.swing.JLabel();
		jEinladung = new javax.swing.JLabel();
		jButtonSpeichern = new javax.swing.JButton();

		// Hintergrundfarbe des Panels
		setBackground(new java.awt.Color(246, 250, 252));
		setPreferredSize(new java.awt.Dimension(691, 559));

		// Text und Schriftart der Labels
		jEinladung.setFont(new java.awt.Font("Tahoma", 1, 18));
		jEinladung.setText("Zusammenfassung deiner Veranstaltung");
		jRueckmeldungenEingegangenText
				.setText("Die Rückmeldungen sind eingegangen. Ein günstiger Veranstaltungsort ist:");
		jAlternativenOrtText.setText("Alternativen Ort setzen:");
		jVeranstaltungsText.setText(veranstaltungstext);

		// Text der Buttons
		jButtonSetzen.setText("Setzen");
		jButtonSpeichern.setText("Speichern");
		jButtonSpeichern.setFont(new java.awt.Font("Tahoma", Font.BOLD, 16));
		jButtonZurueck.setText("Zurück");
		jButtonZurueck.setFont(new java.awt.Font("Tahoma", Font.BOLD, 16));

		// Hinzufügen der Komponenten zu den Panels
		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				javax.swing.GroupLayout.Alignment.TRAILING,
				layout.createSequentialGroup().addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(jButtonZurueck)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(jButtonSpeichern).addGap(40, 40, 40))
				.addGroup(layout.createSequentialGroup().addGap(51, 51, 51).addGroup(layout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jEinladung)
						.addComponent(jVeranstaltungsText).addComponent(jRueckmeldungenEingegangenText)
						.addGroup(layout.createSequentialGroup()
								.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
										.addComponent(ortSetzen, javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(jAlternativenOrtText, javax.swing.GroupLayout.Alignment.LEADING,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(berechneterTreffpunkt, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
								.addGap(64, 64, 64)
								.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
										.addComponent(jButtonAnnehmen, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(jButtonSetzen, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
						.addContainerGap(101, Short.MAX_VALUE)));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
				.createSequentialGroup().addGap(46, 46, 46).addComponent(jEinladung).addGap(48, 48, 48)
				.addComponent(jRueckmeldungenEingegangenText)
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
						.addComponent(jButtonAnnehmen).addComponent(berechneterTreffpunkt,
								javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addGap(47, 47, 47).addComponent(jAlternativenOrtText)
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
						.addComponent(ortSetzen, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(jButtonSetzen))
				.addGap(58, 58, 58).addComponent(jVeranstaltungsText)
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 121, Short.MAX_VALUE)
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
						.addComponent(jButtonSpeichern).addComponent(jButtonZurueck))
				.addGap(48, 48, 48)));
	}
}
