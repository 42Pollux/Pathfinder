package gui;

import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import controlclasses.*;

import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.awt.event.ActionEvent;
import java.awt.Font;

public class Auswahlbildschirm extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField_1;


	/**
	 * Create the frame.
	 */
	public Auswahlbildschirm(Client user) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setBounds(100, 100, 320, 450);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setBackground(new Color(70, 130, 180));
		contentPane.setLayout(null);
		
		JButton btnNeuenGruppenraumErstellen = new JButton("Neuen Gruppenraum erstellen");
		btnNeuenGruppenraumErstellen.setForeground(new Color(0, 0, 0));
		btnNeuenGruppenraumErstellen.setBackground(new Color(255, 255, 255));
		btnNeuenGruppenraumErstellen.setFont(new Font("Century", Font.PLAIN, 14));
		btnNeuenGruppenraumErstellen.addActionListener(new ActionListener() {						//Button für "Gruppenraum erstellen"
			public void actionPerformed(ActionEvent e) {
				
				NeuenGruppenraumErstellen GraumErstellen = new NeuenGruppenraumErstellen(user);			//Öffnen des Frames "Neuen Gruppenraum erstellen"
				GraumErstellen.setVisible(true);
				
			}
		});
		btnNeuenGruppenraumErstellen.setBounds(33, 190, 240, 33);
		contentPane.add(btnNeuenGruppenraumErstellen);
		
		JLabel lblEinemGruppenraumBeitreten = new JLabel("Einem Gruppenraum beitreten");
		lblEinemGruppenraumBeitreten.setForeground(new Color(255, 255, 255));
		lblEinemGruppenraumBeitreten.setHorizontalAlignment(SwingConstants.CENTER);
		lblEinemGruppenraumBeitreten.setFont(new Font("Century", Font.PLAIN, 14));
		lblEinemGruppenraumBeitreten.setBounds(0, 272, 302, 22);									//Label für Gruppenraum beitreten
		contentPane.add(lblEinemGruppenraumBeitreten);
		
		JTextField kuerzelField = new JTextField();
		kuerzelField.setBounds(62, 307, 111, 33);												//Textfeld für Code-Eingabe
		contentPane.add(kuerzelField);
		kuerzelField.setColumns(10);
		
		JButton btnOk = new JButton("OK");
		btnOk.setBackground(new Color(255, 255, 255));
		btnOk.setFont(new Font("Century", Font.PLAIN, 14));
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {										//Button  "OK"
				
				gruppeBeitretenMessage gruppeBM = new gruppeBeitretenMessage(user.getEmail(), kuerzelField.getText());
				
				try {
				ObjectOutputStream out = (ObjectOutputStream) user.clientSocket.getOutputStream();
				ObjectInputStream datain = new ObjectInputStream(user.clientSocket.getInputStream());
			
				while(datain.available()>0) {
					out.writeObject((MethodeMessage) gruppeBM);
					out.flush();
					
				}
				
				if(datain.available()==0) {
					GruppenraumBeitreten graumbeitreten = new GruppenraumBeitreten(user);
					graumbeitreten.setVisible(true);
					}
				}
				catch(IOException e) {
					System.out.println("no correct I/O");
				
				}
				
				
			}
		});
		btnOk.setBounds(173, 307, 67, 33);
		contentPane.add(btnOk);
		
		JLabel lblLocatoR = new JLabel("LocatoR");
		lblLocatoR.setForeground(new Color(255, 255, 255));
		lblLocatoR.setHorizontalAlignment(SwingConstants.CENTER);
		lblLocatoR.setBackground(Color.WHITE);
		lblLocatoR.setBounds(62, 66, 187, 64);
		lblLocatoR.setFont(new Font("Century", Font.PLAIN, 35));								//Label für App-Namen
		contentPane.add(lblLocatoR);
	}


	public JTextField getTextField_1() {
		return textField_1;
	}


	public void setTextField_1(JTextField textField_1) {
		this.textField_1 = textField_1;
	}

}
