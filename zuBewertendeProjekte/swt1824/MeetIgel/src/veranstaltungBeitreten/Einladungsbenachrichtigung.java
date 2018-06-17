package veranstaltungBeitreten;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JFrame;
import startseite.*;
import veranstaltungErstellen.Veranstaltung;
import veranstaltungErstellen.Verifier;

public class Einladungsbenachrichtigung extends javax.swing.JPanel {

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
	private Veranstaltung veran;
	private Date heute;
	private int id;
	private String username;
	private int status;
	private boolean korrekteEingaben;
	String standort1;
	String standort2;
	String standort3;
	java.sql.Date speichertermin1;
	java.sql.Date speichertermin2;
	java.sql.Date speichertermin3;
	boolean termin1gewaehlt;
	boolean termin2gewaehlt;
	boolean termin3gewaehlt;

	// Variablendeklaration für Panelelemente
	private javax.swing.JButton jButtonWeiter;
	private javax.swing.JButton jButtonAbsagen;
	private javax.swing.JLabel jZeileEins;
	private javax.swing.JLabel jZeileZwei;
	private javax.swing.JLabel jZeileDrei;
	private javax.swing.JLabel jZeileVier;
	private javax.swing.JLabel jStandortAngeben;
	private javax.swing.JLabel jnurNochZusagen;
	private javax.swing.JRadioButton termin1;
	private javax.swing.JRadioButton termin2;
	private javax.swing.JRadioButton termin3;
	private javax.swing.JTextField ort1;
	private javax.swing.JTextField ort2;
	private javax.swing.JTextField ort3;
	private javax.swing.JPanel ortePanel;

	// neue Eingabemaske
	public Einladungsbenachrichtigung(JFrame frame, final String username, int vid) {
		this.username = username;
		this.id = vid;
		initComponents(frame);
	}

