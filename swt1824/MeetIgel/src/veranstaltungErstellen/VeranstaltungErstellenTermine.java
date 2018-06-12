package veranstaltungErstellen;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.rmi.server.UID;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.swing.JEditorPane;
import javax.swing.JFrame;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class VeranstaltungErstellenTermine extends javax.swing.JPanel {

	// Variablendeklaration der Komponenten für den Panel
	private javax.swing.JButton jButtonSpeichernWeiter;
	private javax.swing.JLabel jTermin1;
	private javax.swing.JLabel jTermin2;
	private javax.swing.JLabel jTermin3;
	private javax.swing.JLabel jDatum;
	private javax.swing.JLabel jStandortTermin1;
	private javax.swing.JLabel jStandortTermin2;
	private javax.swing.JLabel jStandortTermin3;
	private javax.swing.JLabel jTreffpunktTermin1;
	private javax.swing.JLabel jTreffpunktTermin2;
	private javax.swing.JLabel jTreffpunktTermin3;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JRadioButton radioBerechnenLassen;
	private javax.swing.JRadioButton radioFestlegen;
	private javax.swing.JTextField jTextFieldTermin1;
	private javax.swing.JTextField jTextFieldTermin2;
	private javax.swing.JTextField jTextFieldTermin3;
	private javax.swing.JTextField jTextFieldStandortTermin1;
	private javax.swing.JTextField jTextFieldStandortTermin2;
	private javax.swing.JTextField jTextFieldStandortTermin3;
	private javax.swing.JTextField jTextFieldTreffpunktTermin1;
	private javax.swing.JTextField jTextFieldTreffpunktTermin2;
	private javax.swing.JTextField jTextFieldTreffpunktTermin3;
	private javax.swing.JButton jButtonPlus;

	// Klassenvariablen
	private int clicked = 0;
	private boolean korrekteEingaben = true;
	private boolean auswahlGetroffen = true;

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
	public VeranstaltungErstellenTermine(JFrame frame, Veranstaltung veran, final String username) {
		initComponents(frame);

		// "TT.MM.JJJJ" in den Terminfeldern anzeigen, bis sie angeklickt werden
		jTextFieldTermin1.setText("TT.MM.JJJJ");
		jTextFieldTermin1.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0) {
				if (!Verifier.date(jTextFieldTermin1))
					jTextFieldTermin1.setText("TT.MM.JJJJ");
			}

			@Override
			public void focusGained(FocusEvent arg0) {
				jTextFieldTermin1.setText("");
			}
		});

		jTextFieldTermin2.setText("TT.MM.JJJJ");
		jTextFieldTermin2.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0) {
				if (!Verifier.date(jTextFieldTermin2))
					jTextFieldTermin2.setText("TT.MM.JJJJ");
			}

			@Override
			public void focusGained(FocusEvent arg0) {
				jTextFieldTermin2.setText("");
			}
		});

		jTextFieldTermin3.setText("TT.MM.JJJJ");
		jTextFieldTermin3.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0) {
				if (!Verifier.date(jTextFieldTermin3))
					jTextFieldTermin3.setText("TT.MM.JJJJ");
			}

			@Override
			public void focusGained(FocusEvent arg0) {
				jTextFieldTermin3.setText("");
			}
		});

		/*
		 * Nach dem Klick auf "Speichern" wird geschaut, wie viele Terminfelder
		 * ausgefüllt sind und anhand der Anzahl der Termintyp festgelegt.
		 * Außerdem werden die Treffpunktauswahl im Fall von
		 * "Treffpunkt festlegen" oder die Standorte des Organisators im Fall
		 * von "Treffpunkt berechnen lassen gespeichert.
		 */
		jButtonSpeichernWeiter.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				// Testen, ob eine der beiden Auswahlmöglichkeiten angwählt
				// ist
				if (!radioBerechnenLassen.isSelected() && !radioFestlegen.isSelected())
					auswahlGetroffen = false;
				if (radioBerechnenLassen.isSelected() || radioFestlegen.isSelected())
					auswahlGetroffen = true;

				// Eintragungen des Nutzers für Termine und Ort auslesen
				if((jTextFieldTermin1.getText().equals("TT.MM.JJJJ")) || (jTextFieldTermin1.getText().isEmpty())){
					KeinenTerminAngegeben ktA = new KeinenTerminAngegeben(frame);
					ktA.setModal(true);
					ktA.setLocationRelativeTo(frame);
					ktA.setTitle("Keinen Termin angegeben");
					ktA.setVisible(true);
					
				} else if (!jTextFieldTermin1.getText().equals("TT.MM.JJJJ")) {
					try {
						// Termintyp auf festgelegt setzen und erstes Datum in
						// Terminauswahl speichern
						veran.setTermintyp(1);
						veran.setTerminauswahl(new SimpleDateFormat("dd.MM.yyyy").parse(jTextFieldTermin1.getText()),
								0);
						// falls berechnen gewählt und Ort in DB angegebenen
						// Standort
						// in Standorte speichern
						if (radioBerechnenLassen.isSelected()) {
							if (Verifier.ort(jTextFieldStandortTermin1)) {
								veran.setZugehoerigeStandorte(jTextFieldStandortTermin1.getText(), 0);
								korrekteEingaben = true;
							} else {
								korrekteEingaben = false;
							}
							// falls festlegen
						} else {
							if (Verifier.ort(jTextFieldTreffpunktTermin1)) {
								veran.setZugehoerigeTreffpunkte(jTextFieldTreffpunktTermin1.getText(), 0);
								korrekteEingaben = true;
							} else {
								korrekteEingaben = false;
							}
						}
					} catch (ParseException pe) {
						pe.printStackTrace();
					}
				}

				if(((jTextFieldTermin2.getText().equals("TT.MM.JJJJ")) && (jTextFieldTermin2.isEnabled())) || (jTextFieldTermin2.getText().isEmpty())){
					KeinenTerminAngegeben ktA = new KeinenTerminAngegeben(frame);
					ktA.setModal(true);
					ktA.setLocationRelativeTo(frame);
					ktA.setTitle("Keinen Termin angegeben");
					ktA.setVisible(true);
				} else if (!jTextFieldTermin2.getText().equals("TT.MM.JJJJ")) {
					try {
						veran.setTermintyp(2);
						veran.setTerminauswahl(new SimpleDateFormat("dd.MM.yyyy").parse(jTextFieldTermin2.getText()),
								1);
						if (radioBerechnenLassen.isSelected() == true) {
							if (Verifier.ort(jTextFieldStandortTermin2)) {
								veran.setZugehoerigeStandorte(jTextFieldStandortTermin2.getText(), 1);
								korrekteEingaben = true;
							} else {
								korrekteEingaben = false;
							}
						} else {
							if (Verifier.ort(jTextFieldTreffpunktTermin2)) {
								veran.setZugehoerigeTreffpunkte(jTextFieldTreffpunktTermin2.getText(), 1);
								korrekteEingaben = true;
							} else {
								korrekteEingaben = false;
							}
						}
					} catch (ParseException pe) {
						pe.printStackTrace();
					}
				} else
					veran.setTermintyp(1);

				if(((jTextFieldTermin3.getText().equals("TT.MM.JJJJ")) && (jTextFieldTermin3.isEnabled())) || (jTextFieldTermin3.getText().isEmpty())){
					KeinenTerminAngegeben ktA = new KeinenTerminAngegeben(frame);
					ktA.setModal(true);
					ktA.setLocationRelativeTo(frame);
					ktA.setTitle("Keinen Termin angegeben");
					ktA.setVisible(true);
				} else if (!jTextFieldTermin3.getText().equals("TT.MM.JJJJ")) {
					try {
						veran.setTermintyp(3);
						veran.setTerminauswahl(new SimpleDateFormat("dd.MM.yyyy").parse(jTextFieldTermin3.getText()),
								2);
						if (radioBerechnenLassen.isSelected() == true) {
							if (Verifier.ort(jTextFieldStandortTermin3)) {
								veran.setZugehoerigeStandorte(jTextFieldStandortTermin3.getText(), 2);
								korrekteEingaben = true;
							} else {
								korrekteEingaben = false;
							}
						} else {
							if (Verifier.ort(jTextFieldTreffpunktTermin3)) {
								veran.setZugehoerigeTreffpunkte(jTextFieldTreffpunktTermin3.getText(), 2);
								korrekteEingaben = true;
							} else {
								korrekteEingaben = false;
							}
						}
					} catch (ParseException pe) {
						pe.printStackTrace();
					}
				}

				// wenn Treffpunkt berechnet werden soll, ist Treffpunkttyp = 0
				if (radioBerechnenLassen.isSelected())
					veran.setTreffpunkttyp(0);
				// wenn Treffpunkt festgelegt und nur ein Termin vorgegeben
				// wird, ist Treffpunkttyp = 1
				if (radioFestlegen.isSelected() && veran.getTermintyp() == 1) {
					veran.setTreffpunkttyp(1);
					veran.setTreffpunkt(jTextFieldTreffpunktTermin1.getText());
				}
				// wenn Treffpunkt festgelegt und nur mehrere Termine vorgegeben
				// werden, ist Treffpunkttyp = 2
				if (radioFestlegen.isSelected() && veran.getTermintyp() != 1)
					veran.setTreffpunkttyp(2);

				// Weiterleitung zu den Einladungen, falls alle nötigen Eingaben
				// korrekt gemacht wurden
				if (korrekteEingaben && auswahlGetroffen) {

					anDBsenden(veran, username);

					frame.getContentPane().removeAll();
					frame.getContentPane().add(new Einladungen(frame, veran, username));
					frame.setTitle("Einladungen");
					frame.pack();

				} else if (!auswahlGetroffen) {
					KeineAuswahlGetroffen kag = new KeineAuswahlGetroffen(frame);
					kag.setModal(true);
					kag.setLocationRelativeTo(frame);
					kag.setTitle("Keine Auswahl getroffen");
					kag.setVisible(true);
				} else {
					OrtUnknown ortunknown = new OrtUnknown(frame);
					ortunknown.setModal(true);
					ortunknown.setLocationRelativeTo(frame);
					ortunknown.setTitle("Unbekannter Ort");
					ortunknown.setVisible(true);
				}
			}
		});

		// wenn der Treffpunkt festgelegt werden soll, werden die anderen
		// Textfelder grau gemacht
		radioFestlegen.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// wenn radioFestlegen angewählt wird, wird die eventuelle
				// Auswahl der anderen RadioButtons wieder entfernt
				radioBerechnenLassen.setSelected(false);

				// je nachdem, wie viele Datumsfelder aktiviert sind, wird die
				// Aktivierung der Ortseingabefelder angepasst
				visibleTextFieldsFestlegen(clicked);

				// falls der Radio Button wieder deselected wird
				if (radioFestlegen.isSelected() == false) {
					radioButtonDeselected(clicked);
				}

				veran.setTreffpunkttyp(1); // Treffpunkt fest
			}
		});

		// wenn der Treffpunkt festgelegt werden soll, dann werden die anderen
		// Textfelder grau gemacht
		radioBerechnenLassen.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				radioFestlegen.setSelected(false);

				visibleTextFieldsBerechnen(clicked);

				if (radioBerechnenLassen.isSelected() == false) {
					radioButtonDeselected(clicked);
				}
				veran.setTreffpunkttyp(0); // Treffpunkt variabel
			}
		});

		// beim Klicken des Buttons wird jeweils ein weiteres Textfeld enabled,
		// um Termin festzulegen
		jButtonPlus.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (clicked == 0) {
					jTermin2.setEnabled(true);
					jTextFieldTermin2.setEnabled(true);
					if (radioBerechnenLassen.isSelected()) {
						jStandortTermin2.setEnabled(true);
						jTextFieldStandortTermin2.setEnabled(true);
					} else if (radioFestlegen.isSelected()) {
						jTreffpunktTermin2.setEnabled(true);
						jTextFieldTreffpunktTermin2.setEnabled(true);
					} else {
						jTreffpunktTermin2.setEnabled(true);
						jTextFieldTreffpunktTermin2.setEnabled(true);
						jStandortTermin2.setEnabled(true);
						jTextFieldStandortTermin2.setEnabled(true);
					}
				} else if (clicked == 1) {
					jTermin3.setEnabled(true);
					jTextFieldTermin3.setEnabled(true);
					if (radioBerechnenLassen.isSelected()) {
						jStandortTermin3.setEnabled(true);
						jTextFieldStandortTermin3.setEnabled(true);
					} else if (radioFestlegen.isSelected()) {
						jTreffpunktTermin3.setEnabled(true);
						jTextFieldTreffpunktTermin3.setEnabled(true);
					} else {
						jTreffpunktTermin3.setEnabled(true);
						jTextFieldTreffpunktTermin3.setEnabled(true);
						jStandortTermin3.setEnabled(true);
						jTextFieldStandortTermin3.setEnabled(true);
					}
				}
				clicked++;
			}
		});

	}

	// Initialisieren der Eingabemaske
	private void initComponents(JFrame frame) {

		jPanel1 = new javax.swing.JPanel();
		jPanel2 = new javax.swing.JPanel();
		radioBerechnenLassen = new javax.swing.JRadioButton();
		radioFestlegen = new javax.swing.JRadioButton();
		jTextFieldTermin3 = new javax.swing.JTextField();
		jTextFieldTermin1 = new javax.swing.JTextField();
		jTextFieldTermin2 = new javax.swing.JTextField();
		jTextFieldStandortTermin1 = new javax.swing.JTextField();
		jTextFieldStandortTermin2 = new javax.swing.JTextField();
		jTextFieldStandortTermin3 = new javax.swing.JTextField();
		jTextFieldTreffpunktTermin1 = new javax.swing.JTextField();
		jTextFieldTreffpunktTermin2 = new javax.swing.JTextField();
		jTextFieldTreffpunktTermin3 = new javax.swing.JTextField();
		jStandortTermin1 = new javax.swing.JLabel();
		jStandortTermin2 = new javax.swing.JLabel();
		jStandortTermin3 = new javax.swing.JLabel();
		jTreffpunktTermin1 = new javax.swing.JLabel();
		jTreffpunktTermin2 = new javax.swing.JLabel();
		jTreffpunktTermin3 = new javax.swing.JLabel();
		jDatum = new javax.swing.JLabel();
		jTermin1 = new javax.swing.JLabel();
		jTermin2 = new javax.swing.JLabel();
		jTermin3 = new javax.swing.JLabel();
		jButtonPlus = new javax.swing.JButton();
		jButtonSpeichernWeiter = new javax.swing.JButton();

		// Hintergrundfarbe und Größe des Hauptpanels
		setBackground(new java.awt.Color(53, 90, 154));
		setPreferredSize(new java.awt.Dimension(691, 559));

		// Hintergrundfarbe der JPanels
		jPanel1.setBackground(new java.awt.Color(246, 250, 252));
		jPanel2.setBackground(new java.awt.Color(246, 250, 252));

		// Festlegen des Textes und Schrift(-größe) der Radio Buttons
		radioBerechnenLassen.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
		radioBerechnenLassen.setText("Treffpunkt berechnen lassen, mein persönlicher Standort ist:");
		radioFestlegen.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
		radioFestlegen.setText("Treffpunkt festlegen:");

		// Festlegen des Textes der JLabel
		jStandortTermin1.setText("Standort 1.Termin");
		jStandortTermin2.setText("Standort 2.Termin");
		jStandortTermin3.setText("Standort 3.Termin");
		jTreffpunktTermin1.setText("Treffpunkt 1.Termin");
		jTreffpunktTermin2.setText("Treffpunkt 2.Termin");
		jTreffpunktTermin3.setText("Treffpunkt 3.Termin");
		jDatum.setFont(new java.awt.Font("Tahoma", 1, 16));
		jDatum.setText("Datum");
		jTermin1.setText("1. Termin");
		jTermin2.setText("2. Termin");
		jTermin3.setText("3. Termin");

		// Text und Schriftart der Buttons
		jButtonPlus.setText("+");
		jButtonSpeichernWeiter.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
		jButtonSpeichernWeiter.setText("Speichern und weiter");

		// Speichern und Weiter mit Enter
		frame.getRootPane().setDefaultButton(jButtonSpeichernWeiter);

		// TextFelder im oberen Panel mit den weiteren Terminen werden Enabled
		jTermin2.setEnabled(false);
		jTextFieldTermin2.setEnabled(false);
		jTermin3.setEnabled(false);
		jTextFieldTermin3.setEnabled(false);

		jTextFieldStandortTermin2.setEnabled(false);
		jTextFieldStandortTermin3.setEnabled(false);
		jStandortTermin2.setEnabled(false);
		jStandortTermin3.setEnabled(false);

		jTreffpunktTermin2.setEnabled(false);
		jTreffpunktTermin3.setEnabled(false);
		jTextFieldTreffpunktTermin2.setEnabled(false);
		jTextFieldTreffpunktTermin3.setEnabled(false);

		// Anordnung der Elemente auf den Panels
		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		// Horizontale Ausrichtung der Elemente
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel1Layout.createSequentialGroup().addContainerGap()
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(jTermin1)
								.addComponent(jTextFieldTermin1, javax.swing.GroupLayout.PREFERRED_SIZE, 148,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jDatum))
						.addGap(59, 59, 59)
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(jPanel1Layout.createSequentialGroup().addComponent(jButtonPlus)
										.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
								.addGroup(jPanel1Layout.createSequentialGroup()
										.addGroup(jPanel1Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(jTermin2).addComponent(jTextFieldTermin2,
														javax.swing.GroupLayout.PREFERRED_SIZE, 148,
														javax.swing.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 40,
												Short.MAX_VALUE)
										.addGroup(jPanel1Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(jTermin3).addComponent(jTextFieldTermin3,
														javax.swing.GroupLayout.PREFERRED_SIZE, 148,
														javax.swing.GroupLayout.PREFERRED_SIZE))
										.addGap(76, 76, 76)))));
		// Vertikale Ausrichtung der Elemente auf dem Panel
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel1Layout.createSequentialGroup().addContainerGap()
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(jDatum).addComponent(jButtonPlus, javax.swing.GroupLayout.PREFERRED_SIZE,
										20, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(jTermin1).addComponent(jTermin2).addComponent(jTermin3))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(jTextFieldTermin1, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jTextFieldTermin2, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jTextFieldTermin3, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
		jPanel2.setLayout(jPanel2Layout);
		jPanel2Layout
				.setHorizontalGroup(
						jPanel2Layout
								.createParallelGroup(
										javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(
										jPanel2Layout.createSequentialGroup().addContainerGap().addGroup(jPanel2Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(radioBerechnenLassen).addComponent(radioFestlegen)
												.addGroup(jPanel2Layout.createSequentialGroup().addGap(28, 28, 28)
														.addGroup(jPanel2Layout
																.createParallelGroup(
																		javax.swing.GroupLayout.Alignment.LEADING)
																.addGroup(jPanel2Layout.createSequentialGroup()
																		.addGroup(jPanel2Layout
																				.createParallelGroup(
																						javax.swing.GroupLayout.Alignment.LEADING)
																				.addComponent(jStandortTermin2)
																				.addComponent(jStandortTermin3)
																				.addComponent(jStandortTermin1))
																		.addGap(71, 71, 71)
																		.addGroup(jPanel2Layout
																				.createParallelGroup(
																						javax.swing.GroupLayout.Alignment.LEADING,
																						false)
																				.addComponent(jTextFieldStandortTermin1)
																				.addComponent(jTextFieldStandortTermin2)
																				.addComponent(jTextFieldStandortTermin3,
																						javax.swing.GroupLayout.PREFERRED_SIZE,
																						148,
																						javax.swing.GroupLayout.PREFERRED_SIZE)))
																.addGroup(jPanel2Layout.createSequentialGroup()
																		.addGroup(jPanel2Layout
																				.createParallelGroup(
																						javax.swing.GroupLayout.Alignment.LEADING)
																				.addComponent(jTreffpunktTermin2)
																				.addComponent(jTreffpunktTermin1)
																				.addComponent(jTreffpunktTermin3))
																		.addGap(57, 57, 57)
																		.addGroup(jPanel2Layout
																				.createParallelGroup(
																						javax.swing.GroupLayout.Alignment.LEADING)
																				.addComponent(
																						jTextFieldTreffpunktTermin2,
																						javax.swing.GroupLayout.PREFERRED_SIZE,
																						150,
																						javax.swing.GroupLayout.PREFERRED_SIZE)
																				.addComponent(
																						jTextFieldTreffpunktTermin1,
																						javax.swing.GroupLayout.PREFERRED_SIZE,
																						150,
																						javax.swing.GroupLayout.PREFERRED_SIZE)
																				.addComponent(
																						jTextFieldTreffpunktTermin3,
																						javax.swing.GroupLayout.PREFERRED_SIZE,
																						150,
																						javax.swing.GroupLayout.PREFERRED_SIZE))))))
												.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE,
														Short.MAX_VALUE)));
		jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel2Layout.createSequentialGroup()
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(radioBerechnenLassen)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(jTextFieldStandortTermin1, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jStandortTermin1))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(jTextFieldStandortTermin2, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jStandortTermin2))
						.addGap(15, 15, 15)
						.addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(jTextFieldStandortTermin3, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jStandortTermin3))
						.addGap(18, 18, 18).addComponent(radioFestlegen)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(jTreffpunktTermin1).addComponent(jTextFieldTreffpunktTermin1,
										javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(jTextFieldTreffpunktTermin2, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jTreffpunktTermin2))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(jTextFieldTreffpunktTermin3, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jTreffpunktTermin3))
						.addGap(14, 14, 14)));

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				javax.swing.GroupLayout.Alignment.TRAILING,
				layout.createSequentialGroup().addContainerGap(23, Short.MAX_VALUE)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
								.addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(jButtonSpeichernWeiter).addComponent(jPanel1,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE))
						.addGap(28, 28, 28)));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				javax.swing.GroupLayout.Alignment.TRAILING,
				layout.createSequentialGroup().addGap(40, 40, 40)
						.addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(jButtonSpeichernWeiter).addGap(19, 19, 19)));
	}

	// sichtbare Textfelder, wenn der Button radioBerechnenLassen gedrückt wurde
	public void visibleTextFieldsBerechnen(int clicked) {
		if (clicked == 0) {
			jTextFieldTreffpunktTermin1.setEnabled(false);
			jTextFieldStandortTermin1.setEnabled(true);
			jTextFieldTreffpunktTermin2.setEnabled(false);
		} else if (clicked == 1) {
			jTextFieldTreffpunktTermin1.setEnabled(false);
			jTextFieldStandortTermin1.setEnabled(true);
			jTextFieldTreffpunktTermin2.setEnabled(false);
			jTextFieldStandortTermin2.setEnabled(true);
		} else if (clicked > 1) {
			jTextFieldTreffpunktTermin1.setEnabled(false);
			jTextFieldStandortTermin1.setEnabled(true);
			jTextFieldTreffpunktTermin2.setEnabled(false);
			jTextFieldStandortTermin2.setEnabled(true);
			jTextFieldTreffpunktTermin3.setEnabled(false);
			jTextFieldStandortTermin3.setEnabled(true);
		}
	}

	// sichtbare Textfelder, wenn der Button radioFestlegen gedrückt wurde
	public void visibleTextFieldsFestlegen(int clicked) {

		if (clicked == 0) {
			jTextFieldStandortTermin1.setEnabled(false);
			jTextFieldTreffpunktTermin1.setEnabled(true);
		} else if (clicked == 1) {
			jTextFieldStandortTermin1.setEnabled(false);
			jTextFieldTreffpunktTermin1.setEnabled(true);
			jTextFieldStandortTermin2.setEnabled(false);
			jTextFieldTreffpunktTermin2.setEnabled(true);
		} else if (clicked > 1) {
			jTextFieldStandortTermin1.setEnabled(false);
			jTextFieldTreffpunktTermin1.setEnabled(true);
			jTextFieldStandortTermin2.setEnabled(false);
			jTextFieldTreffpunktTermin2.setEnabled(true);
			jTextFieldStandortTermin3.setEnabled(false);
			jTextFieldTreffpunktTermin3.setEnabled(true);
		}
	}

	// wenn kein Radio Button selected ist, werden die Textfelder,
	// je nachdem wie oft der User den Plus Button geklickt hat, wieder
	// aktiviert
	public void radioButtonDeselected(int clicked) {
		if (clicked == 0) {
			jTextFieldStandortTermin1.setEnabled(true);
			jTextFieldTreffpunktTermin1.setEnabled(true);
		} else if (clicked == 1) {
			jTextFieldStandortTermin1.setEnabled(true);
			jTextFieldTreffpunktTermin1.setEnabled(true);
			jTextFieldStandortTermin2.setEnabled(true);
			jTextFieldTreffpunktTermin2.setEnabled(true);
		} else if (clicked > 1) {
			jTextFieldStandortTermin1.setEnabled(true);
			jTextFieldTreffpunktTermin1.setEnabled(true);
			jTextFieldStandortTermin2.setEnabled(true);
			jTextFieldTreffpunktTermin2.setEnabled(true);
			jTextFieldStandortTermin3.setEnabled(true);
			jTextFieldTreffpunktTermin3.setEnabled(true);
		}
	}

	public void anDBsenden(Veranstaltung veran, String username) {
		try (Connection conn = connect()) {
			// bisher eingetragene Informationen über die
			// Veranstaltung
			// an Datenbank übergeben
			String sql1 = "INSERT INTO VERANSTALTUNG (id, titel, beschreibung, vpasswort, veranstalter) "
					+ "VALUES (?,?,?,?,?)";
			PreparedStatement st1 = conn.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS);
			st1.setInt(1, veran.getId());
			st1.setString(2, veran.getTitel());
			st1.setString(3, veran.getBeschreibung());
			st1.setString(4, veran.getvPasswort());
			st1.setString(5, username);
			st1.executeUpdate();

			String sql10 = "Select email from users where username = ?";
			PreparedStatement st10 = conn.prepareStatement(sql10, Statement.RETURN_GENERATED_KEYS);
			st10.setString(1, username);
			ResultSet rs10 = st10.executeQuery();
			rs10.next();
			String orgamail = rs10.getString(1);

			String sql2 = "INSERT INTO besucht (username, vid, status, email) " + "VALUES (?,?,2,?)";
			PreparedStatement st2 = conn.prepareStatement(sql2, Statement.RETURN_GENERATED_KEYS);
			st2.setString(1, username);
			st2.setInt(2, veran.getId());
			st2.setString(3, orgamail);
			st2.executeUpdate();

			// Termintyp, Terminauswahl und zugehörigeTreffpunkte an
			// Datenbank übergeben

			String sql3 = "Update VERANSTALTUNG set termintyp = ? where id = ?";
			PreparedStatement st3 = conn.prepareStatement(sql3, Statement.RETURN_GENERATED_KEYS);
			st3.setInt(1, veran.getTermintyp());
			st3.setInt(2, veran.getId());
			st3.executeUpdate();

			String sql4 = "Update VERANSTALTUNG set treffpunkttyp = ? where id = ?";
			PreparedStatement st4 = conn.prepareStatement(sql4, Statement.RETURN_GENERATED_KEYS);
			st4.setInt(1, veran.getTreffpunkttyp());
			st4.setInt(2, veran.getId());
			st4.executeUpdate();

			int index = 0;
			if (veran.getTermintyp() == 1) {
				String sql0 = "Update VERANSTALTUNG set endtermin = ? where id = ?";
				PreparedStatement st0 = conn.prepareStatement(sql0, Statement.RETURN_GENERATED_KEYS);
				st0.setDate(1, new java.sql.Date(veran.getTerminauswahl()[0].getTime()));
				st0.setInt(2, veran.getId());
				st0.executeUpdate();
			} else {
				while (index < 3 && veran.getTerminauswahl()[index] != null) {
					String sql5 = "Insert into termin (vid, ort, datum, terminnummer) values(?,?,?,?)";
					PreparedStatement st5 = conn.prepareStatement(sql5, Statement.RETURN_GENERATED_KEYS);
					st5.setInt(1, veran.getId());
					if (veran.getTreffpunkttyp() == 2) {
						st5.setString(2, '"' + veran.getZugehoerigeTreffpunkte()[index] + '"');
					} else {
						st5.setString(2, null);
					}
					st5.setDate(3, new java.sql.Date(veran.getTerminauswahl()[index].getTime()));
					st5.setInt(4, index + 1);
					st5.executeUpdate();
					index++;
				}
			}

			if (veran.getTreffpunkttyp() == 1) {
				String sql0 = "Update VERANSTALTUNG set ort = ? where id = ?";
				PreparedStatement st0 = conn.prepareStatement(sql0, Statement.RETURN_GENERATED_KEYS);
				st0.setString(1, '"' + veran.getTreffpunkt() + '"');
				st0.setInt(2, veran.getId());
				st0.executeUpdate();
			}
			index = 0;
			if (veran.getTreffpunkttyp() == 0) {

				String sql0 = "Update besucht set standort1= ?, standort2 = ?, standort3 = ? where vid = ? and username = ?";
				PreparedStatement st0 = conn.prepareStatement(sql0, Statement.RETURN_GENERATED_KEYS);
				if (veran.getZugehoerigeStandorte()[1] != null) {
					st0.setString(1, '"' + veran.getZugehoerigeStandorte()[0] + '"');
				} else
					st0.setString(1, null);
				if (veran.getZugehoerigeStandorte()[1] != null) {
					st0.setString(2, '"' + veran.getZugehoerigeStandorte()[2] + '"');
				} else
					st0.setString(2, null);
				if (veran.getZugehoerigeStandorte()[1] != null) {
					st0.setString(3, '"' + veran.getZugehoerigeStandorte()[2] + '"');
				} else
					st0.setString(3, null);
				st0.setInt(4, veran.getId());
				st0.setString(5, username);
				st0.executeUpdate();

			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
}
