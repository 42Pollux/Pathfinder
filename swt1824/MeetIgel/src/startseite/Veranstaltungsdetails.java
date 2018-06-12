package startseite;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import veranstaltungErstellen.Veranstaltung;

public class Veranstaltungsdetails extends JDialog {

	// Variablendeklaration für DIalog Komponenten
	private javax.swing.JButton absagenButton;
	private javax.swing.JLabel jBeschreibung;
	private javax.swing.JLabel jBeschreibungDB1;
	private javax.swing.JLabel jBeschreibungDB2;
	private javax.swing.JLabel jBeschreibungDB3;
	private javax.swing.JLabel jName;
	private javax.swing.JLabel jNameDB;
	private javax.swing.JLabel jTermin;
	private javax.swing.JLabel jTerminDB;
	private javax.swing.JLabel jTreffpunkt;
	private javax.swing.JLabel jTreffpunktDB;
	private javax.swing.JLabel jVeranstaltungsdetails;
	private javax.swing.JButton okButton;
	private JButton teilnehmerButton;

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

	// neuer Dialog
	public Veranstaltungsdetails(JFrame frame, Veranstaltung veran, String username) {
		super(frame);
		// Hinzufügen der Komponenten zum Panel
		initComponents();

		// Details der angeklickten Veranstaltung auslesen
		// Festlegen der Schriftart und Schriftgröße der Strings		
		jNameDB.setText(veran.getTitel());
		jNameDB.setFont(new java.awt.Font("Tahoma", Font.PLAIN, 16));
		
		if (veran.getTermintyp() == 1)
			jTerminDB.setText(new java.sql.Date(veran.getTermin().getTime()).toString());
		else
			jTerminDB.setText("noch unbekannt");
		jTerminDB.setFont(new java.awt.Font("Tahoma", Font.PLAIN, 16));
		
		if (veran.getTreffpunkt() != null && !veran.getTreffpunkt().equals("")) {
			jTreffpunktDB.setText(veran.getTreffpunkt());
		} else
			jTreffpunktDB.setText("noch unbekannt");
		jTreffpunktDB.setFont(new java.awt.Font("Tahoma", Font.PLAIN, 16));

		String stringBeschrDB = veran.getBeschreibung();
		// Teilen des Beschreibungstexts an den Leerzeichen
		String[] beschr = stringBeschrDB.split(" ");
		// Strings des jeweiligen Labels zur Beschreibung - noch leer
		String beschrZeile1 = "";
		String beschrZeile2 = "";
		String beschrZeile3 = "";
		// Methode, damit die Beschreibung auf mehrere Labels verteilt wird
		teilenDesTexts(beschrZeile1, beschrZeile2, beschrZeile3, stringBeschrDB, beschr);

		if (username.equals(veran.getOrganisator()))
			absagenButton.setEnabled(false);

		JDialog self = this;

		// Aktion beim Klicken des Absage-Buttons
		absagenButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent evt) {
				// Nachfrage, ob wirklich abgesagt werden soll
				WirklichAbsagen wirklich = new WirklichAbsagen(frame, self, username, veran);
				wirklich.setLocationRelativeTo(frame);
				wirklich.setModal(true);
				wirklich.setTitle("Absagen");
				wirklich.setVisible(true);
			}
		});

		// Klicken -> Liste der Teilnehmer wird angezeigt
		teilnehmerButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Gaesteliste gaesteliste = new Gaesteliste(frame, veran, username);
				gaesteliste.setLocationRelativeTo(frame);
				gaesteliste.setModal(true);
				gaesteliste.setTitle("Gästeliste");
				gaesteliste.setVisible(true);
			}
		});

		// AKtion beim Klicken des OK-Buttons
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

	}

	// Initialisierung der Eingabemaske
	private void initComponents() {

		jName = new javax.swing.JLabel();
		jVeranstaltungsdetails = new javax.swing.JLabel();
		okButton = new javax.swing.JButton();
		absagenButton = new javax.swing.JButton();
		jBeschreibung = new javax.swing.JLabel();
		jTreffpunkt = new javax.swing.JLabel();
		jTermin = new javax.swing.JLabel();

		jNameDB = new JLabel();
		jTerminDB = new JLabel();
		jTreffpunktDB = new JLabel();
		jBeschreibungDB1 = new JLabel();
		jBeschreibungDB2 = new JLabel();
		jBeschreibungDB3 = new JLabel();

		// Schließen, Größe und Hintergrundfarbe des JDialogs
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(500, 400);
		getContentPane().setBackground(new java.awt.Color(246, 250, 252));

		// Text und Schrift der JLabels
		jVeranstaltungsdetails.setFont(new java.awt.Font("Tahoma", 1, 17));
		jVeranstaltungsdetails.setText("Veranstaltungsdetails");
		jName.setFont(new java.awt.Font("Tahoma", 1, 16));
		jName.setText("Name:");
		jTermin.setFont(new java.awt.Font("Tahoma", 1, 16));
		jTermin.setText("Termin:");
		jTreffpunkt.setFont(new java.awt.Font("Tahoma", 1, 16));
		jTreffpunkt.setText("Treffpunkt:");
		jBeschreibung.setFont(new java.awt.Font("Tahoma", 1, 16));
		jBeschreibung.setText("Beschreibung:");

		// Text der Buttons
		okButton.setFont(new java.awt.Font("Tahoma", Font.BOLD, 14));
		okButton.setText("OK");
		absagenButton.setFont(new java.awt.Font("Tahoma", Font.BOLD, 14));
		absagenButton.setText("Absagen");
		teilnehmerButton = new JButton("Teilnehmer anzeigen");
		teilnehmerButton.setFont(new java.awt.Font("Tahoma", Font.BOLD, 14));

		// Platzierung der Komponenten im JDialog
		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		// Horizontale Ausrichtung der Komponenten
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout
						.createSequentialGroup().addGroup(layout.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(layout
										.createSequentialGroup().addGap(74, 74, 74).addGroup(layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addGroup(layout
														.createSequentialGroup().addComponent(jVeranstaltungsdetails))
												.addGroup(layout.createSequentialGroup()
														.addComponent(jName).addGap(26, 26, 26).addComponent(jNameDB))
												.addGroup(layout.createSequentialGroup().addComponent(jTreffpunkt)
														.addGap(6, 6, 6).addComponent(jTreffpunktDB))
												.addGroup(layout.createSequentialGroup().addComponent(jTermin)
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
														.addComponent(jTerminDB))
												.addComponent(jBeschreibungDB3).addComponent(jBeschreibungDB2)
												.addComponent(jBeschreibungDB1).addComponent(jBeschreibung))
										.addGap(0, 0, Short.MAX_VALUE))
								.addGroup(layout.createSequentialGroup().addGap(74)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(teilnehmerButton)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(absagenButton).addComponent(okButton)))
						.addGap(65)));
		// Vertikale Ausrichtung der Komponenten
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				javax.swing.GroupLayout.Alignment.LEADING,
				layout.createSequentialGroup().addContainerGap().addComponent(jVeranstaltungsdetails).addGap(29, 29, 29)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(jName).addComponent(jNameDB))
						.addGap(18, 18, 18)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(jTermin).addComponent(jTerminDB))
						.addGap(18, 18, 18)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(jTreffpunkt).addComponent(jTreffpunktDB))
						.addGap(18, 18, 18).addComponent(jBeschreibung)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(jBeschreibungDB1)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(jBeschreibungDB2)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(jBeschreibungDB3)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 70, Short.MAX_VALUE)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(absagenButton).addComponent(okButton).addComponent(teilnehmerButton))
						.addContainerGap()));

		pack();
	}

	// Methode um den Text auf mehrere Labels/ Zeilen zu verteilen, je nach
	// Länge
	private void teilenDesTexts(String beschrZeile1, String beschrZeile2, String beschrZeile3, String stringBeschrDB,
			String[] beschr) {

		// je nachdem wie lang der String ist, jat jedes Label eine bestimmte
		// Anzahl an Wörtern
		// des Strings
		if (stringBeschrDB.length() <= 60) {
			jBeschreibungDB1 = new javax.swing.JLabel(stringBeschrDB);
			jBeschreibungDB1.setFont(new java.awt.Font("Tahoma", Font.PLAIN, 16));
			jBeschreibungDB2 = new javax.swing.JLabel("");
			jBeschreibungDB3 = new javax.swing.JLabel("");
			jBeschreibungDB2.setVisible(false);
			jBeschreibungDB3.setVisible(false);

		} else if ((stringBeschrDB.length() > 60) && (stringBeschrDB.length() <= 120)) {
			for (int i = 0; i < beschr.length; i++) {
				if (i < 9) {
					beschrZeile1 = beschrZeile1 + beschr[i] + " ";
				} else if ((i >= 9) && (i <= beschr.length)) {
					beschrZeile2 = beschrZeile2 + beschr[i] + " ";
				}
			}
			jBeschreibungDB1 = new javax.swing.JLabel(beschrZeile1);
			jBeschreibungDB1.setFont(new java.awt.Font("Tahoma", Font.PLAIN, 16));
			jBeschreibungDB2 = new javax.swing.JLabel(beschrZeile2);
			jBeschreibungDB2.setFont(new java.awt.Font("Tahoma", Font.PLAIN, 16));
			jBeschreibungDB3 = new javax.swing.JLabel("");
			jBeschreibungDB3.setVisible(false);

		} else if ((stringBeschrDB.length() > 120) && (stringBeschrDB.length() <= 200)) {
			for (int i = 0; i < beschr.length; i++) {
				if (i < 10) {
					beschrZeile1 = beschrZeile1 + beschr[i] + " ";
				} else if ((i >= 10) && (i < 20)) {
					beschrZeile2 = beschrZeile2 + beschr[i] + " ";
				} else if ((i >= 20) && (i < beschr.length)) {
					beschrZeile3 = beschrZeile3 + beschr[i] + " ";
				}
			}
			jBeschreibungDB1 = new javax.swing.JLabel(beschrZeile1);
			jBeschreibungDB1.setFont(new java.awt.Font("Tahoma", Font.PLAIN, 16));
			jBeschreibungDB2 = new javax.swing.JLabel(beschrZeile2);
			jBeschreibungDB2.setFont(new java.awt.Font("Tahoma", Font.PLAIN, 16));
			jBeschreibungDB3 = new javax.swing.JLabel(beschrZeile3);
			jBeschreibungDB3.setFont(new java.awt.Font("Tahoma", Font.PLAIN, 16));
		}
	}
}
