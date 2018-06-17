package gui;

import controlclasses.*;
import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class GruppenraumBeitreten extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	private Vehikel v;
	private Gruppe g;
	

	/**
	 * Create the frame.
	 */
	public GruppenraumBeitreten(Client user) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 320, 450);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setBackground(new Color(70, 130, 180));
		contentPane.setLayout(null);
		
		JLabel lblGruppenraumBeitreten = new JLabel("Gruppenraum beitreten");			//Label für "Gruppenraum beitreten"
		lblGruppenraumBeitreten.setForeground(Color.WHITE);
		lblGruppenraumBeitreten.setFont(new Font("Century", Font.PLAIN, 24));
		lblGruppenraumBeitreten.setHorizontalAlignment(SwingConstants.CENTER);
		lblGruppenraumBeitreten.setBounds(0, 11, 302, 27);
		contentPane.add(lblGruppenraumBeitreten);
		
		JLabel lblNewLabel = new JLabel("Name");										//Label für "Name"
		lblNewLabel.setForeground(Color.WHITE);
		lblNewLabel.setFont(new Font("Century", Font.PLAIN, 14));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(0, 51, 302, 14);
		contentPane.add(lblNewLabel);
		
		textField = new JTextField();										//Textfeld für Nameneingabe
		textField.setBounds(33, 74, 236, 20);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JLabel lblAdresse = new JLabel("Adresse");							//Label für "Adresse"
		lblAdresse.setForeground(Color.WHITE);
		lblAdresse.setFont(new Font("Century", Font.PLAIN, 14));
		lblAdresse.setHorizontalAlignment(SwingConstants.CENTER);
		lblAdresse.setBounds(0, 107, 302, 19);
		contentPane.add(lblAdresse);
		
		textField_1 = new JTextField();										//Textfeld für Straßennamen
		textField_1.setBounds(33, 135, 236, 20);
		contentPane.add(textField_1);
		textField_1.setColumns(10);
		
		textField_2 = new JTextField();										//Textfeld für PLZ
		textField_2.setBounds(33, 166, 236, 20);
		contentPane.add(textField_2);
		textField_2.setColumns(10);
		
		textField_3 = new JTextField();										//Textfeld für Stadt
		textField_3.setBounds(33, 197, 236, 20);
		contentPane.add(textField_3);
		textField_3.setColumns(10);
		
		JLabel lblAnfahrt = new JLabel("Anfahrt");							//Label für Anfahrt
		lblAnfahrt.setForeground(Color.WHITE);
		lblAnfahrt.setFont(new Font("Century", Font.PLAIN, 14));
		lblAnfahrt.setHorizontalAlignment(SwingConstants.CENTER);
		lblAnfahrt.setBounds(0, 230, 302, 19);
		contentPane.add(lblAnfahrt);
		
		JRadioButton rdbtnFahrrad = new JRadioButton("Fahrrad");			//Auswahlbutton für "Fahrrad
		rdbtnFahrrad.setBounds(33, 267, 109, 23);
		contentPane.add(rdbtnFahrrad);
		
		JRadioButton rdbtnAutp = new JRadioButton("Auto");					//Asuswahlbutton für "Auto"
		rdbtnAutp.setBounds(33, 293, 109, 23);
		contentPane.add(rdbtnAutp);
		
		JRadioButton rdbtnNewRadioButton = new JRadioButton("zu Fu\u00DF");			//Auswahlbutton für "Zu Fuß"
		rdbtnNewRadioButton.setBounds(175, 267, 109, 23);
		contentPane.add(rdbtnNewRadioButton);
		
		JRadioButton rdbtnpnv = new JRadioButton("\u00D6PNV");				//Auswahlbutton für "ÖPNV"
		rdbtnpnv.setBounds(175, 293, 109, 23);
		contentPane.add(rdbtnpnv);
		
		 ButtonGroup group = new ButtonGroup(); //Gruppierung so dass nur ein Knopf gedrückt sein kann
		    group.add(rdbtnFahrrad); 
		    group.add(rdbtnAutp);
		    group.add(rdbtnNewRadioButton);
		    group.add(rdbtnpnv);
		    
		    rdbtnFahrrad.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				GruppenraumBeitreten.this.setV(Vehikel.Fahrrad);
				
			}
			
		});    
		
		    rdbtnpnv.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e1) {
				GruppenraumBeitreten.this.setV(Vehikel.ÖPNV);
				
			}
			
		});    
		
		    rdbtnAutp.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e2) {
				GruppenraumBeitreten.this.setV(Vehikel.Auto);
				
			}
			
		});    
		    
		    rdbtnNewRadioButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e3) {
				GruppenraumBeitreten.this.setV(Vehikel.zu_Fuß);
				
			}
			
		}); 
		
		
		JButton btnBesttigen = new JButton("Best\u00E4tigen");			//Button für "Bestätigen"
		btnBesttigen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				Lobby lobby = new Lobby(user, g);						//Öffnen der Lobby
				lobby.setVisible(true);
				
			}
		});
		btnBesttigen.setBounds(175, 342, 109, 23);
		contentPane.add(btnBesttigen);
		
		JButton btnZurck = new JButton("Zur\u00FCck");			//Button für "Zurück"
		btnZurck.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				Auswahlbildschirm auswahl = new Auswahlbildschirm(user);		//Öffnen des Auswahlbildschirms
				auswahl.setVisible(true);
				
			}
		});
		btnZurck.setBounds(33, 342, 109, 23);
		contentPane.add(btnZurck);
	}


	public Vehikel getV() {
		return v;
	}


	public void setV(Vehikel v) {
		this.v = v;
	}
}
