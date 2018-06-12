package veranstaltungErstellen;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class OrtUnknown extends JDialog {

	// Variablendeklaration
	private javax.swing.JButton okButton;
	private javax.swing.JLabel textZeile1;
	private javax.swing.JLabel textZeile2;

	// neuer Dialog 
	public OrtUnknown(JFrame frame) {
		super(frame);
		initComponents();
		
		// Schließen des Dialogs
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

	}
	
	// Initialisieren des Dialogs
	 private void initComponents() {

		textZeile1 = new javax.swing.JLabel();
		textZeile2 = new javax.swing.JLabel();
		okButton = new javax.swing.JButton();

		// Schließen des Dialogs und Hintergrundfarbe des Dialogs 
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		getContentPane().setBackground(new java.awt.Color(246, 250, 252));

		// Textinformation für den Benutzer
		textZeile1.setFont(new java.awt.Font("Tahoma", Font.PLAIN, 16)); 
		textZeile1.setText("Ort unbekannt oder du hast einen Umlaut eingegeben.");
		textZeile2.setFont(new java.awt.Font("Tahoma", Font.PLAIN, 16)); 
		textZeile2.setText("Bitte überprüfe deine Eingabe.");

		// Text und Schrift des Buttons 
		okButton.setFont(new java.awt.Font("Tahoma", Font.BOLD, 14)); 
		okButton.setText("O.K.");

		
		//Platzierung der Komponenten im Dialogfenster 
		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		// Horizontale Anordnung der Komponenten
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addContainerGap(67, Short.MAX_VALUE)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
										layout.createSequentialGroup()
										.addComponent(textZeile1)
										.addGap(87, 87, 87))
								.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
										layout.createSequentialGroup()
										.addComponent(textZeile2)
										.addGap(155, 155, 155))
								.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
										layout.createSequentialGroup()
								.addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(240)))));
		// Vertikale Anordnung der Komponenten
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addGap(39, 39, 39).addComponent(textZeile1)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
								javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(textZeile2).addGap(18, 18, 18).addComponent(okButton).addGap(32, 32, 32)));

		pack();
	}
}
