package veranstaltungErstellen;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

//import javafx.geometry.Insets;
import login.Login;
import mail.Mail;
import mail.MailAccounts;
import main.MeetIgel;
import startseite.BevorstehendeVeranstaltungen;

// Status in besucht-Tabelle: 	0 - eingeladen
//								1 - eingeladen und markiert
//								2 - zugesagt
//								3 - abgesagt

public class Einladungen extends javax.swing.JPanel {

	// Variablendeklaration für Connection
	private final String host = "meta.informatik.uni-rostock.de";
	private final String port = "5432";
	private final String database = "postgres";
	private final String user = "postgres";
	private final String password = "swat1824";
	private int clicked = 9;

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
	private String username;

	private JFrame frame;

	// Variablendeklaration für Panel Elemente
	private javax.swing.JButton jButtonPlus;
	private javax.swing.JButton jButtonEinladungenSenden;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JLabel jLabel6;
	private javax.swing.JLabel jLabel7;
	private javax.swing.JTextField fristFeld;
	private javax.swing.JScrollPane scrollpane;
	private javax.swing.JTextField[] adressen;
	private javax.swing.JCheckBox[] checkboxen;

	public Einladungen(JFrame frame, Veranstaltung veran, final String username) {
		this.frame = frame;
		this.veran = veran;
		this.username = username;

		initComponents();

		// Feld für Eingabe der Frist konfigurieren
		fristFeld.setText("TT.MM.JJJJ");
		fristFeld.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0) {
				if (!Verifier.date(fristFeld))
					fristFeld.setText("TT.MM.JJJJ");
			}

