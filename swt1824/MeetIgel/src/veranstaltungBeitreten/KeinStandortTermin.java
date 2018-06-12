package veranstaltungBeitreten;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JFrame;

public class KeinStandortTermin extends JDialog {

	// Variablendeklaration
	private javax.swing.JLabel txtFalscheAngaben;
	private javax.swing.JButton okButton;
	private javax.swing.JLabel txtZeile2;

	// neuer Dialog
	public KeinStandortTermin(JFrame frame) {
		super(frame);
		initComponents();
	}

	// Initialisierung des Dialogs
	private void initComponents() {

		txtFalscheAngaben = new javax.swing.JLabel();
		okButton = new javax.swing.JButton();
		txtZeile2 = new javax.swing.JLabel();

		// Schließen und Hintergrundfarbe des Dialogs
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		getContentPane().setBackground(new java.awt.Color(246, 250, 252));

		// Text und Schrift der JLabels
		txtFalscheAngaben.setFont(new java.awt.Font("Tahoma", Font.PLAIN, 16));
		txtFalscheAngaben.setText("Du hast keinen Termin ausgewählt oder gültigen Standort angegeben.");
		txtZeile2.setFont(new java.awt.Font("Tahoma", Font.PLAIN, 16));
		txtZeile2.setText("Bitte überprüfe deine Angaben.");

		// Text und Schrift des Buttons
		okButton.setFont(new java.awt.Font("Tahoma", Font.BOLD, 14));
		okButton.setText("O.K.");

		// Dialog schließen beim Klicken des nochmal-Buttons
		// zurückkehren zum Parent-Frame
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		// Platzierung der Komponenten im JDialog
		// Festlegen des Layouts (GroupLayout) für den Dialog
		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
						layout.createSequentialGroup().addContainerGap(23, Short.MAX_VALUE)
								.addComponent(txtFalscheAngaben).addGap(25, 25, 25))
				.addGroup(layout.createSequentialGroup().addGroup(layout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup().addGap(152, 152, 152).addComponent(txtZeile2))
						.addGroup(layout.createSequentialGroup().addGap(226, 226, 226).addComponent(okButton,
								javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)))
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addContainerGap(40, Short.MAX_VALUE)
						.addComponent(txtFalscheAngaben)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(txtZeile2)
						.addGap(33, 33, 33).addComponent(okButton).addContainerGap()));

		pack();
	}

}
