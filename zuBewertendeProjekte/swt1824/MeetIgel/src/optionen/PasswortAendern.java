package optionen;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JFrame;

import login.Login;
import login.LoginCheck;
import login.LoginError;
import login.LoginUserUnknown;
import login.RegErrPW;
import login.RegErrUser;
import login.RegisterUser;
import startseite.BevorstehendeVeranstaltungen;

public class PasswortAendern extends javax.swing.JPanel{
	
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
				conn = DriverManager.getConnection(url,	user, password);
			} catch(SQLException e) {
				System.out.println(e.getMessage());
			}
			return conn;
		}
	
	// Variablendeklaration
		private javax.swing.JButton zurueckButton;
		private javax.swing.JLabel jMeetIgelLogo;
		private javax.swing.JLabel jEmail;
		private javax.swing.JLabel jAltesPasswort;
		private javax.swing.JLabel jPasswort;
		private javax.swing.JLabel jPasswortWiederholen;
		private javax.swing.JPasswordField jPasswordField1;
		private javax.swing.JPasswordField jPasswordField2;
		private javax.swing.JLabel jEmailWert;
		private javax.swing.JPasswordField jPasswordAltField;
		private javax.swing.JButton aendernButton;
	   
		
		// neue Eingabemaske
		public PasswortAendern(JFrame frame, String username) {
			initComponents(frame);
			
			try(Connection conn = connect()) {
	        	String sqlEmail = "select email from users where username=?";
				PreparedStatement stEmail = conn.prepareStatement(sqlEmail);
				stEmail.setString(1, username);
				ResultSet rsEmail = stEmail.executeQuery();
				rsEmail.next();
				jEmailWert.setText(rsEmail.getString("email"));	
			} catch(SQLException ex) {
				ex.printStackTrace();			
			}
			
			// Weiterleitung zur actionPerformed-Methode des Registrieren-Buttons
			aendernButton.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							jButton2ActionPerformed(e, frame, username);
						} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				});
			 
			 // Zurück zum Login
			 zurueckButton.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						frame.getContentPane().removeAll();
						frame.getContentPane().add(new BevorstehendeVeranstaltungen(frame, username));
						frame.setTitle("Startseite");
						frame.pack();
					}
				});
		}

		
		// Initialisieren der Eingabemaske
	    private void initComponents(JFrame frame) {

	        jMeetIgelLogo = new javax.swing.JLabel();
	        jEmail = new javax.swing.JLabel();
	        jAltesPasswort = new javax.swing.JLabel();
	        jPasswort = new javax.swing.JLabel();
	        jPasswortWiederholen = new javax.swing.JLabel();
	        jEmailWert = new javax.swing.JLabel();
	        jPasswordAltField = new javax.swing.JPasswordField();
	        jPasswordField1 = new javax.swing.JPasswordField();
	        jPasswordField2 = new javax.swing.JPasswordField();
	        aendernButton = new javax.swing.JButton();
	        zurueckButton = new javax.swing.JButton();

	        // Hintergrund und Größe des Panels 
	        setPreferredSize(new java.awt.Dimension(691, 559));
	        setBackground(new java.awt.Color(246, 250, 252));

	        // MeetIgel Logo
	        jMeetIgelLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/bilder/rsz_1rsz_meetigel.png"))); 

	        // Text der Labels 
	        jEmail.setText("Deine Email-Adresse:");
	        jEmail.setFont(new java.awt.Font("Tahoma", Font.BOLD, 12)); 
	        jAltesPasswort.setText("Altes Passwort:");
	        jAltesPasswort.setFont(new java.awt.Font("Tahoma", Font.BOLD, 12)); 
	        jPasswort.setText("Neues Passwort:");
	        jPasswort.setFont(new java.awt.Font("Tahoma", Font.BOLD, 12)); 
	        jPasswortWiederholen.setText("Neues Passwort wiederholen:");
	        jPasswortWiederholen.setFont(new java.awt.Font("Tahoma", Font.BOLD, 12)); 
	        
	        
	        // Text und Schriftart der Buttons
	        aendernButton.setFont(new java.awt.Font("Tahoma", 1, 16)); 
	        aendernButton.setText("Ändern");
	        zurueckButton.setFont(new java.awt.Font("Tahoma", 1, 16)); 
	        zurueckButton.setText("Zurück");
	        
	        // Registrieren mit Enter
	        frame.getRootPane().setDefaultButton(aendernButton);

	        // Anordnung der Elemente
	        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
	        this.setLayout(layout);
	        // Horizontale Anordnung im Panel
	        layout.setHorizontalGroup(
	            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(layout.createSequentialGroup()
	                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                    .addGroup(layout.createSequentialGroup()
	                        .addGap(237, 237, 237)
	                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
	                            .addGroup(layout.createSequentialGroup()
	                                .addComponent(zurueckButton)
	                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                                .addComponent(aendernButton))
	                            .addComponent(jEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
	                            .addComponent(jAltesPasswort)
	                            .addComponent(jPasswort)
	                            .addComponent(jPasswortWiederholen, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
	                            .addComponent(jEmailWert)
	                            .addComponent(jPasswordAltField)
	                            .addComponent(jPasswordField1)
	                            .addComponent(jPasswordField2)))
	                    .addGroup(layout.createSequentialGroup()
	                        .addGap(279, 279, 279)
	                        .addComponent(jMeetIgelLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)))
	                .addContainerGap(219, Short.MAX_VALUE))
	        );
	        // Vertikale Anordnung im Panel 
	        layout.setVerticalGroup(
	            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(layout.createSequentialGroup()
	                .addGap(30, 30, 30)
	                .addComponent(jMeetIgelLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
	                .addComponent(jEmail)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                .addComponent(jEmailWert, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addGap(18, 18, 18)
	                .addComponent(jAltesPasswort)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                .addComponent(jPasswordAltField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
	                .addComponent(jPasswort)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                .addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addGap(18, 18, 18)
	                .addComponent(jPasswortWiederholen)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                .addComponent(jPasswordField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addGap(18, 18, 18)
	                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                    .addComponent(zurueckButton)
	                    .addComponent(aendernButton))
	                .addContainerGap(101, Short.MAX_VALUE))
	        );
	    }

	    // action-Performed-Methode des Aendern-Buttons
	    private void jButton2ActionPerformed(ActionEvent e, JFrame frame, String username) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
	    	// Eingaben des Nutzers auslesen und auf Korrektheit prüfen
	    	char[] passwordAlt = jPasswordAltField.getPassword();
			LoginCheck check = new LoginCheck();
			String answer = check.checkIt(username, passwordAlt);
			
			if (answer.equals("Declined")) {
				LoginError err = new LoginError(frame);
				err.setModal(true);
				err.setLocationRelativeTo(frame);
				err.setTitle("Falsches Passwort");
				err.setVisible(true);
			} else {
				try(Connection conn = connect()) {
					if(!Arrays.equals(jPasswordField1.getPassword(),jPasswordField2.getPassword())) {
			    		//Dialog & leere RegSeite & Abbruch
			    		RegErrPW regErrPW = new RegErrPW(frame);
			    		regErrPW.setModal(true);
			    		regErrPW.setLocationRelativeTo(frame);
			    		regErrPW.setTitle("Keine Passwortübereinstimmung");
			    		regErrPW.setVisible(true);
			    		jPasswordField1.setText("");
			    		jPasswordField2.setText("");
			    	} else if(!(new String(jPasswordField1.getPassword()).equals("") || new String(jPasswordField2.getPassword()).equals("")) ){
			    		char[] password = jPasswordField1.getPassword();
						SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
						//SecureRandom generator
				        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
				        //Create array for salt
				        byte[] salt = new byte[16];
//				        byte[] testsalt = new byte[16];

				        //Get a random salt
				        sr.nextBytes(salt);
						KeySpec ks = new PBEKeySpec(password,salt,1024,128);
						SecretKey s = f.generateSecret(ks);
						Key k = new SecretKeySpec(s.getEncoded(),"AES");
						String hashedpassword = k.toString();
			    		String sqlUpdate = "update users set passwort=? where username=?";
						PreparedStatement stUpdate = conn.prepareStatement(sqlUpdate);
						stUpdate.setString(1, hashedpassword);
						stUpdate.setString(2, username);
						stUpdate.executeUpdate();
						stUpdate.close();
						String sqlUpdateSalt = "update users set salt=? where username=?";
						PreparedStatement stUpdateSalt = conn.prepareStatement(sqlUpdateSalt);
						stUpdateSalt.setBytes(1, salt);
						stUpdateSalt.setString(2, username);
						stUpdateSalt.executeUpdate();
						stUpdateSalt.close();
			        	
			        	frame.getContentPane().removeAll();
						frame.getContentPane().add(new BevorstehendeVeranstaltungen(frame, username));
						frame.setTitle("Startseite");
						frame.pack();
			    	}					
				} catch(SQLException ex) {
					ex.printStackTrace();
					
				}
				
				
				
				
			}
	}
}