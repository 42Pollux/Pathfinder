package login;

import javax.swing.JDialog;
import javax.swing.JFrame;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginError extends JDialog {

	// Variablendeklaration
	private javax.swing.JLabel txtFalschesPasswort;
	private javax.swing.JButton nochmalVersuchenButton;

	// Dialog erstellen
	public LoginError(JFrame frame) {
		super(frame);
		initComponents();
	}

	// Dialog initialisieren
	private void initComponents() {

		txtFalschesPasswort = new javax.swing.JLabel();
		nochmalVersuchenButton = new javax.swing.JButton();

		// Schlieﬂen und Hintergrundfarbe des Dialogs
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		getContentPane().setBackground(new java.awt.Color(246, 250, 252));

		// Text und Schrift des JLabels
		txtFalschesPasswort.setFont(new java.awt.Font("Tahoma", Font.PLAIN, 16));
		txtFalschesPasswort.setText("Falsches Passwort!");

		// Text und Schrift des Buttons
		nochmalVersuchenButton.setFont(new java.awt.Font("Tahoma", Font.BOLD, 14));
		nochmalVersuchenButton.setText("Nochmal versuchen");

		// Dialog schlieﬂen beim Klicken des nochmal-versuchen-Buttons
		nochmalVersuchenButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		// Platzierung der Komponenten im JDialog
		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		// Horizontale Anordung der Elemente im DIalog
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
				.createSequentialGroup()
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								layout.createSequentialGroup().addGap(173, 173, 173).addComponent(txtFalschesPasswort))
						.addGroup(layout.createSequentialGroup().addGap(139, 139, 139).addComponent(
								nochmalVersuchenButton, javax.swing.GroupLayout.PREFERRED_SIZE, 203,
								javax.swing.GroupLayout.PREFERRED_SIZE)))
				.addContainerGap(139, Short.MAX_VALUE)));
		// Vertikale Anordung der Elemente im Dialog
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addGap(35, 35, 35).addComponent(txtFalschesPasswort)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 44, Short.MAX_VALUE)
						.addComponent(nochmalVersuchenButton).addGap(33, 33, 33)));

		pack();
	}

}
