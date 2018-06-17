package gui;

import controlclasses.*;
import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.awt.event.ActionEvent;

public class Registierung extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;
	private JPasswordField passwordField;
	private JPasswordField passwordField_1;


	/**
	 * Create the frame.
	 * @throws IOException 
	 */
	public Registierung() throws IOException {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 320, 450);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setBackground(new Color(70, 130, 180));
		contentPane.setLayout(null);
		
		JLabel lblRegistrierung = new JLabel("Registrierung");				//Label "Registrierung"
		lblRegistrierung.setForeground(Color.WHITE);
		lblRegistrierung.setBounds(0, 45, 302, 28);
		lblRegistrierung.setHorizontalAlignment(SwingConstants.CENTER);
		lblRegistrierung.setFont(new Font("Century", Font.PLAIN, 24));
		contentPane.add(lblRegistrierung);
		
		JLabel lblBenutzername = new JLabel("Benutzername");				//Label "Benutzername"
		lblBenutzername.setFont(new Font("Century", Font.PLAIN, 14));
		lblBenutzername.setForeground(Color.WHITE);
		lblBenutzername.setHorizontalAlignment(SwingConstants.CENTER);
		lblBenutzername.setBounds(0, 96, 302, 14);						
		contentPane.add(lblBenutzername);
		
		JTextField benutzername = new JTextField();								//Textfeld für Benutzername
		benutzername.setBounds(64, 115, 169, 25);
		contentPane.add(benutzername);
		benutzername.setColumns(10);
		
		JLabel lblEmailadresse = new JLabel("E-Mail-Adresse");		//Label "E-Mail-Adresse"
		lblEmailadresse.setFont(new Font("Century", Font.PLAIN, 14));
		lblEmailadresse.setForeground(Color.WHITE);
		lblEmailadresse.setHorizontalAlignment(SwingConstants.CENTER);
		lblEmailadresse.setBounds(0, 150, 302, 20);
		contentPane.add(lblEmailadresse);
		
		JTextField email = new JTextField();								//Textfeld für E-Mail-Adresse
		email.setBounds(64, 172, 169, 25);
		contentPane.add(email);
		email.setColumns(10);
		
		JLabel lblPasswort = new JLabel("Passwort");				//Label für Passwort
		lblPasswort.setFont(new Font("Century", Font.PLAIN, 14));
		lblPasswort.setForeground(Color.WHITE);
		lblPasswort.setHorizontalAlignment(SwingConstants.CENTER);
		lblPasswort.setBounds(0, 205, 302, 20);
		contentPane.add(lblPasswort);
		
		JPasswordField password= new JPasswordField();						//Passworteingabefeld
		password.setBounds(64, 228, 169, 25);
		contentPane.add(password);
		
		JLabel lblPasswortBesttigen = new JLabel("Passwort best\u00E4tigen");		//Label für "Passwort bestätigen"
		lblPasswortBesttigen.setFont(new Font("Century", Font.PLAIN, 14));
		lblPasswortBesttigen.setForeground(Color.WHITE);
		lblPasswortBesttigen.setHorizontalAlignment(SwingConstants.CENTER);
		lblPasswortBesttigen.setBounds(0, 266, 302, 20);
		contentPane.add(lblPasswortBesttigen);
		
		JPasswordField passwordbest = new JPasswordField();						//zweite Passwortfeldeingabe
		passwordbest.setBounds(64, 289, 169, 25);
		contentPane.add(passwordbest);
		
		if (password.getName() != passwordbest.getName()){			//Kontrolle, ob Passwort richtig zur Bestätigung eingegeben wurde
			System.out.println("Password wurde falsch bestaetigt.");
		}
		JButton btnRegistrierungAbschlieen = new JButton("Registrierung abschlie\u00DFen");			//Button, um Registrierung abzuschließen
		btnRegistrierungAbschlieen.addActionListener(new ActionListener() {
			

			public void actionPerformed(ActionEvent e) {
				registerMessage  registerM = new registerMessage(benutzername.getText(),email.getText(), password.getPassword());
		    											// Client wird bei Registrierung angelegt
		    	
				try {
					Client neuerNutzer = new Client(benutzername.getText(),email.getText(),passwordbest.getPassword());
					OutputStream out = neuerNutzer.clientSocket.getOutputStream();
					neuerNutzer.clientSocket = new Socket("127.0.0.1",3306);
					((ObjectOutputStream) out).writeObject(registerM);
					InputStream datain = new ObjectInputStream(neuerNutzer.clientSocket.getInputStream());
					
					while(datain.available()>0) {
						((ObjectOutputStream) out).writeObject((MethodeMessage) registerM);
						out.flush();
							
					}
					if(datain.available()==0) {
						neuerNutzer.setEmail(email.getText());							
						neuerNutzer.setPassword(password.getPassword());
					}
					datain.close();
					
					Auswahlbildschirm auswahlbild = new Auswahlbildschirm(neuerNutzer);
					auswahlbild.setVisible(true);																//durch Drücken gelangt man zum Auswahlbildschirm
				
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
					System.out.println("unknown user");
					System.exit(1);
				} catch (IOException e1) {
					e1.printStackTrace();
					System.out.println("1 No I/O");
					System.exit(1);
				} catch (NullPointerException np1) {
					np1.getMessage();
				}
			}
		});
		btnRegistrierungAbschlieen.setBounds(52, 343, 197, 28);
		contentPane.add(btnRegistrierungAbschlieen);
		
		JButton btnNewButton = new JButton("<");
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				VisualClass Startbildschirm = new VisualClass();			//durch Drücken von zurück gelangt man wieder zum Startbildschirm
				Startbildschirm.setVisible(true);
				
			}
		});
		btnNewButton.setBounds(10, 11, 40, 40);
		contentPane.add(btnNewButton);
		
	}


	public JTextField getTextField() {
		return textField;
	}


	public void setTextField(JTextField textField) {
		this.textField = textField;
	}


	public JTextField getTextField_1() {
		return textField_1;
	}


	public void setTextField_1(JTextField textField_1) {
		this.textField_1 = textField_1;
	}


	public JPasswordField getPasswordField_1() {
		return passwordField_1;
	}


	public void setPasswordField_1(JPasswordField passwordField_1) {
		this.passwordField_1 = passwordField_1;
	}


	public JPasswordField getPasswordField() {
		return passwordField;
	}


	public void setPasswordField(JPasswordField passwordField) {
		this.passwordField = passwordField;
	}
}
