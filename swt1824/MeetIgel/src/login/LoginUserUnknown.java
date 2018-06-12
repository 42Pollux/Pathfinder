package login;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginUserUnknown extends JDialog {

	// Variablendeklaration
	private JButton cancelButton;
	private JButton registrierenButton;
	private JLabel txtUserUnbekannt;

	// Dialog erstellen
	public LoginUserUnknown(JFrame frame) {
		super(frame);
		initComponents(frame);
	}

	// Initialisieren des Dialogs
	private void initComponents(JFrame frame) {

		txtUserUnbekannt = new javax.swing.JLabel();
		registrierenButton = new javax.swing.JButton();
		cancelButton = new javax.swing.JButton();

		// Schlieﬂen des Fensters und Festlegen der Hintergrundfarbe
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		getContentPane().setBackground(new java.awt.Color(246, 250, 252));

		// Text des Labels, sowie Schriftart und Schriftgrˆﬂe
		txtUserUnbekannt.setFont(new java.awt.Font("Tahoma", Font.PLAIN, 16));
		txtUserUnbekannt.setText("Nutzername unbekannt. Willst du dich bei MeetIgel registrieren?");

		// Text und Schrift der Buttons
		registrierenButton.setFont(new java.awt.Font("Tahoma", 1, 14));
		registrierenButton.setText("Registrieren");
		cancelButton.setFont(new java.awt.Font("Tahoma", 1, 14));
		cancelButton.setText("Cancel");

		// Weiterleitung zur Registrierung
		registrierenButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				frame.getContentPane().removeAll();
				frame.getContentPane().add(new Registrieren(frame));
				frame.setTitle("Registrieren");
				frame.pack();
			}
		});

		// Dialog schlieﬂen
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		// Anordnung der Elemente
		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		// Horizontale Ausrichtung der Elemente im Dialog
		layout.setHorizontalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup().addGroup(layout
								.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
										.createSequentialGroup()
										.addGap(165, 165, 165)
										.addComponent(registrierenButton, javax.swing.GroupLayout.PREFERRED_SIZE, 155,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(cancelButton))
								.addGroup(layout.createSequentialGroup().addGap(55, 55, 55)
										.addComponent(txtUserUnbekannt)))
								.addContainerGap(60, Short.MAX_VALUE)));
		// Vertikale Ausrichtung der Elemente im Dialog
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addGap(35, 35, 35).addComponent(txtUserUnbekannt)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 44, Short.MAX_VALUE)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(registrierenButton).addComponent(cancelButton))
						.addGap(33, 33, 33)));

		pack();
	}
}
