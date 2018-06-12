package login;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.GroupLayout;
import javax.swing.JFrame;

import veranstaltungBeitreten.VeranstaltungBeitreten;

public class Registrieren extends javax.swing.JPanel {

	// Variablendeklaration
	private javax.swing.JButton zurueck;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jNutzername;
	private javax.swing.JLabel jEMailAdresse;
	private javax.swing.JLabel jPasswort;
	private javax.swing.JLabel jPasswortWiederholen;
	private javax.swing.JPasswordField jPasswordField1;
	private javax.swing.JPasswordField jPasswordField2;
	private javax.swing.JTextField jTextFieldNutzername;
	private javax.swing.JTextField jTextFieldEMail;
	private javax.swing.JButton registrieren;

	// neue Eingabemaske
	public Registrieren(JFrame frame) {
		initComponents(frame);
	}

	// Initialisieren der Eingabemaske
	private void initComponents(JFrame frame) {

		jLabel1 = new javax.swing.JLabel();
		jNutzername = new javax.swing.JLabel();
		jEMailAdresse = new javax.swing.JLabel();
		jPasswort = new javax.swing.JLabel();
		jPasswortWiederholen = new javax.swing.JLabel();
		jTextFieldNutzername = new javax.swing.JTextField();
		jTextFieldEMail = new javax.swing.JTextField();
		jPasswordField1 = new javax.swing.JPasswordField();
		jPasswordField2 = new javax.swing.JPasswordField();
		registrieren = new javax.swing.JButton();
		zurueck = new javax.swing.JButton();

		setPreferredSize(new java.awt.Dimension(691, 559));
		setBackground(new java.awt.Color(246, 250, 252));

		jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/bilder/rsz_1rsz_meetigel.png")));

		jNutzername.setText("Nutzername:");
		jNutzername.setFont(new java.awt.Font("Tahoma", Font.BOLD, 12)); 
		jEMailAdresse.setText("E-Mail-Adresse:");
		jEMailAdresse.setFont(new java.awt.Font("Tahoma", Font.BOLD, 12)); 
		jPasswort.setText("Passwort:");
		jPasswort.setFont(new java.awt.Font("Tahoma", Font.BOLD, 12)); 
		jPasswortWiederholen.setText("Passwort wiederholen:");
		jPasswortWiederholen.setFont(new java.awt.Font("Tahoma", Font.BOLD, 12)); 

		registrieren.setFont(new java.awt.Font("Tahoma", 1, 16)); 
		registrieren.setText("Registrieren");

		// Weiterleitung zur actionPerformed-Methode des Registrieren-Buttons
		registrieren.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					jButton2Registrieren(e, frame);
				} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		// Registrieren mit Enter
		frame.getRootPane().setDefaultButton(registrieren);

		zurueck.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
		zurueck.setText("Zurück");

		// Zurück zum Login
		zurueck.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				frame.getContentPane().removeAll();
				frame.getContentPane().add(new Login(frame));
				frame.setTitle("Login");
				frame.pack();
			}
		});

		// Anordnung der Elemente
		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		// Horizontale Anordnung der Elemente 
		layout.setHorizontalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
						.createSequentialGroup().addGroup(layout
								.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
										.createSequentialGroup().addGap(237, 237, 237).addGroup(layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
												.addGroup(layout.createSequentialGroup().addComponent(zurueck)
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addComponent(registrieren))
												.addComponent(jNutzername, javax.swing.GroupLayout.PREFERRED_SIZE, 130,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(jEMailAdresse).addComponent(jPasswort)
												.addComponent(jPasswortWiederholen,
														javax.swing.GroupLayout.PREFERRED_SIZE, 190,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(jTextFieldNutzername).addComponent(jTextFieldEMail)
												.addComponent(jPasswordField1).addComponent(jPasswordField2)))
								.addGroup(layout.createSequentialGroup().addGap(279, 279, 279).addComponent(jLabel1,
										javax.swing.GroupLayout.PREFERRED_SIZE, 115,
										javax.swing.GroupLayout.PREFERRED_SIZE)))
						.addContainerGap(219, Short.MAX_VALUE)));
		// Vertikale Anordnung der Elemente
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addGap(30, 30, 30)
						.addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 89,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(jNutzername)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(jTextFieldNutzername, javax.swing.GroupLayout.PREFERRED_SIZE, 26,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(18, 18, 18).addComponent(jEMailAdresse)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(jTextFieldEMail, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(jPasswort)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(18, 18, 18).addComponent(jPasswortWiederholen)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(jPasswordField2, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(18, 18, 18)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(zurueck).addComponent(registrieren))
						.addContainerGap(101, Short.MAX_VALUE)));
	}

	// action-Performed-Methode des Registrieren-Buttons
	private void jButton2Registrieren(ActionEvent e, JFrame frame) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {

		RegisterUser regUser = new RegisterUser();

		// Eingaben des Nutzers auslesen und auf Korrektheit prüfen
		String username = jTextFieldNutzername.getText();
		String email = jTextFieldEMail.getText();

		// Prüfen, ob Passwörter übereinstimmen, falls nicht Neueingabe
		// notwendig
		if (!Arrays.equals(jPasswordField1.getPassword(),jPasswordField2.getPassword())) {
			// Dialog & leere RegSeite & Abbruch
			RegErrPW regErrPW = new RegErrPW(frame);
			regErrPW.setModal(true);
			regErrPW.setLocationRelativeTo(frame);
			regErrPW.setTitle("Falsches Passwort");
			regErrPW.setVisible(true);
			jPasswordField1.setText("");
			jPasswordField2.setText("");

			// falls beide Passwortfelder korrekt gefüllt sind Nutzer
			// registrieren
		} else if (!(jPasswordField1.getPassword().equals("")
				|| new String(jPasswordField2.getPassword()).equals(""))) {
			char[] password = jPasswordField1.getPassword();
			SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			//SecureRandom generator
	        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
	        //Create array for salt
	        byte[] salt = new byte[16];

	        //Get a random salt
	        sr.nextBytes(salt);

			KeySpec ks = new PBEKeySpec(password,salt,1024,128);
			SecretKey s = f.generateSecret(ks);
			Key k = new SecretKeySpec(s.getEncoded(),"AES");
			String hashedpassword = k.toString();
			// Nutzer registrieren und zum Login zurückkehren
			regUser.register(username, email, hashedpassword, salt, frame);
			frame.getContentPane().removeAll();
			frame.getContentPane().add(new Login(frame));
			frame.setTitle("Login");
			frame.pack();
		} else {

		}
	}
}
