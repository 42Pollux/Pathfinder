package veranstaltungBeitreten;


import javax.swing.JDialog;
import javax.swing.JFrame;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VerErrPW extends JDialog {

	// Variabledeklaration 
	private javax.swing.JLabel txtFalschesPasswort;
	private javax.swing.JButton nochmalButton;

	// neuer Dialog
	public VerErrPW(JFrame frame) {
		super(frame);
		initComponents();
		
		// Aufrufen der Methode dispose beim Klicken des nochmal Buttons 
		// und Zurückkehren zum Parent Frame
		nochmalButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
	}
	
	
	// Initialisieren des Dialogs  	
    private void initComponents() {

        txtFalschesPasswort = new javax.swing.JLabel();
        nochmalButton = new javax.swing.JButton();

        // Schließen und Hintergrundfarbe des Dialogs
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new java.awt.Color(246, 250, 252));

        // Text und Schrift des JLabels
        txtFalschesPasswort.setFont(new java.awt.Font("Tahoma", Font.PLAIN, 16)); 
        txtFalschesPasswort.setText("Falsches Passwort!");

        // Text und Schrift des Buttons
        nochmalButton.setFont(new java.awt.Font("Tahoma", Font.BOLD, 14)); 
        nochmalButton.setText("Nochmal versuchen");

        // Platzierung der Komponenten im JDialog 
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        // Horizontale Ausrichtung der Komponenten im Dialog
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(173, 173, 173)
                        .addComponent(txtFalschesPasswort))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(139, 139, 139)
                        .addComponent(nochmalButton, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(139, Short.MAX_VALUE))
        );
        // Vertikale Ausrichtung der Komponenten im Dialog
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(txtFalschesPasswort)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 44, Short.MAX_VALUE)
                .addComponent(nochmalButton)
                .addGap(33, 33, 33))
        );

        pack();
    }
    
}