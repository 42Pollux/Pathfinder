package login;

import javax.swing.JDialog;
import javax.swing.JFrame;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegErrPW extends JDialog {

	// Variablendeklaration
	private javax.swing.JButton jButtonOK;
	private javax.swing.JLabel lblDieAngegebenenPasswrter;

	// Dialog erstellen
	public RegErrPW(JFrame frame) {
		super(frame);
		initComponents();
	}

	// Initialisieren des Dialogs
	void initComponents() {
		lblDieAngegebenenPasswrter = new javax.swing.JLabel();
		jButtonOK = new javax.swing.JButton();

		// Schließen des Dialogs und Festlegen der Hintergrundfarbe
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		getContentPane().setBackground(new java.awt.Color(246, 250, 252));

		// Text und Schriftart-und größe des JLabels
		lblDieAngegebenenPasswrter.setFont(new java.awt.Font("Tahoma", Font.PLAIN, 16));
		lblDieAngegebenenPasswrter.setText("Die angegebenen Passwörter stimmen nicht überein.");

		// Text und Schriftart-und größe des JButtons
		jButtonOK.setFont(new java.awt.Font("Tahoma", Font.BOLD, 14));
		jButtonOK.setText("O.K.");

		// Dialog schließen beim Klicken des OK-Buttons
		jButtonOK.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		// Platzierung der einzelnen Komponenten im JDialog
		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		// Horizontale Anordnung der Komponenten
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
				.createSequentialGroup()
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup().addGap(196, 196, 196).addComponent(jButtonOK,
								javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGroup(layout.createSequentialGroup().addGap(55, 55, 55)
								.addComponent(lblDieAngegebenenPasswrter)))
				.addContainerGap(58, Short.MAX_VALUE)));
		// Vertikale Anordnung der Komponenten
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addGap(37, 37, 37).addComponent(lblDieAngegebenenPasswrter)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
						.addComponent(jButtonOK).addGap(32, 32, 32)));

		pack();
	}

}