			@Override
			public void focusGained(FocusEvent arg0) {
				fristFeld.setText("");
			}
		});

		// Senden-Button konfigurieren
		jButtonEinladungenSenden.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(!fristFeld.getText().equals("TT.MM.JJJJ")) {

					// Rückmeldefrist auslesen
					try {
						Date frist = new SimpleDateFormat("dd.MM.yyyy").parse(fristFeld.getText());
						veran.setFrist(frist);
					} catch (ParseException pe) {
						pe.printStackTrace();
					}

					// f1.getText().trim()
					// String str = jTextFieldArray[0].getText().trim();
					// System.out.println(str);
					//
					// InternetAddress[] recipients = new InternetAddress[2];

					// Rückmeldefrist an Datenbank übergeben
					try (Connection conn = connect()) {

						String sql1 = "Update VERANSTALTUNG set frist = ? where id = ?";
						PreparedStatement st1 = conn.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS);
						try {
							st1.setDate(1, new java.sql.Date(
									(new SimpleDateFormat("dd.MM.yyyy").parse(fristFeld.getText()).getTime())));
						} catch (ParseException e1) {
							e1.printStackTrace();
						}
						st1.setInt(2, veran.getId());
						st1.executeUpdate();

					} catch (SQLException ex) {
						ex.printStackTrace();
					}

					teilnehmerEinladen();

					// Feedback geben, dass Veranstaltung erstellt wurde
					Abschluss abschluss = new Abschluss(frame);
					abschluss.setModal(true);
					abschluss.setLocationRelativeTo(frame);
					abschluss.setTitle("Abschluss");
					abschluss.setVisible(true);

					// zur Startseite zurückkehren
					frame.getContentPane().removeAll();
					frame.getContentPane().add(new BevorstehendeVeranstaltungen(frame, username));
					frame.pack();
				} else {

					KeineRueckmeldefrist abschluss = new KeineRueckmeldefrist(frame);
					abschluss.setModal(true);
					abschluss.setLocationRelativeTo(frame);
					abschluss.setVisible(true);
				}
			}
		});
		add(jButtonEinladungenSenden);

	}

	private void initComponents() {

		jLabel1 = new javax.swing.JLabel();
		jLabel2 = new javax.swing.JLabel();
		jLabel3 = new javax.swing.JLabel();
		jLabel4 = new javax.swing.JLabel();
		jLabel5 = new javax.swing.JLabel();
		jLabel6 = new javax.swing.JLabel();
		jButtonEinladungenSenden = new javax.swing.JButton();
		jButtonPlus = new javax.swing.JButton();
		jLabel7 = new javax.swing.JLabel();
		fristFeld = new javax.swing.JTextField();
		scrollpane = new javax.swing.JScrollPane();
		checkboxen = new javax.swing.JCheckBox[100];
		adressen = new javax.swing.JTextField[100];

		// Hintergrundfarbe und Größe des Panels
		setBackground(new java.awt.Color(246, 250, 252));
		setPreferredSize(new java.awt.Dimension(691, 559));

		// Scrollpane einrichten
		// neues JPanel für Textfelder und Checkboxen
		JPanel textundbox = new JPanel();
		textundbox.setLayout(new GridBagLayout());
		textundbox.setMinimumSize(new java.awt.Dimension(240, 770));

		// Größe des Scrollpanels
		scrollpane.setSize(new Dimension(232, 352));

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = GridBagConstraints.RELATIVE;
		c.weightx = 1.0f;
		c.weighty = 0.0f;
		textundbox.setBackground(new java.awt.Color(246, 250, 252));

		// die ersten 10 Felder für Mailadressen sollen anfangs sichtbar sein
		panelInhalt(c, 0, textundbox, 9);

		// JPanel mit Textfeldern und Checkboxen zum Scrollpanel hinzufügen
		scrollpane.setViewportView(textundbox);
		this.add(scrollpane);

		// ein neues Feld für eine E-Mail-Adresse hinzufügen
		jButtonPlus.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				clicked++;
				if ((clicked > 9) && (clicked < 100)) {

					c.gridx = 0;
					c.weightx = 1.0;
					c.gridy = clicked;
					adressen[clicked] = new JTextField("");
					adressen[clicked].setPreferredSize(new Dimension(10, 30));
					textundbox.add(adressen[clicked], c);

					c.gridx = 1;
					c.weightx = 0.0;
					checkboxen[clicked] = new JCheckBox();
					checkboxen[clicked].setPreferredSize(new Dimension(20, 30));
					textundbox.add(checkboxen[clicked], c);

					frame.validate();
					frame.repaint();

				} else {

				}

			}
		});

		// Festlegen des Label Inhaltes und der Schriftart
		jLabel1.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
		jLabel1.setText("Wer soll an der Veranstaltung teilnehmen?");
		jLabel2.setText("Füge jeden Teilnehmer hinzu und markiere Personen,");
		jLabel3.setText("die noch weitere Gäste hinzufuegen sollen.");
		jLabel4.setText("Falls deine Gäste noch nicht bei MeetIgel als Nutzer registriert sind,");
		jLabel5.setText("laden wir sie gleich ein.");
		jLabel6.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
		jLabel6.setText("E-Mail-Adressen der Einzuladenden");
		jLabel7.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
		jLabel7.setText("Rueckmeldefrist");

		// Plus Button
		jButtonPlus.setText("+");

		// Einladungen senden Button
		jButtonEinladungenSenden.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
		jButtonEinladungenSenden.setText("Einladungen senden");

		// Hinzufügen der einzelnen Komponenten zum JPanel
		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
						.addContainerGap(450, Short.MAX_VALUE).addComponent(jButtonEinladungenSenden).addGap(40, 40,
								40))
				.addGroup(
						layout.createSequentialGroup().addGap(59, 59,
								59)
								.addGroup(layout.createParallelGroup(
										javax.swing.GroupLayout.Alignment.LEADING)
										.addGroup(layout
												.createSequentialGroup().addGroup(layout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(jLabel5).addComponent(jLabel4)
														.addComponent(jLabel3).addComponent(
																jLabel2)
														.addComponent(jLabel1,
																javax.swing.GroupLayout.PREFERRED_SIZE, 458,
																javax.swing.GroupLayout.PREFERRED_SIZE))
												.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
										.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
												layout.createSequentialGroup().addGroup(layout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING,
																false)
														.addGroup(layout.createSequentialGroup().addComponent(jLabel6,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
																.addGap(18, 18, 18)
																.addComponent(jButtonPlus))
														.addComponent(scrollpane))
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
														.addGroup(layout
																.createParallelGroup(
																		javax.swing.GroupLayout.Alignment.LEADING,
																		false)
																.addComponent(jLabel7,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		Short.MAX_VALUE)
																.addComponent(fristFeld))
														.addGap(75, 75, 75)))));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
				.createSequentialGroup().addGap(32, 32, 32).addComponent(jLabel1)
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jLabel2)
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jLabel3)
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jLabel4)
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jLabel5)
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING).addGroup(layout
								.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel6)
								.addComponent(jButtonPlus, javax.swing.GroupLayout.PREFERRED_SIZE, 20,
										javax.swing.GroupLayout.PREFERRED_SIZE)))
				.addGap(18, 18, 18)
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup().addComponent(scrollpane)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 24,
										Short.MAX_VALUE)
								.addComponent(jButtonEinladungenSenden).addGap(48, 48, 48))
						.addGroup(layout.createSequentialGroup()
								.addComponent(fristFeld, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))));
	}

	public void teilnehmerEinladen() {
		// Emails versenden
		String know_recipients_str = "";
		String unkn_recipients_str = "";
		for (JTextField r : adressen) {
			if (r != null) {
				String rText = r.getText().trim();
				int c = 1;
				if (!rText.equals("")) {
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

			}
		}

		// String frist = new
		// SimpleDateFormat("dd.MM.yyyy").parse(fristFeld.getText()).getTime().toString();

		// String recipient = str; //"meetigelinfo@gmail.com";
		System.out.println(know_recipients_str);
		System.out.println(unkn_recipients_str);
		if (know_recipients_str.trim() != "") {
			String subject = "Einladung zur MeetIgel-Veranstaltung \"" + veran.getTitel() + "\"";
			String text = "Hallo, \n\nDu wurdest von " + username + " zu der Veranstaltung \"" + veran.getTitel() + "\" mit der ID \""
					+ veran.getId() + "\"  eingeladen. " + "\nDas Passwort für die Veranstaltung ist \""
					+ veran.getvPasswort() + "\"." + "\nBitte melde Dich bis zum "
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
				if (adressen[i] != null && !adressen[i].equals("")) {
					String emailadresse = adressen[i].getText().trim();
					if (!emailadresse.equals("")) {
						boolean markiert = checkboxen[i].isSelected();
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
								String sql3 = "INSERT INTO besucht (username, vid, status, email) "
										+ "VALUES (?,?,?,?)";
								PreparedStatement st3 = conn.prepareStatement(sql3, Statement.RETURN_GENERATED_KEYS);
								st3.setString(1, nutzer);
								st3.setInt(2, veran.getId());
								st3.setInt(3, status);
								st3.setString(4, emailadresse);
								st3.executeUpdate();
							}
							// falls Nutzer noch nicht registriert ist, muss
							// seine
							// E-Mail-Adresse in besucht eingetragen werden und
							// wird
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
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public void panelInhalt(GridBagConstraints c, int clicked, JPanel textundbox, int max) {
		for (int i = 0; i < max; i++) {
			c.gridx = 0;
			c.weightx = 1.0;
			// c.gridy=i;
			adressen[i] = new JTextField("");
			adressen[i].setPreferredSize(new Dimension(10, 30));
			textundbox.add(adressen[i], c);
			c.gridx = 1;
			c.weightx = 0.0;
			checkboxen[i] = new JCheckBox();
			checkboxen[i].setPreferredSize(new Dimension(20, 30));
			textundbox.add(checkboxen[i], c);

		}
	}
}
