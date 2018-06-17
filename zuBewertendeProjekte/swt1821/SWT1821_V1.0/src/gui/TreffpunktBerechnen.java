package gui;

import controlclasses.*;
import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;

public class TreffpunktBerechnen extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private Standort treffpunkt;

	
	/**
	 * Create the frame.
	 */
	public TreffpunktBerechnen(Client user, Gruppe g) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 320, 450);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setBackground(new Color(70, 130, 180));
		contentPane.setLayout(null);
		
		JButton btnZurck = new JButton("<");			//Button für "Zurück"
		btnZurck.setFont(new Font("Tahoma", Font.PLAIN, 11));
		btnZurck.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				Lobby lobby = new Lobby(user, g);				//Öffnen der Lobby
				lobby.setVisible(true);
				
			}
		});
		btnZurck.setBounds(5, 5, 40, 40);
		contentPane.add(btnZurck);
		
		JRadioButton Treffpunkt1 = new JRadioButton("");			//Auswahlbutton für ersten Treffpunkt
		Treffpunkt1.setBounds(200, 89, 21, 23);
		contentPane.add(Treffpunkt1);
		
		JRadioButton Treffpunkt2 = new JRadioButton("");	//Auswahlbutton für zweiten Treffpunkt
		Treffpunkt2.setBounds(200, 126, 21, 23);
		contentPane.add(Treffpunkt2);
		
		JRadioButton Treffpunkt3 = new JRadioButton("");			//Auswahlbutton für dritten Treffpunkt
		Treffpunkt3.setBounds(200, 169, 21, 23);
		contentPane.add(Treffpunkt3);
		
		 ButtonGroup group = new ButtonGroup(); //Gruppierung so dass nur ein Knopf gedrückt sein kann
		    group.add(Treffpunkt1); 
		    group.add(Treffpunkt2);
		    group.add(Treffpunkt3);
	
		    
		    Treffpunkt1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				TreffpunktBerechnen.this.treffpunkt = new Standort(Treffpunkt.Treffpunkt1.toString(), "Hier Straße angeben");
				
			}
			
		});    
		
		    Treffpunkt2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e1) {
				TreffpunktBerechnen.this.treffpunkt = new Standort(Treffpunkt.Treffpunkt2.toString(), "Hier Straße angeben");
				
			}
			
		});    
		
		    Treffpunkt3.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e2) {
				TreffpunktBerechnen.this.treffpunkt = new Standort(Treffpunkt.Treffpunkt3.toString(), "Hier Straße angeben");
				
			}
			
		});    
		    	
		
		JLabel lblTreffpunkt_1 = new JLabel("Treffpunkt1");				//Label, wo erster Treffpunkt angezeigt wird
		lblTreffpunkt_1.setBounds(68, 89, 126, 23);
		lblTreffpunkt_1.setFont(new Font("Century", Font.PLAIN, 14));
		lblTreffpunkt_1.setForeground(Color.WHITE);
		contentPane.add(lblTreffpunkt_1);
		
		JLabel lblTreffpunkt_2 = new JLabel("Treffpunkt2");			//Label, wo zweiter Treffpunkt angezeigt wird
		lblTreffpunkt_2.setBounds(68, 126, 126, 23);
		lblTreffpunkt_2.setFont(new Font("Century", Font.PLAIN, 14));
		lblTreffpunkt_2.setForeground(Color.WHITE);
		contentPane.add(lblTreffpunkt_2);
		
		JLabel lblTreffpunkt_3 = new JLabel("Treffpunkt3");			//Label, wo dritter Treffpunkt angezeigt wird
		lblTreffpunkt_3.setBounds(68, 169, 126, 23);
		lblTreffpunkt_3.setFont(new Font("Century", Font.PLAIN, 14));
		lblTreffpunkt_3.setForeground(Color.WHITE);
		contentPane.add(lblTreffpunkt_3);
		
		JButton btnNewButton = new JButton("Ausw\u00E4hlen");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				if(!(group.isSelected((ButtonModel) Treffpunkt1)||group.isSelected((ButtonModel) Treffpunkt1)||group.isSelected((ButtonModel) Treffpunkt1))) {
				
						new Exception("kein Treffpunkt gewählt");
				
				}
				else {	
					g.setTreffpunkt(treffpunkt);
					Lobby lobby = new Lobby(user, g);				//Öffnen der Lobby
					lobby.setVisible(true);
				}
			}
		});
		btnNewButton.setBounds(68, 253, 153, 23);					//Button für "Auswählen"
		
		contentPane.add(btnNewButton);
	}

}
