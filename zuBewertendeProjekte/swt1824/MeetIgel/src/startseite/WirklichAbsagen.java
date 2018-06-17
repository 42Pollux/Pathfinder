package startseite;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import login.Registrieren;
import veranstaltungErstellen.Veranstaltung;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class WirklichAbsagen extends JDialog {

	// Variablendeklaration der Panel Komponenten 
	private JButton cancelButton;
	private JButton absagenButton;
	private JLabel wirklichAbsagen;

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
	public WirklichAbsagen(JFrame frame, JDialog details, String username, Veranstaltung veran) {
		super(frame);
		initComponents();

		// Dialog schließen
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		// Aktion beim Klicken des Absagen Buttons und Weiterleiten zur Startseite 
		absagenButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Teilnehmer aus Veranstaltung löschen, indem sein Status
				// in der besucht-Tabelle auf abgesagt geändert wird
				try (Connection conn = connect()) {
					String sql2 = "Update besucht set status = ?, standort = ? where username = ? and vid = ?";
					PreparedStatement st2 = conn.prepareStatement(sql2, Statement.RETURN_GENERATED_KEYS);
					st2.setInt(1,3);
					st2.setObject(2, null);
					st2.setString(3, username);
					st2.setInt(4, veran.getId());
					st2.executeUpdate();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				frame.getContentPane().removeAll();
				frame.getContentPane().add(new BevorstehendeVeranstaltungen(frame, username));
				frame.pack();
				details.dispose();
				dispose();
			}
		});
	}

	// Initialisieren des Dialogs 
	private void initComponents() {

		wirklichAbsagen = new javax.swing.JLabel();
		absagenButton = new javax.swing.JButton();
		cancelButton = new javax.swing.JButton();

		// Fenster schließen und Festlegen der Hintergrundfarbe 
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		getContentPane().setBackground(new java.awt.Color(246, 250, 252));

		// Text und Schriftart-/größe des Labels 
		wirklichAbsagen.setFont(new java.awt.Font("Tahoma", Font.PLAIN, 16));
		wirklichAbsagen.setText("Bist du sicher, dass du dem Veranstalter absagen möchtest?");

		// Text und Schrift für Buttons 
		absagenButton.setFont(new java.awt.Font("Tahoma", 1, 14));
		absagenButton.setText("Absagen");
		cancelButton.setFont(new java.awt.Font("Tahoma", 1, 14));
		cancelButton.setText("Cancel");

		// Anordnung der Elemente
		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		// Horizontale Anordnung im Dialog
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(layout.createSequentialGroup().addGap(140, 140, 140)
										.addComponent(absagenButton, javax.swing.GroupLayout.PREFERRED_SIZE, 155,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(cancelButton))
								.addGroup(layout.createSequentialGroup().addGap(55, 55, 55)
										.addComponent(wirklichAbsagen)))
						.addContainerGap(60, Short.MAX_VALUE)));
		// Vertikale Anordnung der Komponenten im Dialog 
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addGap(35, 35, 35).addComponent(wirklichAbsagen)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 44, Short.MAX_VALUE)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(absagenButton).addComponent(cancelButton))
						.addGap(33, 33, 33)));

		pack();
	}
}
