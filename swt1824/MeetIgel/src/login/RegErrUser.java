package login;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import startseite.BevorstehendeVeranstaltungen;

import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextArea;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class RegErrUser extends JDialog {

	// Variablendeklaration
	private javax.swing.JLabel textZeile2;
	private javax.swing.JButton okButton;
	private javax.swing.JLabel textZeile1;

	// Dialog erstellen
	public RegErrUser(JFrame frame) {
		super(frame);
		initComponents();
	}

	// Dialog initialisieren
	private void initComponents() {

		textZeile1 = new javax.swing.JLabel();
		okButton = new javax.swing.JButton();
		textZeile2 = new javax.swing.JLabel();

		// Festlegen der Hintergrundfarbe und Schließen des Dialogs
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		getContentPane().setBackground(new java.awt.Color(246, 250, 252));

		// Text für den Nutzer
		textZeile1.setText("Username oder Email ist schon vorhanden.");
		textZeile2.setText("Bitte wähle einen anderen Usernamen oder Email.");

		// Button, um wieder zum Registrieren zu kommen
		okButton.setFont(new java.awt.Font("Tahoma", 1, 14));
		okButton.setText("O.K.");

		// Dialog schließen beim Klicken des OK-Buttons
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		// Hinzufügen der einzelnen Komponenten zum DIalog
		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		// Horizontale Anordnung der Komponenten
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addContainerGap(67, Short.MAX_VALUE)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
										layout.createSequentialGroup().addComponent(textZeile1).addGap(87, 87, 87))
								.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
										layout.createSequentialGroup().addComponent(textZeile2).addGap(65, 65, 65))
								.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
										layout.createSequentialGroup()
												.addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 84,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addGap(160, 160, 160)))));
		// Vertikale Anordnung der Komponenten
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addGap(39, 39, 39).addComponent(textZeile1)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
								javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(textZeile2).addGap(18, 18, 18).addComponent(okButton).addGap(32, 32, 32)));

		pack();
	}
}
