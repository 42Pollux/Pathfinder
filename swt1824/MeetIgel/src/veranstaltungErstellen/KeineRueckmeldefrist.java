package veranstaltungErstellen;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JFrame;

public class KeineRueckmeldefrist extends JDialog {

	// Variablendeklaration 
	private javax.swing.JButton okButton;
	private javax.swing.JLabel text;

	// neuer Dialogs 
	public KeineRueckmeldefrist(JFrame frame) {
		super(frame);
		initComponents();
		
		// Dialog beseitigen beim Klicken des okButtons
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

	}
	
	// Initialisieren des Dialogs
	 private void initComponents() {

			text = new javax.swing.JLabel();
			okButton = new javax.swing.JButton();

			// Schließen des Dialogs und Hintergrundfarbe des Dialogs 
			setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			getContentPane().setBackground(new java.awt.Color(246, 250, 252));

			// Textinformation für den Benutzer
			text.setFont(new java.awt.Font("Tahoma", Font.PLAIN, 16)); 
			text.setText("Bitte gib eine Rückmeldefrist an.");

			// Text und Schrift des Buttons 
			okButton.setFont(new java.awt.Font("Tahoma", Font.BOLD, 14)); 
			okButton.setText("O.K.");

			
			//Platzierung der Komponenten im Dialogfenster 
			javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
			getContentPane().setLayout(layout);
			// Horizontale Ausrichtung der Elemente 
		        layout.setHorizontalGroup(
		            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
		            .addGroup(layout.createSequentialGroup()
		                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
		                    .addGroup(layout.createSequentialGroup()
		                        .addGap(181, 181, 181)
		                        .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
		                    .addGroup(layout.createSequentialGroup()
		                        .addGap(96, 96, 96)
		                        .addComponent(text)))
		                .addContainerGap(97, Short.MAX_VALUE))
		        );
		        // Vertikale Ausrichtung der Elemente
		        layout.setVerticalGroup(
		            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
		            .addGroup(layout.createSequentialGroup()
		                .addGap(33, 33, 33)
		                .addComponent(text)
		                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 47, Short.MAX_VALUE)
		                .addComponent(okButton)
		                .addGap(32, 32, 32))
		        );

		        pack();
		    }
}