	// Initialisieren der Maske
	private void initComponents(JFrame frame) {

		veran = detailsAuslesen();

		jZeileEins = new javax.swing.JLabel();
		jZeileZwei = new javax.swing.JLabel();
		jZeileDrei = new javax.swing.JLabel();
		jZeileVier = new javax.swing.JLabel();
		jStandortAngeben = new javax.swing.JLabel();
		ortePanel = new javax.swing.JPanel();
		ort1 = new javax.swing.JTextField();
		ort2 = new javax.swing.JTextField();
		ort3 = new javax.swing.JTextField();
		jButtonWeiter = new javax.swing.JButton();
		jButtonAbsagen = new javax.swing.JButton();
		termin1 = new javax.swing.JRadioButton();
		termin2 = new javax.swing.JRadioButton();
		termin3 = new javax.swing.JRadioButton();

		// Text für JLabel; nicht sichtbar, solange die Frist noch nicht
		// abgelaufen ist
		jnurNochZusagen = new javax.swing.JLabel(
				"Frist abgelaufen. Du kannst zu- oder absagen, aber nicht mehr an der Abstimmung teilnehmen.");
		jnurNochZusagen.setFont(new java.awt.Font("Tahoma", Font.BOLD, 12)); 
		jnurNochZusagen.setVisible(false);

		// falls Nutzer markiert wurde, wird er darüber informiert
		if (status == 1) {
			jnurNochZusagen.setText(
					"Du wurdest vom Organisator aufgefordert, auf der nächsten Seite weitere Gäste hinzuzufügen. Dort kannst du auch absagen");
			jnurNochZusagen.setVisible(true);
		}

		// Hintergrundfarbe und Größe der Panels und RadioButtons
		setBackground(new java.awt.Color(246, 250, 252));
		setPreferredSize(new java.awt.Dimension(691, 559));
		ortePanel.setBackground(new java.awt.Color(246, 250, 252));
		termin1.setBackground(new java.awt.Color(246, 250, 252));
		termin2.setBackground(new java.awt.Color(246, 250, 252));
		termin3.setBackground(new java.awt.Color(246, 250, 252));

		// Text des Weiter-Buttons, Schriftart und Größe
		jButtonWeiter.setText("Weiter");
		jButtonWeiter.setFont(new java.awt.Font("Tahoma", 1, 16));

		// Text des Absagen-Buttons, Schriftart und Größe
		jButtonAbsagen.setText("Absagen");
		jButtonAbsagen.setFont(new java.awt.Font("Tahoma", 1, 16));

		// aktuelles Datum abspeichern
		heute = null;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String heutigesDatum = formatter.format(new Date());
		try {
			heute = new SimpleDateFormat("yyyy-MM-dd").parse(heutigesDatum);
		} catch (ParseException e2) {
			e2.printStackTrace();
		}

		// Termin- Treffpunktfelder mit Daten aus der Datenbank füllen
		try (Connection conn = connect()) {

			// Status des aktuellen Nutzers bezüglich der Veranstaltung abfragen
			// ( prüfen, ob markiert oder nicht)
			String sql6 = "SELECT status from besucht where vid = ? and username = ?";
			PreparedStatement st6 = conn.prepareStatement(sql6, Statement.RETURN_GENERATED_KEYS);
			st6.setInt(1, this.id);
			st6.setString(2, username);
			ResultSet rs6 = st6.executeQuery();
			rs6.next();
			status = rs6.getInt(1);

			// falls er markiert ist, kann er erst auf der nächsten Seite
			// absagen
			if (status == 1) {
				jButtonAbsagen.setEnabled(false);
				jnurNochZusagen
						.setText("Auf der nächsten Seite darfst du weitere Personen einladen und erst dann absagen.");
				jnurNochZusagen.setVisible(true);
			}

			// falls die Rückmeldefrist abgelaufen ist, kann er nur noch zu-
			// oder absagen
			if (new java.sql.Date(veran.getFrist().getTime()).compareTo(heute) < 0) {
				jnurNochZusagen.setVisible(true);
				termin1.setEnabled(false);
				termin2.setEnabled(false);
				termin3.setEnabled(false);
				ort1.setEnabled(false);
				ort2.setEnabled(false);
				ort3.setEnabled(false);

			} else {

				// termintyp=1: Endtermin kann eingetragen werden, alles ab
				// Zeile 2 kann deaktiviert werden
				if (veran.getTermintyp() == 1) {
					termin2.setEnabled(false);
					termin3.setEnabled(false);
					ort2.setEnabled(false);
					ort3.setEnabled(false);

					termin1.setText(new java.sql.Date(veran.getTermin().getTime()).toString());

					// wenn Termine zur Auswahl stehen
				} else if (veran.getTermintyp() >= 2) {

					// wenn Treffpunkte zur Auswahl stehen, werden die
					// Ortsfelder nicht-editierbar gesetzt
					if (veran.getTreffpunkttyp() == 2) {
						ort1.setEditable(false);
						ort2.setEditable(false);
						ort3.setEditable(false);

						// solange es einen nächsten Termin in der
						// termin-Tabelle gibt, werden termin und zugehöriger
						// Ort aufgelistet
						String sql5 = "SELECT ort, datum from termin where vid = ? and terminnummer = 1";
						PreparedStatement st5 = conn.prepareStatement(sql5, Statement.RETURN_GENERATED_KEYS);
						st5.setInt(1, id);
						ResultSet rs5 = st5.executeQuery();
						rs5.next();
						ort1.setText(rs5.getString("ort").substring(1, rs5.getString("ort").length()-1));
						termin1.setText(rs5.getDate("datum").toString());

						String sql7 = "SELECT ort, datum from termin where vid = ? and terminnummer = 2";
						PreparedStatement st7 = conn.prepareStatement(sql7, Statement.RETURN_GENERATED_KEYS);
						st7.setInt(1, id);
						ResultSet rs7 = st7.executeQuery();
						rs7.next();
						ort2.setText(rs7.getString("ort").substring(1, rs7.getString("ort").length()-1));
						termin2.setText(rs7.getDate("datum").toString());

						if (veran.getTermintyp() == 3) {
							String sql8 = "SELECT ort, datum from termin where vid = ? and terminnummer = 3";
							PreparedStatement st8 = conn.prepareStatement(sql8, Statement.RETURN_GENERATED_KEYS);
							st8.setInt(1, id);
							ResultSet rs8 = st8.executeQuery();
							rs8.next();
							ort3.setText(rs8.getString("ort").substring(1, rs7.getString("ort").length()-1));
							termin3.setText(rs8.getDate("datum").toString());
						} else
							// falls es keinen dritten Termin gibt, werden
							// die zugehörigen Felder deaktiviert
							termin3.setEnabled(false);

						// falls Treffpunkt berechnet werden soll, bleiben die
						// Ortsfelder offen
					} else if (veran.getTreffpunkttyp() == 0) {

						String sql5 = "SELECT datum, terminnummer from termin where vid = ? order by terminnummer";
						PreparedStatement st5 = conn.prepareStatement(sql5, Statement.RETURN_GENERATED_KEYS);
						st5.setInt(1, id);
						ResultSet rs5 = st5.executeQuery();
						rs5.next();

						// solange Termine vorhanden sind, werden sie
						// eingetragen
						termin1.setText(rs5.getDate("datum").toString());
						speichertermin1 = rs5.getDate("datum");
						if (rs5.next()) {
							termin2.setText(rs5.getDate("datum").toString());
							speichertermin2 = rs5.getDate("datum");
							if (rs5.next()) {
								termin3.setText(rs5.getDate("datum").toString());
								speichertermin3 = rs5.getDate("datum");
							} else {
								termin3.setEnabled(false);
								ort3.setEnabled(false);
							}

						} else {
							termin2.setEnabled(false);
							ort2.setEnabled(false);
						}
					}
				}

				// falls Treffpunkt feststeht, steht auch der Termin fest und
				// der Ort wird in das erste Feld eingetragen
				if (veran.getTreffpunkttyp() == 1) {
					ort1.setEditable(false);
					ort1.setText(veran.getTreffpunkt());
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// bei Klicken des Weiter-Buttons wird "PersonenHinzufuegen" geöffnet
		jButtonWeiter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				eingabenPruefen();

				if (korrekteEingaben) {
					try (Connection conn = connect()) {

						// status auf zugesagt setzen und Standort eintragen
						// (falls
						// treffpunkttyp nicht 0 wird sowieso null übertragen)
						String sql0 = "Update besucht set status = 2, standort1 = ?, standort2 = ?, standort3 = ? where vid = ? and username = ?";
						PreparedStatement st0 = conn.prepareStatement(sql0, Statement.RETURN_GENERATED_KEYS);
						st0.setString(1, standort1);
						st0.setString(2, standort2);
						st0.setString(3, standort3);
						st0.setInt(4, id);
						st0.setString(5, username);
						st0.executeUpdate();

						// Votes für gewählte Termine an Datenbank übergeben
						if (veran.getTermintyp() > 1 && termin1gewaehlt) {
							upvote(1);
						}

						if (veran.getTermintyp() > 1 && termin2gewaehlt) {
							upvote(2);
						}

						if (veran.getTermintyp() > 1 && termin3gewaehlt) {
							upvote(3);
						}

					} catch (SQLException e1) {
						e1.printStackTrace();
					}

					// falls Nutzer markiert ist zu PersonenHinzufuegen
					// weiterleiten
					if (status == 1) {
						frame.getContentPane().removeAll();
						frame.getContentPane().add(new PersonenHinzufuegen(frame, username, veran));
						frame.pack();
						// falls nicht, ist Abstimmung abgeschlossen und Nutzer
						// wird zu seiner Startseite weitergeleitet
					} else {
						frame.getContentPane().removeAll();
						frame.getContentPane().add(new BevorstehendeVeranstaltungen(frame, username));
						frame.pack();
					}
					// falls Eingaben inkorrekt sind, erscheint Hinweisdialog
				} else {
					KeinStandortTermin error = new KeinStandortTermin(frame);
					error.setModal(true);
					error.setLocationRelativeTo(frame);
					error.setTitle("Fehlende Angaben");
					error.setVisible(true);
				}
			}
		});

		// bei Klicken des Absagen-Buttons wird der Status des Nutzers in der
		// besucht-Tabelle auf 3=abgesagt gesetzt und
		// "BevorstehendeVeranstaltungen"geöffnet
		jButtonAbsagen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try (Connection conn = connect()) {
					String sql0 = "Update besucht set status = 3 where id = ?";
					PreparedStatement st0 = conn.prepareStatement(sql0, Statement.RETURN_GENERATED_KEYS);
					st0.setInt(1, id);
					st0.executeUpdate();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
				frame.getContentPane().removeAll();
				frame.getContentPane().add(new BevorstehendeVeranstaltungen(frame, username));
				frame.pack();
			}
		});

		// Text der Labels
		jZeileEins.setText(veran.getOrganisator() + " hat dich zu ihrer/seiner Veranstaltung " + veran.getTitel()
				+ " eingeladen.");
		jZeileZwei.setText("Bitte wähle (falls möglich) einen oder mehrere Termine aus und gib (falls möglich) an, ");
		jZeileDrei.setText("wo du dich an dem Tag aufhältst, damit ein günstiger Treffpunkt für alle Beteiligten");
		jZeileVier.setText("bestimmt werden kann oder sage ab.");
		jStandortAngeben.setFont(new java.awt.Font("Tahoma", 1, 16));
		jStandortAngeben.setText("Dein Standort");

		// Hinzufügen der Komponenten zu den Panels
		// Panel, wo Orte ausgewählt werden können
		javax.swing.GroupLayout orteLayout = new javax.swing.GroupLayout(ortePanel);
		ortePanel.setLayout(orteLayout);
		// Horizontale Ausrichtung der Elemente im Panel
		orteLayout.setHorizontalGroup(orteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, orteLayout.createSequentialGroup()
						.addGroup(orteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
								.addComponent(jnurNochZusagen, javax.swing.GroupLayout.Alignment.LEADING,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)
								.addGroup(orteLayout.createSequentialGroup().addContainerGap()
										.addGroup(orteLayout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(termin1).addComponent(termin2).addComponent(termin3))
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addGroup(orteLayout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(ort1, javax.swing.GroupLayout.Alignment.TRAILING,
														javax.swing.GroupLayout.PREFERRED_SIZE, 182,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(ort2, javax.swing.GroupLayout.PREFERRED_SIZE, 182,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(ort3, javax.swing.GroupLayout.PREFERRED_SIZE, 182,
														javax.swing.GroupLayout.PREFERRED_SIZE))))
						.addGap(32, 32, 32)));
		// Vertikale Ausrichtung der Komponenten im Panel
		orteLayout.setVerticalGroup(orteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(orteLayout.createSequentialGroup().addContainerGap()
						.addGroup(orteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(termin1).addComponent(ort1, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addGroup(orteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(termin2).addComponent(ort2, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addGroup(orteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(termin3).addComponent(ort3, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
						.addComponent(jnurNochZusagen)));

		// Panel: Enthält ortePanel und weitere Elemente
		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		// Horizontale Ausrichtung
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				javax.swing.GroupLayout.Alignment.TRAILING,
				layout.createSequentialGroup().addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(jButtonAbsagen)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(jButtonWeiter)
						.addGap(80))
				.addGroup(layout.createSequentialGroup().addGap(74, 74, 74)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
								.addComponent(jStandortAngeben).addComponent(jZeileVier)
								.addComponent(jZeileEins, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(jZeileZwei)
								.addComponent(ortePanel, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(jZeileDrei, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addGap(50)));
		// Vertikale Ausrichtung
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addContainerGap(55, Short.MAX_VALUE).addComponent(jZeileEins)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jZeileZwei)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jZeileDrei)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jZeileVier)
						.addGap(26, 26, 26).addComponent(jStandortAngeben)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(ortePanel, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(90, 90, 90)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(jButtonWeiter).addComponent(jButtonAbsagen))
						.addGap(48, 48, 48)));
	}

	public Veranstaltung detailsAuslesen() {
		try (Connection conn = connect()) {
			Veranstaltung veran = new Veranstaltung();

			String sql1 = "SELECT * from veranstaltung where id = ?";
			PreparedStatement st1 = conn.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS);
			st1.setInt(1, this.id);
			ResultSet rs1 = st1.executeQuery();
			rs1.next();

			veran.setId(rs1.getInt("id"));
			veran.setFrist(new java.util.Date(rs1.getDate("frist").getTime()));
			veran.setOrganisator(rs1.getString("veranstalter"));
			veran.setTitel(rs1.getString("titel"));
			veran.setTermintyp(rs1.getInt("termintyp"));
			veran.setTreffpunkttyp(rs1.getInt("treffpunkttyp"));
			veran.setvPasswort(rs1.getString("vpasswort"));
			if (veran.getTreffpunkttyp() == 1) {
				veran.setTreffpunkt(rs1.getString("ort"));
			}
			if (veran.getTermintyp() == 1) {
				veran.setTermin(new java.util.Date(rs1.getDate("endtermin").getTime()));
			}
			return veran;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return veran;
	}

	// Prüfen, ob mindestens ein Termin gewählt wurde, Standorte auslesen und
	// verifizieren
	public void eingabenPruefen() {
		if (veran.getTermintyp() == 1) {
			if (veran.getTreffpunkttyp() == 0) {
				if (Verifier.ort(ort1)) {
					standort1 = ort1.getText();
					korrekteEingaben = true;
				} else
					korrekteEingaben = false;
			}
		}

		if (veran.getTermintyp() == 2) {
			termin1gewaehlt = termin1.isSelected();
			termin2gewaehlt = termin2.isSelected();
			if (veran.getTreffpunkttyp() == 0) {
				if (termin1gewaehlt) {
					if (Verifier.ort(ort1)) {
						standort1 = ort1.getText();
						korrekteEingaben = true;
					} else
						korrekteEingaben = false;
				}
				if (termin2gewaehlt) {
					if (Verifier.ort(ort2)) {
						standort2 = ort2.getText();
						korrekteEingaben = true;
					} else
						korrekteEingaben = false;
				}
			}
		}

		if (veran.getTermintyp() == 3) {
			termin1gewaehlt = termin1.isSelected();
			termin2gewaehlt = termin2.isSelected();
			termin3gewaehlt = termin3.isSelected();
			if (veran.getTreffpunkttyp() == 0) {
				if (termin1gewaehlt) {
					if (Verifier.ort(ort1)) {
						standort1 = '"' + ort1.getText() + '"';
						korrekteEingaben = true;
					} else
						korrekteEingaben = false;
				}
				if (termin2gewaehlt) {
					if (Verifier.ort(ort2)) {
						standort2 = '"' + ort2.getText() + '"';
						korrekteEingaben = true;
					} else
						korrekteEingaben = false;
				}
				if (termin3gewaehlt) {
					if (Verifier.ort(ort3)) {
						standort3 = '"' + ort3.getText() + '"';
						korrekteEingaben = true;
					} else
						korrekteEingaben = false;
				}
			}
		}
		if (termin1.isEnabled()) {
			if (!termin1.isSelected() && !termin2.isSelected() && !termin3.isSelected()) {
				korrekteEingaben = false;
			} else
				korrekteEingaben = true;
		} else
			korrekteEingaben = true;

		if (new java.sql.Date(veran.getFrist().getTime()).compareTo(heute) < 0)
			korrekteEingaben = true;
	}

	public void upvote(int terminnummer) {
		// votes für Termin 'terminnumer' abfragen
		try (Connection conn = connect()) {
			String sql2 = "SELECT votes from termin where vid = ? and terminnummer = ?";
			PreparedStatement st2 = conn.prepareStatement(sql2, Statement.RETURN_GENERATED_KEYS);
			st2.setInt(1, id);
			st2.setInt(2, terminnummer);

			ResultSet rs2 = st2.executeQuery();
			rs2.next();
			int votes = rs2.getInt(1);

			// votes um 1 inkrementieren
			String sql3 = "Update termin set votes = ? where vid = ? and terminnummer = ?";
			PreparedStatement st3 = conn.prepareStatement(sql3, Statement.RETURN_GENERATED_KEYS);
			st3.setInt(1, votes + 1);
			st3.setInt(2, id);
			st3.setInt(3, terminnummer);

			st3.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
