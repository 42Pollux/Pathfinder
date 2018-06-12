package veranstaltungBeitreten;

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
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JTextField;

import mail.Mail;
import mail.MailAccounts;
import startseite.BevorstehendeVeranstaltungen;
import veranstaltungErstellen.Veranstaltung;

public class PersonenHinzufuegen extends javax.swing.JPanel {

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
	private javax.swing.JButton jButtonPlus;
	private javax.swing.JButton jButtonZusagen;
	private javax.swing.JButton jButtonAbsagen;
	private javax.swing.JLabel weiterePersonenHinzufügen;
	private javax.swing.JTextField[] adressen = new javax.swing.JTextField[5];
	private javax.swing.JCheckBox[] jCheckBoxArray = new javax.swing.JCheckBox[5];
	private javax.swing.JPanel jPanel1;

	private int clicked = 2;
	private Veranstaltung veran;
	private String username;

	// neue Eingabemaske
	public PersonenHinzufuegen(JFrame frame, final String username, Veranstaltung veran) {
		this.veran = veran;
		this.username = username;
		initComponents();

		// Hinzufügen von TextFeldern beim Plus Button klicken
		jButtonPlus.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (clicked < 5) {
					clicked++;
					adressen[clicked].setVisible(true);
					jCheckBoxArray[clicked].setVisible(true);
				}
			}
		});

		// Löschen des aktuellen Fensters und Hinzufügen von
		// BevorstehendenVeranstaltungen beim Absagen-Button klicken
		jButtonAbsagen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				teilnehmerEinladen();

				try (Connection conn = connect()) {

					// status auf zugesagt setzen und Standort eintragen (falls
					// treffpunkttyp nicht 0 wird sowieso null übertragen)
					String sql0 = "Update besucht set status = 3 where vid = ? and username = ?";
					PreparedStatement st0 = conn.prepareStatement(sql0, Statement.RETURN_GENERATED_KEYS);
					st0.setInt(1, veran.getId());
					st0.setString(2, username);
					st0.executeUpdate();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}

				frame.getContentPane().removeAll();
				frame.getContentPane().add(new BevorstehendeVeranstaltungen(frame, username));
				frame.setTitle("Startseite");
				frame.pack();
			}
		});

		// Löschen des aktuellen Fensters und Hinzufügen von
		// BevorstehendenVeranstaltungen beim Zusagen-Button klicken
		jButtonZusagen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				teilnehmerEinladen();

				frame.getContentPane().removeAll();
				frame.getContentPane().add(new BevorstehendeVeranstaltungen(frame, username));
				frame.setTitle("Startseite");
				frame.pack();
			}
		});
	}

	// Initialisieren der Eingabemaske
	private void initComponents() {

		jPanel1 = new javax.swing.JPanel();
		weiterePersonenHinzufügen = new javax.swing.JLabel();
		jButtonPlus = new javax.swing.JButton();
		jButtonZusagen = new javax.swing.JButton();
		jButtonAbsagen = new javax.swing.JButton();

		// Textfelder als Array
		for (int i = 0; i < adressen.length; i++) {
			adressen[i] = new javax.swing.JTextField();
		}

		// CheckBoxes als Array
		for (int i = 0; i < jCheckBoxArray.length; i++) {
			jCheckBoxArray[i] = new javax.swing.JCheckBox();
		}

		// Festlegen der Hintergrundfarbe der Panels
		setBackground(new java.awt.Color(246, 250, 252));
		jPanel1.setBackground(new java.awt.Color(246, 250, 252));
		setPreferredSize(new java.awt.Dimension(691, 559));

		// Text und Schrift der Labels
		weiterePersonenHinzufügen.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
		weiterePersonenHinzufügen.setText("Weitere Personen einladen:");

		// Text und Schrift der Buttons
		jButtonPlus.setText("+");
		jButtonZusagen.setText("Zusagen und senden");
		jButtonZusagen.setFont(new java.awt.Font("Tahoma", 1, 16));
		jButtonAbsagen.setText("Absagen und senden");
		jButtonAbsagen.setFont(new java.awt.Font("Tahoma", 1, 16));

		// Sichtbarkeit und Hintergrundfarbe der CheckBoxes
		jCheckBoxArray[0].setBackground(new java.awt.Color(246, 250, 252));
		jCheckBoxArray[1].setBackground(new java.awt.Color(246, 250, 252));
		jCheckBoxArray[2].setBackground(new java.awt.Color(246, 250, 252));
		jCheckBoxArray[3].setBackground(new java.awt.Color(246, 250, 252));
		jCheckBoxArray[3].setVisible(false);
		jCheckBoxArray[4].setBackground(new java.awt.Color(246, 250, 252));
		jCheckBoxArray[4].setVisible(false);

		// Sichtbarkeit der Textfelder
		adressen[3].setVisible(false);
		adressen[4].setVisible(false);

		// Hinzufügen der Komponenten zu den Panels
		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		// Horizontale Ausrichtung
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
						.addContainerGap(201, Short.MAX_VALUE).addGroup(jPanel1Layout
								.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
										jPanel1Layout.createSequentialGroup().addComponent(jButtonAbsagen)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
												.addComponent(jButtonZusagen).addGap(40, 40, 40))
								.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
										jPanel1Layout.createSequentialGroup()
												.addGroup(jPanel1Layout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
																jPanel1Layout
																		.createSequentialGroup()
																		.addComponent(weiterePersonenHinzufügen)
																		.addGap(31, 31, 31))
														.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
																jPanel1Layout.createSequentialGroup()
																		.addGroup(jPanel1Layout
																				.createParallelGroup(
																						javax.swing.GroupLayout.Alignment.LEADING)
																				.addComponent(adressen[0],
																						javax.swing.GroupLayout.PREFERRED_SIZE,
																						227,
																						javax.swing.GroupLayout.PREFERRED_SIZE)
																				.addComponent(adressen[1],
																						javax.swing.GroupLayout.PREFERRED_SIZE,
																						227,
																						javax.swing.GroupLayout.PREFERRED_SIZE)
																				.addComponent(adressen[2],
																						javax.swing.GroupLayout.PREFERRED_SIZE,
																						227,
																						javax.swing.GroupLayout.PREFERRED_SIZE)
																				.addComponent(adressen[3],
																						javax.swing.GroupLayout.PREFERRED_SIZE,
																						227,
																						javax.swing.GroupLayout.PREFERRED_SIZE)
																				.addComponent(adressen[4],
																						javax.swing.GroupLayout.PREFERRED_SIZE,
																						227,
																						javax.swing.GroupLayout.PREFERRED_SIZE))
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
												.addGroup(jPanel1Layout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(jButtonPlus).addComponent(jCheckBoxArray[0])
														.addComponent(jCheckBoxArray[1]).addComponent(jCheckBoxArray[2])
														.addComponent(jCheckBoxArray[3])
														.addComponent(jCheckBoxArray[4]))
												.addGap(181, 181, 181)))));
		// Vertikale Ausrichtung 
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel1Layout.createSequentialGroup().addGap(34, 34, 34)
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(weiterePersonenHinzufügen).addComponent(jButtonPlus,
										javax.swing.GroupLayout.PREFERRED_SIZE, 20,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGap(37, 37, 37)
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(adressen[0], javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jCheckBoxArray[0]))
						.addGap(18, 18, 18)
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
								.addComponent(adressen[1], javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jCheckBoxArray[1]))
						.addGap(18, 18, 18)
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
								.addComponent(adressen[2], javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jCheckBoxArray[2]))
						.addGap(18, 18, 18)
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
								.addComponent(adressen[3], javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jCheckBoxArray[3]))
						.addGap(18, 18, 18)
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
								.addComponent(adressen[4], javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jCheckBoxArray[4]))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 48, Short.MAX_VALUE)
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(jButtonZusagen).addComponent(jButtonAbsagen))
						.addGap(48, 48, 48)));

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE,
				javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE,
				javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
	}

	public void teilnehmerEinladen() {
		// Emails versenden
		String know_recipients_str = "";
		String unkn_recipients_str = "";
		int index = 0;
		while (index < adressen.length && adressen[index] != null && !adressen[index].getText().equals("")) {
			String rText = adressen[index].getText().trim();
			int c = 1;
			if (rText != "") {
				try (Connection conn = connect()) {
					String sqlEmail = "select count(*) as c from users where email = ?";
					PreparedStatement stEmail = conn.prepareStatement(sqlEmail, Statement.RETURN_GENERATED_KEYS);
					stEmail.setString(1, rText);
					ResultSet rsEmail = stEmail.executeQuery();
					rsEmail.next();
					c = rsEmail.getInt("c");
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
				if (c == 0) {
					unkn_recipients_str = unkn_recipients_str + rText + ",";
				} else {
					know_recipients_str = know_recipients_str + rText + ",";
				}

			}
			index++;
		}

		// String frist = new
		// SimpleDateFormat("dd.MM.yyyy").parse(fristFeld.getText()).getTime().toString();

		// String recipient = str; //"meetigelinfo@gmail.com";
		System.out.println(know_recipients_str);
		System.out.println(unkn_recipients_str);
		if (know_recipients_str.trim() != "") {
			String subject = "Einladung zu einer MeetIgel-Veranstaltung";
			String text = "Hallo, \n\nDu wurdest zu der Veranstaltung \"" + veran.getTitel() + "\" mit der ID \""
					+ veran.getId() + "\"  eingeladen. " + "\nDas Passwort für die Veranstaltung ist \""
					+ veran.getvPasswort() + "\"." + "\nBitte melde dich bis zum "
					+ new java.sql.Date(veran.getFrist().getTime()) + " zurück."
					+ "\n\nMit freundlichen Grüßen \n\nDein MeetIgel-Team";
			try {
				Mail.send(MailAccounts.GOOGLEMAIL, know_recipients_str, subject, text);
			} catch (AddressException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		if (unkn_recipients_str.trim() != "") {
			String subject = "Einladung zu einer MeetIgel-Veranstaltung";
			String text = "Hallo, \n\nDu wurdest über unsere Anwendung \"MeetIgel\" zu der Veranstaltung \""
					+ veran.getTitel() + "\"  eingeladen. "
					+ "\nBitte registriere dich bei MeetIgel, um zu- oder abzusagen und weitere Infos zu erhalten."
					+ "\nUm der Veranstaltung beitreten zu können, brauchst du folgende Zugangsdaten:"
					+ "\n		VeranstaltungsID:		" + veran.getId() + "\n 		Veranstaltungspasswort: 	"
					+ veran.getvPasswort() + "\nBitte melde dich bis zum "
					+ new java.sql.Date(veran.getFrist().getTime()) + " zurück."
					+ "\n\nHerzlich Willkommen bei MeetIgel \n\nDein MeetIgel-Team";
			try {
				Mail.send(MailAccounts.GOOGLEMAIL, unkn_recipients_str, subject, text);
			} catch (AddressException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		// Nutzer zu E-Mail-Adressen bestimmen und in
		// besucht-Tabelle mit Status 0=eingeladen bzw.
		// 1=eingeladen
		// und markiert eintragen
		try (Connection conn = connect()) {
			for (int i = 0; i < adressen.length; i++) {
				String emailadresse = adressen[i].getText().trim();
				if (!emailadresse.equals("")) {
					boolean markiert = jCheckBoxArray[i].isSelected();
					int status = 0;
					if (markiert)
						status = 1;

					String sql4 = "SELECT count(*) from users where email = ?";
					PreparedStatement st4 = conn.prepareStatement(sql4, Statement.RETURN_GENERATED_KEYS);
					st4.setString(1, emailadresse);
					ResultSet rs4 = st4.executeQuery();
					rs4.next();
					int count = rs4.getInt(1);

					// username zu E-Mail bestimmen, falls registriert
					if (count == 1) {
						String sql2 = "SELECT username from users where email = ?";
						PreparedStatement st2 = conn.prepareStatement(sql2, Statement.RETURN_GENERATED_KEYS);
						st2.setString(1, emailadresse);
						ResultSet rs2 = st2.executeQuery();
						rs2.next();

						String nutzer = rs2.getString(1);

						if (!nutzer.equals(username)) {
							String sql3 = "INSERT INTO besucht (username, vid, status, email) " + "VALUES (?,?,?,?)";
							PreparedStatement st3 = conn.prepareStatement(sql3, Statement.RETURN_GENERATED_KEYS);
							st3.setString(1, nutzer);
							st3.setInt(2, veran.getId());
							st3.setInt(3, status);
							st3.setString(4, emailadresse);
							st3.executeUpdate();
						}
						// falls Nutzer noch nicht registriert ist, muss seine
						// E-Mail-Adresse in besucht eingetragen werden und wird
						// ersetzt, sobald er sich registriert
					} else {
						String sql3 = "INSERT INTO besucht (username, vid, status, email) " + "VALUES (?,?,?,?)";
						PreparedStatement st3 = conn.prepareStatement(sql3, Statement.RETURN_GENERATED_KEYS);
						st3.setString(1, "unregistrierter Nutzer");
						st3.setInt(2, veran.getId());
						st3.setInt(3, status);
						st3.setString(4, emailadresse);
						st3.executeUpdate();
					}
				}
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
}
