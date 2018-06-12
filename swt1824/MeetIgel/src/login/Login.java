package login;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

import javax.swing.JFrame;

import startseite.BevorstehendeVeranstaltungen;
import veranstaltungBeitreten.VeranstaltungBeitreten;

public class Login extends javax.swing.JPanel {

	// Variablendeklaration
	private javax.swing.JButton jButtonLogin;
	private javax.swing.JButton jButtonRegistrieren;
	private javax.swing.JLabel jHerzlichWilkommen;
	private javax.swing.JLabel jNutzername;
	private javax.swing.JLabel jPasswort;
	private javax.swing.JLabel jNeuHier;
	private javax.swing.JLabel jMeetIgelLogo;
	private javax.swing.JPasswordField jPasswordField1;
	private javax.swing.JTextField jTextField1;
	private javax.swing.JButton jButtonBeenden;

	// neue Eingabemaske
	public Login(JFrame frame) {
		initComponents(frame);
	}

	// Initialisieren der Eingabemaske
	private void initComponents(JFrame frame) {

		jHerzlichWilkommen = new javax.swing.JLabel();
		jNutzername = new javax.swing.JLabel();
		jPasswort = new javax.swing.JLabel();
		jTextField1 = new javax.swing.JTextField();
		jPasswordField1 = new javax.swing.JPasswordField();
		jButtonLogin = new javax.swing.JButton();
		jNeuHier = new javax.swing.JLabel();
		jButtonRegistrieren = new javax.swing.JButton();
		jMeetIgelLogo = new javax.swing.JLabel();
		jButtonBeenden = new javax.swing.JButton();

		// Festlegen der Hintergrundfarbe und der Größe des Panels
		setBackground(new java.awt.Color(246, 250, 252));
		setPreferredSize(new java.awt.Dimension(691, 559));

		// Text der Labels
		jHerzlichWilkommen.setFont(new java.awt.Font("Tahoma", 1, 22)); // NOI18N
		jHerzlichWilkommen.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		jHerzlichWilkommen.setText("Herzlich Willkommen!");
		jNutzername.setText("Nutzername/ E-Mail-Adresse");
		jNutzername.setFont(new java.awt.Font("Tahoma", Font.BOLD, 12)); 
		jPasswort.setText("Passwort");
		jPasswort.setFont(new java.awt.Font("Tahoma", Font.BOLD, 12)); 
		jNeuHier.setText("Neu hier?");
		jNeuHier.setFont(new java.awt.Font("Tahoma", Font.BOLD, 12)); 

		// Login-Button konfigurieren
		jButtonLogin.setText("Login");
		jButtonLogin.setFont(new java.awt.Font("Tahoma", 1, 16));
		
		// Aufrufen der actionPerformed-Methode des Login-Buttons
		jButtonLogin.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				try {
					jButton1Login(evt, frame);
				} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		// Login durch Entertaste auslösen
		frame.getRootPane().setDefaultButton(jButtonLogin);

		// Registrieren-Button konfigurieren
		jButtonRegistrieren.setText("Registrieren");
		jButtonRegistrieren.setFont(new java.awt.Font("Tahoma", 1, 16));
		
		// Aufruf der actionPerformed-Methode des Registrieren-Buttons
		jButtonRegistrieren.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton2Registrieren(evt, frame);
			}
		});

		// Beenden-Button konfigurieren
		jButtonBeenden.setFont(new java.awt.Font("Tahoma", 0, 15));
		jButtonBeenden.setText("Beenden");
		// Vollständiges Beenden des Programms
				jButtonBeenden.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						frame.dispose();
						System.exit(0);
					}
				});

		// MeetIgel Logo 
		jMeetIgelLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/bilder/rsz_1rsz_meetigel.png"))); // NOI18N

		// Anordnung der Elemente im Panel 
		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		// Horizontale Anordung der Komponenten 
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout
						.createSequentialGroup().addGroup(layout
								.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(layout.createSequentialGroup().addContainerGap()
										.addComponent(jMeetIgelLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 115,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGap(80, 80, 80).addComponent(jHerzlichWilkommen,
												javax.swing.GroupLayout.PREFERRED_SIZE, 265,
												javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGroup(layout.createSequentialGroup().addGap(234, 234, 234).addGroup(layout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(jNeuHier)
										.addGroup(layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
												.addComponent(jPasswordField1)
												.addComponent(jPasswort, javax.swing.GroupLayout.PREFERRED_SIZE, 100,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(jNutzername, javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addComponent(jButtonLogin).addComponent(jTextField1,
														javax.swing.GroupLayout.PREFERRED_SIZE, 211,
														javax.swing.GroupLayout.PREFERRED_SIZE))
										.addComponent(jButtonRegistrieren))))
						.addGap(0, 210, Short.MAX_VALUE))
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
						layout.createSequentialGroup()
								.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(jButtonBeenden).addContainerGap()));
		// Vertikale Anordnung der Komponenten im Panel 
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
				.createSequentialGroup()
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup().addGap(8, 8, 8).addComponent(jMeetIgelLogo,
								javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGroup(layout.createSequentialGroup().addGap(75, 75, 75).addComponent(jHerzlichWilkommen,
								javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
				.addGap(32, 32, 32).addComponent(jNutzername)
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 26,
						javax.swing.GroupLayout.PREFERRED_SIZE)
				.addGap(18, 18, 18).addComponent(jPasswort)
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, 26,
						javax.swing.GroupLayout.PREFERRED_SIZE)
				.addGap(18, 18, 18).addComponent(jButtonLogin).addGap(57, 57, 57).addComponent(jNeuHier)
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(jButtonRegistrieren)
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 68, Short.MAX_VALUE)
				.addComponent(jButtonBeenden).addContainerGap()));
	}

	// actionPerformed-Methode des Login-Buttons
	private void jButton1Login(java.awt.event.ActionEvent evt, JFrame frame) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
		// Nutzereingabe auslesen und mithilfe von LoginCheck auf Korrektheit überprüfen
		String user = jTextField1.getText().trim();
		char[] password = jPasswordField1.getPassword();
		LoginCheck check = new LoginCheck();
		String answer = check.checkIt(user, password);
		// je nach Antwort von Login-Check zur Startseite weiterleiten oder einen Hinweis-Dialog öffnen
		if (answer.equals("User unknown")) {
			LoginUserUnknown usrUnknwn = new LoginUserUnknown(frame);
			usrUnknwn.setModal(true); // Solange der Dialog offen ist, kann im frame nichts verändert werden
			usrUnknwn.setLocationRelativeTo(frame); // Mittig im frame 
			usrUnknwn.setTitle("Unbekannter Nutzer"); 
			usrUnknwn.setVisible(true);
			
		} else if (answer.equals("Declined")) {
			LoginError err = new LoginError(frame);
			err.setModal(true);
			err.setLocationRelativeTo(frame);
			err.setTitle("Login Error");
			err.setVisible(true);
			
		} else {
			frame.getContentPane().removeAll();
			frame.getContentPane().add(new BevorstehendeVeranstaltungen(frame, user));
			frame.setTitle("Startseite");
			frame.pack();
		}
	}

	// actionPerformed-Methode des Registrieren-Buttons: Weiterleiten zur Registrierung
	private void jButton2Registrieren(java.awt.event.ActionEvent evt, JFrame frame) {
		frame.getContentPane().removeAll();
		frame.getContentPane().add(new Registrieren(frame));
		frame.setTitle("Registrieren");
		frame.pack();
	}
}
