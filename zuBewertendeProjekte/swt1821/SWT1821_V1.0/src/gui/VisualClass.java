package gui;

import controlclasses.*;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import java.awt.Color;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.event.ActionEvent;

public class VisualClass extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VisualClass frame = new VisualClass();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */ 
	public VisualClass() {
		setTitle("LocatoR");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 320, 450);
		contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setBackground(new Color(70, 130, 180));
		contentPane.setLayout(null);
		
		JLabel lblName = new JLabel("LocatoR");						//Label für Applikationsname
		lblName.setHorizontalAlignment(SwingConstants.CENTER);
		lblName.setBounds(0, 54, 302, 62);
		lblName.setForeground(Color.WHITE);							
		lblName.setVerticalAlignment(SwingConstants.TOP);
		lblName.setFont(new Font("Century", Font.PLAIN, 24));
		contentPane.add(lblName);
		
		JButton btnLogin = new JButton("Einloggen");					//Button, um zum Einloggen-Fenster zu gelangen
		btnLogin.setFont(new Font("Century", Font.PLAIN, 14));
		btnLogin.setBounds(53, 175, 189, 41);
		btnLogin.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {				
				
				Login Login = new Login();
				Login.setVisible(true);									//durch Drücken gelangt man zum Login-Fenster
				
			}
		});
		contentPane.add(btnLogin);
		
		JLabel lblLogin = new JLabel("Bitte melde dich an ");			//Label für Anmeldung
		lblLogin.setHorizontalAlignment(SwingConstants.CENTER);
		lblLogin.setBounds(0, 111, 302, 62);
		lblLogin.setFont(new Font("Century", Font.PLAIN, 14));
		lblLogin.setForeground(Color.WHITE);
		contentPane.add(lblLogin);
		
		JLabel lblRegistrierung = new JLabel("Bitte erstelle dir einen Account");		//Label für Registrierung
		lblRegistrierung.setHorizontalAlignment(SwingConstants.CENTER);
		lblRegistrierung.setBounds(0, 242, 302, 62);
		lblRegistrierung.setFont(new Font("Century", Font.PLAIN, 14));	
		lblRegistrierung.setForeground(Color.WHITE);
		contentPane.add(lblRegistrierung);
		
		JButton btnNewButton = new JButton("Registrierung");							//Button, um zum Registrierungs-Fenster zu gelangen
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {								
				
				Registierung registrierung;
				try {
					registrierung = new Registierung();
					registrierung.setVisible(true);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}			//durch Drücken gelangt man zum Registrierungsfenster
				
			
			}
		});
		btnNewButton.setFont(new Font("Century", Font.PLAIN, 14));
		btnNewButton.setBounds(55, 304, 187, 41);
		contentPane.add(btnNewButton);
	}
}
