package veranstaltungErstellen;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JFrame;

public class TitelZuLang extends JDialog {

	// Variablendeklaration
	private javax.swing.JLabel txtFalschesPasswort;
	private javax.swing.JButton okButton;

	// Dialog erstellen
	public TitelZuLang(JFrame frame, String titelZuLang) {
		super(frame);
		initComponents(titelZuLang);
	}

	// Dialog initialisieren
	private void initComponents(String titelZuLang) {

		txtFalschesPasswort = new javax.swing.JLabel();
		okButton = new javax.swing.JButton();

		// Schlieﬂen und Hintergrundfarbe des Dialogs
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		getContentPane().setBackground(new java.awt.Color(246, 250, 252));

		// Text und Schrift des JLabels
		txtFalschesPasswort.setFont(new java.awt.Font("Tahoma", Font.PLAIN, 16));
		txtFalschesPasswort.setText("Der Titel ist mit "  + titelZuLang.length() + " Zeichen zu lang. Bitte gib maximal 40 Zeichen ein.");

		// Text und Schrift des Buttons
		okButton.setFont(new java.awt.Font("Tahoma", Font.BOLD, 14));
		okButton.setText("O.K.");

		// Dialog schlieﬂen beim Klicken des nochmal-versuchen-Buttons
		okButton.addActionListener(new ActionListener() {

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
											layout.createSequentialGroup().addGap(50, 50, 50)
											.addComponent(txtFalschesPasswort))
									.addGroup(layout.createSequentialGroup().addGap(194, 194, 194).addComponent(
											okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 203,
											javax.swing.GroupLayout.PREFERRED_SIZE)))
							.addGap(50)));
					// Vertikale Anordung der Elemente im Dialog
					layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
							.addGroup(layout.createSequentialGroup().addGap(35, 35, 35).addComponent(txtFalschesPasswort)
									.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 44, Short.MAX_VALUE)
									.addComponent(okButton).addGap(33, 33, 33)));

					pack();
				}
}
