package gui;

import controlclasses.*;
import java.awt.Color;
import java.awt.EventQueue;

import java.util.*;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class NeuenGruppenraumErstellen extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtGruppennamenEingeben;
	private JTextField Gruppenraum;
	private JComboBox<String> auswahl;
	private JComboBox<String> comboBox;
	private JButton btnNewButton;
	private JButton btnNewButton_1;

	/**
	 * Create the frame.
	 */
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Client user = new Client();
					NeuenGruppenraumErstellen frame = new NeuenGruppenraumErstellen(user);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	
	public NeuenGruppenraumErstellen(Client user) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 320, 450);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setBackground(new Color(70, 130, 180));
		contentPane.setLayout(null);
		
		JLabel lblNeuenGruppenraumErstellen = new JLabel("Neuer Gruppenraum");
		lblNeuenGruppenraumErstellen.setFont(new Font("Century", Font.PLAIN, 24));
		lblNeuenGruppenraumErstellen.setForeground(Color.WHITE);
		lblNeuenGruppenraumErstellen.setHorizontalAlignment(SwingConstants.CENTER);						//Label für "Neuen Gruppenraum erstellen"
		lblNeuenGruppenraumErstellen.setBounds(0, 0, 303, 83);
		contentPane.add(lblNeuenGruppenraumErstellen);
		
		JTextField txtGruppennamenEingeben = new JTextField();											//Textfeld, um Gruppennamen einzugeben
		txtGruppennamenEingeben.setHorizontalAlignment(SwingConstants.CENTER);
		txtGruppennamenEingeben.setBounds(43, 96, 228, 27);
		contentPane.add(txtGruppennamenEingeben);
		txtGruppennamenEingeben.setColumns(10);
		
		Gruppenraum = new JTextField();
		Gruppenraum.setHorizontalAlignment(SwingConstants.CENTER);
		Gruppenraum.setToolTipText("");
		Gruppenraum.setBounds(43, 163, 228, 27);
		contentPane.add(Gruppenraum);
		Gruppenraum.setColumns(10);
		
		JLabel Treffpunktlabel = new JLabel("Treffpunktsatmosph\u00E4re w\u00E4hlen");				//Label für "Treffpunktsatmosphäre wählen"
		Treffpunktlabel.setFont(new Font("Century", Font.PLAIN, 14));
		Treffpunktlabel.setHorizontalAlignment(SwingConstants.CENTER);
		Treffpunktlabel.setForeground(Color.WHITE);
		Treffpunktlabel.setBounds(0, 214, 303, 27);
		contentPane.add(Treffpunktlabel);
		
		
		comboBox = new JComboBox<String>();
		comboBox.setBounds(43, 239, 228, 34);
		contentPane.add(comboBox);
		List<String> cBoxListe;						//Auswahlbox mit Treffpunktsatmosphären
		cBoxListe = new ArrayList<String>();
		cBoxListe.add(feel.Einzelarbeit.toString());
		cBoxListe.add(feel.Gruppenarbeit.toString());
		cBoxListe.add(feel.Gesellig.toString());
		// TODO adde hier weitere Kategorisierungen falls neue eingeführt werden.
		
		//cBoxListe.add("Bitte auswählen");
		//cBoxListe.add("Einzelarbeit");
		//cBoxListe.add("Gruppenarbeit");
		//cBoxListe.add("Gruppentreff");
		comboBox.addItem(cBoxListe.get(0));
		comboBox.addItem(cBoxListe.get(1));
		comboBox.addItem(cBoxListe.get(2));
		// TODO adde weitere ITEMS falls neue Kategorisierungen geaddet wurden siehe oben.
		
		btnNewButton = new JButton("Zur\u00FCck");
		btnNewButton.setFont(new Font("Century", Font.PLAIN, 14));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {							//zurück zum Auswahlbildschirm
				
				Auswahlbildschirm auswahlbild = new Auswahlbildschirm(user);		
				auswahlbild.setVisible(true);										//Öffnen des Frames "Auswahlbildschirm"
				
			}
		});
		btnNewButton.setBounds(43, 320, 111, 34);
		contentPane.add(btnNewButton);
		
		btnNewButton_1 = new JButton("Erstellen");						//Button für "Erstellen"
		btnNewButton_1.setFont(new Font("Century", Font.PLAIN, 14));
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				Lobby lobby = new Lobby(user, new Gruppe());
				lobby.setVisible(true);							//Öffnen der Lobby
				
			}
		});
		btnNewButton_1.setBounds(160, 320, 111, 34);
		contentPane.add(btnNewButton_1);
		
		JLabel lblGruppennamenEingeben = new JLabel("Gruppennamen eingeben");
		lblGruppennamenEingeben.setHorizontalAlignment(SwingConstants.CENTER);
		lblGruppennamenEingeben.setFont(new Font("Century", Font.PLAIN, 14));
		lblGruppennamenEingeben.setBounds(0, 71, 303, 27);
		contentPane.add(lblGruppennamenEingeben);
		
		JLabel lblBenutzernamenEingeben = new JLabel("Benutzernamen eingeben");
		lblBenutzernamenEingeben.setHorizontalAlignment(SwingConstants.CENTER);
		lblBenutzernamenEingeben.setFont(new Font("Century", Font.PLAIN, 14));
		lblBenutzernamenEingeben.setBounds(0, 136, 303, 27);
		contentPane.add(lblBenutzernamenEingeben);
	}


	public JTextField getTxtGruppennamenEingeben() {
		return txtGruppennamenEingeben;
	}


	public void setTxtGruppennamenEingeben(JTextField txtGruppennamenEingeben) {
		this.txtGruppennamenEingeben = txtGruppennamenEingeben;
	}


	public JComboBox<String> getAuswahl() {
		return auswahl;
	}


	public void setAuswahl(JComboBox<String> auswahl) {
		this.auswahl = auswahl;
	}
}
