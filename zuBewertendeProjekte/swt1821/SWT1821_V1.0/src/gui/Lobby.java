package gui;

import controlclasses.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Color;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;

public class Lobby extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;



	/**
	 * Create the frame.
	 */
	public Lobby(Client user, Gruppe g) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 320, 450);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));			
		setContentPane(contentPane);	
		contentPane.setBackground(new Color(70, 130, 180));
		contentPane.setLayout(null);
		
		JLabel lblIdlabel = new JLabel(g.getKuerzel());		//Label, welches die zufällig erzeugt ID anzeigt
		lblIdlabel.setForeground(Color.WHITE);
		lblIdlabel.setFont(new Font("Century", Font.BOLD, 14));
		lblIdlabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblIdlabel.setBounds(0, 1, 69, 34);
		contentPane.add(lblIdlabel);
		
		JLabel lbllerngruppe = new JLabel("\"Lerngruppe\"");		//Label für "Lerngruppe"
		lbllerngruppe.setForeground(Color.WHITE);
		lbllerngruppe.setFont(new Font("Century", Font.PLAIN, 24));
		lbllerngruppe.setHorizontalAlignment(SwingConstants.CENTER);
		lbllerngruppe.setBounds(0, 11, 302, 34);
		contentPane.add(lbllerngruppe);
		
		JLabel lblAktuellerTreffpunkt = new JLabel("Aktueller Treffpunkt");			//Label für "Aktueller Treffpunkt"
		lblAktuellerTreffpunkt.setForeground(Color.WHITE);
		lblAktuellerTreffpunkt.setFont(new Font("Century", Font.PLAIN, 18));
		lblAktuellerTreffpunkt.setHorizontalAlignment(SwingConstants.CENTER);
		lblAktuellerTreffpunkt.setBounds(0, 58, 302, 24);
		contentPane.add(lblAktuellerTreffpunkt);
		
		JLabel lblTpktlabel = new JLabel("TPKT_LABEL");				//Label, welche den ausgewählten Treffpunkt anzeigt
		lblTpktlabel.setForeground(Color.WHITE);
		lblTpktlabel.setFont(new Font("Century", Font.PLAIN, 18));
		lblTpktlabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblTpktlabel.setBounds(0, 91, 302, 24);
		contentPane.add(lblTpktlabel);
		
		JLabel lblMitglieder = new JLabel("Mitglieder");
		lblMitglieder.setForeground(Color.WHITE);
		lblMitglieder.setHorizontalAlignment(SwingConstants.CENTER);
		lblMitglieder.setFont(new Font("Century", Font.BOLD, 14));
		lblMitglieder.setBounds(0, 141, 302, 24);					//Label für "Mitglieder"
		contentPane.add(lblMitglieder);
		
		
		JLabel LabelMitglied_1 = new JLabel("Mitglied1");		//Label für "Mitglied 1"
		LabelMitglied_1.setForeground(Color.WHITE);
		LabelMitglied_1.setHorizontalAlignment(SwingConstants.CENTER);
		LabelMitglied_1.setFont(new Font("Century", Font.PLAIN, 14));
		LabelMitglied_1.setBounds(0, 166, 302, 24);
		contentPane.add(LabelMitglied_1);
		
		JLabel LabelMitglied_2 = new JLabel("Mitglied2");		//Label für "Mitglied 2"
		LabelMitglied_2.setForeground(Color.WHITE);
		LabelMitglied_2.setHorizontalAlignment(SwingConstants.CENTER);
		LabelMitglied_2.setFont(new Font("Century", Font.PLAIN, 14));
		LabelMitglied_2.setBounds(0, 193, 302, 24);
		contentPane.add(LabelMitglied_2);
		
		JLabel lblMitglied_3 = new JLabel("Mitglied3");		//Label für "Mitglied 3"
		lblMitglied_3.setForeground(Color.WHITE);
		lblMitglied_3.setHorizontalAlignment(SwingConstants.CENTER);
		lblMitglied_3.setFont(new Font("Century", Font.PLAIN, 14));
		lblMitglied_3.setBounds(0, 218, 302, 24);
		contentPane.add(lblMitglied_3);
		
		JLabel lblMitglied_4 = new JLabel("Mitglied4");		//Label für "Mitglied 4"
		lblMitglied_4.setForeground(Color.WHITE);
		lblMitglied_4.setHorizontalAlignment(SwingConstants.CENTER);
		lblMitglied_4.setFont(new Font("Century", Font.PLAIN, 14));
		lblMitglied_4.setBounds(0, 243, 302, 24);
		contentPane.add(lblMitglied_4);
		
		JLabel lblMitglied_5 = new JLabel("Mitglied 5");	//Label für "Mitglied 5"
		lblMitglied_5.setForeground(Color.WHITE);
		lblMitglied_5.setHorizontalAlignment(SwingConstants.CENTER);
		lblMitglied_5.setFont(new Font("Century", Font.PLAIN, 14));
		lblMitglied_5.setBounds(0, 266, 302, 24);
		contentPane.add(lblMitglied_5);
		
		JButton btnNewButton = new JButton("Treffpunkt berechnen");				//Button für "Treffpunkt berechnen"
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				TreffpunktBerechnen treffpunktberechn = new TreffpunktBerechnen(user, g);			//Öffnen des Frames "Treffpunkt berechnen"
				treffpunktberechn.setVisible(true);
				
			}
		});
		btnNewButton.setBounds(46, 304, 209, 23);
		contentPane.add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("Gruppeneinstellungen ");				//Button für "Gruppeneinstellungen"
		btnNewButton_1.setBounds(46, 338, 209, 23);
		contentPane.add(btnNewButton_1);
		
		JButton btnNewButton_2 = new JButton("Gruppenraum l\u00F6schen");			//Button für "Gruppenraum löschen"
		btnNewButton_2.setBounds(46, 372, 209, 23);
		contentPane.add(btnNewButton_2);
	}
}
